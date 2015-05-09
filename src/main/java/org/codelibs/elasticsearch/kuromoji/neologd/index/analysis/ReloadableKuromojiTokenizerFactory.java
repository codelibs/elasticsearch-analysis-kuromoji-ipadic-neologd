/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.codelibs.elasticsearch.kuromoji.neologd.index.analysis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.EnumMap;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeSource;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer.Mode;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer.Type;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.dict.Dictionary;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.dict.TokenInfoFST;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.dict.UserDictionary;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettings;

public class ReloadableKuromojiTokenizerFactory extends
        AbstractTokenizerFactory {

    private static final boolean VERBOSE = false; // debug

    protected static final Reader ILLEGAL_STATE_READER = new Reader() {
        @Override
        public int read(final char[] cbuf, final int off, final int len) {
            throw new IllegalStateException(
                    "TokenStream contract violation: reset()/close() call missing, "
                            + "reset() called multiple times, or subclass does not call super.reset(). "
                            + "Please see Javadocs of TokenStream class for more information about the correct consuming workflow.");
        }

        @Override
        public void close() {
        }
    };

    protected final Field inputPendingField;

    protected final Field userDictionaryField;

    protected final Field userFSTField;

    protected final Field userFSTReaderField;

    protected final Field dictionaryMapField;

    private Environment env;

    private Settings settings;

    private File reloadableFile = null;

    protected volatile long dictionaryTimestamp;

    private volatile long lastChecked;

    private long reloadInterval;

    private volatile UserDictionary userDictionary;

    private final Mode mode;

    private boolean discartPunctuation;

    @Inject
    public ReloadableKuromojiTokenizerFactory(final Index index,
            @IndexSettings final Settings indexSettings, final Environment env,
            @Assisted final String name, @Assisted final Settings settings) {
        super(index, indexSettings, name, settings);
        this.env = env;
        this.settings = settings;
        mode = KuromojiTokenizerFactory.getMode(settings);
        userDictionary = KuromojiTokenizerFactory.getUserDictionary(env,
                settings);
        discartPunctuation = settings.getAsBoolean("discard_punctuation", true);

        try {
            inputPendingField = Tokenizer.class
                    .getDeclaredField("inputPending");
            inputPendingField.setAccessible(true);
            userDictionaryField = JapaneseTokenizer.class
                    .getDeclaredField("userDictionary");
            userDictionaryField.setAccessible(true);
            userFSTField = JapaneseTokenizer.class.getDeclaredField("userFST");
            userFSTField.setAccessible(true);
            userFSTReaderField = JapaneseTokenizer.class
                    .getDeclaredField("userFSTReader");
            userFSTReaderField.setAccessible(true);
            dictionaryMapField = JapaneseTokenizer.class
                    .getDeclaredField("dictionaryMap");
            dictionaryMapField.setAccessible(true);
        } catch (final Exception e) {
            throw new ElasticsearchIllegalArgumentException(
                    "Failed to load fields.", e);
        }

        dictionaryTimestamp = System.currentTimeMillis();

        final String monitoringFilePath = settings.get("user_dictionary");
        if (monitoringFilePath != null) {
            URL fileUrl = env.resolveConfig(monitoringFilePath);

            try {
                final File file = new File(fileUrl.toURI());
                if (file.exists()) {
                    reloadableFile = file;
                    dictionaryTimestamp = reloadableFile.lastModified();

                    reloadInterval = settings.getAsTime("reload_interval",
                            TimeValue.timeValueMinutes(1)).getMillis();

                    if (VERBOSE) {
                        System.out.println("Check "
                                + reloadableFile.getAbsolutePath()
                                + " (interval: " + reloadInterval + "ms)");
                    }
                }
            } catch (Exception e) {
                throw new ElasticsearchIllegalArgumentException(
                        "Could not access " + monitoringFilePath, e);
            }
        }

    }

    @Override
    public Tokenizer create(final Reader input) {
        return new TokenizerWrapper(input);
    }

    private void updateUserDictionary() {
        if (reloadableFile != null
                && System.currentTimeMillis() - lastChecked > reloadInterval) {
            lastChecked = System.currentTimeMillis();
            long timestamp = reloadableFile.lastModified();
            if (timestamp != dictionaryTimestamp) {
                synchronized (reloadableFile) {
                    if (timestamp != dictionaryTimestamp) {
                        userDictionary = KuromojiTokenizerFactory
                                .getUserDictionary(env, settings);
                        dictionaryTimestamp = timestamp;
                    }
                }
            }
        }
    }

    public final class TokenizerWrapper extends Tokenizer {

        private JapaneseTokenizer tokenizer;

        private long tokenizerTimestamp;

        TokenizerWrapper(final Reader input) {
            super(ILLEGAL_STATE_READER);

            tokenizerTimestamp = dictionaryTimestamp;
            tokenizer = new JapaneseTokenizer(input, userDictionary,
                    discartPunctuation, mode);

            try {
                Field attributesField = AttributeSource.class
                        .getDeclaredField("attributes");
                attributesField.setAccessible(true);
                final Object attributesObj = attributesField.get(tokenizer);
                attributesField.set(this, attributesObj);

                Field attributeImplsField = AttributeSource.class
                        .getDeclaredField("attributeImpls");
                attributeImplsField.setAccessible(true);
                final Object attributeImplsObj = attributeImplsField
                        .get(tokenizer);
                attributeImplsField.set(this, attributeImplsObj);

                Field currentStateField = AttributeSource.class
                        .getDeclaredField("currentState");
                currentStateField.setAccessible(true);
                final Object currentStateObj = currentStateField.get(tokenizer);
                currentStateField.set(this, currentStateObj);
            } catch (final Exception e) {
                throw new IllegalStateException(
                        "Failed to update the tokenizer.", e);
            }
        }

        @Override
        public void close() throws IOException {
            tokenizer.close();
        }

        @Override
        public void reset() throws IOException {
            updateUserDictionary();

            if (dictionaryTimestamp > tokenizerTimestamp) {
                if (VERBOSE) {
                    System.out.println("Update KuromojiTokenizer ("
                            + tokenizerTimestamp + "," + dictionaryTimestamp
                            + ")");
                }
                if (userDictionary != null) {
                    try {
                        tokenizerTimestamp = dictionaryTimestamp;
                        userDictionaryField.set(tokenizer, userDictionary);
                        TokenInfoFST userFst = userDictionary.getFST();
                        userFSTField.set(tokenizer, userFst);
                        userFSTReaderField.set(tokenizer,
                                userFst.getBytesReader());
                        @SuppressWarnings("unchecked")
                        EnumMap<Type, Dictionary> dictionaryMap = (EnumMap<Type, Dictionary>) dictionaryMapField.get(tokenizer);
                        dictionaryMap.put(Type.USER, userDictionary);
                    } catch (final Exception e) {
                        throw new IllegalStateException(
                                "Failed to update the tokenizer.", e);
                    }
                }
            }

            final Reader inputPending = getInputPending();
            if (inputPending != ILLEGAL_STATE_READER) {
                tokenizer.setReader(inputPending);
            }
            tokenizer.reset();
        }

        @Override
        public boolean incrementToken() throws IOException {
            return tokenizer.incrementToken();
        }

        @Override
        public void end() throws IOException {
            tokenizer.end();
        }

        @Override
        public int hashCode() {
            return tokenizer.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return tokenizer.equals(obj);
        }

        @Override
        public String toString() {
            return tokenizer.toString();
        }

        private Reader getInputPending() {
            try {
                return (Reader) inputPendingField.get(this);
            } catch (final Exception e) {
                return null;
            }
        }

    }

}

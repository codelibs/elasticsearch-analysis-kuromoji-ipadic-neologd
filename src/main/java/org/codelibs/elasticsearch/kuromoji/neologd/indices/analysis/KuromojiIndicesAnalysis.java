/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codelibs.elasticsearch.kuromoji.neologd.indices.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseAnalyzer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseIterationMarkCharFilter;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapanesePartOfSpeechStopFilter;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseReadingFormFilter;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer.Mode;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltCharFilterFactoryFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenizerFactoryFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

/**
 * Registers indices level analysis components so, if not explicitly configured,
 * will be shared among all indices.
 */
public class KuromojiIndicesAnalysis extends AbstractComponent {

    @Inject
    public KuromojiIndicesAnalysis(Settings settings,
                                   IndicesAnalysisService indicesAnalysisService) {
        super(settings);

        indicesAnalysisService.analyzerProviderFactories().put("kuromoji_neologd",
                new PreBuiltAnalyzerProviderFactory("kuromoji_neologd", AnalyzerScope.INDICES,
                        new JapaneseAnalyzer()));

        indicesAnalysisService.charFilterFactories().put("kuromoji_neologd_iteration_mark",
                new PreBuiltCharFilterFactoryFactory(new CharFilterFactory() {
                    @Override
                    public String name() {
                        return "kuromoji_neologd_iteration_mark";
                    }

                    @Override
                    public Reader create(Reader reader) {
                        return new JapaneseIterationMarkCharFilter(reader,
                                JapaneseIterationMarkCharFilter.NORMALIZE_KANJI_DEFAULT,
                                JapaneseIterationMarkCharFilter.NORMALIZE_KANA_DEFAULT);
                    }
                }));

        indicesAnalysisService.tokenizerFactories().put("kuromoji_neologd_tokenizer",
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
                    @Override
                    public String name() {
                        return "kuromoji_neologd_tokenizer";
                    }

                    @Override
                    public Tokenizer create() {
                        return new JapaneseTokenizer(null, true, Mode.SEARCH);
                    }
                }));

        indicesAnalysisService.tokenFilterFactories().put("kuromoji_neologd_baseform",
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                    @Override
                    public String name() {
                        return "kuromoji_neologd_baseform";
                    }

                    @Override
                    public TokenStream create(TokenStream tokenStream) {
                        return new JapaneseBaseFormFilter(tokenStream);
                    }
                }));

        indicesAnalysisService.tokenFilterFactories().put(
                "kuromoji_neologd_part_of_speech",
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                    @Override
                    public String name() {
                        return "kuromoji_neologd_part_of_speech";
                    }

                    @Override
                    public TokenStream create(TokenStream tokenStream) {
                        return new JapanesePartOfSpeechStopFilter(tokenStream, JapaneseAnalyzer
                                .getDefaultStopTags());
                    }
                }));

        indicesAnalysisService.tokenFilterFactories().put(
                "kuromoji_neologd_readingform",
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                    @Override
                    public String name() {
                        return "kuromoji_neologd_readingform";
                    }

                    @Override
                    public TokenStream create(TokenStream tokenStream) {
                        return new JapaneseReadingFormFilter(tokenStream, true);
                    }
                }));

        indicesAnalysisService.tokenFilterFactories().put("kuromoji_neologd_stemmer",
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                    @Override
                    public String name() {
                        return "kuromoji_neologd_stemmer";
                    }

                    @Override
                    public TokenStream create(TokenStream tokenStream) {
                        return new JapaneseKatakanaStemFilter(tokenStream);
                    }
                }));
    }
}

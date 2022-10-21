package org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.PosConcatenationFilter;
import org.codelibs.analysis.ja.PosConcatenationFilter.PartOfSpeechSupplier;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

public class PosConcatenationFilterFactory extends AbstractTokenFilterFactory {

    private Set<String> posTags = new HashSet<>();

    public PosConcatenationFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);

        List<String> tagList = Analysis.getWordList(environment, settings, "tags");
        if (tagList != null) {
            posTags.addAll(tagList);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        final PartOfSpeechAttribute posAtt = tokenStream.addAttribute(PartOfSpeechAttribute.class);
        return new PosConcatenationFilter(tokenStream, posTags, new PartOfSpeechSupplier() {
            @Override
            public String get() {
                return posAtt.getPartOfSpeech();
            }
        });
    }
}

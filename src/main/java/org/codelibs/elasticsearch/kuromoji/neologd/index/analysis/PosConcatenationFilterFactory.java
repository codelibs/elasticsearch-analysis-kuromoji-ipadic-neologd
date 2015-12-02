package org.codelibs.elasticsearch.kuromoji.neologd.index.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.PosConcatenationFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.index.settings.IndexSettingsService;

public class PosConcatenationFilterFactory extends AbstractTokenFilterFactory {

    private Set<String> posTags = new HashSet<>();

    @Inject
    public PosConcatenationFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, Environment env) {
        super(index, indexSettingsService.getSettings(), name, settings);

        List<String> tagList = Analysis.getWordList(env, settings, "tags");
        if (tagList != null) {
            posTags.addAll(tagList);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new PosConcatenationFilter(tokenStream, posTags);
    }
}

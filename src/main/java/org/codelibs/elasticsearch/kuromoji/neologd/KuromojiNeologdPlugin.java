package org.codelibs.elasticsearch.kuromoji.neologd;

import java.util.Collection;

import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiAnalyzerProvider;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiBaseFormFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiKatakanaStemmerFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiPartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiReadingFormFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiTokenizerFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.ReloadableKuromojiTokenizerFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.indices.analysis.KuromojiIndicesAnalysisModule;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class KuromojiNeologdPlugin extends AbstractPlugin {
    @Override
    public String name() {
        return "analysis-kuromoji-neologd";
    }

    @Override
    public String description() {
        return "This plugin provides analysis library for Kuromoji with Neologd.";
    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        return ImmutableList.<Class<? extends Module>> of(KuromojiIndicesAnalysisModule.class);
    }

    public void onModule(AnalysisModule module) {
        module.addCharFilter("kuromoji_neologd_iteration_mark", KuromojiIterationMarkCharFilterFactory.class);
        module.addAnalyzer("kuromoji_neologd", KuromojiAnalyzerProvider.class);
        module.addTokenizer("kuromoji_neologd_tokenizer", KuromojiTokenizerFactory.class);
        module.addTokenFilter("kuromoji_neologd_baseform", KuromojiBaseFormFilterFactory.class);
        module.addTokenFilter("kuromoji_neologd_part_of_speech", KuromojiPartOfSpeechFilterFactory.class);
        module.addTokenFilter("kuromoji_neologd_readingform", KuromojiReadingFormFilterFactory.class);
        module.addTokenFilter("kuromoji_neologd_stemmer", KuromojiKatakanaStemmerFactory.class);

        module.addTokenizer("reloadable_kuromoji_neologd_tokenizer", ReloadableKuromojiTokenizerFactory.class);
        module.addTokenizer("reloadable_kuromoji_neologd", ReloadableKuromojiTokenizerFactory.class);
    }
}

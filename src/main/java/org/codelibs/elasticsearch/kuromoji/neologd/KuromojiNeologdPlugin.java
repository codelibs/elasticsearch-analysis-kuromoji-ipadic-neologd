package org.codelibs.elasticsearch.kuromoji.neologd;

import java.util.Collection;

import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiAnalyzerProvider;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiBaseFormFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiKatakanaStemmerFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiPartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiReadingFormFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.KuromojiTokenizerFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.PosConcatenationFilterFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.index.analysis.ReloadableKuromojiTokenizerFactory;
import org.codelibs.elasticsearch.kuromoji.neologd.indices.analysis.KuromojiIndicesAnalysisModule;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

import com.google.common.collect.ImmutableList;

public class KuromojiNeologdPlugin extends Plugin {
    @Override
    public String name() {
        return "analysis-kuromoji-neologd";
    }

    @Override
    public String description() {
        return "Kuromoji with Neologd analysis support";
    }

    @Override
    public Collection<Module> indexModules(Settings indexSettings) {
        return ImmutableList.<Module> of(new KuromojiIndicesAnalysisModule());
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

        module.addTokenFilter("kuromoji_neologd_pos_concat", PosConcatenationFilterFactory.class);
    }
}

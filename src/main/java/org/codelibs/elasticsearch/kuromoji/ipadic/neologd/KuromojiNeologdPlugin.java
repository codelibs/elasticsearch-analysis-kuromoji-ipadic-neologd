package org.codelibs.elasticsearch.kuromoji.ipadic.neologd;

import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiAnalyzerProvider;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiBaseFormFilterFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiKatakanaStemmerFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiNumberFilterFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiPartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiReadingFormFilterFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.KuromojiTokenizerFactory;
import org.codelibs.elasticsearch.kuromoji.ipadic.neologd.index.analysis.PosConcatenationFilterFactory;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class KuromojiNeologdPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        return singletonMap("kuromoji_ipadic_neologd_iteration_mark", KuromojiIterationMarkCharFilterFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("kuromoji_ipadic_neologd_baseform", KuromojiBaseFormFilterFactory::new);
        extra.put("kuromoji_ipadic_neologd_part_of_speech", KuromojiPartOfSpeechFilterFactory::new);
        extra.put("kuromoji_ipadic_neologd_readingform", KuromojiReadingFormFilterFactory::new);
        extra.put("kuromoji_ipadic_neologd_stemmer", KuromojiKatakanaStemmerFactory::new);
        extra.put("kuromoji_ipadic_neologd_number", KuromojiNumberFilterFactory::new);
        extra.put("kuromoji_ipadic_neologd_pos_concat", PosConcatenationFilterFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("kuromoji_ipadic_neologd_tokenizer", KuromojiTokenizerFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return singletonMap("kuromoji_ipadic_neologd", KuromojiAnalyzerProvider::new);
    }
}

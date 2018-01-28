Elasticsearch Analysis Kuromoji Neologd
=======================

## Overview

Elasticsearch Analysis Neologd Plugin provides Tokenizer/CharFilter/TokenFilter for Kuromoji with Neologd.

## Version

[Versions in Maven Repository](http://central.maven.org/maven2/org/codelibs/elasticsearch-analysis-kuromoji-neologd/)

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-analysis-kuromoji-neologd/issues "issue").
(Japanese forum is [here](https://github.com/codelibs/codelibs-ja-forum "here").)

## Installation

### For 5.x - 6.x

    $ $ES_HOME/bin/elasticsearch-plugin install org.codelibs:elasticsearch-analysis-kuromoji-neologd:5.6.1

### For 2.x

    $ $ES_HOME/bin/plugin install org.codelibs/elasticsearch-analysis-kuromoji-neologd/2.4.1

## References

### Analyzer, Tokenizer, TokenFilter, CharFilter

The plugin includes these analyzer and tokenizer, tokenfilter.

| name                                     | type        |
|:-----------------------------------------|:-----------:|
| kuromoji\_neologd\_iteration\_mark       | charfilter  |
| kuromoji\_neologd                        | analyzer    |
| kuromoji\_neologd\_tokenizer             | tokenizer   |
| kuromoji\_neologd\_baseform              | tokenfilter |
| kuromoji\_neologd\_part\_of\_speech      | tokenfilter |
| kuromoji\_neologd\_readingform           | tokenfilter |
| kuromoji\_neologd\_stemmer               | tokenfilter |
| reloadable\_kuromoji\_neologd\_tokenizer | tokenizer   |

### Usage

See [Elasticsearch Kuromoji](https://github.com/elastic/elasticsearch-analysis-kuromoji "elasticsearch-analysis-kuromoji").

### Update Kuromoji Jar File

If you want to replace with the latest Lucene Neologd jar file, download it from http://maven.codelibs.org/org/codelibs/lucene-analyzers-kuromoji-ipadic-neologd/ and then replace old file in $ES_HOME/plugins/analysis-kuromoji-neologd.

### What is NEologd

See [mecab-ipadic-NEologd](https://github.com/neologd/mecab-ipadic-neologd "mecab-ipadic-NEologd").

### References to build Lucene Kuromoji with NEologd

* http://d.hatena.ne.jp/Kazuhira/20150316/1426520209
* http://mocobeta-backup.tumblr.com/post/114318023832

## Use Lucene Kuromoji for Neologd

If you want to use Lucene Kuromoji for Neologd in your application other than elasticsearch, you can use lucene-analyzers-kuromoji-ipadic-neologd jar file, not this plugin.
To use the jar file, put the following settings into your pom.xml.

    ...
    <repositories>
        <repository>
            <id>codelibs.org</id>
            <name>CodeLibs Repository</name>
            <url>http://maven.codelibs.org/</url>
        </repository>
    </repositories>
    ...
    <dependencies>
        <dependency>
            <groupId>org.codelibs</groupId>
            <artifactId>lucene-analyzers-kuromoji-ipadic-neologd</artifactId>
            <version>5.4.1-20160218</version>
            <!-- http://maven.codelibs.org/org/codelibs/lucene-analyzers-kuromoji-ipadic-neologd/ --->
        </dependency>
    ...

See [CodeLibs Maven Repository](http://maven.codelibs.org/org/codelibs/lucene-analyzers-kuromoji-ipadic-neologd/).


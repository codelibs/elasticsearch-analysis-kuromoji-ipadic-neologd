Elasticsearch Analysis Kuromoji Neologd
=======================

## Overview

Elasticsearch Analysis Neologd Plugin provides Tokenizer/CharFilter/TokenFilter for Kuromoji with Neologd.

## Version

| Version   | Tested on ES | neologd  |
|:---------:|:------------:|:--------:|
| master    | 2.1.X        |          |
| 2.1.1     | 2.1.1        | 20151231 |
| 2.1.0     | 2.1.0        | 20151214 |
| 2.0.0     | 2.0.0        | 20151102 |
| 1.7.1     | 1.7.1        | 20150918 |
| 1.6.0     | 1.6.0        | 20150608 |
| 1.5.1     | 1.5.2        | 20150501 |
| 1.5.0     | 1.5.0        | 20150324 |
| 1.4.0     | 1.4.4        | 20150319 |

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-analysis-kuromoji-neologd/issues "issue").
(Japanese forum is [here](https://github.com/codelibs/codelibs-ja-forum "here").)

## Installation

    $ $ES_HOME/bin/plugin install org.codelibs/elasticsearch-analysis-kuromoji-neologd/2.1.1

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




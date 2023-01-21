[![Release](https://jitpack.io/v/umjammer/lucene-gosen.svg)](https://jitpack.io/#umjammer/lucene-gosen)
[![Java CI](https://github.com/umjammer/lucene-gosen/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/lucene-gosen/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/lucene-gosen/actions/workflows/codeql.yml/badge.svg)](https://github.com/umjammer/lucene-gosen/actions/workflows/codeql.yml)
![Java](https://img.shields.io/badge/Java-8-b07219)
[![Parent](https://img.shields.io/badge/Parent-vavi--sen-pink)](https://gitlab.com/umjammer/sen)

# lucene-gosen

mavenized lucene-gosen

## TODO

 * extract java version dictionary converter (some dictionaries are different)
 * back port unit tests to sen 
 * https://github.com/lucene-gosen/lucene-gosen/

---
[Original](https://code.google.com/p/lucene-gosen/)

Installation With Apache Solr 3.6:

1. run 'ant'. this will make lucene-gosen-{version}.jar
2. create example/solr/lib and put this jar file in it.
3. copy stopwords_ja.txt and stoptags_ja.txt into example/solr/conf
4. add "text_ja_gosen" fieldtype: see example/schema.xml.snippet for example configuration.

refer to example/ for an example japanese configuration with comments explaining
   what the various configuration options are.

Installation with Apache Lucene 3.6:

1. run 'ant'. this will make lucene-gosen-{version}.jar
2. add this jar file to your classpath, and use GosenAnalyzer, or make your own analyzer from
   the various filters. Its recommended you extend ReusableAnalyzerBase to make any custom analyzer!

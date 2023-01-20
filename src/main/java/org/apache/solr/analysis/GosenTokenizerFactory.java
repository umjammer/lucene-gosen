/*
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import net.java.sen.filter.stream.CompositeTokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.gosen.GosenTokenizer;
import org.apache.lucene.analysis.standard.ClassicFilterFactory;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.util.IOUtils;
import org.apache.solr.core.SolrResourceLoader;


/**
 * Factory for {@link GosenTokenizer}.
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.GosenTokenizerFactory" compositePOS="compositePOS.txt" dictionaryDir="/opt/dictionary" /&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 */
public class GosenTokenizerFactory extends ClassicFilterFactory implements ResourceLoaderAware {

    private CompositeTokenFilter compositeTokenFilter;
    private String dictionaryDir;

    public GosenTokenizerFactory(Map<String, String> args) {
        super(args);
    }

    public void inform(ResourceLoader loader) {
        String compositePosFile = getOriginalArgs().get("compositePOS");
        if (compositePosFile != null) {
            compositeTokenFilter = new CompositeTokenFilter();
            InputStreamReader isr = null;
            BufferedReader reader = null;
            try {
                isr = new InputStreamReader(
                        loader.openResource(compositePosFile), StandardCharsets.UTF_8);
                reader = new BufferedReader(isr);
                compositeTokenFilter.readRules(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    IOUtils.close(reader, isr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String dirVal = getOriginalArgs().get("dictionaryDir");
        if (dirVal != null) {
            // no-dic jar
            SolrResourceLoader solrLoader = (SolrResourceLoader) loader;
            File d0 = new File(dirVal);
            File d = d0;
            if (!d.isAbsolute())
                d = new File(solrLoader.getConfigDir() + dirVal);
            if (d.isDirectory() && d.canRead()) {
                // relative path (from solr/conf)
                dictionaryDir = d.getAbsolutePath();
            } else if (d != d0 && d0.isDirectory() && d0.canRead()) {
                // relative path (from user.dir java properties)
                dictionaryDir = d0.getAbsolutePath();
            } else {
                // absolute path
                dictionaryDir = dirVal;
            }
        }
    }

    public Tokenizer create() {
        return new GosenTokenizer(compositeTokenFilter, dictionaryDir);
    }
}

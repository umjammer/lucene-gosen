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

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.gosen.GosenPartOfSpeechStopFilter;
import org.apache.lucene.analysis.standard.ClassicFilterFactory;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;


/**
 * Factory for {@link GosenPartOfSpeechStopFilter}.
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_ja" class="solr.TextField"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.GosenTokenizerFactory"/&gt;
 *     &lt;filter class="solr.GosenPartOfSpeechStopFilterFactory" 
 *             tags="stopTags.txt" 
 *             enablePositionIncrements="true"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 */
public class GosenPartOfSpeechStopFilterFactory extends ClassicFilterFactory implements ResourceLoaderAware {
    private boolean enablePositionIncrements;
    private Set<String> stopTags;

    public GosenPartOfSpeechStopFilterFactory(Map<String, String> args) {
        super(args);
    }

    public void inform(ResourceLoader loader) {
        String stopTagFiles = getOriginalArgs().get("tags");
        enablePositionIncrements = getBoolean(getOriginalArgs(), "enablePositionIncrements", false);
        try {
            CharArraySet cas = getWordSet(loader, stopTagFiles, false);
            stopTags = new HashSet<>();
            for (Object element : cas) {
                char[] chars = (char[]) element;
                stopTags.add(new String(chars));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TokenFilter create(TokenStream stream) {
        return new GosenPartOfSpeechStopFilter(enablePositionIncrements, stream, stopTags);
    }
}

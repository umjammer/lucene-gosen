/**
 * Copyright 2004 The Apache Software Foundation
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.analysis;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.util.LuceneTestCase;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.Test;


public class TestGosenTokenizerFactory extends LuceneTestCase {

    private File baseDir;
    private File confDir;
    private File dicDir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        File testRoot = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
        baseDir = new File(testRoot, "core-test");
        baseDir.mkdir();
        confDir = new File(baseDir, "conf");
        confDir.mkdir();
        dicDir = new File(confDir, "custom-dic");
        dicDir.mkdir();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        dicDir.delete();
        confDir.delete();
        baseDir.delete();
    }

    @Test
    public void testDictionaryDir() throws Exception {

        SolrResourceLoader loader = new SolrResourceLoader(baseDir.toPath(), GosenTokenizerFactory.class.getClassLoader());
        Map<String, String> args = new HashMap<>();
        GosenTokenizerFactory factory = new GosenTokenizerFactory(args);
        factory.inform(loader);
        Field field = GosenTokenizerFactory.class.getDeclaredField("dictionaryDir");
        field.setAccessible(true);
        assertNull("dictionaryDir must be null.", field.get(factory));

        // relative path (from conf dir)
        args.put("dictionaryDir", dicDir.getName());
        factory = new GosenTokenizerFactory(args);
        factory.inform(loader);
        assertEquals("dictionaryDir is incorrect.", dicDir.getName(), field.get(factory));

        // absolute path
        args.put("dictionaryDir", dicDir.getAbsolutePath());
        factory = new GosenTokenizerFactory(args);
        factory.inform(loader);
        assertEquals("dictionaryDir is incorrect.", dicDir.getAbsolutePath(), field.get(factory));

        // not exists path
        String notExistsPath = dicDir.getAbsolutePath() + "/hogehoge";
        args.put("dictionaryDir", notExistsPath);
        factory = new GosenTokenizerFactory(args);
        factory.inform(loader);
        assertEquals("dictionaryDir is incorrect.", notExistsPath, field.get(factory));
    }
}

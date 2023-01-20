/**
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

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.core.SolrResourceLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class TestGosenTokenizerFactory {

    private File baseDir;
    private File confDir;
    private File dicDir;

    @BeforeEach
    public void setUp() throws Exception {
        File testRoot = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
        baseDir = new File(testRoot, "core-test");
        baseDir.mkdir();
        confDir = new File(baseDir, "conf");
        confDir.mkdir();
        dicDir = new File(confDir, "custom-dic");
        dicDir.mkdir();
    }

    @AfterEach
    public void tearDown() throws Exception {
        dicDir.delete();
        confDir.delete();
        baseDir.delete();
    }

    @Test
    void testDictionaryDir() throws Exception {

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
        assertEquals(dicDir.getAbsolutePath(), field.get(factory), "dictionaryDir is incorrect.");

        // absolute path
        args.put("dictionaryDir", dicDir.getAbsolutePath());
        factory = new GosenTokenizerFactory(args);
        factory.inform(loader);
        assertEquals(dicDir.getAbsolutePath(), field.get(factory), "dictionaryDir is incorrect.");

        // not exists path
        String notExistsPath = dicDir.getAbsolutePath() + "/hogehoge";
        args.put("dictionaryDir", notExistsPath);
        factory = new GosenTokenizerFactory(args);
        factory.inform(loader);
        assertEquals(notExistsPath, field.get(factory), "dictionaryDir is incorrect.");
    }
}
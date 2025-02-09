/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.analysis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import net.java.sen.SenTestUtil;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import org.junit.Test;


public class TestGosenPartOfSpeechKeepFilterFactory extends BaseTokenStreamTestCase {

    private File baseDir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        File testRoot = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
        baseDir = new File(testRoot, "core-test");
        baseDir.mkdir();
    }

    @Override
    public void tearDown() throws Exception {
        baseDir.delete();
        super.tearDown();
    }

    @Test
    public void testBasics() throws IOException {
        String tags =
                "#  verb-main:\n" +
                        "動詞-自立\n";

        Map<String, String> args = new HashMap<>();
        args.put("dictionaryDir", SenTestUtil.IPADIC_DIR);
        GosenTokenizerFactory tokenizerFactory = new GosenTokenizerFactory(args);
        tokenizerFactory.inform(new StringMockResourceLoader(""));
        Tokenizer tokenizer = tokenizerFactory.create(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
        tokenizer.setReader(new StringReader("私は制限スピードを超える。"));
        args.put("tags", "stoptags.txt");
        GosenPartOfSpeechKeepFilterFactory factory = new GosenPartOfSpeechKeepFilterFactory(args);
        factory.inform(new StringMockResourceLoader(tags));
        TokenStream ts = factory.create(tokenizer);
        assertTokenStreamContents(ts,
                new String[] {"超える"}
        );
    }

    @Test
    public void testRequireArguments() throws Exception {
        try {
            Map<String, String> args = new HashMap<>();
            args.put("bogusArg", "bogusValue");
            new GosenPartOfSpeechKeepFilterFactory(args);
            fail();
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Configuration Error: missing parameter "));
        }
    }

    @Test
    public void testBogusArguments() throws Exception {
        try {
            Map<String, String> args = new HashMap<>();
            args.put("tags", "tags");
            args.put("bogusArg", "bogusValue");
            new GosenPartOfSpeechKeepFilterFactory(args);
            fail();
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Unknown parameters"));
        }
    }
}
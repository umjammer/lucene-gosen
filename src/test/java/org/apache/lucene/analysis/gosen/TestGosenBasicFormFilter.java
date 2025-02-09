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

package org.apache.lucene.analysis.gosen;

import java.io.IOException;
import java.util.Random;

import net.java.sen.SenTestUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;


@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class TestGosenBasicFormFilter extends BaseTokenStreamTestCase {

    static Random random;

    @BeforeClass
    public static void setUpBeforeClass() {
        random = random();
    }

    private Analyzer analyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String field) {
            Tokenizer tokenizer = new GosenTokenizer(newAttributeFactory(), null, SenTestUtil.IPADIC_DIR, false);
            TokenStream stream = new GosenBasicFormFilter(tokenizer);
            return new TokenStreamComponents(tokenizer, stream);
        }
    };

    @Test
    void testBasics() throws IOException {
        assertAnalyzesTo(analyzer, "それはまだ実験段階にあります。",
                new String[] {"それ", "は", "まだ", "実験", "段階", "に", "ある", "ます", "。"}
        );
    }

    @Test
    void testRandomStrings() throws IOException {
        checkRandomData(random(), analyzer, 10000);
    }
}

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

package org.apache.lucene.analysis.gosen.tokenAttributes;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;


public class SentenceStartAttributeImpl extends AttributeImpl implements SentenceStartAttribute, Cloneable {

    private static final long serialVersionUID = 1L;

    private boolean sentenceStart;

    public boolean getSentenceStart() {
        return sentenceStart;
    }

    public void setSentenceStart(boolean sentenceStart) {
        this.sentenceStart = sentenceStart;
    }

    @Override
    public void clear() {
        this.sentenceStart = false;
    }

    @Override
    public void reflectWith(AttributeReflector attributeReflector) {
    }

    @Override
    public void copyTo(AttributeImpl target) {
        SentenceStartAttribute t = (SentenceStartAttribute) target;
        t.setSentenceStart(sentenceStart);
    }
}

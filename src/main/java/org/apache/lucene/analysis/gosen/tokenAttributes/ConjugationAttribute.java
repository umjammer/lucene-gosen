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

import net.java.sen.dictionary.Morpheme;
import org.apache.lucene.util.Attribute;


/**
 * Attribute for {@link Morpheme#getConjugationalForm()} and
 * {@link Morpheme#getConjugationalType()}.
 * <p>
 * Note: depending on part of speech, these values may not be applicable,
 * and will be set to "*"
 */
public interface ConjugationAttribute extends Attribute {
    String getConjugationalForm();

    String getConjugationalType();

    void setMorpheme(Morpheme morpheme);
}

/*
 * Copyright (C) 2001-2007
 * Takashi Okamoto <tora@debian.org>
 * Matt Francis <asbel@neosheffield.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package net.java.sen.tokenizers.ja;

import net.java.sen.dictionary.CToken;
import net.java.sen.dictionary.Dictionary;
import net.java.sen.dictionary.Morpheme;
import net.java.sen.dictionary.Node;
import net.java.sen.dictionary.SentenceIterator;
import net.java.sen.dictionary.Tokenizer;
import net.java.sen.trie.CharIterator;

import static java.lang.Character.UnicodeBlock.LATIN_1_SUPPLEMENT;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_A;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_B;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_C;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_D;
import static java.lang.Character.UnicodeBlock.GREEK;
import static java.lang.Character.UnicodeBlock.GREEK_EXTENDED;
import static java.lang.Character.UnicodeBlock.CYRILLIC;
import static java.lang.Character.UnicodeBlock.CYRILLIC_EXTENDED_A;
import static java.lang.Character.UnicodeBlock.CYRILLIC_EXTENDED_B;
import static java.lang.Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY;

/**
 * A Tokenizer for Japanese text
 */
public class JapaneseTokenizer extends Tokenizer {

    /** Character class for non-Japanese text */
    static final int OTHER = 0x80;

    /** Character class for whitespace */
    static final int SPACE = 0x81;

    /** Character class for Kanji */
    static final int KANJI = 0x82;

    /** Character class for Katakana */
    static final int KATAKANA = 0x83;

    /** Character class for Hiragana */
    static final int HIRAGANA = 0x84;

    /** Character class for half-width forms */
    static final int HALF_WIDTH = 0x85;

    /**
     * Gets the character class of the given character
     *
     * @param c The character
     * @return The character class
     */
    private int getCharClass(char c) {
        // TODO: I think we are recalculating this over and over (n^2),
        // which is why the method is so sensitive to performance.
        // maybe instead we should calculate up-front in the sentence?
        // TODO: We should utilize char.def, which was introduced with MeCab,
        // that enables us to modify the tokenizer behavior without updating the code.
        if (c <= 0x7F) {
            return (c == ' ' || c == '\t' || c == '\r' || c == '\n') ? SPACE : Character.getType(Character.toLowerCase(c));
        } else if (c >= 0x3040 && c <= 0x309F) {
            return HIRAGANA;
        } else if ((c >= 0x30A0 && c <= 0x30FF && Character.getType(c) != Character.CONNECTOR_PUNCTUATION && c != 0x30FB) || (c >= 0x31F0 && c <= 0x31FF) || (c >= 0xFF66 && c <= 0xFF9F)) {
            return KATAKANA;
        } else if (c >= 0x4E00 && c <= 0x9FFF) {
            return KANJI;
        } else {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == LATIN_1_SUPPLEMENT || ub == LATIN_EXTENDED_ADDITIONAL || ub == LATIN_EXTENDED_A || ub == LATIN_EXTENDED_B || ub == LATIN_EXTENDED_C || ub == LATIN_EXTENDED_D) {
                // Latin Letters
                return Character.getType(Character.toLowerCase(c));
            } else if (ub == GREEK || ub == GREEK_EXTENDED) {
                // Greek
                return Character.getType(Character.toLowerCase(c));
            } else if (ub == CYRILLIC || ub == CYRILLIC_EXTENDED_A || ub == CYRILLIC_EXTENDED_B || ub == CYRILLIC_SUPPLEMENTARY) {
                // Cyrillic
                return Character.getType(Character.toLowerCase(c));
            } else {
                // Symbols
                return OTHER;
            }
        }
    }

    /**
     * Find the length to use for an unknown token
     *
     * @param iterator The iterator to read from
     * @return The length
     */
    private int findUnknownToken(CharIterator iterator) {
        int length = 0;

        if (iterator.hasNext()) {
            int charClass = getCharClass(iterator.next());
            switch (charClass) {
            case HIRAGANA:
            case KANJI:
            case OTHER:
                length = 1;
                break;

            case KATAKANA:
                if (isTokenizeUnknownKatakana()) {
                    length = 1;
                    break;
                }

            default:
                length = 1;
                while (iterator.hasNext() && (getCharClass(iterator.next()) == charClass)) {
                    length++;
                }
                break;
            }
        }

        return length;
    }

    @Override
    public Node lookup(SentenceIterator iterator, char[] surface) {
        Node resultNode = null;

        int charClass = getCharClass(iterator.current());

        int skipped = iterator.skippedCharCount();
        CToken[] t = getDictionary().commonPrefixSearch(iterator);
        for (int i = 0; !t[i].terminator; i++) {
            Node newNode = new Node();
            newNode.setCToken(t[i]);
            newNode.length = t[i].length;
            newNode.start = iterator.origin();
            newNode.span = t[i].length + skipped;
            newNode.rnext = resultNode;
            newNode.morpheme = new Morpheme(getDictionary(), t[i].partOfSpeechIndex);

            resultNode = newNode;
        }

        if ((resultNode != null) && (charClass == HIRAGANA || charClass == KANJI || (isTokenizeUnknownKatakana() && charClass == KATAKANA))) {
            return resultNode;
        }

        // Synthesize token for longest consecutive run of same character class
        iterator.rewindToOrigin();
        int unknownTokenLength = findUnknownToken(iterator);

        Node unknownNode = getUnknownNode(surface, iterator.origin(), unknownTokenLength, skipped + unknownTokenLength);
        unknownNode.rnext = resultNode;

        return unknownNode;
    }

    /**
     * Creates a JapaneseTokenizer with the given Dictionary
     *
     * @param dictionary                     The Dictionary in which to search for possible morphemes
     * @param unknownPartOfSpeechDescription The part-of-speech code to use for unknown tokens
     * @param tokenizeUnknownKatakana Grouping unknown tokens to generate a token.
     */
    public JapaneseTokenizer(Dictionary dictionary, String unknownPartOfSpeechDescription, boolean tokenizeUnknownKatakana) {
        super(dictionary, unknownPartOfSpeechDescription, tokenizeUnknownKatakana);
    }
}

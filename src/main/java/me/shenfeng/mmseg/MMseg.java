package me.shenfeng.mmseg;

import static java.lang.Character.OTHER_LETTER;
import static java.lang.Character.getType;

public class MMseg {

    char[] chars;
    int idx = 0;
    int start = 0;
    private Dictionary dic;
    private int lastType;

    public MMseg(Dictionary dic, String str) {
        this.dic = dic;
        chars = Utils.getChars(str);
    }

    public MMseg(Dictionary dic, char[] chars) {
        this.dic = dic;
        this.chars = chars;
    }

    private void advance() {
        while (++idx < chars.length) {
            if (getType(chars[idx]) != lastType) {
                break;
            }
        }
    }

    public Word next() {

        if (start < idx) {
            return next0();
        }

        char ch;
        while (idx < chars.length) {
            ch = chars[idx];
            lastType = Character.getType(ch);
            switch (lastType) {
            case OTHER_LETTER:
                start = idx;
                advance();
                return next0();
            case Character.UPPERCASE_LETTER:
            case Character.LOWERCASE_LETTER:
                start = idx;
                advance();
                int offset = start;
                start = idx;
                return new Word(chars, offset, idx - offset);
            }
            ++idx;
        }
        return null;
    }

    public Word next0() {
        int max = dic.maxMath(chars, start, idx - start);
        int offset = start;
        start += max;
        return new Word(chars, offset, max);
    }
}

package me.shenfeng.mmseg;

import static java.lang.Character.DECIMAL_DIGIT_NUMBER;
import static java.lang.Character.LOWERCASE_LETTER;
import static java.lang.Character.OTHER_LETTER;
import static java.lang.Character.UPPERCASE_LETTER;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public final class SimpleMMsegTokenizer extends Tokenizer {

    static final int EN = 1;
    static final int ZH = 2;
    static final int UNKNOW = 3;

    char[] buffer = new char[32]; // 32 for Chinese sentence, max en word
    int bufferIdx = 0;
    int bufferStart = 0;
    private int lastType = EN;

    int idx = 0; // reader index
    int read; // current read from reader

    private Dictionary dic;

    private PushbackReader reader;

    private CharTermAttribute termAtt;
    private OffsetAttribute offsetAtt;
    private boolean lowercase = true;

    private int type(int ch) {
        int t = Character.getType(ch);
        if (t == OTHER_LETTER) {
            return ZH;
        } else if (t == LOWERCASE_LETTER || t == UPPERCASE_LETTER) {
            return EN;
        } else if (t == DECIMAL_DIGIT_NUMBER) {
            if (lastType == UNKNOW) {
                // in case detail_2012, 2012 should be a word
                return EN;
            } else {
                return lastType;
            }
        }
        return UNKNOW;
    }

    public SimpleMMsegTokenizer(Dictionary dic, Reader input) {
        super(input);
        if (input instanceof PushbackReader) {
            reader = (PushbackReader) input;
        } else {
            reader = new PushbackReader(input, 1);
        }
        this.dic = dic;
        termAtt = addAttribute(CharTermAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
    }

    public SimpleMMsegTokenizer(Dictionary dic, Reader input,
            boolean lowercase) {
        this(dic, input);
        this.lowercase = lowercase;
    }

    public void addToBuffer(int read) {
        if (buffer.length == bufferIdx) {
            buffer = Arrays.copyOf(buffer, bufferIdx * 2);
        }
        buffer[bufferIdx++] = (char) read;
    }

    private void advance() throws IOException {
        while ((read = reader.read()) != -1) {
            if (type(read) == lastType) {
                ++idx; // advance reader index
                addToBuffer(read);
            } else {
                reader.unread(read);
                return;
            }
        }
    }

    public void nextCh() {
        int max = dic.maxMath(buffer, bufferStart, bufferIdx - bufferStart);
        int offset = bufferStart;
        bufferStart += max;
        termAtt.copyBuffer(buffer, offset, max);
        int start = idx - (bufferIdx - offset);
        offsetAtt.setOffset(start, start + max);
    }

    public void nextEn() {
        if (lowercase) {
            for (int i = bufferStart; i < bufferIdx; i++) {
                buffer[i] = Character.toLowerCase(buffer[i]);
            }
        }
        int length = bufferIdx - bufferStart;
        termAtt.copyBuffer(buffer, bufferStart, length);
        offsetAtt.setOffset(idx - length, idx);
        bufferIdx = bufferStart = 0; // clear
    }

    public boolean incrementToken() throws IOException {
        clearAttributes();
        if (bufferStart < bufferIdx) {
            nextCh(); // current, only Chinese
            return true;
        }

        bufferIdx = bufferStart = 0; // reset buffer

        while ((read = reader.read()) != -1) {
            ++idx;
            lastType = type(read);
            switch (lastType) {
            case ZH: // Chinese, etc
                addToBuffer(read);
                advance();
                nextCh();
                return true;
            case EN:
                addToBuffer(read);
                advance();
                nextEn();
                return true;
            }
        }

        return false;
    }
}

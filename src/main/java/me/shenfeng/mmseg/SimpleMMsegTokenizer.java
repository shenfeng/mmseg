package me.shenfeng.mmseg;

import static java.lang.Character.LOWERCASE_LETTER;
import static java.lang.Character.OTHER_LETTER;
import static java.lang.Character.UPPERCASE_LETTER;
import static java.lang.Character.getType;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public final class SimpleMMsegTokenizer extends Tokenizer {

    char[] buffer = new char[32]; // 32 for Chinese sentence, max en word
    int bufferIdx = 0;
    int bufferStart = 0;

    int idx = 0; // reader index

    int read; // current read from reader

    private Dictionary dic;

    private int lastType;
    private PushbackReader reader;

    private CharTermAttribute termAtt;
    private OffsetAttribute offsetAtt;

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

    public void addToBuffer(int read) {
        if (buffer.length == bufferIdx) {
            buffer = Arrays.copyOf(buffer, bufferIdx * 2);
        }
        buffer[bufferIdx++] = (char) read;
    }

    private void advance() throws IOException {
        while ((read = reader.read()) != -1) {
            if (getType(read) == lastType) {
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
        termAtt.copyBuffer(buffer, bufferStart, bufferIdx - bufferStart);
        offsetAtt.setOffset(idx - (bufferIdx - bufferStart), idx);
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
            lastType = getType(read);
            switch (lastType) {
            case OTHER_LETTER: // Chinese, etc
                addToBuffer(read);
                advance();
                nextCh();
                return true;
            case UPPERCASE_LETTER:
            case LOWERCASE_LETTER:
                addToBuffer(read);
                advance();
                nextEn();
                return true;
            }
        }

        return false;
    }
}

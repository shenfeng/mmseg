package me.shenfeng.mmseg;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CharReader extends Reader {

    private char[] ch;
    int idx = 0;

    public CharReader(char[] ch) {
        this.ch = ch;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if (off >= ch.length) {
            return -1;
        }

        int min = Math.min(len, ch.length - off);
        System.arraycopy(ch, off, cbuf, 0, min);
        return min;
    }

    public int read() throws IOException {
        if (idx == ch.length) {
            return -1;
        } else {
            return ch[idx++];
        }
    }

    public void close() throws IOException { // noop
    }
}

public class PerformanceTest {

    private static Logger logger = LoggerFactory
            .getLogger(PerformanceTest.class);

    char[] data;
    Dictionary bs;
    Dictionary hash;

    @Before
    public void setup() throws IOException {
        InputStream is = PerformanceTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");

        InputStream is2 = PerformanceTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");

        char[] book1 = Utils.getCharsFromResource("book1.txt");
        char[] book2 = Utils.getCharsFromResource("book2.txt");

        // copy as one array
        data = Arrays.copyOf(book1, book1.length + book2.length);
        System.arraycopy(book2, 0, data, book1.length - 1, book2.length);

        hash = new HashSetDictionary(is);
        bs = new BSDictionary(is2);
    }

    @Test
    public void testPerf() throws IOException {
        logger.info("Warm up");
        for (int i = 0; i < 6; i++) { // warm up
            SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(hash,
                    new CharReader(data));
            loopResult(tokenizer, hash, false);
            tokenizer = new SimpleMMsegTokenizer(bs, new CharReader(data));
            loopResult(tokenizer, hash, false);
        }

        SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(hash,
                new CharReader(data));
        loopResult(tokenizer, hash, true);
        tokenizer = new SimpleMMsegTokenizer(bs, new CharReader(data));
        loopResult(tokenizer, bs, true);
    }

    int loopResult(Tokenizer tokenizer, Dictionary dic, boolean print)
            throws IOException {
        long start = System.currentTimeMillis();
        CharTermAttribute termAtt = tokenizer
                .getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = tokenizer
                .getAttribute(OffsetAttribute.class);
        int i = 0;
        while (tokenizer.incrementToken()) {
            String word = new String(termAtt.buffer(), 0, termAtt.length());
            int s = offsetAtt.startOffset();
            int e = offsetAtt.endOffset();
            i = e;
        }
        if (print) {
            long time = System.currentTimeMillis() - start;
            logger.info("Dictionary: {} takes {}ms to seg {} char",
                    new Object[] { dic.getClass().getSimpleName(), time,
                            data.length });

        }
        return i; // prevent jit
    }
}

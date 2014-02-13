package me.shenfeng.mmseg;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class PerformanceTest {

    private static Logger logger = LoggerFactory
            .getLogger(PerformanceTest.class);

    char[] data;
    String datastr;
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

        datastr = new String(data);

        hash = new HashSetDictionary(is);
        bs = new BSDictionary(is2);
    }

    @Test
    public void testPerf() throws IOException {
        logger.info("Warm up");
        for (int i = 0; i < 3; i++) { // warm up
            SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(hash,
                    new StringReader(datastr));
            loopResult(tokenizer, hash, false);
            tokenizer = new SimpleMMsegTokenizer(bs,
                    new StringReader(datastr));
            loopResult(tokenizer, hash, false);
        }

        SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(hash,
                new StringReader(datastr));
        loopResult(tokenizer, hash, true);
        for (int i = 0; i < 3; ++i) {
            tokenizer = new SimpleMMsegTokenizer(bs,
                    new StringReader(datastr));
            loopResult(tokenizer, bs, true);
        }
    }

    String loopResult(Tokenizer tokenizer, Dictionary dic, boolean print)
            throws IOException {
        long start = System.currentTimeMillis();
        CharTermAttribute termAtt = tokenizer
                .getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = tokenizer
                .getAttribute(OffsetAttribute.class);
        String i = "";
        while (tokenizer.incrementToken()) {
            String word = new String(termAtt.buffer(), 0, termAtt.length());
            // int s = offsetAtt.startOffset();
            // int e = offsetAtt.endOffset();
            i = word;
        }
        if (print) {
            long time = System.currentTimeMillis() - start;
            logger.info("Dictionary: {} takes {}ms to seg {} char",
                    new Object[]{dic.getClass().getSimpleName(), time,
                            data.length});

        }
        return i; // prevent jit
    }
}

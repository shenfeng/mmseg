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


    String datastr;
    Dictionary bs;
    Dictionary trie;
    Dictionary hash;

    @Before
    public void setup() throws IOException {
        InputStream is = PerformanceTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");

        InputStream is2 = PerformanceTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");

        InputStream is3 = PerformanceTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");

        InputStream is4 = PerformanceTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");


        datastr = getBook();

        hash = new HashSetDictionary(is);
        bs = new BSDictionary(is2);
        trie = new TrieDictionary(is3);
//        new me.shenfeng.mmseg.Trie2Dictionary(is4);
    }

    public static String getBook() throws IOException {
        char[] book1 = Utils.getCharsFromResource("book1.txt");
        char[] book2 = Utils.getCharsFromResource("book2.txt");

        // copy as one array
        char[] data = Arrays.copyOf(book1, book1.length + book2.length);
        System.arraycopy(book2, 0, data, book1.length - 1, book2.length);

        return new String(data);
    }

    @Test
    public void testPerf() throws IOException {
        logger.info("Warm up");
        for (int i = 0; i < 10; i++) { // warm up

            boolean p = true;
            SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(hash,
                    new StringReader(datastr));
            loopResult(tokenizer, hash, p);

            tokenizer = new SimpleMMsegTokenizer(bs,
                    new StringReader(datastr));
            loopResult(tokenizer, bs, p);

            tokenizer = new SimpleMMsegTokenizer(trie,
                    new StringReader(datastr));
            loopResult(tokenizer, trie, p);

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
                            datastr.length()}
            );

        }
        return i; // prevent jit
    }
}

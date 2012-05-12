package me.shenfeng.mmseg;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
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

    public void close() throws IOException {

    }

}

public class PerfBench {

    private static Logger logger = LoggerFactory.getLogger(PerfBench.class);

    public static void main(String[] args) throws IOException {

        File file = new File(PerfBench.class.getClassLoader()
                .getResource("data/words.dic").getFile());

        char[] book1 = Utils.getCharsFromResource("book1.txt");
        char[] book2 = Utils.getCharsFromResource("book2.txt");

        Dictionary hash = new HashSetDictionary(file);
        Dictionary bs = new BSDictionary(file);

        for (int i = 0; i < 5; i++) {

            SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(hash,
                    new CharReader(book1));
            loopResult("hash, book 1", tokenizer); // 138ms
            tokenizer = new SimpleMMsegTokenizer(bs, new CharReader(book1));
            loopResult("binary search, book1", tokenizer); // 290ms

            tokenizer = new SimpleMMsegTokenizer(hash, new CharReader(book2));
            loopResult("hash, book2", tokenizer); // 79ms

            tokenizer = new SimpleMMsegTokenizer(bs, new CharReader(book2));
            loopResult("binary search, book2", tokenizer); // 181ms
        }

    }

    static void loopResult(String name, Tokenizer tokenizer)
            throws IOException {
        long start = System.currentTimeMillis();
        CharTermAttribute termAtt = tokenizer
                .getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = tokenizer
                .getAttribute(OffsetAttribute.class);
        while (tokenizer.incrementToken()) {
            String word = new String(termAtt.buffer(), 0, termAtt.length());
            int s = offsetAtt.startOffset();
            int e = offsetAtt.endOffset();
        }

        long time = System.currentTimeMillis() - start;
        logger.info("{}, time: {}ms", name, time);

    }
}

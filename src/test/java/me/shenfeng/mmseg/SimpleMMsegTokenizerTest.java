package me.shenfeng.mmseg;

import static me.shenfeng.mmseg.Utils.getReaderFromResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;

public class SimpleMMsegTokenizerTest {

    Dictionary dic;

    public SimpleMMsegTokenizerTest() throws IOException {
        URL url = SimpleMMsegTokenizerTest.class.getClassLoader()
                .getResource("data/words.dic");
        dic = new HashSetDictionary(new File(url.getFile()));
    }

    void printResult(Tokenizer tokenizer) throws IOException {
        CharTermAttribute termAtt = tokenizer
                .getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = tokenizer
                .getAttribute(OffsetAttribute.class);
        while (tokenizer.incrementToken()) {
            String word = new String(termAtt.buffer(), 0, termAtt.length());
            int start = offsetAtt.startOffset();
            int end = offsetAtt.endOffset();
            System.out.println(start + "\t" + end + "\t" + word);
        }
    }

    @Test
    public void testChinese() throws IOException {
        SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(dic,
                getReaderFromResource("hlby1.txt"));
        printResult(tokenizer);
    }

    @Test
    public void testCHEN() throws IOException {

        SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(dic,
                Utils.getReader("how about this好书推荐：土得掉渣的原生态生活：到黑夜想你没办法"));
        printResult(tokenizer);
    }

    @Test
    public void testEN() throws IOException {
        SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(dic,
                Utils.getReader("how about this"));
        printResult(tokenizer);
    }
}

package me.shenfeng.mmseg;

import static me.shenfeng.mmseg.Utils.getReaderFromResource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleMMsegTokenizerTest {

    static Dictionary bsDic;
    static Dictionary hashDic;
    static Dictionary trieDic;
    private static Dictionary trie2Dic;

    @BeforeClass
    public static void loadDic() throws IOException {
        InputStream is = SimpleMMsegTokenizerTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");
        InputStream is2 = SimpleMMsegTokenizerTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");
        InputStream is3 = SimpleMMsegTokenizerTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");
        InputStream is4 = SimpleMMsegTokenizerTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");

        bsDic = new BSDictionary(is);
        trieDic = new TrieDictionary(is3);
        trie2Dic = new TrieDictionary(is4);
        hashDic = new HashSetDictionary(is2);
    }

    // test to see if they are the same
    private void test(Tokenizer... tokenizers) throws IOException {
        String prevWord = null;
        int prevStart = -1, prevEnd = -1;
        int i = 0;
        outer:
        while (true) {
            for (Tokenizer tokenizer : tokenizers) {
                CharTermAttribute termAtt = tokenizer
                        .getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAtt = tokenizer
                        .getAttribute(OffsetAttribute.class);
                if (tokenizer.incrementToken()) {
                    String word = new String(termAtt.buffer(), 0,
                            termAtt.length());
                    int start = offsetAtt.startOffset();
                    int end = offsetAtt.endOffset();
                    if (prevWord == null) {
                        prevWord = word;
                    } else {
                        Assert.assertTrue(prevWord.equals(word));
                    }
                    if (prevStart == -1) {
                        prevStart = start;
                    } else {
                        Assert.assertEquals(word, start, prevStart);
                    }

                    if (prevEnd == -1) {
                        prevEnd = end;
                    } else {
                        Assert.assertEquals(word, end, prevEnd);
                    }
                } else {
                    break outer;
                }

                if (i++ % tokenizers.length == tokenizers.length - 1) {
                    prevStart = -1;
                    prevEnd = -1;
                    prevWord = null;
                }
            }
        }
    }

    private void print(String input) throws IOException {
        StringBuilder sb = new StringBuilder();
        SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer(trie2Dic,
                new StringReader(input));

        CharTermAttribute termAtt = tokenizer
                .getAttribute(CharTermAttribute.class);
        while (tokenizer.incrementToken()) {
            String word = new String(termAtt.buffer(), 0, termAtt.length());
            sb.append(word).append("|");
        }
        System.out.println(input + " => " + sb.toString());
    }

    @Test
    public void test1() throws IOException {
        // detail|2012|09|06|17404564|0|shtm
        print("detail_2012_09/06/17404564_0.shtml");
        print("研究生命起源.a.abc.");
        print("neo4j version 3.4.5");
        print("化装和服装");
        print("眼看就要来了");
        print("朋友真背叛了你了");
        print("今天真热，是游泳的好日子");
        print("《卫报》图说24小时（2012年6月27日）");
        print("小明把大便当作每天早上起床第一件要做的事");
        print("老师说明天每个人参加大队接力时，一定要尽力");
        print("This IS a test");
        print("neo4j: World's Leading Graph Database");
    }

    @Test
    public void testChinese() throws IOException {
        SimpleMMsegTokenizer bsTokenizer = new SimpleMMsegTokenizer(bsDic,
                getReaderFromResource("hlby1.txt"));
        SimpleMMsegTokenizer hashTokenizer = new SimpleMMsegTokenizer(
                hashDic, getReaderFromResource("hlby1.txt"));
        SimpleMMsegTokenizer trieTokenizer = new SimpleMMsegTokenizer(
                trieDic, getReaderFromResource("hlby1.txt"));
        SimpleMMsegTokenizer trie2Tokenizer = new SimpleMMsegTokenizer(
                trie2Dic, getReaderFromResource("hlby1.txt"));


        test(bsTokenizer, hashTokenizer, trieTokenizer, trie2Tokenizer);
//        test(bsTokenizer, trieTokenizer);
    }

    @Test
    public void testCHEN() throws IOException {
        String str = "how about this好书推荐：土得掉渣的原生态生活：到黑夜想你没办法";
        SimpleMMsegTokenizer bsTokenizer = new SimpleMMsegTokenizer(bsDic,
                Utils.getReader(str));
        SimpleMMsegTokenizer hashTokenizer = new SimpleMMsegTokenizer(
                hashDic, Utils.getReader(str));
        test(bsTokenizer, hashTokenizer);
    }

    @Test
    public void testEN() throws IOException {
        String str = "how about this";
        SimpleMMsegTokenizer bsTokenizer = new SimpleMMsegTokenizer(bsDic,
                Utils.getReader(str));
        SimpleMMsegTokenizer hashTokenizer = new SimpleMMsegTokenizer(
                hashDic, Utils.getReader(str));
        test(bsTokenizer, hashTokenizer);
    }

    @Test
    public void testFile() throws IOException {
        String str = new String(Utils.getCharsFromResource("book1.txt"));
        SimpleMMsegTokenizer bsTokenizer = new SimpleMMsegTokenizer(bsDic,
                Utils.getReader(str));
        SimpleMMsegTokenizer hashTokenizer = new SimpleMMsegTokenizer(
                hashDic, Utils.getReader(str));
        test(bsTokenizer, hashTokenizer); // they are the same
    }

}

package me.shenfeng.mmseg;

import static me.shenfeng.mmseg.Utils.getChars;
import static me.shenfeng.mmseg.Utils.printMemory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashSetDictionaryTest {


    @Before
    public void setup() throws IOException {
    }

    private InputStream getInputStream() {
        return HashSetDictionaryTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");
    }


    @Test
    public void performanceTest() throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {

        for (int i = 0; i < 8; i++) {
//            Utils.printMemory();

            Class<? extends Dictionary>[] classes = new Class[]{
                    HashSetDictionary.class, Trie2Dictionary.class, TrieDictionary.class, BSDictionary.class
            };

            for (Class<? extends Dictionary> aClass : classes) {

                for (Constructor constructor : aClass.getConstructors()) {
                    System.gc();
                    long ramStart = ramUsage();
                    InputStream is = getInputStream();

                    long start = System.currentTimeMillis();
                    Object o = constructor.newInstance(is);
                    long ts = System.currentTimeMillis() - start;
                    System.gc();
                    long ram = ramUsage() - ramStart;

                    String book = PerformanceTest.getBook();
                    start = System.currentTimeMillis();
                    SimpleMMsegTokenizer tokenizer = new SimpleMMsegTokenizer((Dictionary) o,
                            new StringReader(book));
                    CharTermAttribute termAtt = tokenizer
                            .getAttribute(CharTermAttribute.class);
                    OffsetAttribute offsetAtt = tokenizer
                            .getAttribute(OffsetAttribute.class);
                    String w = "";
                    while (tokenizer.incrementToken()) {
                        String word = new String(termAtt.buffer(), 0, termAtt.length());
                        // int s = offsetAtt.startOffset();
                        // int e = offsetAtt.endOffset();
                        w = word;
                    }
                    long segs = System.currentTimeMillis() - start;


                    System.out.println(String.format("%d ms, %.2f/%.2f KB RAM, seg %d chars: %d ms, last: %s, %s", ts, ram / 1024.0, ramStart / 1024.0,
                            book.length(), segs, w,
                            o.getClass()));
                    is.close();


//                    System.out.println(ts + "ms, " + o.getClass());
                }

            }
            System.out.println();

//            for (Constructor<?> constructor : HashSetDictionary.class.getConstructors()) {
//                Object o = constructor.newInstance(getInputStream());
//
//            }


//            Dictionary dict = new HashSetDictionary(getInputStream());
//            System.out.println("---------- load hash set dictionary");
//            Utils.printMemory();
//
//            System.out.println("------------------load trie");
//            Dictionary dict2 = new TrieDictionary(getInputStream());
//            printMemory();
//
//            System.out.println("------------------load binary search");
//            BSDictionary bsDictionary = new BSDictionary(getInputStream());
//            printMemory();
//
//            System.out.println("------------------load trie2 search");
//            Trie2Dictionary trie2Dictionary = new Trie2Dictionary(getInputStream());
//            printMemory();
        }


//        int n = 8;
//        String str = "一个国家两种制度111";
//        Assert.assertEquals(n, dict.maxMath(getChars(str), 0, str.length()));
//
//        Assert.assertEquals(n, dict2.maxMath(getChars(str), 0, str.length()));
    }

    private long ramUsage() {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        return total - free;
    }
}

package me.shenfeng.mmseg;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class SimpleMMsegTest {

    Dictionary dic;

    public SimpleMMsegTest() throws IOException {
        URL url = SimpleMMsegTest.class.getClassLoader().getResource(
                "data/words.dic");
        dic = new HashSetDictionary(new File(url.getFile()));
    }

    @Test
    public void testChinese() throws IOException {
        Word w;
        MMseg seg = new MMseg(dic, Utils.getCharsFromResource("hlby1.txt"));
        while ((w = seg.next()) != null) {
            System.out.println(w);
        }
    }

    @Test
    public void testCHEN() throws IOException {
        Word w;
        MMseg seg = new MMseg(dic,
                Utils.getChars("how about this好书推荐：土得掉渣的原生态生活：到黑夜想你没办法"));
        while ((w = seg.next()) != null) {
            System.out.println(w);
        }
    }

    @Test
    public void testEN() throws IOException {
        Word w;
        MMseg seg = new MMseg(dic, Utils.getChars("how about this"));
        while ((w = seg.next()) != null) {
            System.out.println(w);
        }
    }
}

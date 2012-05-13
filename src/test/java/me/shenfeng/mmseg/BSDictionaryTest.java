package me.shenfeng.mmseg;

import static me.shenfeng.mmseg.Utils.getChars;
import static me.shenfeng.mmseg.Utils.printMemory;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BSDictionaryTest {
    Dictionary dict;

    @Before
    public void setup() throws IOException {
        Utils.printMemory();
        Utils.printMemory();
        Utils.printMemory();
        InputStream is = BSDictionaryTest.class.getClassLoader()
                .getResourceAsStream("data/words.dic");
        dict = new BSDictionary(is);
        printMemory();
        Utils.printMemory();
    }

    @Test
    public void test2() {
        int n = 8;
        String str = "一个国家两种制度111";
        Assert.assertEquals(n, dict.maxMath(getChars(str), 0, str.length()));
    }
}

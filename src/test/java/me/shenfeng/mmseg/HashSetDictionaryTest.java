package me.shenfeng.mmseg;

import static me.shenfeng.mmseg.Utils.getChars;
import static me.shenfeng.mmseg.Utils.printMemory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashSetDictionaryTest {

    Dictionary dict;

    @Before
    public void setup() throws IOException {
        Utils.printMemory();
        Utils.printMemory();
        Utils.printMemory();
        URL url = HashSetDictionaryTest.class.getClassLoader().getResource(
                "data/words.dic");
        dict = new HashSetDictionary(new File(url.getFile()));
        printMemory();
        printMemory();
    }

    @Test
    public void test2() {
        int n = 8;
        String str = "一个国家两种制度111";
        Assert.assertEquals(n, dict.maxMath(getChars(str), 0, str.length()));
    }
}

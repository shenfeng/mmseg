package me.shenfeng.mmseg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Utils {
    public static void printMemory() {
        for (int i = 0; i < 4; ++i) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        }
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long max = rt.maxMemory();
        System.out.println(String.format("total=%d, free=%d, max=%d, use=%d",
                total / 1024, free / 1024, max / 1024, (total - free) / 1024));
    }

    public static char[] getChars(String str) {
        char ch[] = new char[str.length()];
        str.getChars(0, str.length(), ch, 0);
        return ch;
    }

    public static char[] getChars(File file) throws IOException {
        FileReader fr = new FileReader(file);
        char chars[] = new char[(int) file.length()];
        int read;
        int count = 0;
        while ((read = fr.read(chars, count, chars.length - count)) != -1) {
            count += read;
        }
        return Arrays.copyOf(chars, count);
    }

    public static char[] getCharsFromResource(String name) throws IOException {
        String file = Utils.class.getClassLoader().getResource(name).getFile();
        return getChars(new File(file));
    }
}

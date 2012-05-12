package me.shenfeng.mmseg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HashSet {
    private Object[] data;
    final int prime;

    public HashSet(int count) {
        prime = getPrime(count);
        data = new Object[prime];
    }

    public void insert(Word w) {
        int i = w.hashCode() % prime;
        Object val = data[i];
        if (val == null) {
            data[i] = w;
        } else {
            Word[] arr;
            if (val instanceof Word) {
                arr = new Word[] { (Word) val };
            } else {
                arr = (Word[]) val;
            }
            arr = Arrays.copyOf(arr, arr.length + 1);
            arr[arr.length - 1] = w;
            data[i] = arr;
        }
    }

    public Counter getLoad() {
        Counter c = new Counter();
        for (Object slot : data) {
            if (slot == null) {
                c.add(0);
            } else if (slot instanceof Word) {
                c.add(1);
            } else {
                c.add(((Word[]) slot).length);
            }
        }
        return c;
    }

    public boolean contains(Word w) {
        int i = w.hashCode() % prime;
        Object val = data[i];
        if (val instanceof Word) {
            return w.equals(val);
        } else if (val instanceof Word[]) {
            Word arr[] = (Word[]) val;
            for (Word word : arr) {
                if (word.equals(w)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getPrime(int n) {
        int prev = 0;
        BigInteger b = BigInteger.valueOf(n);
        int prime = 0;
        int max = n + n / 2;
        while ((prime = b.nextProbablePrime().intValue()) < max) {
            prev = prime;
            b = BigInteger.valueOf(prime);
        }
        return prev;
    }
}

public class HashSetDictionary implements Dictionary {
    private Logger logger = LoggerFactory.getLogger(HashSetDictionary.class);

    public HashSetDictionary(File file) throws IOException {
        load(file);
    }

    private int maxWordLength = 0;
    private HashSet set;

    private void load(File file) throws IOException {
        long start = System.currentTimeMillis();
        char buffer[] = new char[1024 * 768];
        int offsets[] = new int[1024 * 40];
        int lengths[] = new int[1024 * 40];
        FileReader fr = new FileReader(file);
        int charIdx = 0;
        int wordIdx = 0;
        int length = 0;
        int read = 0;
        while ((read = fr.read()) != -1) {
            if (read == '\r') { // ignore
            } else if (read == '\n') {
                if (length != 0) {
                    if (wordIdx == offsets.length) {
                        offsets = Arrays.copyOf(offsets, wordIdx * 2);
                        lengths = Arrays.copyOf(lengths, wordIdx * 2);
                    }
                    lengths[wordIdx] = length;
                    offsets[wordIdx] = charIdx - length;
                    wordIdx++;
                    length = 0;
                }
            } else {
                if (charIdx == buffer.length) {
                    buffer = Arrays.copyOf(buffer, charIdx * 2);
                }
                length++;
                buffer[charIdx++] = (char) read;
            }
        }
        buffer = Arrays.copyOf(buffer, charIdx);
        set = new HashSet(wordIdx);
        for (int i = 0; i < wordIdx; i++) {
            if (lengths[i] > maxWordLength) {
                maxWordLength = lengths[i];
            }
            Word w = new Word(buffer, offsets[i], lengths[i]);
            set.insert(w);
        }
        long time = System.currentTimeMillis() - start;
        logger.info(
                "load: {}ms, word: {}, max word length: {}, bucket: {}, hash: {}",
                new Object[] { time, wordIdx, maxWordLength, set.prime,
                        set.getLoad() });
    }

    public int maxMath(char[] sen, int offset, int length) {
        int maxLength = Math.min(length, maxWordLength);
        for (int i = maxLength; i > 1; --i) {
            Word w = new Word(sen, offset, i);
            if (set.contains(w)) {
                return i;
            }
        }
        return 1;
    }
}

package me.shenfeng.mmseg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BSDictionary implements Dictionary {

    private Logger logger = LoggerFactory.getLogger(BSDictionary.class);

    private boolean binarySearch(char[] data, char[] target, int offset,
            int length) {
        int low = 0;
        int high = data.length / length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int start = mid * length;
            int i = 0;
            for (; i < length; i++) {
                if (data[start + i] < target[offset + i]) {
                    low = mid + 1;
                    break;
                } else if (data[start + i] > target[offset + i]) {
                    high = mid - 1;
                    break;
                }
            }
            if (i == length) {
                return true;
            }

        }
        return false;
    }

    public int maxMath(char[] sen, int offset, int length) {
        int maxLength = Math.min(length, maxWordLength);
        for (int i = maxLength; i > 1; --i) {
            if (binarySearch(groups[i - 1], sen, offset, i)) {
                return i;
            }
        }
        return 1;
    }

    public BSDictionary(File file) throws IOException {
        load(file);
    }

    private int maxWordLength = 0;
    char[][] groups; // fewer objects, gc friendly

    private void load(File file) throws IOException {
        long start = System.currentTimeMillis();
        char buffer[] = new char[1024 * 768];
        int offsets[] = new int[1024 * 40];
        int lengths[] = new int[1024 * 40];
        FileReader fr = new FileReader(file);
        int charIdx = 0;
        int wordCnt = 0;
        int length = 0;
        int read = 0;
        while ((read = fr.read()) != -1) {
            if (read == '\r') { // ignore
            } else if (read == '\n') {
                if (length != 0) {
                    if (wordCnt == offsets.length) {
                        offsets = Arrays.copyOf(offsets, wordCnt * 2);
                        lengths = Arrays.copyOf(lengths, wordCnt * 2);
                    }
                    lengths[wordCnt] = length;
                    offsets[wordCnt] = charIdx - length;
                    wordCnt++;
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

        for (int i = 0; i < wordCnt; ++i) {
            if (lengths[i] > maxWordLength) {
                maxWordLength = lengths[i];
            }
        }

        groups = new char[maxWordLength][];
        int counter[] = new int[maxWordLength];

        for (int i = 0; i < maxWordLength; ++i) {
            groups[i] = new char[buffer.length]; // will resize
        }

        for (int i = 0; i < wordCnt; ++i) {
            int len = lengths[i];
            int index = len - 1;
            System.arraycopy(buffer, offsets[i], groups[index],
                    counter[index] * len, len);
            counter[index]++; // increment counter
        }

        for (int i = 0; i < maxWordLength; ++i) {
            groups[i] = Arrays.copyOf(groups[i], counter[i] * (i + 1));
        }

        long time = System.currentTimeMillis() - start;
        logger.info("load: {}ms, word: {}, max word length: {}",
                new Object[] { time, wordCnt, maxWordLength });

    }
}

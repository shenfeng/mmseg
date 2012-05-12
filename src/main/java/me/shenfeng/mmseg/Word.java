package me.shenfeng.mmseg;

public class Word {
    char[] data;
    int offset;
    int length;

    public int hashCode() {
        int h = 0;
        int off = offset;
        for (int i = 0; i < length; ++i) {
            h = 31 * h + data[off++];
        }

        // h ^= (h >>> 20) ^ (h >>> 12);
        // h = h ^ (h >>> 7) ^ (h >>> 4);

        return Math.abs(h);
    }

    public Word(char[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            Word another = (Word) obj;
            int n = length;
            if (another.length == length) {
                char[] v2 = another.data;
                int i = offset;
                int j = another.offset;
                while (n-- != 0) {
                    if (data[i++] != v2[j++])
                        return false;
                }
                return true;

            }
        }
        return false;
    }

    @Override
    public String toString() {
        return new String(data, offset, length);
    }
}
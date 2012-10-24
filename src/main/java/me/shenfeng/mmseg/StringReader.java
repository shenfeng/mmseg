package me.shenfeng.mmseg;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

class NoopReader extends Reader {
    public int read(char[] cbuf, int off, int len) throws IOException {
        return 0;
    }

    public void close() throws IOException {
    }

}

// lock free, unread is optimistic
// test indicate no much perf boost 460ms vs 470ms
public class StringReader extends PushbackReader {
    private String str;
    private int next = 0;
    private int length;

    public StringReader(String str) {
        super(new NoopReader()); // prevent NPE
        this.str = str;
        this.length = str.length();
    }

    public int read() throws IOException {
        if (next >= length)
            return -1;
        return str.charAt(next++);
    }

    public void unread(int c) throws IOException {
        --next;
    }
}

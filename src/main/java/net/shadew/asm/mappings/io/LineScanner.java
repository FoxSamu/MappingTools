package net.shadew.asm.mappings.io;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

public class LineScanner implements AutoCloseable {
    private static final int CHUNK_SIZE = 1024;

    private final Reader reader;
    private char[] tmp = new char[CHUNK_SIZE];
    private char[] buf = new char[CHUNK_SIZE];
    private int charsInBuf = 0;
    private boolean ended = false;
    private boolean closed;
    private String next;

    public LineScanner(Reader reader) {
        if (reader == null)
            throw new NullPointerException("reader = null");
        this.reader = reader;
    }

    private void ensureNotClosed() throws IOException {
        if (closed)
            throw new IOException("Scanner closed");
    }

    private void resizeBuf(int size) {
        assert size >= 0;

        if (size < buf.length)
            return;

        char[] newbuf = new char[size];
        System.arraycopy(buf, 0, newbuf, 0, Math.min(size, buf.length));
        buf = newbuf;

        assert buf.length >= size;
    }

    private String consume(int amount, int strip) {
        assert amount >= 0;
        assert strip <= amount;
        assert amount <= buf.length;

        String out = new String(buf, 0, amount - strip);
        System.arraycopy(buf, amount, buf, 0, charsInBuf - amount);
        charsInBuf -= amount;
        return out;
    }

    private void extendFromTmp(int amount) {
        assert amount > 0;
        assert amount <= tmp.length;

        int reqSize = charsInBuf + amount;
        if (reqSize > buf.length) {
            int newSize = (reqSize / CHUNK_SIZE + 1) * CHUNK_SIZE;
            resizeBuf(newSize);
        }

        assert buf.length >= reqSize;

        System.arraycopy(tmp, 0, buf, charsInBuf, amount);
        charsInBuf += amount;
    }

    private void extend() throws IOException {
        if (ended) return;
        int amount = reader.read(tmp);
        if (amount < 0)
            ended = true;
        else
            extendFromTmp(amount);
    }

    public boolean hasNext() throws IOException {
        ensureNotClosed();
        return next != null || !ended || charsInBuf > 0;
    }

    public String nextLine() throws IOException {
        ensureNotClosed();
        if (!hasNext())
            throw new NoSuchElementException("No more lines");

        if (next != null) {
            String n = next;
            next = null;
            return n;
        }

        int len = 0;
        int strip = 0;
        while (true) {
            if (!ended && len > charsInBuf - 2) {
                extend();
                continue;
            }

            if (len == charsInBuf) {
                assert ended;
                break;
            }

            assert ended || len <= charsInBuf - 2;

            char c1 = buf[len];

            if (c1 == '\n') { // LF (linux/unix)
                strip = 1;
                break;
            }
            if (c1 == '\r') {
                if (len == charsInBuf - 1) { // CR (osx), CRLF not possible
                    assert ended;
                    strip = 1;
                    break;
                }

                char c2 = buf[len + 1];
                if (c2 == '\n') { // CRLF (windows)
                    strip = 2;
                } else { // CR (osx)
                    strip = 1;
                }
                break;
            }
            len++;
        }

        String out = consume(len + strip, strip);

        if (charsInBuf == 0 && strip > 0 && ended) {
            next = "";
        }

        return out;
    }

    @Override
    public void close() throws Exception {
        if (closed) return;

        reader.close();
        buf = null;
        tmp = null;
        closed = true;
    }

    @Override
    protected void finalize() throws Throwable {
        close(); // Catch resource leak
    }
}

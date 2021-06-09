package terminaltest.unit;
import java.io.Serializable;
import java.util.Arrays;

public class BytesBuilder implements Serializable {
    private static final long serialVersionUID = 1298478082864104805L;

    private byte[] bytes;

    private int capacity;
    private int count;

    public BytesBuilder() {
        this(64);
    }

    public BytesBuilder(int capacity) {
        this.bytes = new byte[capacity];
        this.capacity = capacity;
        this.count = 0;
    }

    public BytesBuilder append(byte[] bs) {
        if (bs == null || bs.length == 0) return this;

        // 扩容byte数组
        int len = bs.length;
        if (count + len > bytes.length) {
            int len2 = bytes.length + capacity + len;
            byte[] bs2 = Arrays.copyOf(bytes, len2);

            this.bytes = bs2;
        }

        // 将本次数据给存放进来
        System.arraycopy(bs, 0, bytes, count, len);

        this.count += bs.length;

        return this;
    }

    public BytesBuilder appendLE(byte[] bs) {
        return append(invert(bs));
    }

    public BytesBuilder append(String hex) {
        return append(ByteUtil.toByte(hex));
    }

    public BytesBuilder appendLE(String hex) {
        return appendLE(ByteUtil.toByte(hex));
    }

    public BytesBuilder append(String hex, int bs) {
        return append(ByteUtil.toByte(hex, bs));
    }

    public BytesBuilder appendLE(String hex, int bs) {
        return appendLE(ByteUtil.toByte(hex, bs));
    }

    public BytesBuilder append(int i, int bs) {
        return append(ByteUtil.toByte(i, bs));
    }

    public BytesBuilder appendLE(int i, int bs) {
        return appendLE(ByteUtil.toByte(i, bs));
    }

    public BytesBuilder append(byte b1) {
        return append(new byte[] { b1 });
    }

    public BytesBuilder appendLE(byte b1) {
        return appendLE(new byte[] { b1 });
    }

    public BytesBuilder append(byte b1, byte b2) {
        return append(new byte[] { b1, b2 });
    }

    public BytesBuilder appendLE(byte b1, byte b2) {
        return appendLE(new byte[] { b1, b2 });
    }

    public BytesBuilder append(byte b1, byte b2, byte b3) {
        return append(new byte[] { b1, b2, b3 });
    }

    public BytesBuilder appendLE(byte b1, byte b2, byte b3) {
        return appendLE(new byte[] { b1, b2, b3 });
    }

    public BytesBuilder append(byte b1, byte b2, byte b3, byte b4) {
        return append(new byte[] { b1, b2, b3, b4 });
    }

    public BytesBuilder appendLE(byte b1, byte b2, byte b3, byte b4) {
        return appendLE(new byte[] { b1, b2, b3, b4 });
    }

    public byte[] sub(int start, int end) {
        return Arrays.copyOfRange(bytes, start, end);
    }

    public int length() {
        return count;
    }

    public byte[] toArray() {
        return Arrays.copyOfRange(bytes, 0, count);
    }

    @Override
    public String toString() {
        return ByteUtil.toHex(toArray());
    }

    private static byte[] invert(byte[] bs) {
        if (bs == null || bs.length == 0) return bs;

        int length = bs.length;
        byte[] answer = new byte[length];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = bs[length - i - 1];
        }

        return answer;
    }

}
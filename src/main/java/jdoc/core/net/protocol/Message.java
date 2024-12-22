package jdoc.core.net.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public record Message(MessageType type, RequestToken requestToken, byte[] data) {
    public Message(MessageType type, RequestToken requestToken, String data) {
        this(type, requestToken, data.getBytes());
    }

    public Message(MessageType type, String data) {
        this(type, RequestToken.EMPTY, data.getBytes());
    }

    public Message(MessageType type, byte[] data) {
        this(type, RequestToken.EMPTY, data);
    }

    public byte[] toByteArray() {
        var buffer = ByteBuffer.allocate(1 + 8 + 4 + data.length);
        buffer.put((byte) type.ordinal());
        buffer.putLong(requestToken.value());
        buffer.putInt(data.length);
        buffer.put(data);
        return buffer.array();
    }

    public static Message from(byte[] bytes) {
        var buffer = ByteBuffer.wrap(bytes);
        return new Message(MessageType.values()[buffer.get()], buffer.array());
    }

    public static Message from(InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        var type = MessageType.values()[dataIn.read()];
        var requestToken = new RequestToken(dataIn.readLong());
        var length = dataIn.readInt();
        var data = new byte[length];
        if (dataIn.read(data) == -1) return null;
        return new Message(type, requestToken, data);
    }

    public String dataString() {
        return new String(data);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", requestToken=" + requestToken +
                ", data=" + dataString() +
                '}';
    }
}

package jdoc.core.net.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public record Message(int type, byte[] data, String sender) {
    public Message(int type, String data, String sender) {
        this(type, data.getBytes(), sender);
    }
    public Message(int type, byte[] data) {
        this(type, data, null);
    }
    public Message(int type, String data) {
        this(type, data, null);
    }
    public static final int GET = 0;
    public static final int EDIT_DOCUMENT = 3;
    public static final int DOCUMENT_SYNC_REQUEST = 7;
    public static final int DOCUMENT_SYNC_RESPONSE = 8;
    public static final int GET_CLIENTS = 4;
    public static final int SET_USERNAME = 5;
    public static final int EDIT_USERS = 11;
    public static final int USERS_SYNC_REQUEST = 12;
    public static final int USERS_SYNC_RESPONSE = 13;
    public static final int GET_USER_ID_REQUEST = 9;
    public static final int GET_USER_ID_RESPONSE = 10;

    public byte[] toByteArray() {
        var buffer = ByteBuffer.allocate(data.length + 4 * 2);
        buffer.putInt(type);
        buffer.putInt(data.length);
        buffer.put(data);
        return buffer.array();
    }

    public static Message from(byte[] bytes) {
        var buffer = ByteBuffer.wrap(bytes);
        return new Message(buffer.getInt(), buffer.array());
    }

    public static Message from(InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        var type = dataIn.readInt();
        var length = dataIn.readInt();
        var data = new byte[length];
        if (dataIn.read(data) == -1) return null;
        return new Message(type, data);
    }

    public String dataString() {
        return new String(data);
    }

    @Override
    public String toString() {
        return "Message[type=" + type + ", data = " + new String(data) + "]";
    }
}

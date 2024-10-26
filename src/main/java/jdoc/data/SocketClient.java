package jdoc.data;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jdoc.domain.Client;
import jdoc.domain.Message;

import java.io.*;
import java.net.Socket;

public class SocketClient implements Client {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final PublishProcessor<Message> messages = PublishProcessor.create();
    private final Flowable<Message> messagesCached = messages.cache();

    public SocketClient(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                while (true) {
                    var message = Message.from(in);
                    if (message == null) continue;
                    messages.onNext(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void send(Message message) {
        var encoded = message.toByteArray();
        try {
            out.write(encoded);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Flowable<Message> incoming() {
        return messagesCached;
    }
}

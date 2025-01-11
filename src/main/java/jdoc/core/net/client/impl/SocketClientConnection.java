package jdoc.core.net.client.impl;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import jdoc.core.net.client.ClientConnection;
import jdoc.core.net.protocol.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class SocketClientConnection implements ClientConnection, Runnable {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final ReplayProcessor<Message> messages = ReplayProcessor.create(128);
    private final Thread thread;

    public SocketClientConnection(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void send(Message message) {
        var encoded = message.toByteArray();
        try {
            out.write(encoded);
            out.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Flowable<Message> incoming() {
        return messages;
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
        socket.close();
        out.close();
        in.close();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                var message = Message.from(in);
                if (message == null) continue;
                log.info("GOT NEW MESSAGE: {}", message);
                messages.onNext(message);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}

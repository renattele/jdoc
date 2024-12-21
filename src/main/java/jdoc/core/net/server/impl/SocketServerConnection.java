package jdoc.core.net.server.impl;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jdoc.core.net.server.ServerConnection;
import jdoc.core.util.FlowableList;
import jdoc.core.net.protocol.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SocketServerConnection implements ServerConnection, Runnable {
    private final int port;
    private final FlowableList<ClientHandler> clients = new FlowableList<>();
    private final PublishProcessor<Message> messages = PublishProcessor.create();
    private final Flowable<Message> messagesCached = messages.cache();
    private final PublishProcessor<ClientHandler> newClients = PublishProcessor.create();
    private final Flowable<ClientHandler> newClientsCached = newClients.cache();
    private final Thread thread;

    public SocketServerConnection(int port) {
        this.port = port;
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try (var socket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                var clientSocket = socket.accept();
                var handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
                clients.add(handler);
                newClients.onNext(handler);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Flowable<Message> messages() {
        return messagesCached;
    }

    @Override
    public Flowable<List<String>> clients() {
        return clients.flowable().map(clients ->
                clients.stream().map(client -> client.addr).toList()
        );
    }

    @Override
    public Flowable<String> newClients() {
        return newClientsCached.map(client -> client.addr);
    }

    @Override
    public void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    private synchronized void broadcast(ClientHandler thiz, Message message) {
        messages.onNext(message);
        for (ClientHandler client : clients) {
            if (client == thiz) continue;
            client.send(message);
        }
    }

    @Override
    public void send(String addr, Message message) {
        for (ClientHandler client : clients) {
            if (Objects.equals(client.addr, addr)) {
                client.send(message);
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
        for (ClientHandler client : clients) {
            client.close();
        }
    }

    private class ClientHandler implements AutoCloseable, Runnable {
        private final Socket socket;
        private final InputStream in;
        private final OutputStream out;
        private final String addr;

        private ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
            addr = socket.getRemoteSocketAddress().toString();
        }

        @Override
        public void run() {
            try (this) {
                while (socket.isConnected()) {
                    var incoming = Message.from(in);
                    if (incoming == null) continue;
                    var modified = new Message(incoming.type(), incoming.data(), addr);
                    broadcast(this, modified);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                clients.remove(this);
            }
        }

        public void send(Message message) {
            try {
                out.write(message.toByteArray());
                out.flush();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        @Override
        public void close() throws IOException {
            socket.close();
            in.close();
            out.close();
        }
    }
}

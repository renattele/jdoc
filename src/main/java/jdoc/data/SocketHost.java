package jdoc.data;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import jdoc.domain.Host;
import jdoc.domain.FlowableList;
import jdoc.domain.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocketHost implements Host, Runnable {
    private final int port;
    private final FlowableList<ClientHandler> clients = new FlowableList<>();
    private final PublishProcessor<Message> messages = PublishProcessor.create();
    private final Flowable<Message> messagesCached = messages.cache();

    public SocketHost(int port) {
        this.port = port;
        new Thread(this).start();
    }

    private synchronized void broadcast(ClientHandler thiz, Message message) {
        messages.onNext(message);
        for (ClientHandler client : clients) {
            if (client == thiz) continue;
            client.send(message);
        }
    }

    @Override
    public void run() {
        try {
            var socket = new ServerSocket(port);
            while (true) {
                var clientSocket = socket.accept();
                var handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
                clients.add(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Flowable<Message> messages() {
        return messagesCached;
    }

    @Override
    public Flowable<List<String>> clients() {
        return clients.flowable().map(clients ->
                clients.stream().map(client -> client.socket.getRemoteSocketAddress().toString()).toList()
        );
    }

    @Override
    public void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final InputStream in;
        private final OutputStream out;

        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                while (socket.isConnected()) {
                    var incoming = Message.from(in);
                    if (incoming == null) continue;
                    var modified = new Message(incoming.type(), incoming.data(), socket.getRemoteSocketAddress().toString());
                    broadcast(this, modified);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clients.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(Message message) {
            try {
                out.write(message.toByteArray());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread {
        private Socket socket;

        public Handler (Socket socket) {
            this.socket = socket;
        }
        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом : " + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)){
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом.");
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            do {
                Message message = connection.receive();

                if (message.getType() == MessageType.TEXT) {
                    String sb = userName + ": " + message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, sb));
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            } while (true);
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            connectionMap.forEach((name , con ) -> {
                try {
                    if (!name.equals(userName)) connection.send(new Message(MessageType.USER_ADDED, name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while(true) {
                connection.send(new Message(MessageType.NAME_REQUEST, "Enter your name, please! :"));
                Message message = connection.receive();
                if (message.getType() == MessageType.USER_NAME) {
                    String userName = message.getData();
                    if (!userName.isEmpty() || !userName.equals("")) {
                        if (!connectionMap.containsKey(userName)) {
                            connectionMap.put(userName, connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED, userName));
                            return userName;
                        }
                    }
                }
            }
        }
    }

    public static void sendBroadcastMessage(Message message) {
        connectionMap.forEach((k,connection)-> {
            try{
                connection.send(message);
            } catch (IOException e) {
                System.out.println("Извините. Мы не смогли отправить Ваше сообщение.");
            }
        });
    }

    public static void main(String[] args) {
        ConsoleHelper helper = new ConsoleHelper();

        int port = helper.readInt();
        try(ServerSocket serverSocket  = new ServerSocket(port)) {

                System.out.println("Server is stated.");
                while(!serverSocket.isClosed()) {
                    try {
                        Handler handler = new Handler(serverSocket.accept());
                        handler.start();
                        continue;
                    } catch (SocketException e) {
                        System.out.println(e);
                        serverSocket.close();
                    }
                }

        } catch (IOException e) {
            System.out.println(e);

        }
    }
}
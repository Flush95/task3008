package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Admin on 20.03.2017.
 */
public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ConsoleHelper.writeMessage("Введите порт: ");
        int portName = ConsoleHelper.readInt();

        try(ServerSocket serverSocket = new ServerSocket(portName)) {
            ConsoleHelper.writeMessage("Сервер запущен.");
            while (true) {
                Socket socket = serverSocket.accept();
                Thread handler = new Handler(socket);
                handler.start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage(e.getMessage());
        }
    }

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet())
                pair.getValue().send(message);
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка отправки сообщения.");
        }
    }

    /**
     * Handler class
     */
    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом: " + socket.getRemoteSocketAddress());
            String name = "";

            try (Connection connection = new Connection(socket)) {
                name = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, name));
                sendListOfUsers(connection, name);
                serverMainLoop(connection, name);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом.");
            } catch (ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом");
            }
            connectionMap.remove(name);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED, name));
            ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто.");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();

                if (message.getType() == MessageType.USER_NAME) {
                    if (!message.getData().isEmpty()) {
                        if (connectionMap.get(message.getData()) == null) {
                            connectionMap.put(message.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return message.getData();
                        }
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                String clientName = pair.getKey();
                if (!clientName.equals(userName))
                    connection.send(new Message(MessageType.USER_ADDED, clientName));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    stringBuilder.append(userName);
                    stringBuilder.append(": ");
                    stringBuilder.append(message.getData());
                    sendBroadcastMessage(new Message(MessageType.TEXT, stringBuilder.toString()));
                    stringBuilder.setLength(0);
                } else {
                    ConsoleHelper.writeMessage("Ошибка ввода.");
                }
            }
        }
    }
}

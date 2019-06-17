package ga.servicereq;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public final class Server {
    private static Socket connection;
    private static DataOutputStream writeSocket;
    private static BufferedReader readSocket;

    public static void initializeConnection(int port) {
        if(connection == null) {
            try {
                connection = new Socket("localhost", port);
                writeSocket = new DataOutputStream(connection.getOutputStream());
                readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void terminateConnection() {
        try {
            Server.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        try {
            writeSocket.writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readMessage() {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(readSocket.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}

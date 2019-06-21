package ga.servicereq;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public final class Server implements Runnable {
    private static Socket connection;
    private static DataOutputStream writeSocket;
    private static BufferedReader readSocket;
    private static boolean activeConnection = false;

    public static boolean hasMessage = false;
    public static String message;

    public static void terminateConnection() {
        activeConnection = false;
    }

    public static boolean isActiveConnection() {
        return activeConnection;
    }

    public static String getLatestMessage() {
        hasMessage = false;
        return message;
    }

    public static void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(writeSocket == null)
                        Thread.sleep(100);
                    writeSocket.write(message.getBytes());
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void run() {
        activeConnection = true;
        try {
            connection = new Socket("192.168.137.1", 6789);
            writeSocket = new DataOutputStream(connection.getOutputStream());
            readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (activeConnection) {
                String readMessage;
                if (!hasMessage) {
                    readMessage = readSocket.readLine();
                    while (readMessage == null || readMessage.equals("[CheckConnection]")) {
                        Thread.sleep(100);
                        readMessage = readSocket.readLine();
                    }

                    Log.e("SERVER",readMessage.toString());
                    message = readMessage;
                    hasMessage = true;
                } else Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package ga.servicereq;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class Server implements Runnable {

    private static Socket connection;
    private static DataOutputStream writeSocket;
    private static BufferedReader readSocket;
    private static boolean activeConnection = false;
    private static Context appContext;

    private static List<String> messages;

    public Server(Context appContext) {
        Server.appContext = appContext;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void terminateConnection() {
        activeConnection = false;
    }

    public static boolean isActiveConnection() {
        return activeConnection;
    }

    public static void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (writeSocket == null)
                        Thread.sleep(100);
                    writeSocket.write(message.getBytes());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void run() {
        messages = new ArrayList<>();

        activeConnection = true;
        try {
            connection = new Socket("192.168.43.5", 6787);
//            connection = new Socket("192.168.0.173", 6787);
            writeSocket = new DataOutputStream(connection.getOutputStream());
            readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (activeConnection) {
                String readMessage;
                readMessage = readSocket.readLine();
                while (readMessage == null) {
                    readMessage = readSocket.readLine();
                }
                if (readMessage.split(";;").length > 0) {
                    for (String m : readMessage.split(";;")) {
                        Log.d("SERVER", m.contains("[CheckConnection]") ? "" : m);
                        if (!m.equals("[CheckConnection]")) {
                            if (m.substring(0, 1).equals("P")) {
                                String[] data = m.split(";");
                                PostsAdapter.serverAdd(new Post(data[1], data[2], data[3], data[4], data[5], data[6], data[7]));
                            }
                            if (m.substring(0, 1).equals("M")) {
                                String[] data = m.split(";");
                                MessagesAdapter.serverAdd(new Message(data[1], data[2], data[3], data[4], data[5], data[6]));
                            } else messages.add(m);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMessage(int index) {
        return Server.messages.remove(index);
    }

    public static int messagesCount() {
        return Server.messages.size();
    }
}
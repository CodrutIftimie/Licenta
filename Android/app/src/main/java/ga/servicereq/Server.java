package ga.servicereq;

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

    public static List<String> messages;

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
        messages = new ArrayList<>();

        activeConnection = true;
        try {
            connection = new Socket("192.168.137.1", 6789);
            writeSocket = new DataOutputStream(connection.getOutputStream());
            readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (activeConnection) {
                String readMessage;
                readMessage = readSocket.readLine();
                while (readMessage == null || readMessage.equals("[CheckConnection]")) {
                    readMessage = readSocket.readLine();
                }

                Log.e("SERVER",readMessage);
                if(readMessage.split(";;").length > 1) {
                    String[] msgs = readMessage.split(";;");
                    messages.add(msgs[0]);
                    msgs[0] = null;
                    for(String m:msgs) {
                        if(m!=null)
                            if(m.substring(0,1).equals("P")) {
                                String[] data = m.split(";");
                                PostsAdapter.serverAdd(new Post(data[1],data[2],data[3],data[4],data[5],data[6]));
                            }
                    }

                }
                if(readMessage.substring(0,1).equals("P")) {
                    String[] data = readMessage.split(";");
                    PostsAdapter.serverAdd(new Post(data[1],data[2],data[3],data[4],data[5],data[6]));
                }
                else {
                    messages.add(readMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(connection   != null)
                    connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
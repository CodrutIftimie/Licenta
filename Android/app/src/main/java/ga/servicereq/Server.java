package ga.servicereq;

import android.content.Context;
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
    private static boolean activeConnection;
    public static String message;

    public static void initializeConnection(final int port, final Context context) {
//        if(connection == null) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        connection = new Socket("192.168.2.101", 7689);
//                        writeSocket = new DataOutputStream(connection.getOutputStream());
//                        readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
    }

    public static void terminateConnection() {
        activeConnection = false;
    }

    public static void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writeSocket.write(message.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void run() {
        activeConnection = true;
        try {
            connection = new Socket("192.168.137.1", 6788);
            writeSocket = new DataOutputStream(connection.getOutputStream());
            readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while(activeConnection) {
                message = readSocket.readLine();
                if (message != null && !message.equals("[CheckConnection]"))
                    Log.e("SERVER", "new message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
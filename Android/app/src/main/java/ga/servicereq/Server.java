package ga.servicereq;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    byte[] messageInBytes = message.getBytes(StandardCharsets.UTF_8);
                    byte[] messageLength = ByteBuffer.allocate(4).putInt(messageInBytes.length).array();
                    writeSocket.write(messageLength, 0, 4);
                    writeSocket.flush();
                    writeSocket.write(messageInBytes, 0, messageInBytes.length);
                    writeSocket.flush();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String readMessage() throws IOException {
        if (readSocket.ready()) {
            return readSocket.readLine();
        }
        return null;
    }

    @Override
    public void run() {
        messages = new ArrayList<>();

        activeConnection = true;
        try {
//            connection = new Socket("192.168.43.5", 6787);
            connection = new Socket("192.168.0.173", 6787);
//            connection = new Socket("192.168.137.1",6787);
            writeSocket = new DataOutputStream(connection.getOutputStream());
            readSocket = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (activeConnection) {
                String readMessage = readMessage();
                if (readMessage != null && readMessage.split(";;").length > 0) {
                    for (String m : readMessage.split(";;")) {
                        Log.d("SERVER", m.contains("[CheckConnection]") ? "" : m);
                        if (!m.equals("[CheckConnection]")) {
                            if (m.substring(0, 1).equals("P")) {
                                String[] data = m.split(";");
                                Post post;
                                if (data.length > 9) {
                                    post = new Post(data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], true);
                                    PostsAdapter.serverAdd(post);
                                } else {
                                    post = new Post(data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], false);
                                    PostsAdapter.serverAdd(post);
                                }
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
                                String helperCategories = Objects.requireNonNull(preferences.getString("cat", ""));
                                if(helperCategories.contains(post.getCategory()))
                                    createNotification("ServiceReq: [Helper] O nouă postare", "A apărut o nouă postare dintr-o categorie în care ajutați!");
                            } else if (m.substring(0, 1).equals("M")) {
                                String[] data = m.split(";");
                                Message message;
                                if (data.length > 7) {
                                    message = new Message(data[2], data[3], data[4], data[5], data[6], true);
                                    if(data[1].equals("0"))
                                        message.activityAdded = true;
                                    MessagesAdapter.serverAdd(message);
                                    MessagingActivity.staticAdd(message);
                                } else {
                                    message = new Message(data[2], data[3], data[4], data[5], data[6], false);
                                    if(data[1].equals("0"))
                                        message.activityAdded = true;
                                    MessagesAdapter.serverAdd(message);
                                    MessagingActivity.staticAdd(message);
                                }
                                String notificationMessage = message.firstName + " " + message.lastName + ": ";
                                if (message.lastMessage.length() > 35)
                                    notificationMessage += message.lastMessage.substring(0, 35) + "...";
                                else notificationMessage += message.lastMessage;
                                createNotification("ServiceReq: Aveți un nou mesaj", notificationMessage);
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

    public static void clearMessages() {
        Server.messages.clear();
    }

    private void createNotification(String title, String message) {


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String name = "ServiceReq";
            String description = "Channel for ServiceReq notifications";
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel("ServiceReq", name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(description);
            NotificationManager manager = (NotificationManager) (appContext.getSystemService(Context.NOTIFICATION_SERVICE));
            manager.createNotificationChannel(notificationChannel);
        }

        Intent notificationIntent = new Intent(appContext, LoginActivity.class);
        PendingIntent intent = PendingIntent.getActivity(appContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, "ServiceReq")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId("ServiceReq")
                .setContentIntent(intent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(appContext);
        notificationManager.notify("ServiceReq", 6787, builder.build());
    }
}
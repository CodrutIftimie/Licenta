package ga.servicereq;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BackgroundChecks extends JobService {

    public boolean jobCancelled;
    @Override
    public boolean onStartJob(JobParameters params) {
        backgroundChecks(params);
        return true;
    }

    private void backgroundChecks(final JobParameters params) {
        Log.d("BACKGROUND", "Background service started");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(jobCancelled)
                    return;

                if(!Server.isActiveConnection()) {
                    new Thread(new Server(getApplicationContext())).start();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String userId = preferences.getString("gid", "");
                    long count = Server.messagesCount();
                    Server.sendMessage("C;" + userId + ";;");
                    while (count == Server.messagesCount()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Server.terminateConnection();
                    jobFinished(params, false);
                }
//                String message = Server.getMessage(Server.messagesCount()-1);
//                String[] data = message.split(";");
//                Message messageObj;
//                if(data.length > 2) {
//                    messageObj = new Message(data[3], data[4], data[5], data[6], data[7], false);
//                    String notificationMessage = messageObj.firstName + " " + messageObj.lastName + ": ";
//                    if (messageObj.lastMessage.length() > 35)
//                        notificationMessage += messageObj.lastMessage.substring(0, 35) + "...";
//                    else notificationMessage += messageObj.lastMessage;
//                    Server.createNotification("ServiceReq: Aveți un nou mesaj", notificationMessage);
//                }
//                else Server.createNotification("ServiceReq: Aveți mesaje noi!", "Aveți noi mesaje care așteaptă răspunsul dumneavoastră!");
                Log.d("BACKGROUND", "Background service finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return false;
    }
}

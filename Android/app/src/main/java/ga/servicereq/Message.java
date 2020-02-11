package ga.servicereq;

public class Message {
    public String senderId;
    public String firstName;
    public String lastName;
    public String lastMessage;
    public String date;
    public boolean isHelper;
    public boolean activityAdded = false;

    public Message(String sId, String f, String l, String m, String d, boolean isHelper) {
        this.senderId = sId;
        this.firstName = f;
        this.lastName = l;
        this.lastMessage = m;
        this.date = d;
        this.isHelper = isHelper;
    }
}

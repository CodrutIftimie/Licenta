package ga.servicereq;

public class Message {
    public String senderId;
    public String receiverId;
    public String firstName;
    public String lastName;
    public String lastMessage;
    public String date;
    public boolean activityAdded = false;

    public Message(String sId, String rId, String f, String l, String m, String d) {
        this.senderId = sId;
        this.receiverId = rId;
        this.firstName = f;
        this.lastName = l;
        this.lastMessage = m;
        this.date = d;
    }
}

package ga.servicereq;

public class Message {
    public String senderId;
    public String firstName;
    public String lastName;
    public String lastMessage;
    public String date;

    public Message(String sId, String f, String l, String m, String d) {
        this.senderId = sId;
        this.firstName = f;
        this.lastName = l;
        this.lastMessage = m;
        this.date = d;
    }
}
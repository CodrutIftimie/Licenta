package ga.servicereq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
    public static long conversationsCount = 0;
    public long viewTag;
    public String firstName;
    public String lastName;
    public String senderId;
    public List<ExchangedMessage> conversation;

    public Conversation(String sender) {
        this.senderId = sender;
        this.conversation = new ArrayList<>();
    }

    public Conversation(String sender, String firstname, String lastname) {
        this.senderId = sender;
        this.conversation = new ArrayList<>();
        this.firstName = firstname;
        this.lastName = lastname;
    }

    public void addMessage(ExchangedMessage message) {
        conversation.add(message);
    }
}

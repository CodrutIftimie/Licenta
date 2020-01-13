package ga.servicereq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
    public static long conversationsCount = 0;
    public String firstName;
    public String lastName;
    public String receiverId;
    public List<ExchangedMessage> conversation;

    public Conversation(String receiver) {
        this.receiverId = receiver;
        this.conversation = new ArrayList<>();
    }

    public Conversation(String receiver, String firstname, String lastname) {
        this.receiverId = receiver;
        this.conversation = new ArrayList<>();
        this.firstName = firstname;
        this.lastName = lastname;
    }

    public void addMessage(ExchangedMessage message) {
        conversation.add(message);
    }
}

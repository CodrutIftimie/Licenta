package ga.servicereq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
    String firstName;
    String lastName;
    String receiverId;
    List<ExchangedMessage> conversation;

    public Conversation(String receiver) {
        this.receiverId = receiver;
        this.conversation = new ArrayList<>();
    }

    Conversation(String receiver, String firstname, String lastname) {
        this.receiverId = receiver;
        this.conversation = new ArrayList<>();
        this.firstName = firstname;
        this.lastName = lastname;
    }

    void addMessage(ExchangedMessage message) {
        conversation.add(message);
    }
}

package ga.servicereq;


class ExchangedMessage {
    String senderId;
    String message;
    String date;


    ExchangedMessage(String sender, String message, String date) {
        this.senderId = sender;
        this.message = message;
        this.date = date;
    }

    ExchangedMessage(String sender, String message) {
        this.senderId = sender;
        this.message = message;
    }
}
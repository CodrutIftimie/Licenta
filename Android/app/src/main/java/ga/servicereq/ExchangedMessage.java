package ga.servicereq;


class ExchangedMessage {
    public String senderId;
    public String message;
    public String date;

    public ExchangedMessage(String sender, String message, String date) {
        this.senderId = sender;
        this.message = message;
        this.date = date;
    }

    public ExchangedMessage(String sender, String message) {
        this.senderId = sender;
        this.message = message;
    }
}
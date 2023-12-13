package Message;

public class MessageModel {
	private String text;
	private String sender;
	private String recipient;

	public MessageModel(String sender, String recipient, String text) {
		this.sender = sender;
		this.recipient = recipient;
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public String getSender() {
		return this.sender;
	}

	public String getRecipient() {
		return this.recipient;
	}
}

package sapphire.appexamples.wordscramblewithfriends.app;

import java.io.Serializable;

public final class Message implements Serializable {
	private int msg;
	//private Object msgObj;
	private Serializable msgObj;
	
	//public Message(Integer msg, Object msgObj) {
	public Message(Integer msg, Serializable msgObj) {
		if (msg == null)
			throw new IllegalArgumentException();
		this.msg = msg;
		this.msgObj = msgObj;
	}
	
	public int getMessage() {
		return msg;
	}
	
	public Object getMessageObject() {
		return msgObj;
	}
}

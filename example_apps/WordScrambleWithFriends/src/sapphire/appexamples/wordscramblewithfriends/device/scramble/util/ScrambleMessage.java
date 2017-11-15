package sapphire.appexamples.wordscramblewithfriends.device.scramble.util;

import java.io.Serializable;

public final class ScrambleMessage implements Serializable {
	private int msg;
	private Serializable[] msgObjs;
	
	public ScrambleMessage(Integer msg, Serializable... msgObjs) {
		if (msg == null)
			throw new IllegalArgumentException();
		this.msg = msg;
		this.msgObjs = msgObjs;
	}
	
	public int getMessage() {
		return msg;
	}
	
	public Serializable[] getMessageObjects() {
		return msgObjs;
	}
}

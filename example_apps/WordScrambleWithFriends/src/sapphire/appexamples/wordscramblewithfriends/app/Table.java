package sapphire.appexamples.wordscramblewithfriends.app;

import sapphire.app.SapphireObject;

public class Table implements SapphireObject {
	private String hostname;
	private String guestname;
	private int tableId;

	public Table() {
		this.hostname = null;
		this.guestname = null;
		this.tableId = -1;
	}
	
	public void initialize(String hostname, Integer tableId) {
		this.hostname = hostname;
		this.tableId = tableId;
	}
	
	public boolean joinTable(String guestname) {
		if (hostname != null && tableId >= 0 && this.guestname == null) {
			this.guestname = guestname;
			return true;
		}
		System.out.println("Error joining table" + tableId);
		System.out.println(toString());
		return false;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public String getGuestname() {
		return guestname;
	}
	
	public int getTableId() {
		return tableId;
	}
	
	public boolean isFull() {
		return hostname != null && guestname != null;
	}
	
	public void reset() {
		this.hostname = null;
		this.guestname = null;
		this.tableId = -1;
	}
}

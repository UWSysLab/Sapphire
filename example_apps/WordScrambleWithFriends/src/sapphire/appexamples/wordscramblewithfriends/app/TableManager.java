package sapphire.appexamples.wordscramblewithfriends.app;

import static sapphire.runtime.Sapphire.new_;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sapphire.app.SapphireObject;

public class TableManager implements SapphireObject {
	private static final long NANO_TO_MILLI = 1000000;
	private static final long MIN_LOCATION_UPDATE = 600000;
	private static final float DEFAULT_DISTANCE = 1000;
	private static final int RADIUS_EARTH = 6371;	//km
	private static final int MAX_FREE_TABLES = 5;
	
	//private final List<Table> hostedTables = new ArrayList<Table>();
	private final List<TableEntry> hostedTables = new ArrayList<TableEntry>();
	private final List<Table> freeTables = new LinkedList<Table>();
	private final Map<Integer, Table> partiallyFreeTables = new Hashtable<Integer, Table>();

	private static long lastLocTimestamp;
	private int currentTableId;
	private UserManager userManager;
	
	public TableManager(UserManager userManager) {
		this.currentTableId = 0;
		this.userManager = userManager;
		lastLocTimestamp = System.nanoTime() / NANO_TO_MILLI;
	}
	
//	public Table hostTable(String hostname, LocationService ls) {
//	User host = userManager.getUser(hostname);
//	Table t = null;
//	if (freeTables.isEmpty()) {
//		System.out.println("Creating new table");
//		t = (Table) new_(Table.class);
//	} else {
//		System.out.println("Using old table");
//		t = freeTables.remove(0);
//	}
//	t.initialize(hostname, Integer.valueOf(currentTableId));
//	hostedTables.add(new TableEntry(t, ls));
//	System.out.println("@" + host.getUserInfo().getUsername() + " is now hosting table" + currentTableId);
//	++currentTableId;
//	return t;
//}
//	
//	public Table joinTable(String guestname, LocationService ls) {
//		User guest = userManager.getUser(guestname);
//		if (!hostedTables.isEmpty()) {
//			TableEntry te = null;
//			if (ls != null)
//				te = getClosestHost(ls.getLocation());
//			else
//				te = getClosestHost(null);
//			if (!te.table.joinTable(guestname)) {
//				hostedTables.add(te);
//				return null;
//			}
//			System.out.println("@" + guest.getUserInfo().getUsername() + " joined table" + te.table.getTableId());
//			return te.table;
//		}
//		if (hostedTables.isEmpty())
//			System.out.println("There are no available tables to join.");
//		return null;
//	}
	
	public Table hostTable(String hostname, double latitude, double longitude) {
		User host = userManager.getUser(hostname);
		Table t = null;
		if (freeTables.isEmpty()) {
			System.out.println("Creating new table");
			t = (Table) new_(Table.class);
		} else {
			System.out.println("Using old table");
			t = freeTables.remove(0);
		}
		t.initialize(hostname, Integer.valueOf(currentTableId));
		hostedTables.add(new TableEntry(t, latitude, longitude));
		System.out.println("@" + host.getUserInfo().getUsername() + " is now hosting table" + currentTableId);
		++currentTableId;
		return t;
	}
	
	public Table joinTable(String guestname, double latitude, double longitude) {
		User guest = userManager.getUser(guestname);
		if (!hostedTables.isEmpty()) {
			//TableEntry te = hostedTables.remove(0);
			TableEntry te = getClosestHost(latitude, longitude);
			if (!te.table.joinTable(guestname)) {
				hostedTables.add(te);
				System.out.println("Error joining table");
				return null;
			}
			System.out.println("@" + guest.getUserInfo().getUsername() + " joined table" + te.table.getTableId());
			return te.table;
		}
		if (hostedTables.isEmpty())
			System.out.println("There are no available tables to join.");
		return null;
	}
	
	public boolean tableAvailable() {
		return !hostedTables.isEmpty();
	}
	
	// Both players need to return the table
	// When a player calls this method they are promising to not reference this table object again
	public void leaveTable(Table t) {
		if (t != null) {
			Table table = partiallyFreeTables.remove(t.getTableId());
			if (table != null && freeTables.size() < MAX_FREE_TABLES) {
				// This is the last player to leave the table
				table.reset();
				freeTables.add(table);
			} else {
				// This is the first player to leave the table. We must wait for the other player to
				// leave the table before adding it to the free list.
				partiallyFreeTables.put(t.getTableId(), t);
			}
		}
	}
	
//	private TableEntry getClosestHost(Location guestLocation) {
//		if (guestLocation == null) {
//			return hostedTables.remove(0);
//		}
//		// Update host locations if last timestamp < minimum location update time
//		long timestamp = System.nanoTime() / NANO_TO_MILLI;
//		if (timestamp - lastLocTimestamp > MIN_LOCATION_UPDATE) {
//			for (TableEntry te : hostedTables) {
//				if (te.ls != null) {
//					Location newLocation = te.ls.getLocation();
//					if (newLocation != null)
//						te.location = newLocation;
//				}
//			}
//			lastLocTimestamp = timestamp;
//		}
//		// Find table with host whose location is the closest to the guest
//		int minDistIndex = 0;
//		float minDistance = DEFAULT_DISTANCE;
//		Location hostLocation = hostedTables.get(minDistIndex).location;
//		if (hostLocation != null)
//			minDistance = guestLocation.distanceTo(hostedTables.get(minDistIndex).location);
//		for (int i = 1; i < hostedTables.size() - 1; ++i) {
//			hostLocation = hostedTables.get(i).location;
//			if (hostLocation != null) {
//				float distance = guestLocation.distanceTo(hostLocation);
//				if (distance < minDistance) {
//					minDistance = distance;
//					minDistIndex = i;
//				}
//			}
//		}
//		return hostedTables.remove(minDistIndex);
//	}

	private TableEntry getClosestHost(double guestLon, double guestLat) {
	// Find table with host whose location is the closest to the guest
	int minDistIndex = 0;
	TableEntry te = hostedTables.get(minDistIndex);
	float minDistance = (float) computeDistance(te.longitude, te.latitude, guestLon, guestLat);
	for (int i = 1; i < hostedTables.size() - 1; ++i) {
		te = hostedTables.get(i);
		float distance = (float) computeDistance(te.longitude, te.latitude, guestLon, guestLat);
		if (distance < minDistance) {
			minDistance = distance;
			minDistIndex = i;
		}
	}
	return hostedTables.remove(minDistIndex);
}

	private class TableEntry {
		Table table;
		//LocationService ls;
		//Location location;
		double longitude;
		double latitude;
		
//		public TableEntry(Table table, LocationService ls) {
//			this.table = table;
//			this.ls = ls;
//			location = ls.getLocation();
//		}
		
		public TableEntry(Table table, double latitude, double longitude) {
			this.table = table;
			this.longitude = longitude;
			this.latitude = latitude;
		}
	}
	
	private static double computeDistance(double lon1, double lat1, double lon2, double lat2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double a = Math.sin(dLat/2)*Math.sin(dLat/2)+Math.sin(dLon/2)*Math.sin(dLon/2)*Math.cos(rLat1)*Math.cos(rLat2);
		double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return RADIUS_EARTH*c;
	}
}

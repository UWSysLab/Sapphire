package sapphire.appexamples.wordscramblewithfriends.device.generator;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sapphire.appexamples.wordscramblewithfriends.app.Game;
import sapphire.appexamples.wordscramblewithfriends.app.ScrambleManager;
import sapphire.appexamples.wordscramblewithfriends.app.Table;
import sapphire.appexamples.wordscramblewithfriends.app.TableManager;
import sapphire.appexamples.wordscramblewithfriends.app.User;
import sapphire.appexamples.wordscramblewithfriends.app.UserManager;
import sapphire.kernel.server.KernelServer;
import sapphire.kernel.server.KernelServerImpl;
import sapphire.oms.OMSServer;

public class ScrambleWorldGenerator {
	static final int USERS_NUM = 20;
	static final int GAMES_NUM = 3;
	static final int EVENTS_NUM = 10;
	static final int MAX_ROUNDS = 20;
	static final int MAX_ROUND_POINTS = 5;
	
	private static final String[] events = {"HOST", "HOST", "JOIN", "JOIN", "PLAY", "PLAY", "PLAY", "PLAY", "END"};
	
	private static final Random gen = new Random();
	
	private static String getUserName(int userId) {
		return "user" + Integer.toString(userId);
	}

	public static void main(String[] args) {
		Registry registry;
		
		try {
			registry = LocateRegistry.getRegistry(args[0],Integer.parseInt(args[1]));
			OMSServer server = (OMSServer) registry.lookup("SapphireOMS");

            KernelServer nodeServer = new KernelServerImpl(null, null, null, null);
            
            /* Get Scramble and User Manager */
			ScrambleManager sm = (ScrambleManager) server.getAppEntryPoint();
            UserManager um  = sm.getUserManager();
            TableManager tm = sm.getTableManager();

            /* Create the users */
//            for (int i = 0; i < USERS_NUM; i++) {
//            	long start = System.nanoTime();
//            	String userName = "user" + Integer.toString(i);
//            	um.addUser(userName, userName);
//            	long end = System.nanoTime();
//            	System.out.println("Added " + userName + " in:" + ((end - start) / 1000000) + "ms");
//            }
//            
//            System.out.println("Added users!\n");
//            
//            /* Host some games */
//            for (int i = 0; i < GAMES_NUM; ++i) {
//            	int userId = gen.nextInt(USERS_NUM);
//            	while (tm.hostTable(um.getUser(getUserName(userId))) == null)
//            		userId = gen.nextInt();
//            }
//            System.out.println("Done hosting some games!\n");
//
//            /* Have other users join the games */
//            List<Table> tables = new ArrayList<Table>();
//            while (tm.tableAvailable()) {
//            	Table t = tm.joinTable(um.getUser(getUserName(gen.nextInt(USERS_NUM))));
//            	if (t != null)
//            		tables.add(t);
//            }
//            System.out.println("Done joining the games!\n");
//            
//            /* Initialize the games */
//            List<Integer> gameIds = new ArrayList<Integer>();
//            for (int i = tables.size() - 1; i >= 0; --i) {
//            	Table t = tables.remove(i);
//            	Game g = gm.newGame(t);
//            	if (g != null)
//            		gameIds.add(t.getTableId());
//            }
//            for (int i = 0; i < gameIds.size(); ++i) {
//            	gm.getGame(gameIds.get(i)).toString();
//            }
//            
//            for (int i = 0; i < EVENTS_NUM; ++i) {
//            	String nextEvent = events[gen.nextInt(events.length)];
//            	
//            	if (nextEvent.equals("HOST")) {
//            		System.out.println("host!");
//            		tm.hostTable(um.getUser(getUserName(gen.nextInt(USERS_NUM))));
//            		continue;
//            	}
//            	
//            	if (nextEvent.equals("JOIN")) {
//            		System.out.println("join!");
//            		Table t = tm.joinTable(um.getUser(getUserName(gen.nextInt(USERS_NUM))));
//                	if (t != null) {
//                		Game g = gm.newGame(t);
//                		if (g != null)
//                    		gameIds.add(t.getTableId());
//                	}
//                	continue;
//            	}
//            	
//            	if (gameIds.isEmpty()) {	/* no active games */
//            		continue;
//            	}
//            	int gameIdIndex = gen.nextInt(gameIds.size());
//            	Integer gameId = gameIds.get(gameIdIndex);
//            	if (nextEvent.equals("END")) {
//            		System.out.println("end!");
//            		if (gm.endGame(gameId)) {
//            			System.out.println("Ended game" + gameId);
//            			gameIds.remove(gameIdIndex);
//            		}
//            	}
//            	
//            	if (nextEvent.equals("PLAY")) {
//            		System.out.println("play!");
//            	}
//            }
//            System.out.println("done!");
//            
//            System.out.println("USERS");
//            for (int i = 0; i < USERS_NUM; ++i) {
//            	User u = um.getUser(getUserName(i));
//            	System.out.println(u.toString());
//            }
//            
//            System.out.println("GAMES");
//            for (int i = 0; i < gameIds.size(); ++i) {
//            	System.out.println(gameIds.get(i).toString());
//            }
//            
          } catch (Exception e) {
			e.printStackTrace();
		}
	}
}


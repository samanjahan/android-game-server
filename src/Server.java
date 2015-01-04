import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The game server contains the main method.
 * Acts as sort of controller between clients
 *
 */
public class Server {

	/**
	 * The ServerSocket that will listen for connections on a port
	 */
	private static ServerSocket serverSocket;

	/**
	 * Socket to store the client connection
	 */
	private static Socket clientSocket;

	/**
	 * The port that the ServerSocket will listen on. Currently hard-coded but
	 * can be changed later
	 */
	private static String port = "63403";

	/**
	 * The PrintWriter currently not used
	 */
	// public PrintWriter outToClient;

	/**
	 * BufferedReader from the clientSocket inputStream to get Data
	 */
	private BufferedReader bufferedReaderFrom;

	/**
	 * The userName of the client
	 */
	private String userName;

	/**
	 * Storing the client related data as an instance of ServerHandle - Client
	 */
	private ServerHandle client;

	/**
	 * String used to globally send String to user
	 */
	private String sendToUser;

	/**
	 * HashMap containing a list of all clients(ServerHandle.class) Key(String
	 * UserName) Value(ServerHandle client)
	 */
	private HashMap<String, ServerHandle> userList = new HashMap<String, ServerHandle>();

	/**
	 * A pair of clients playing together in a game(pairGameList)
	 */
	private HashMap<String, PairGame> pairGameList = new HashMap<String, PairGame>();

	// Constructor not used commented out
	/*
	 * public Server(ServerHandle serverHandle){ this.client = serverHandle; }
	 */

	/**
	 * No argument constructor used when the Server starts
	 */
	public Server() {
	}

	/**
	 * Game servers main method that initiates the game by creating an instance
	 * of Server with no arguments
	 * 
	 * @param args
	 *            not implemented
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	/**
	 * Start method that initiates the ServerSocket and starts waiting for
	 * clients to connect. For each client that connects it adds them to the
	 * userList and starts the ServerHandle
	 */
	public void start() {
		System.out.println("'Server' start");
		try {
			serverSocket = new ServerSocket(Integer.valueOf(port));
			System.out.println("'Server' Waiting for clients...");
			while (true) {
				clientSocket = serverSocket.accept();
				bufferedReaderFrom = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				userName = bufferedReaderFrom.readLine().toString().trim();
				System.out.println("'Server' " + userName + " has connected to the server");
				client = new ServerHandle(clientSocket, userName, this);
				userList.put(userName, client);
				client.start();
			}
		} catch (EOFException e) {
			System.out.println("'Server' connection refused!");
		} catch (IOException ie) {
			System.out
					.println("'Server' IOException caught in - Start method");
			ie.printStackTrace();
		}
	}

	/**
	 * Method to remove a user from the UserList HashMap.
	 * 
	 * @param userName
	 *            The userName of the client to remove
	 */
	public void removeClient(String userName) {
		userList.get(userName).closeConnection();
		userList.remove(userName);
		messageUserToUpdate();
	}

	/**
	 * Method to get all current clients (userNames) in the userList HashMap
	 * 
	 * @return String of all users userName separated by " "
	 */
	public String getAllUser() {
		StringBuilder st = new StringBuilder();
		for (String key : userList.keySet()) {
			st.append(key + " ");
		}
		return st.toString();
	}

	/**
	 * Method used to notify all clients to update their list of current users.
	 * Used when a new user connects or a users discconnects
	 * 
	 */
	public void messageUserToUpdate() {
		for (String key : userList.keySet()) {
			userList.get(key).uppdateUser();
		}
	}

	/**
	 * Method used when a user wants to initiate a game with another user. Takes
	 * String of userName and returns the actual client
	 * 
	 * @param userName	The userName of the user that initiates the game request
	 * @param sendToUser The userName of the user that the request is for
	 * @return the ServerHandle object (Client) of the user that the request is
	 * 			directed for
	 */
	public ServerHandle getTwoUser(String userName, String sendToUser) {
		this.sendToUser = sendToUser;
		ServerHandle user = null;
		for (String key : userList.keySet()) {
			if (key.equals(userName)) {
				user = userList.get(key);
				return user;
			}
		}
		return user;
	}

	/**
	 * Send request to user (sendToUser) reference updated in the getTwoUser method
	 * @param user
	 */
	public void sendMessageToUser(ServerHandle user) {
		user.sendMessageToUser(sendToUser);
	}

	/**
	 * Return the Client object of the user that has the given userName
	 * @param userName The userName of the client to get
	 * @return	the clients ServerHandle Object 
	 */
	public ServerHandle getUser(String userName) {
		ServerHandle user = null;
		for (String key : userList.keySet()) {
			if (key.equals(userName)) {
				user = userList.get(key);
				return user;
			}
		}
		return user;
	}

	/**
	 * Create a pairGame (Game) and Start a game when a pairGame is configured.
	 * Add the game to the pairGameList
	 * 
	 * @param userOne The first user userName
	 * @param userTwo The secont user UserName
	 */
	public void StartGame(String userOne, String userTwo) {
		PairGame pairGame = new PairGame(getUser(userOne), getUser(userTwo));
		String key = userOne.concat(" " + userTwo);
		pairGameList.put(key, pairGame);
		pairGame.sendQuestionToClient();
	}

	/**
	 * If a user Leaves the game this method can send a message to the other
	 * user that was currently in a game with the leaving user.
	 * 
	 * @param userName of the user that has left
	 */
	public void leavesGame(String userName) {
		for (String key : pairGameList.keySet()) {
			String[] keys = key.split(" ");

			if (keys[0].equals(userName)) {
				PairGame gm = pairGameList.get(key);
				gm.leavesGame(keys[1]);
				pairGameList.remove(key);
			} else if (keys[1].equals(userName)) {
				PairGame gm = pairGameList.get(key);
				gm.leavesGame(keys[0].toString());
				pairGameList.remove(key);
			}
		}
	}


	/**
	 * Check the answer From userName and check if both users have answered the
	 * question (gm.ifAllUserHasAnswered)
	 * 
	 * @param userName The user that has answered
	 * @param answer The answer that the user has provided
	 */
	public void checkAnswer(String userName, String answer) {
		for (String key : pairGameList.keySet()) {
			String[] keys = key.split(" ");

			if (keys[0].equals(userName) || keys[1].equals(userName)) {
				PairGame gm = pairGameList.get(key);
				if (keys[0].equals(userName)) {
					gm.setUserOneAnswer(answer);
				} else {
					gm.setUserTwoAnswer(answer);
				}
				if ((gm.ifAllUserHasAnswered())) {
					gm.checkAnswer();
					break;
				}
			}
		}
	}
}

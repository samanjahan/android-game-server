import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class that works as sort of a client or player. it contains all information needed about the client
 *
 */
public class ServerHandle extends Thread {
	/**
	 * The connection Socket to the client
	 */
	private Socket clientSocket;
	
	/**
	 * Used for input from the client
	 */
	private BufferedReader  inFromClient;
	
	/***
	 * Used for output to the client
	 */
	private PrintWriter outToClient;
	
	/**
	 * Contains the message received from the client
	 */
	private String msgFromClient;
	
	/**
	 * Used to check whether or not the server is running
	 */
	private Boolean isServerRunning;
	
	/**
	 * The userName of the client
	 */
	private String userName;
	
	/**
	 * The Server object is used as sort of a controller
	 */
	private Server server;
	
	/**
	 * Message String from client is split by " " into commands
	 */
	private String[] commands;
	
	/**
	 * The answer received for the current question
	 */
	private String answer;
	
	
	/**
	 * Constructor to create the ServerHandle
	 * 
	 * @param clientSocket The connection to the client
	 * @param userName	The userName of the client
	 * @param server	The current server instance.
	 */
	public ServerHandle(Socket clientSocket, String userName,Server server) {
		this.server = server;
		this.clientSocket = clientSocket;	
		this.userName = userName;
	}
	
	/**
	 * Creates the input and output streams. Calls the method on the server to
	 * notify all users to update their user list. Updates the list of this client
	 * waits while server is running loop for input and checks this input
	 */
	public void run() {
		isServerRunning = true;
	
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToClient = new PrintWriter(clientSocket.getOutputStream());
			server.messageUserToUpdate();
			uppdateUser();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (isServerRunning) {
			try {
				msgFromClient = inFromClient.readLine();
				System.out.println("'ServerHandle' Message from client " + userName + " is\n" + msgFromClient);
                commands = msgFromClient.split(" ");
                
				if(commands[0].equals("quit")){
					server.removeClient(userName);
				}
				if(commands[0].equals("play")){
					server.sendMessageToUser(server.getTwoUser(commands[1],userName));			
				}
				
				if(commands[0].equals("accepted")){
					server.StartGame(commands[1], commands[2]);	
				}
				
				if(commands[0].equals("answer")){
					System.out.println("'ServerHandle' Answer Commands length: " + commands.length);
					if (commands.length < 2) {
						answer = commands[1];
					}else {
						answer = "";
						for (int i = 1; i <= commands.length-1; i++) {
							if (i == 1) {
								answer = commands[i];
							}else {
							answer = answer + " " + commands[i];
							}
						}
					}
					System.out.println("'ServerHandle' Answer from user " + userName + " Is: " + answer);
					server.checkAnswer(userName,answer);
				}
				if(commands[0].equals("leaves")){
					server.leavesGame(userName);
				}
			} catch (IOException e) {
				System.out.println("'ServerHandle' User " + userName + " Closed the connection!");
				break;
			}
		}
	}
	
	/**
	 * Send an update of the userList to the client
	 */
	public void uppdateUser(){
		outToClient.println("userList " + server.getAllUser());
		outToClient.flush();
	}
	
	/**
	 * Get the answer stored in the answer field
	 * @return the answer String
	 */
	public String getAnswer(){
		return answer;
	}
	
	/**
	 * Close the client connection socket
	 */
	public void closeConnection(){
		outToClient.close();
		try {
			inFromClient.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to the client
	 * 
	 * @param sendToUser the String to send to the clients
	 */
	public void sendMessageToUser(String sendToUser){
		outToClient.println("request " + sendToUser);
		outToClient.flush();
	}
	
	/**
	 * Notify the client that another user has left
	 * 
	 * @param sendToUser the user that has left
	 */
	public void leavesGame(String sendToUser){
		//System.out.println("'ServerHandle' Leaves " + sendToUser);
		outToClient.println("leavesGame" + " " + sendToUser);
		outToClient.flush();
	}
	
	/**
	 * Send a question to the client
	 * 
	 * @param question the String containing the question
	 */
	public void sendQuestion(String question){
		outToClient.println("question" + " " + question);
		outToClient.flush();
	}
	
	/**
	 * Return this clients userNAme
	 * 
	 * @return userName of this client
	 */
	public String getUserName(){
		return userName;
	}
	
	/**
	 * Notify this client that it has won the game
	 * 
	 * @param msg additional message to send to the user.
	 */
	public void isWinner(String msg){
		outToClient.println("winner" + " " + msg);
		outToClient.flush();
	}
	

	/**
	 * Notify this client that it has lost the game
	 * 
	 * @param msg additional message to send to the user
	 */
	public void isLoser(String msg){
		outToClient.println("loser" + " " + msg);
		outToClient.flush();
	}
	
	/**
	 * Notify the user that the game is a tie
	 * 
	 * @param msg additional message to send to the user
	 */
	public void samePoint(String msg){
		outToClient.println("samePoint" + " " + msg);
		outToClient.flush();
	}

}

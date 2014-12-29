import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author syst3m
 *
 */

public class ServerHandle extends Thread {
	private Socket clientSocket;
	private BufferedReader  inFromClient;
	private PrintWriter outToClient;
	private String msgFromClient;
	private Boolean isServerRunning;
	private String userName;
	private Server server;
	private String[] commands;
	

	public ServerHandle(Socket clientSocket, String userName,Server server) {
		this.server = server;
		this.clientSocket = clientSocket;	
		this.userName = userName;
	}
	
	public void run() {
		isServerRunning = true;
	
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToClient = new PrintWriter(clientSocket.getOutputStream());
			server.massageUserToUpdate();
			uppdateUser();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (isServerRunning) {
			try {
				msgFromClient = inFromClient.readLine();
                commands = msgFromClient.split(" ");
                
				if(commands[0].equals("quit")){
					server.removeClient(userName);
				}
				if(commands[0].equals("play")){
					System.out.println("play " + commands[1]);
					server.SendMassageToUser(server.getUser(commands[1],userName));			
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Cloced!");
				break;
			}
		}
	}
	
	public void uppdateUser(){
		outToClient.println("userList " + server.getAllUser());
		outToClient.flush();
	}
	
	public void closeConnection(){
		outToClient.close();
		try {
			inFromClient.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMassageToUser(String sendToUser){
		outToClient.println("request " + sendToUser);
		outToClient.flush();
	}
}

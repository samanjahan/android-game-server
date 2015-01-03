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
	private String answer;
	
	

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
				System.out.println(msgFromClient);
                commands = msgFromClient.split(" ");
                
				if(commands[0].equals("quit")){
					server.removeClient(userName);
				}
				if(commands[0].equals("play")){
					server.SendMassageToUser(server.getTwoUser(commands[1],userName));			
				}
				
				if(commands[0].equals("accepted")){
					server.StartGame(commands[1], commands[2]);	
				}
				
				if(commands[0].equals("answer")){
					answer = commands[1];
					server.chechAnswer(userName,answer);
				}
				if(commands[0].equals("leaves")){
					server.leavesGame(userName);
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
	
	public String getAnswer(){
		return answer;
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
	
	public void leavesGame(String sendToUser){
		System.out.println("Leavses " + sendToUser);
		outToClient.println("leavesGame" + " " + sendToUser);
		outToClient.flush();
	}
	
	public void sendQuestion(String question){
		outToClient.println("question" + " " + question);
		outToClient.flush();
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void isWinner(String msg){
		outToClient.println("winner" + " " + msg);
		outToClient.flush();
	}
	public void isLoser(String msg){
		outToClient.println("loser" + " " + msg);
		outToClient.flush();
	}
	
	public void samePoint(String msg){
		outToClient.println("samePoint" + " " + msg);
		outToClient.flush();
	}

}

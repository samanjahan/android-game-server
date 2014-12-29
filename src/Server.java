 import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
public class Server{
	

   
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static String port;
	public PrintWriter outToClient;
    private BufferedReader bufferedReaderFrom;
    private String userName;
    private ServerHandle client;
    private String sendToUser;
	private HashMap<String,ServerHandle> list = new HashMap<String,ServerHandle>();

	public Server(ServerHandle serverHandle){
		this.client = serverHandle;
	}
	public Server(){
		
	}

	
	public static void main(String[] args) throws IOException{
		port = "63403";		
		Server server = new Server();		
		server.start();
	}
	
	/**
	 * @param args
	 */
	public void start() throws IOException{
		System.out.println("server");		
		try {
				serverSocket = new ServerSocket(Integer.valueOf(port));		
				System.out.println("Waiting for clients...");			
				while(true){
					clientSocket = serverSocket.accept();
	                bufferedReaderFrom = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	                userName = bufferedReaderFrom.readLine().toString();
	                System.out.println(userName);
	                client = new ServerHandle(clientSocket,userName,this);
					list.put(userName, client);			
					client.start();
				}
		} catch (EOFException e) {
			System.out.println("connection refused!");
		} 
	}
	
	public void removeClient(String userName){

			System.out.println("removing");
			list.get(userName).closeConnection();
			list.remove(userName);
			System.out.println(list.size());
			System.out.println("Removed " + userName);
			massageUserToUpdate();
	}
	
	public String getAllUser(){
		StringBuilder st = new StringBuilder();
		for ( String key : list.keySet() ) {
			st.append(key + " ");
		}
		System.out.println(st.toString());
		return st.toString();
	}
	
	public void massageUserToUpdate(){
		System.out.println(list.size());
		for ( String key : list.keySet() ) {
		   list.get(key).uppdateUser();		    
		}
	}
	
	public ServerHandle getUser(String userName,String sendToUser){
		this.sendToUser = sendToUser;
		ServerHandle user = null;
		for ( String key : list.keySet() ) {
			   if(key.equals(userName)){
				   user = list.get(key);
				   return user;
			   }		    
			}
		return user;
		
	}
	
	public void SendMassageToUser(ServerHandle user){
		user.sendMassageToUser(sendToUser);
	}
}



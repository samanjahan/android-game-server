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
    
    //List off all uesr
	private HashMap<String,ServerHandle> userList = new HashMap<String,ServerHandle>();
	
	//List of two players play togheter
	private HashMap<String,PairGame> pairGameList = new HashMap<String,PairGame>();


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
	                userList.put(userName, client);			
					client.start();
				}
		} catch (EOFException e) {
			System.out.println("connection refused!");
		} 
	}
	
	public void removeClient(String userName){
			userList.get(userName).closeConnection();
			userList.remove(userName);
			massageUserToUpdate();
	}
	
	//Retuen all users of userList to update ListView on client (Android)
	public String getAllUser(){
		StringBuilder st = new StringBuilder();
		for ( String key : userList.keySet() ) {
			st.append(key + " ");
		}
		return st.toString();
	}
	
	//Send msg to client to uppdate listView if some user has leaves / connected to server
	public void massageUserToUpdate(){
		for ( String key : userList.keySet() ) {
			userList.get(key).uppdateUser();		    
		}
	}
	
	/*
	 * Tow parameters
	 * userName : user has send request
	 *  sendToUser : user has resive request
	 * */
	public ServerHandle getTwoUser(String userName,String sendToUser){
		this.sendToUser = sendToUser;
		ServerHandle user = null;
		for ( String key : userList.keySet() ) {
			   if(key.equals(userName)){
				   user = userList.get(key);
				   return user;
			   }		    
			}
		return user;		
	}
	
	//Send request to user (sendToUser) from getTwoUser method
	public void SendMassageToUser(ServerHandle user){
		user.sendMassageToUser(sendToUser);
	}
	
	//Return one user
	public ServerHandle getUser(String userName){
		ServerHandle user = null;
		for ( String key : userList.keySet() ) {
			   if(key.equals(userName)){
				   user = userList.get(key);
				   return user;
			   }		    
			}
		return user;		
	}
	

	
	public void StartGame(String userOne, String userTow){
		PairGame pairGame = new PairGame(getUser(userOne), getUser(userTow));
		String key = userOne.concat(" " + userTow);
		pairGameList.put(key, pairGame);
		pairGame.sendQuestionToClient();
	}
	
	/*
	 * username the user has leaves the game
	 * send massage to other user 
	 * */
	public void leavesGame(String userName){
		for ( String key : pairGameList.keySet() ) {
			String[] keys = key.split(" ");
			
		   if(keys[0].equals(userName)){
			   PairGame gm = pairGameList.get(key);
			   gm.leavesGame(keys[1]);
			   pairGameList.remove(key);
		   }else if(keys[1].equals(userName)){
			   PairGame gm = pairGameList.get(key);
			   gm.leavesGame(keys[0].toString());
			   pairGameList.remove(key);
		   }		    
		}
	}
	
	/*
	 * check the answer form userName and check if two users has answered the question (gm.ifAllUserHasAnswered)
	 * 
	 * */
	public void chechAnswer(String userName , String answer){
		for ( String key : pairGameList.keySet() ) {
			String[] keys = key.split(" ");
			System.out.println("keyyys[0] " + keys[0]);
			
		   if(keys[0].equals(userName)){
			   PairGame gm = pairGameList.get(key);
			   gm.setUserOneAnswer(answer);

			   if((gm.ifAllUserHasAnswered())){
				   gm.chechAnsewr();
				   break;
			   }
		   }if(keys[1].equals(userName)){
			   PairGame gm = pairGameList.get(key);
			   System.out.println("GGMMM "+ gm);
			   gm.setUserTowAnswer(answer);
			   if((gm.ifAllUserHasAnswered())){
				   gm.chechAnsewr();
				   break;
			   }
		   }		    
		}
	}
}



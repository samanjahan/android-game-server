import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private static String port;
	private static String ip;
	private static boolean isClientRunning;

	
	public static void main(String[] args) throws UnknownHostException, IOException{
		// TODO Auto-generated method stub
		isClientRunning = true;
		final Socket socket;
		String guess = "";
        System.out.print("port number:");
        port = "63403";

		   
	   System.out.print("\nEnter you name:");
	   ip = "localhost";	   
	   OutputStream outToServer = null;
        
		socket = new Socket(ip,Integer.valueOf(port));
		outToServer = socket.getOutputStream();
		//Send to server
 		PrintWriter out = new PrintWriter(outToServer, true);
 		//Ge from console
 		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		Runnable listener = new Runnable() {
			public void run() {
				String msgFmServer = null;
	        	InputStream inToClient = null;
	        
	        	try {
					inToClient = socket.getInputStream();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	     		//Get from Server
	     		BufferedReader bufferedReaderFrom = new BufferedReader(new InputStreamReader(inToClient));
	     		try {
					System.out.println(bufferedReaderFrom.readLine());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	     		while (isClientRunning) {
	    				try {
							msgFmServer = bufferedReaderFrom.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    				
	    				if(null == msgFmServer || msgFmServer.equals("quit")){
	    					System.out.println("Closed!");
	    					isClientRunning = false;
	    					System.exit(0);
	    				}
	    				System.out.println(msgFmServer);
	    			}
	    		}
	         
	     };
	    new Thread(listener).start();
	    System.out.println();
	    //Send to server
		while(isClientRunning){
			guess = bufferedReader.readLine();		
			out.println(guess);
			out.flush();
		}
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Java based Game Client. Can be used to play the game
 *
 */
public class Client {

	/**
	 * The port to be used when connecting to the game server
	 */
	private static String port = "63403";
	
	/**
	 * The IP address of the server 
	 */
	private static String ip = "localhost";
	
	/**
	 * boolean to store whether this client is connected or not
	 */
	private static boolean isClientRunning;

	/**
	 * main method to of the Client. Main thread listen
	 * for input from console and sends these to the server. While a separate
	 * Thread is created and listens for messages from the server.
	 * 
	 * @param args not implemented
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException,
	IOException {

		isClientRunning = true;
		final Socket socket;
		String guess = "";
		System.out.print("port number:");
		System.out.print("\nEnter you name:");

		OutputStream outToServer = null;

		socket = new Socket(ip, Integer.valueOf(port));
		outToServer = socket.getOutputStream();

		PrintWriter out = new PrintWriter(outToServer, true);

		// Get input from console
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(System.in));

		/**
		 * Runnable method that is run from a separate thread. used to listen
		 * for messages from server
		 */
		Runnable listener = new Runnable() {
			public void run() {
				String msgFmServer = null;
				InputStream inToClient = null;

				try {
					inToClient = socket.getInputStream();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// Get from Server
				BufferedReader bufferedReaderFrom = new BufferedReader(
						new InputStreamReader(inToClient));
				try {
					System.out.println(bufferedReaderFrom.readLine());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				while (isClientRunning) {
					try {
						msgFmServer = bufferedReaderFrom.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (null == msgFmServer || msgFmServer.equals("quit")) {
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
		// Send to server
		while (isClientRunning) {
			guess = bufferedReader.readLine();
			out.println(guess);
			out.flush();
		}
	}
}

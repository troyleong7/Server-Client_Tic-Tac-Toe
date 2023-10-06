//Name: Yun Keng Leong	StudentID: 1133704

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * This class retrieves a reference to the remote object from the RMI registry. It
 * invokes the methods on the remote object as if it was a local object of the type of the 
 * remote interface.
 *
 */
public class Client {
	private static String username;
	private static String ip;
	private static int port;
	
	public static void main(String[] args) {
		try {
			username = args[0];
			ip = args[1];
			port = Integer.parseInt(args[2]);
			Registry registry = LocateRegistry.getRegistry(ip, port);
			Service server = (Service) registry.lookup("Server");
			System.out.println("Connected to server ");
			ClientFunction client = new ClientService(server, username);
			
			while(true) {
				try {
	                server.crashNotify();
	            } catch (Exception e) {
	               	client.serverCrash();
	               	break;
	            }
				
				Thread.sleep(1000);
			}
			
			
		}catch (IOException e) {
			//Error for when no dictionary server found with the ip and port
			System.err.println("Server host not found! Make sure to type in the correct IP and Port! Or make sure server is opened!");
		}catch (Exception e) {
			//Error for when invalid format is typed in
			System.err.println("Please enter username, IP and port number in valid format. "
					+ "Format: java –jar Client.jar <username> <server-ip> <server-port> . <server-port> has to be numbers only.");
		}
	
	}
	
}

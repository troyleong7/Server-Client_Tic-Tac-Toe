//Name: Yun Keng Leong	StudentID: 1133704

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
			System.setProperty("java.rmi.server.hostname", ip);
			Registry registry = LocateRegistry.getRegistry(port);
			Service server = (Service) registry.lookup("Server");
			System.out.println("Connected to server ");
			ClientFunction client = new ClientService(server, username);
			
			while(true) {
				try {
	                server.crashNotify();
	                Thread.sleep(1000);
	            } catch (Exception e) {
	               	client.serverCrash();
	               	break;
	            }
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	
	}
	
}

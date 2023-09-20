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
	

	public static void main(String[] args) {
		try {
			String username;
			username = args[0];
			Registry registry = LocateRegistry.getRegistry("localhost");
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

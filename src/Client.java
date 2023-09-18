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
			ClientGUI GUI = new ClientGUI(username);
			ClientFunction client = new ClientService(server, username, GUI);
			
			System.out.println("Connected to server ");
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	
	}
	
}

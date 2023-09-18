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
			//Connect to the rmiregistry that is running on localhost
			Registry registry = LocateRegistry.getRegistry("localhost");
           
			//Retrieve the stub/proxy for the remote math object from the registry
			RMI remote = (RMI) registry.lookup("Server");
           
			//Call methods on the remote object as if it was a local object
			System.out.println("Connected to server ");
		}catch(Exception e) {
			e.printStackTrace();
		}
	
	}
	
}

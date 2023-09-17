//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Creates an instance of the RemoteMath class and
 * publishes it in the rmiregistry
 * 
 */
public class Server {

	public static void main(String[] args)  {
		
		try {
			
			RMI remote = new SRMI();
            
            //Publish the remote object's stub in the registry under the name "Compute"
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Compute", remote);
            
            System.out.println("Server ready");
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

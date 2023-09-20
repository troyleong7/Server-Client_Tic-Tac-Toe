//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{
	private static int port = 1099;
	public static void main(String[] args)  {
		try {
			Service server = new ServerService();
			Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("Server", server);
            System.out.println("Server is running.");
            
		} catch (Exception e) {
			System.out.println("Server problem.");
		}
		
	}

}

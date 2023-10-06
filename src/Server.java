//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{
	private static int port;
	private static String ip;
	public static void main(String[] args)  {
		try {
			ip = args[0];
			port = Integer.parseInt(args[1]);
			System.setProperty("java.rmi.server.hostname", ip);
			Service server = new ServerService();
			Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("Server", server);
            System.out.println("Server is running.");
            
		} catch (Exception e) {
			System.out.println("Server problem.");
		}
		
	}

}

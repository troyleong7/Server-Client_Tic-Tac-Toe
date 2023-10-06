//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{
	private static int port;
	private static String ip;
	
	public static void main(String[] args)  {
		try{
			ip = args[0];
			port = Integer.parseInt(args[1]);
			System.setProperty("java.rmi.server.hostname", ip);
		}
		catch(Exception e) {
			//Error for when invalid format is typed in
			System.err.println("Please enter ip address and port number in valid format. "
					+ "Format: java –jar Server.jar <ip> <port>. <port> has to be numbers only.");
			System.exit(0);
		}
		try {
			Service server = new ServerService();
			Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("Server", server);
            System.out.println("Server is running.");
            
		} catch (Exception e) {
			System.out.println("Server problem. Port might be in used, change another port number");
			System.exit(0);
		}
		
	}

}

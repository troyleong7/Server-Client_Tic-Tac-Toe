//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements RMI{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int port = 1098;
	
	protected Server() throws RemoteException {
		super();
	}

	@Override
	public void registerClient(Client client) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void joinChat(Client client) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String sendMessage(String message) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recieveMessage(String Message) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
public static void main(String[] args)  {
		
		try {
			
			Server server = new Server();
			java.rmi.registry.LocateRegistry.createRegistry(port);
            java.rmi.Naming.rebind("Server", server);
            System.out.println("Chat server is running.");
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

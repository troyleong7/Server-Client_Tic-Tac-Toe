//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Server side implementation of the remote interface.
 * Must extend UnicastRemoteObject, to allow the JVM to create a 
 * remote proxy/stub.
 *
 */
public class SRMI extends UnicastRemoteObject implements RMI {
	private Queue<Client> waitingClients = new LinkedList<>();
    private Queue<Client> pairedClients = new LinkedList<>();

	/**
	 * Added 
	 */
	//private static final long serialVersionUID = 1L;
	
	protected SRMI() throws RemoteException {
	}

	@Override
	
	public String sendMessage(String message) throws RemoteException {
		return message;
	}
	public String recieveMessage(String message) throws RemoteException {
		return message;
	}

	@Override
	public synchronized void registerClient(Client client) throws RemoteException {
        
    }

	@Override
	public void joinChat(Client client) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
}
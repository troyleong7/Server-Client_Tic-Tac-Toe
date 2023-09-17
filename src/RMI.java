//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Remote interface - must be shared between client and server.
 * All methods must throw RemoteException.
 * All parameters and return types must be either primitives or Serializable.
 *  
 * Any object that is a remote object must implement this interface.
 * Only those methods specified in a "remote interface" are available remotely.
 */
public interface RMI extends Remote {
	public void registerClient(Client client) throws RemoteException;
    public void joinChat(Client client) throws RemoteException;
	public String sendMessage(String message) throws RemoteException;
	public String recieveMessage(String Message) throws RemoteException;
	
}
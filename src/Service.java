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
public interface Service extends Remote {

	void registerClient(ClientFunction client) throws RemoteException;

	void unregister(ClientFunction client) throws RemoteException;

	void sendMessage(String username, String message) throws RemoteException;
	
	void randomAssign(ClientFunction client1, ClientFunction client2) throws RemoteException;
	
	int isGameOver(TicTacToe tictactoe) throws RemoteException;

	void sendBoardState(String username, char[][] board) throws RemoteException;

	void announceWinner(String username, char[][] board) throws RemoteException;

	void drawGame(String username, char[][] board) throws RemoteException;
 
}
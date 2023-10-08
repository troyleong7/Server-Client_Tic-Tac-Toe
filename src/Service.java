//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {
	void registerClient(ClientFunction client) throws RemoteException;
	void sendMessage(ClientFunction client, String message) throws RemoteException;
	void randomAssign(ClientFunction client1, ClientFunction client2) throws RemoteException;
	int isGameOver(TicTacToe tictactoe) throws RemoteException;
	void sendBoardState(ClientFunction client, char[][] board) throws RemoteException;
	void announceWinner(ClientFunction client, char[][] board) throws RemoteException;
	void drawGame(ClientFunction client, char[][] board) throws RemoteException;
	void unregister(ClientFunction client) throws RemoteException;
	void forfeitGame(ClientFunction client) throws RemoteException;
	void newGame(ClientFunction client) throws RemoteException;
	void informOpponent(ClientFunction newClient) throws RemoteException;
	int crashNotify() throws RemoteException;
	void logIn(ClientFunction newClient) throws RemoteException;
	void reconnectBoardState(ClientFunction client, char[][] board, char currentPlayer, boolean disTurn) throws RemoteException;
	void removeWaiting(String opponent) throws RemoteException;
	void clientStatus() throws RemoteException;
 
}
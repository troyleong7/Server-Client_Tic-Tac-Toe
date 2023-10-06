import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientFunction extends Remote{
	String getUsername() throws RemoteException;
	String getPartnerName() throws RemoteException;
    void receiveMessage(String message) throws RemoteException;
    ClientFunction getPartner() throws RemoteException;
    void setPartner(ClientFunction partner) throws RemoteException;
	void playerFound() throws RemoteException;
	void startMove(boolean move) throws RemoteException;
	void receiveBoardState(char[][] board) throws RemoteException;
	void assignSymb(char symb) throws RemoteException;
	void receiveWinner(String username) throws RemoteException;
	void receiveDraw() throws RemoteException;
	void newGame() throws RemoteException;
	void serverCrash() throws RemoteException;
	void setPoint(int point) throws RemoteException;
	int getPoint() throws RemoteException;
	void setRanking(int rank) throws RemoteException;
	void setPartnerRanking(int rank) throws RemoteException;
	void waitReconnect(boolean turn) throws RemoteException;
	void receiveReconnect() throws RemoteException;
	boolean getTurn() throws RemoteException;
	int isAlive() throws RemoteException;
	boolean gameStart() throws RemoteException;;
}

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientFunction extends Remote{
	String getUsername() throws RemoteException;
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
}

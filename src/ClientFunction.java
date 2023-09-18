import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientFunction extends Remote{
	String getUsername() throws RemoteException;
    void receiveMessage(String message) throws RemoteException;
    ClientFunction getPartner() throws RemoteException;
    void setPartner(ClientFunction partner) throws RemoteException;
	void playerFound() throws RemoteException;
}

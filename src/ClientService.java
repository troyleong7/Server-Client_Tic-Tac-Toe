import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientService extends UnicastRemoteObject implements ClientFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ClientGUI GUI;
	public Service server;
	public String username;
	public ClientFunction partner;
	
	protected ClientService(Service server, String username, ClientGUI GUI) throws RemoteException {
		this.server = server;
		this.username = username;
		this.GUI = GUI;
		server.registerClient(this);
	}
	
	
	@Override
	public String getUsername() throws RemoteException {
		return username;
	}


	@Override
	public void receiveMessage(String message) throws RemoteException {
		GUI.showMessage(partner.getUsername(), message);
	}
	
	@Override
	public ClientFunction getPartner() throws RemoteException {
		return partner;
	}
	
	@Override
	public void setPartner(ClientFunction partner) throws RemoteException {
		this.partner = partner;
	}

	@Override
	public void playerFound() throws RemoteException{
		GUI.startTimer();
	}
	

}

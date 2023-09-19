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
	public boolean yourTurn;
	
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
		GUI.getPartner(partner.getUsername());
	}

	@Override
	public void playerFound() throws RemoteException{
		GUI.startTimer();
	}


	@Override
	public void startMove(boolean move) throws RemoteException {
		GUI.turn(move);
	}


	@Override
	public void receiveBoardState(char[][] board) throws RemoteException {
		GUI.updateBoard(board);
	}


	@Override
	public void assignSymb(char symb) throws RemoteException {
		GUI.getSymbol(symb);
	}
	

}

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
	
	protected ClientService(Service server, String username) throws RemoteException {
		this.server = server;
		this.username = username;
		GUI = new ClientGUI(username, server);
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
		if(partner != null) {
			GUI.getPartner(partner.getUsername());
		}
		else {
			GUI.getPartner(null);
		}
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


	@Override
	public void receiveWinner(String username) throws RemoteException {
		GUI.announceWinner(username);
		GUI.showOption();
	}


	@Override
	public void receiveDraw() throws RemoteException {
		GUI.announceDraw();
		GUI.showOption();
	}
	
	@Override
	public void newGame() throws RemoteException {
		this.partner = null;
		GUI.getPartner(null);
		GUI.resetGUI();
	}
	

}

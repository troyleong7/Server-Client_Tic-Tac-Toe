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
	public int points;
	
	protected ClientService(Service server, String username) throws RemoteException {
		this.server = server;
		this.username = username;
		server.logIn(this);
		GUI = new ClientGUI(username, server);
		server.registerClient(this);
		System.out.println(points);
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


	@Override
	public void serverCrash() throws RemoteException {
		GUI.serverCrash();
	}
	
	public void setPoint(int point) throws RemoteException {
		if(point <= 0) {
			this.points = 0;
		}
		else {
			this.points = point;
		}
	}
	
	public int getPoint() throws RemoteException {
		return points;
	}

}

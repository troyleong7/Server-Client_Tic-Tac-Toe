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
	public String partnerName;
	public boolean yourTurn;
	public int points;
	public int ranking;
	public int partnerRanking;
	public boolean gameStart;
	
	protected ClientService(Service server, String username) throws RemoteException {
		this.server = server;
		this.username = username;
		server.logIn(this);
		GUI = new ClientGUI(this, username, server);
		server.registerClient(this);
	}
	
	
	@Override
	public String getUsername() throws RemoteException {
		return username;
	}
	
	@Override
	public String getPartnerName() throws RemoteException {
		return partnerName;
	}
	
	@Override
	public boolean getTurn() throws RemoteException {
		return yourTurn;
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
			this.gameStart = true;
		}
		else {
			GUI.getPartner(null);
			this.gameStart = false;
		}
		this.partnerName = partner.getUsername();
	}

	@Override
	public void playerFound() throws RemoteException{
		GUI.startTimer();
	}


	@Override
	public void startMove(boolean move) throws RemoteException {
		GUI.turn(move);
		this.yourTurn = move;
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
		this.gameStart = false;
		GUI.announceWinner(username);
		GUI.showOption();
	}


	@Override
	public void receiveDraw() throws RemoteException {
		this.gameStart = false;
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
	
	@Override
	public void setPoint(int point) throws RemoteException {
		if(point <= 0) {
			this.points = 0;
		}
		else {
			this.points = point;
		}
	}
	
	@Override
	public int getPoint() throws RemoteException {
		return points;
	}
	
	@Override
	public void setRanking(int rank) throws RemoteException {
		this.ranking = rank;
		GUI.getRanking(rank);
	}
	
	@Override
	public void setPartnerRanking(int rank) throws RemoteException {
		this.partnerRanking = rank;
		GUI.getPartnerRanking(rank);
	}


	@Override
	public void waitReconnect(boolean turn) throws RemoteException {
		GUI.waitReconnect(turn);
	}


	@Override
	public void receiveReconnect() throws RemoteException {
		GUI.receiveReconnect();
		
	}

	@Override
	public int isAlive() throws RemoteException {
		return 1;
	}
	
	@Override
	public boolean gameStart() throws RemoteException {
		return gameStart;
	}
}

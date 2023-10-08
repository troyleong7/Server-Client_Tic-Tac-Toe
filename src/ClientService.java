//Name: Yun Keng Leong	StudentID: 1133704

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
	public ClientFunction opponent;
	public String opponentName;
	public boolean yourTurn;
	public int points;
	public int ranking;
	public int opponentRanking;
	public boolean gameStart;
	
	protected ClientService(Service server, String username) throws RemoteException {
		this.server = server;
		this.username = username;
		server.logIn(this);
		GUI = new ClientGUI(this, username, server);
		server.registerClient(this);
	}
	
	// Get client username
	@Override
	public String getUsername() throws RemoteException {
		return username;
	}
	
	// Get opponent username
	@Override
	public String getOpponentName() throws RemoteException {
		return opponentName;
	}
	
	// Get client's current turn
	@Override
	public boolean getTurn() throws RemoteException {
		return yourTurn;
	}

	// Receive message 
	@Override
	public void receiveMessage(String message) throws RemoteException {
		GUI.showMessage(opponent.getUsername(), message);
	}
	
	// Get opponent
	@Override
	public ClientFunction getOpponent() throws RemoteException {
		return opponent;
	}
	
	// Set opponent
	@Override
	public void setOpponent(ClientFunction opponent) throws RemoteException {
		this.opponent = opponent;
		if(opponent != null) {
			GUI.getOpponent(opponent.getUsername());
			this.gameStart = true;
		}
		else {
			GUI.getOpponent(null);
			this.gameStart = false;
		}
		this.opponentName = opponent.getUsername();
	}

	// When opponent is found
	@Override
	public void playerFound() throws RemoteException{
		GUI.startTimer();
	}

	// Client's turn 
	@Override
	public void startMove(boolean move) throws RemoteException {
		GUI.turn(move);
		this.yourTurn = move;
	}

	// Receive board state from opponent
	@Override
	public void receiveBoardState(char[][] board) throws RemoteException {
		GUI.updateBoard(board);
	}

	// Assigning client's symbol
	@Override
	public void assignSymb(char symb) throws RemoteException {
		GUI.getSymbol(symb);
	}

	// Receive the winner announcement
	@Override
	public void receiveWinner(String username) throws RemoteException {
		this.gameStart = false;
		GUI.announceWinner(username);
		GUI.showOption();
	}

	// Receive the draw announcement
	@Override
	public void receiveDraw() throws RemoteException {
		this.gameStart = false;
		GUI.announceDraw();
		GUI.showOption();
	}
	
	// Start new game
	@Override
	public void newGame() throws RemoteException {
		this.opponent = null;
		GUI.getOpponent(null);
		GUI.resetGUI();
	}

	// Server crash notify
	@Override
	public void serverCrash() throws RemoteException {
		GUI.serverCrash();
	}
	
	// Set points
	@Override
	public void setPoint(int point) throws RemoteException {
		if(point <= 0) {
			this.points = 0;
		}
		else {
			this.points = point;
		}
	}
	
	// Get points
	@Override
	public int getPoint() throws RemoteException {
		return points;
	}
	
	// Set the ranking
	@Override
	public void setRanking(int rank) throws RemoteException {
		this.ranking = rank;
		GUI.getRanking(rank);
	}
	
	// get the opponent's ranking
	@Override
	public void setOpponentRanking(int rank) throws RemoteException {
		this.opponentRanking = rank;
		GUI.getOpponentRanking(rank);
	}

	// Waiting for opponent to reconnect
	@Override
	public void waitReconnect(boolean turn) throws RemoteException {
		GUI.waitReconnect(turn);
	}

	// Opponent reconnected notify
	@Override
	public void receiveReconnect() throws RemoteException {
		GUI.receiveReconnect();
		
	}
	
	// Update client's status
	@Override
	public int isAlive() throws RemoteException {
		return 1;
	}
	
	// Check if the game started
	@Override
	public boolean gameStart() throws RemoteException {
		return gameStart;
	}
}

//Name: Yun Keng Leong	StudentID: 1133704

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;

public class ServerService extends UnicastRemoteObject implements Service {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static LinkedHashMap<String, Integer> ranks;
	private static Map<String, Integer> clients;
	private List<String> disconnectedClients;
	private List<ClientFunction> activeClients;
    private Queue<ClientFunction> waitingClients;
    
	protected ServerService() throws RemoteException {
		super();
		ranks = new  LinkedHashMap<String, Integer>();
		clients = new HashMap<String, Integer>();
		disconnectedClients = new ArrayList<>();
		activeClients = new ArrayList<>();
        waitingClients = new ConcurrentLinkedQueue<>();
	}
	
	// To see if the server crashed
	@Override
	public int crashNotify() throws RemoteException{
        return 1;
    }
	
	// Client connect and log in to server function
	@Override
	public synchronized void logIn(ClientFunction newClient) throws RemoteException {
		String name = newClient.getUsername();
		System.out.println("Player " + name + " Connected!");
		if (clients.isEmpty()){
			newClient.setPoint(0);
			clients.put(name, 0);
		} 
		else {
			boolean old = false;
			int oldPoints = 0;
			for (String i : clients.keySet()){
				if(i.equals(name)) {
					old = true;
				}
			}
			if(old) {
				oldPoints = clients.get(name);
				newClient.setPoint(oldPoints);
			}
			else {
				clients.put(name, 0);
			}
		}
		System.out.println(clients);
	}
	
	// Client reconnect to server 
	private void reconnect(ClientFunction newClient) throws RemoteException {
		for(ClientFunction opponent : activeClients) {
			if(opponent.getOpponentName().equals(newClient.getUsername())) {
				try {
					ranks = sortRanking(clients);
					opponent.setOpponent(newClient);
					newClient.setOpponent(opponent);
			        newClient.setRanking(ranks.get(newClient.getUsername()));
			        opponent.setRanking(ranks.get(opponent.getUsername()));
			        newClient.setOpponentRanking(ranks.get(opponent.getUsername()));
			        opponent.setOpponentRanking(ranks.get(newClient.getUsername()));
			        opponent.receiveReconnect();
				} catch (RemoteException e) {
					unregister(opponent);
					unregister(newClient);
					System.out.println("Error in reconnect function");
				}
			}
		}
		
	}
	
	// Register the client to active player list
	@Override
	public synchronized void registerClient(ClientFunction client) throws RemoteException {
		boolean dc;
		dc = disconnectedClients.remove(client.getUsername());
		if(dc) {
			reconnect(client);
			activeClients.add(client);
		}
		else {
			activeClients.add(client);
			if (waitingClients.isEmpty()) {
	            waitingClients.add(client);
	        } else {
	        	ClientFunction opponent = waitingClients.poll();
	            pair(client, opponent);
	        }
		}
		
		
	}
	
	// Unregister the client in active player list
	@Override
    public synchronized void unregister(ClientFunction client) throws RemoteException {
		try {
			activeClients.remove(client);
			if(!waitingClients.isEmpty()) {
				 if(waitingClients.peek().equals(client)){
					 waitingClients.remove(client);
				 }
			}
		} catch (Exception e) {
			System.out.println("error in unregister");
		}
	}
	
	// Pair two clients
	private void pair(ClientFunction client1, ClientFunction client2) throws RemoteException {
        try {
        	ranks = sortRanking(clients);
            client1.setOpponent(client2);
            client2.setOpponent(client1);
            client1.setRanking(ranks.get(client1.getUsername()));
            client2.setRanking(ranks.get(client2.getUsername()));
            client1.setOpponentRanking(ranks.get(client2.getUsername()));
            client2.setOpponentRanking(ranks.get(client1.getUsername()));
            randomAssign(client1, client2);
        } catch (RemoteException e) {
        	unregister(client1);
        	unregister(client2);
        	System.out.println("Error in pair function: one of the client not found (" 
        			+ client1.getUsername() + " " + client2.getUsername() + ")");
        	registerClient(client1);
        }
    }

	// Random assign symbols to client and which client start first
	@Override
	public void randomAssign(ClientFunction client1, ClientFunction client2) throws RemoteException {
		Random random = new Random();
        int randomNumber = random.nextInt(2);
        if(randomNumber == 0) {
        	client1.assignSymb('X');
        	client2.assignSymb('O');
        	client1.startMove(true);
        	client2.startMove(false);
        }
        else {
        	client2.assignSymb('X');
        	client1.assignSymb('O');
        	client2.startMove(true);
        	client1.startMove(false);
        }
	}
	
	// Check winning condition
	@Override
	public int isGameOver(TicTacToe tictactoe) throws RemoteException {
		char[][] board = tictactoe.board;
		char currentPlayer = tictactoe.currentPlayer;
        // Check for a win condition
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return 1; // Horizontal win
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return 1; // Vertical win
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return 1; // Diagonal win (top-left to bottom-right)
        }

        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return 1; // Diagonal win (top-right to bottom-left)
        }

        // Check for a draw
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return 0; // There are still empty spaces
                }
            }
        }
        
        return 2;
    }

	// Client send message to opponent, server handles the transmission 
	@Override
	public void sendMessage(ClientFunction client, String message) throws RemoteException {
			try {
				client.getOpponent().receiveMessage(message);
			} catch (RemoteException e) {
				System.out.println("Error in sendMessage function: one of the client disconnected(" 
						+ client.getUsername() + " " + client.getOpponentName() + ")");
			}
		}
	
	// Server update opponent the clients board state
	@Override
	public void sendBoardState(ClientFunction client, char[][] board) throws RemoteException {
		client.startMove(false);
		try {
			client.getOpponent().receiveBoardState(board);
			client.getOpponent().startMove(true);
		} catch (RemoteException e) {
			System.out.println("Error in sendBoardState function: one of the client disconnected(" 
					+ client.getUsername() + " " + client.getOpponentName() + ")");
		}
	}
	
	// Update board state to reconnected client
	@Override
	public void reconnectBoardState(ClientFunction client, char[][] board, char currentSymb, boolean disTurn) throws RemoteException {
		try {
			client.getOpponent().receiveBoardState(board);
			if(currentSymb == 'X') {
				client.getOpponent().assignSymb('O');
			}else {
				client.getOpponent().assignSymb('X');
			}
			client.getOpponent().startMove(!disTurn);
        	client.startMove(disTurn);
		} catch (RemoteException e) {
			unregister(client.getOpponent());
			System.out.println("Error in reconnectBoardState function: one of the client disconnected(" 
					+ client.getUsername() + " " + client.getOpponentName() + ")");
		}
	}
	
	// Announcing the winner to clients
	@Override
	public void announceWinner(ClientFunction client, char[][] board) throws RemoteException {
		try {
			client.getOpponent().receiveBoardState(board);
			client.startMove(false);
			client.getOpponent().startMove(false);
			client.setPoint(client.getPoint()+ 5);
			client.getOpponent().setPoint(client.getPoint() - 5);
			changeScore(client.getUsername(), 5);
			changeScore(client.getOpponent().getUsername(), (-5));
			client.receiveWinner(client.getUsername());
			client.getOpponent().receiveWinner(client.getUsername());
			ranks = sortRanking(clients);
		} catch (RemoteException e) {
			unregister(client.getOpponent());
			client.startMove(false);
			client.setPoint(client.getPoint()+ 5);
			changeScore(client.getUsername(), 5);
			changeScore(client.getOpponentName(), (-5));
			client.receiveWinner(client.getUsername());
			ranks = sortRanking(clients);
			System.out.println("Error in announceWinner function: one of the client disconnected(" 
					+ client.getUsername() + " " + client.getOpponentName() + ")");
		}
		System.out.println(clients);
	}
	
	// Announcing game drawn to clients
	@Override
	public void drawGame(ClientFunction client, char[][] board) throws RemoteException {
		try {
			client.getOpponent().receiveBoardState(board);
			client.startMove(false);
			client.getOpponent().startMove(false);
			client.setPoint(client.getPoint()+ 2);
			client.getOpponent().setPoint(client.getPoint() + 2);
			changeScore(client.getUsername(), 2);
			changeScore(client.getOpponentName(), 2);
			client.getOpponent().receiveDraw();
			client.receiveDraw();
			ranks = sortRanking(clients);
		} catch (RemoteException e) {
			unregister(client.getOpponent());
			client.startMove(false);
			client.setPoint(client.getPoint()+ 2);
			changeScore(client.getUsername(), 2);
			changeScore(client.getOpponentName(), 2);
			client.receiveDraw();
			ranks = sortRanking(clients);
		}
		System.out.println(clients);
	}
	
	// Inform opponent game client forfeit the game
	@Override
	public void forfeitGame(ClientFunction client) throws RemoteException {
		client.startMove(false);
		if(client.getOpponent() != null) {
			try {
				client.setPoint(client.getPoint() - 5);
				client.getOpponent().setPoint(client.getPoint() + 5);
				changeScore(client.getUsername(), (-5));
				changeScore(client.getOpponentName(), 5);
				informOpponent(client);
				client.receiveWinner(client.getOpponentName());
				client.getOpponent().startMove(false);
				client.getOpponent().receiveWinner(client.getOpponentName());
				ranks = sortRanking(clients);
			} catch (RemoteException e) {
				unregister(client.getOpponent());
				client.setPoint(client.getPoint() - 5);
				changeScore(client.getUsername(), (-5));
				changeScore(client.getOpponentName(), 5);
				client.receiveWinner(client.getOpponentName());
				ranks = sortRanking(clients);
				System.out.println("Error in forfeitGame function: one of the client disconnected(" 
						+ client.getUsername() + " " + client.getOpponentName() + ")");
			}
		}
		System.out.println(clients);
	}
	
	// Start new game
	@Override
	public void newGame(ClientFunction client) throws RemoteException {
		try {
			informOpponent(client);
			client.newGame();
			unregister(client);
			registerClient(client);		
		} catch (Exception e){
			System.out.println("Error in newGame function");
		}
	}
	
	// Inform opponent client left
	@Override
	public void informOpponent(ClientFunction client) throws RemoteException {
		ClientFunction opponent = client.getOpponent();
		try {
			if(opponent != null) {
				opponent.receiveMessage("Disconnected!");
				opponent.setOpponent(null);
			}
		}catch (Exception e){
			unregister(opponent);
		}
	}
	
	// update the point scoreboard
	private void changeScore(String username, int score) {
		int currentScore = clients.get(username);
		if(score < 0){
			if(currentScore >= 5) {
				clients.put(username, currentScore + score);
			}else {
				clients.put(username, 0);
			}
		}
		else {
			clients.put(username, currentScore + score);
		}
	}
	
	// Sort the ranking of each client 
	private LinkedHashMap<String, Integer> sortRanking(Map<String, Integer> hm) {
		List<Map.Entry<String, Integer>> list = new ArrayList<>(hm.entrySet());

        // Define a custom Comparator to sort by values in reverse order
        Comparator<Map.Entry<String, Integer>> valueComparator = (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue());

        // Sort the list based on the custom Comparator
        Collections.sort(list, valueComparator);

        // Create a new LinkedHashMap to store the sorted entries
        LinkedHashMap<String, Integer> sortedHashMap = new LinkedHashMap<>();

        // Iterate through the sorted list of entries and put them into the LinkedHashMap
        int rank = 0;
        for (Map.Entry<String, Integer> entry : list) {
        	rank++;
            sortedHashMap.put(entry.getKey(), rank);
        }
        
        return sortedHashMap;
	}

	// Remove client from waiting list
	@Override
	public void removeWaiting(String opponent) throws RemoteException {
		disconnectedClients.remove(opponent);
	}
	
	// Keep track of each clients status
	@Override
	public void clientStatus() throws RemoteException {
		List<ClientFunction> removeList = new ArrayList<>();;
		for(ClientFunction client : activeClients) {
			try {
				client.isAlive();
			}catch (Exception e){
				removeList.add(client);
			}
			
		}
		
		for(ClientFunction dcClient : removeList) {
			activeClients.remove(dcClient);
			waitingClients.remove(dcClient);
		}
		
		for(ClientFunction client : activeClients) {
			if(client.getOpponent()!= null && removeList.contains(client.getOpponent())) {
				try {
					opponentDown(client);
				}catch (Exception e){
					System.out.println("Error in clientStatus function");
				}
			}
		}	
	}
	
	// When the client disconnected function 
	public void opponentDown(ClientFunction client) throws RemoteException {
		System.out.println("Player " + client.getOpponentName() + " Disconnected!");
		if(client.gameStart()) {
			unregister(client.getOpponent());
			client.waitReconnect(client.getTurn());
			client.startMove(false);
			disconnectedClients.add(client.getOpponentName());
		}
	}

	
}


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
	
	@Override
	public int crashNotify() throws RemoteException{
        return 1;
    }
	
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
	
	private void reconnect(ClientFunction newClient) throws RemoteException {
		for(ClientFunction partner : activeClients) {
			if(partner.getPartnerName().equals(newClient.getUsername())) {
				try {
					ranks = sortRanking(clients);
					partner.setPartner(newClient);
					newClient.setPartner(partner);
			        newClient.setRanking(ranks.get(newClient.getUsername()));
			        partner.setRanking(ranks.get(partner.getUsername()));
			        newClient.setPartnerRanking(ranks.get(partner.getUsername()));
			        partner.setPartnerRanking(ranks.get(newClient.getUsername()));
			        partner.receiveReconnect();
				} catch (RemoteException e) {
					unregister(partner);
					unregister(newClient);
					System.out.println("Error in reconnect function");
				}
			}
		}
		
	}

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
	        	ClientFunction partner = waitingClients.poll();
	            pair(client, partner);
	        }
		}
		
		
	}
	
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
	
	private void pair(ClientFunction client1, ClientFunction client2) throws RemoteException {
        try {
        	ranks = sortRanking(clients);
            client1.setPartner(client2);
            client2.setPartner(client1);
            client1.setRanking(ranks.get(client1.getUsername()));
            client2.setRanking(ranks.get(client2.getUsername()));
            client1.setPartnerRanking(ranks.get(client2.getUsername()));
            client2.setPartnerRanking(ranks.get(client1.getUsername()));
            randomAssign(client1, client2);
        } catch (RemoteException e) {
        	unregister(client1);
        	unregister(client2);
        	System.out.println("Error in pair function: one of the client not found (" 
        			+ client1.getUsername() + " " + client2.getUsername() + ")");
        	registerClient(client1);
        }
    }

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

	
	@Override
	public void sendMessage(ClientFunction client, String message) throws RemoteException {
			try {
				client.getPartner().receiveMessage(message);
			} catch (RemoteException e) {
				System.out.println("Error in sendMessage function: one of the client disconnected(" 
						+ client.getUsername() + " " + client.getPartnerName() + ")");
			}
		}
	
	@Override
	public void sendBoardState(ClientFunction client, char[][] board) throws RemoteException {
		client.startMove(false);
		try {
			client.getPartner().receiveBoardState(board);
			client.getPartner().startMove(true);
		} catch (RemoteException e) {
			System.out.println("Error in sendBoardState function: one of the client disconnected(" 
					+ client.getUsername() + " " + client.getPartnerName() + ")");
		}
	}
	
	@Override
	public void reconnectBoardState(ClientFunction client, char[][] board, char currentSymb, boolean disTurn) throws RemoteException {
		try {
			client.getPartner().receiveBoardState(board);
			if(currentSymb == 'X') {
				client.getPartner().assignSymb('O');
			}else {
				client.getPartner().assignSymb('X');
			}
			client.getPartner().startMove(!disTurn);
        	client.startMove(disTurn);
		} catch (RemoteException e) {
			unregister(client.getPartner());
			System.out.println("Error in reconnectBoardState function: one of the client disconnected(" 
					+ client.getUsername() + " " + client.getPartnerName() + ")");
		}
	}

	@Override
	public void announceWinner(ClientFunction client, char[][] board) throws RemoteException {
		try {
			client.getPartner().receiveBoardState(board);
			client.startMove(false);
			client.getPartner().startMove(false);
			client.setPoint(client.getPoint()+ 5);
			client.getPartner().setPoint(client.getPoint() - 5);
			changeScore(client.getUsername(), 5);
			changeScore(client.getPartner().getUsername(), (-5));
			client.receiveWinner(client.getUsername());
			client.getPartner().receiveWinner(client.getUsername());
			ranks = sortRanking(clients);
		} catch (RemoteException e) {
			unregister(client.getPartner());
			client.startMove(false);
			client.setPoint(client.getPoint()+ 5);
			changeScore(client.getUsername(), 5);
			changeScore(client.getPartnerName(), (-5));
			client.receiveWinner(client.getUsername());
			ranks = sortRanking(clients);
			System.out.println("Error in announceWinner function: one of the client disconnected(" 
					+ client.getUsername() + " " + client.getPartnerName() + ")");
		}
		System.out.println(clients);
	}

	@Override
	public void drawGame(ClientFunction client, char[][] board) throws RemoteException {
		try {
			client.getPartner().receiveBoardState(board);
			client.startMove(false);
			client.getPartner().startMove(false);
			client.setPoint(client.getPoint()+ 2);
			client.getPartner().setPoint(client.getPoint() + 2);
			changeScore(client.getUsername(), 2);
			changeScore(client.getPartnerName(), 2);
			client.getPartner().receiveDraw();
			client.receiveDraw();
			ranks = sortRanking(clients);
		} catch (RemoteException e) {
			unregister(client.getPartner());
			client.startMove(false);
			client.setPoint(client.getPoint()+ 2);
			changeScore(client.getUsername(), 2);
			changeScore(client.getPartnerName(), 2);
			client.receiveDraw();
			ranks = sortRanking(clients);
		}
		System.out.println(clients);
	}
	

	@Override
	public void forfeitGame(ClientFunction client) throws RemoteException {
		client.startMove(false);
		if(client.getPartner() != null) {
			try {
				client.setPoint(client.getPoint() - 5);
				client.getPartner().setPoint(client.getPoint() + 5);
				changeScore(client.getUsername(), (-5));
				changeScore(client.getPartnerName(), 5);
				informPartner(client);
				client.receiveWinner(client.getPartnerName());
				client.getPartner().startMove(false);
				client.getPartner().receiveWinner(client.getPartnerName());
				ranks = sortRanking(clients);
			} catch (RemoteException e) {
				unregister(client.getPartner());
				client.setPoint(client.getPoint() - 5);
				changeScore(client.getUsername(), (-5));
				changeScore(client.getPartnerName(), 5);
				client.receiveWinner(client.getPartnerName());
				ranks = sortRanking(clients);
				System.out.println("Error in forfeitGame function: one of the client disconnected(" 
						+ client.getUsername() + " " + client.getPartnerName() + ")");
			}
		}
		System.out.println(clients);
	}
	

	@Override
	public void newGame(ClientFunction client) throws RemoteException {
		try {
			informPartner(client);
			client.newGame();
			unregister(client);
			registerClient(client);		
		} catch (Exception e){
			System.out.println("Error in newGame function");
		}
	}
	
	@Override
	public void informPartner(ClientFunction client) throws RemoteException {
		ClientFunction partner = client.getPartner();
		try {
			if(partner != null) {
				partner.receiveMessage("Disconnected!");
				partner.setPartner(null);
			}
		}catch (Exception e){
			unregister(partner);
		}
	}
	
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

	@Override
	public void removeWaiting(String partner) throws RemoteException {
		disconnectedClients.remove(partner);
	}

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
			if(client.getPartner()!= null && removeList.contains(client.getPartner())) {
				try {
					partnerDown(client);
				}catch (Exception e){
					System.out.println("Error in clientStatus function");
				}
			}
		}	
	}
	
	public void partnerDown(ClientFunction client) throws RemoteException {
		System.out.println("Player " + client.getPartnerName() + " Disconnected!");
		if(client.gameStart()) {
			unregister(client.getPartner());
			client.waitReconnect(client.getTurn());
			client.startMove(false);
			disconnectedClients.add(client.getPartnerName());
		}
	}

	
}

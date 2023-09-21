
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
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
	private static Map<String, Integer> clients;
	private List<ClientFunction> activeClients;
    private Queue<ClientFunction> waitingClients;
    
	protected ServerService() throws RemoteException {
		super();
		clients = new HashMap<String, Integer>();
		activeClients = new ArrayList<>();
        waitingClients = new ConcurrentLinkedQueue<>();
	}
	
	public int crashNotify() throws RemoteException{
        return 1;
    }
	
	@Override
	public synchronized void logIn(ClientFunction newClient) throws RemoteException {
		if (clients.isEmpty()){
			newClient.setPoint(0);
			int point = newClient.getPoint();
			String name = newClient.getUsername();
			clients.put(name, point);
		} 
		else {
			boolean old = false;
			int oldPoints = 0;
			for (String i : clients.keySet()){
				if(i.equals(newClient.getUsername())) {
					old = true;
				}
			}
			if(old) {
				oldPoints = clients.get(newClient.getUsername());
				newClient.setPoint(oldPoints);
			}
			else {
				String name = newClient.getUsername();
				clients.put(name, 0);
			}
			System.out.println(clients);
		}
	}
	@Override
	public synchronized void registerClient(ClientFunction client) throws RemoteException {
		activeClients.add(client);
		if (waitingClients.isEmpty()) {
            waitingClients.add(client);
        } else {
        	ClientFunction partner = waitingClients.poll();
            pair(client, partner);
        }
		
	}
	
	@Override
    public synchronized void unregister(String username) throws RemoteException {
		for (int i = 0; i < activeClients.size(); i++){
			if(activeClients.get(i).getUsername().equals(username)) {
				ClientFunction removeClient = activeClients.get(i);
				activeClients.remove(removeClient);
				if(waitingClients.peek() == removeClient) {
					waitingClients.remove(removeClient);
				}
				break;
			}
		}
    }


	@Override
	public void sendMessage(String username, String message) throws RemoteException {
        for (ClientFunction client: activeClients) {
			try {
				if(client.getUsername().equals(username)) {
					client.getPartner().receiveMessage(message);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	
	
	private void pair(ClientFunction client1, ClientFunction client2) {
        try {
            client1.setPartner(client2);
            client2.setPartner(client1);
            randomAssign(client1, client2);
        } catch (RemoteException e) {
            e.printStackTrace();
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
	public void sendBoardState(String username, char[][] board) throws RemoteException {
		for (ClientFunction client: activeClients) {
			try {
				if(client.getUsername().equals(username)) {
					client.getPartner().receiveBoardState(board);
					client.startMove(false);
					client.getPartner().startMove(true);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void announceWinner(String username, char[][] board) throws RemoteException {
		for (ClientFunction client: activeClients) {
			try {
				if(client.getUsername().equals(username)) {
					client.getPartner().receiveBoardState(board);
					client.startMove(false);
					client.getPartner().startMove(false);
					client.setPoint(client.getPoint()+ 5);
					client.getPartner().setPoint(client.getPoint() - 5);
					changeScore(client.getUsername(), 5);
					changeScore(client.getPartner().getUsername(), (-5));
					client.receiveWinner(username);
					client.getPartner().receiveWinner(username);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void drawGame(String username, char[][] board) {
		for (ClientFunction client: activeClients) {
			try {
				if(client.getUsername().equals(username)) {
					client.getPartner().receiveBoardState(board);
					client.startMove(false);
					client.getPartner().startMove(false);
					client.getPartner().receiveDraw();
					client.receiveDraw();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void forfeitGame(String username) throws RemoteException {
		for (ClientFunction client: activeClients) {
			try {
				if(client.getUsername().equals(username)) {
					client.startMove(false);
					if(client.getPartner() != null) {
						client.setPoint(client.getPoint() - 5);
						client.getPartner().setPoint(client.getPoint() + 5);
						changeScore(client.getUsername(), (-5));
						changeScore(client.getPartner().getUsername(), 5);
						informPartner(username);
						client.receiveWinner(client.getPartner().getUsername());
						client.getPartner().startMove(false);
						client.getPartner().receiveWinner(client.getPartner().getUsername());
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void newGame(String username) throws RemoteException {
		for (int i = 0; i < activeClients.size(); i++){
			if(activeClients.get(i).getUsername().equals(username)) {
				ClientFunction renewClient = activeClients.get(i);
				informPartner(username);
				renewClient.newGame();
				unregister(username);
				registerClient(renewClient);
				break;
			}
		}
	}
	
	@Override
	public void informPartner(String username) throws RemoteException {
		for (int i = 0; i < activeClients.size(); i++){
			if(activeClients.get(i).getUsername().equals(username)) {
				ClientFunction partner = activeClients.get(i).getPartner();
				if(partner != null) {
					partner.receiveMessage("Disconnected!");
					partner.setPartner(null);
					break;
				}
			}
		}
	}
	
	private void changeScore(String username, int score) {
		int currentScore = clients.get(username);
		if(score < 0){
			if(currentScore >= 5) {
				clients.put(username, currentScore + score);
			}else {
				clients.put(username, currentScore);
			}
		}
		else {
			clients.put(username, currentScore + score);
		}
	}
}

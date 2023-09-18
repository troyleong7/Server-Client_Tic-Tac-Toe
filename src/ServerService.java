
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerService extends UnicastRemoteObject implements Service {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ClientFunction> activeClients;
    private Queue<ClientFunction> waitingClients;
    
	protected ServerService() throws RemoteException {
		super();
		activeClients = new ArrayList<>();
        waitingClients = new ConcurrentLinkedQueue<>();
	}

	@Override
	public synchronized void registerClient(ClientFunction client) throws RemoteException {
		if (waitingClients.isEmpty()) {
            waitingClients.add(client);
        } else {
        	ClientFunction partner = waitingClients.poll();
            pair(client, partner);
            client.playerFound();
            partner.playerFound();
        }
		
	}
	
	@Override
    public synchronized void unregister(ClientFunction client) throws RemoteException {
        activeClients.remove(client);
    }


	@Override
	public void sendMessage(String username, String message) throws RemoteException {
        for (ClientFunction client: activeClients) {
			try {
				if(client.getUsername().equals(username)) {
					client.receiveMessage(message);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	
	
	private void pair(ClientFunction client1, ClientFunction client2) {
        try {
        	activeClients.add(client1);
            activeClients.add(client2);
            client1.setPartner(client2);
            client2.setPartner(client1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

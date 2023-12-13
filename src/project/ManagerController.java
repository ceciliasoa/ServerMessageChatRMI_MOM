package project;

import java.rmi.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import Message.MessageModel;

public class ManagerController implements RMIServerInterface {
	
	private static String SERVER_NAME = "ChatServer";
	private Registry registry = null;
	private MOMService momService = new MOMService();
    
    public void initialize() {
    	try {
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			Remote object = UnicastRemoteObject.exportObject(this, 0);
			registry.bind(SERVER_NAME, object);
			System.out.println("Valor encontrado: " + 2);
			momService.startConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void stop() {
		try {
			registry.unbind(SERVER_NAME);
			momService.stopConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getUserNames() {
		List<String> userNames = new ArrayList<>();
		try {
			userNames.addAll(momService.getExistingQueuesNames());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return userNames;
	}
	
	public int getUserMessageAmount(String userName) {
		int amount = 0;
		try {
			amount = momService.getAmountOfPendingMessagesFromQueue(userName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}

	public List<MessageModel> getMessages(String userName) {
		try {
			return momService.getPendingQueueMessage("sender");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void deleteUser(String userName) {
		try {
			momService.deleteQueue(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendRMIMessage(String sender, String receiver, String message) throws Exception {
		RMIClientInterface client = (RMIClientInterface) registry.lookup(receiver);
		System.out.print(sender);
		client.deliveryMessage(sender, message);
	}

	@Override
	public void connect(String userName) throws RemoteException {

		try {
			for (MessageModel message : momService.getPendingQueueMessage(userName)) {
				sendRMIMessage(message.getSender(), userName, message.getText());
			}

			momService.consumePendingQueueMessage(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void sendMessage(String sender, String receiver, String message) throws RemoteException {
			try {
				sendRMIMessage(sender, receiver, message);
			} catch (Exception e) {
				addToQueue(sender, receiver, message);
			}
	}
	
	private void addToQueue(String sender, String receiver, String message) throws RemoteException {
		try {
			momService.sendMessageToQueue(sender, receiver, message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Could not send message to offline client");
		}
	}
	

	
}

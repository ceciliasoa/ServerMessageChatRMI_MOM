package project;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {
	public void deliveryMessage(String sender, String message) throws RemoteException;
}

package project;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
	public void connect(String userName) throws RemoteException;

	public void sendMessage(String sender, String receiver, String message) throws RemoteException;

}

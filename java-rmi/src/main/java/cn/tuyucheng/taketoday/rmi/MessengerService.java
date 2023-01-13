package cn.tuyucheng.taketoday.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessengerService extends Remote {

	String sendMessage(String clientMessage) throws RemoteException;

	Message sendMessage(Message clientMessage) throws RemoteException;
}
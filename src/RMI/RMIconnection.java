package RMI;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIconnection extends Remote{
    public void connect() throws RemoteException, MalformedURLException, NotBoundException;
    public void stopWaiting() throws RemoteException;
}

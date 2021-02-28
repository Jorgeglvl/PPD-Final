package RMI;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

public class Mediador extends UnicastRemoteObject implements RMIconnection{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Registry registry;
    public RMIconnection partner;
    
    private String ip = "127.0.0.1";
    private int port = 9999;
    
    private boolean waiting = true;


	protected Mediador() throws RemoteException {		
	    
        try {
			System.out.println("Conectando ao servidor");
			registry = LocateRegistry.getRegistry(port);
			registry.bind("//"+ip+":"+port+"/Client",this);			
			System.out.println("Conectado");			
			System.out.println("Cliente Registrado!");
            this.partner = (RMIconnection)registry.lookup("//"+ip+":"+port+"/Server");
			System.out.println("Jogador 1 conectado");
			System.out.println();
			this.partner.connect();
			stopWaiting();
			this.partner.stopWaiting();
		}
		catch (ConnectException|AlreadyBoundException e) {
			try {
				System.out.println("Não há servidores disponíveis");				
				System.out.println("Registrando servidor");
				registry = LocateRegistry.createRegistry(port);
				registry.bind("//"+ip+":"+port+"/Server",this);				
				System.out.println("Servidor Registrado!");
				JOptionPane.showMessageDialog(null, "" + "Aguardando resposta da outra parte");
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, "" + e.getMessage());
				e2.printStackTrace();
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "" + e.getMessage());
			e.printStackTrace();
		}

		while(waiting) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "" + e.getMessage());
				e.printStackTrace();
			}
        }
	}

    @Override
	public void connect() throws MalformedURLException, RemoteException, NotBoundException {		
		this.partner = (RMIconnection)registry.lookup("//"+ip+":"+port+"/Client");
		System.out.println("Jogador 2 conectado");
    }
    
    @Override
	public void stopWaiting() throws RemoteException {
		waiting = false;
	}
    
    public static void main(String[] args) throws RemoteException {
    	Mediador mediador = new Mediador();
    }

}

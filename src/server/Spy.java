package server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import RMI.RemoteClient;
import RMI.RemoteServer;

public class Spy extends UnicastRemoteObject implements RemoteClient{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Registry registry;
	private RemoteServer server;
    
    private String ip = "127.0.0.1";
    private int port = 9999;
    private String nickname = "Espião";
        
    private SpyFrame spyFrame;


	protected Spy() throws RemoteException {
		
		try {
			registry = LocateRegistry.getRegistry(port);
			server = (RemoteServer)registry.lookup("//"+ip+":"+port+"/Servidor");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "NÃ£o foi possivel conectar com o servidor");
			e.printStackTrace();
			System.exit(0);
		}
		
		this.connect();
		spyFrame = new SpyFrame(this);
		this.recebeMensagem("Monitoramento", false);

	}
	
	public ArrayList<String> getSuspectList() {
		return spyFrame.getMessage_list();
	}
	
	public boolean connect(){
		
		try {
			
			if(this.nickname!=null) {
				int resposta = server.conectaUsuario(this);
				if(resposta==-1) {
					JOptionPane.showMessageDialog(null, "Client '"+nickname+"' ja está conectado");
				}
				else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean enviaMensagem(String nickname, String conteudoMsg, boolean tipoFila) {
		
		try {
			if(tipoFila) {
				if(!server.produzMensagemFila(nickname, conteudoMsg)) {
					JOptionPane.showMessageDialog(null, "Usuario nao existe");
					return false;
				}
			}
			else {
				if(!server.produzMensagemTopico(nickname, conteudoMsg)) {
					JOptionPane.showMessageDialog(null, "Topico nao existe");
					return false;
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public ArrayList<String> recebeMensagem(String nickname, boolean tipoFila) {
		
		try {
			if(tipoFila) {
				return server.recebeMensagemFila(nickname);
			}
			else {
				if(!server.assinaTopico(nickname, this.nickname)) {
//					já inscrito
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public void notificaMensagem() throws RemoteException {
		ArrayList<String> notificationsList = this.recebeMensagem("Monitoramento", false);
		System.out.println(notificationsList);
	}
	
	public void notificaDesconexao() throws RemoteException {
		JOptionPane.showMessageDialog(null, "Voce foi desconectado");
		System.exit(0);
	}
	
	public void setMensagemTopico(String mensagem) throws RemoteException {
		System.out.println("Spy: setMensagemTopico");
	}
	
	public ArrayList<String> getClients() {
		try {
			return server.getUsuariosOnline();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public ArrayList<String> getTopics() {
		try {
			return server.getTopicosDisponiveis();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public String getNome() throws RemoteException {
		return nickname;
	}
	
	public boolean notifySuspect(String message) {
		return this.enviaMensagem("Monitoramento", "Monitoramento"+"<Servidor: "+message, false);
	}

}

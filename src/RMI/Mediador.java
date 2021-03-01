package RMI;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import server.SpyFrame;

public class Mediador extends UnicastRemoteObject implements RemoteClient{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Registry registry;
	private RemoteServer server;
    
    private String ip = "127.0.0.1";
    private int port = 9999;
    private String nickname = "Mediador";

    
    MediadorFrame mediadorFrame;


	protected Mediador() throws RemoteException {		
	    
		
		try {
			registry = LocateRegistry.getRegistry(port);
			server = (RemoteServer)registry.lookup("//"+ip+":"+port+"/Servidor");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Não foi possivel conectar com o servidor");
			e.printStackTrace();
			System.exit(0);
		}
		
		this.connect();
		mediadorFrame = new MediadorFrame(this);	
		this.recebeMensagem("Monitoramento", false);
		notificaMensagem();
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
//					de boa
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public void notificaMensagem() throws RemoteException {
//		não é pra receber
	}
	
	public void notificaDesconexao() throws RemoteException {
		JOptionPane.showMessageDialog(null, "VocÃª foi desconectado");
		System.exit(0);
	}
	
	public void setMensagemTopico(String mensagem) throws RemoteException {
		mediadorFrame.addNotification(mensagem);
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
	
	public static void main(String[] args) throws RemoteException {
		Mediador mediador = new Mediador();
		}

}

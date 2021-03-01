package RMI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class Client extends UnicastRemoteObject implements RemoteClient {
	
	private static final long serialVersionUID = 1L;
	private Registry registro;
	private RemoteServer server;
//	private Home janela;
	private String host;
	private int porta;
	
	public String nickname;
	
	public Client(String ip, int port, String nickname) throws RemoteException{
//		this.janela = janela;
		
		host = ip;
		porta = port;
		this.nickname = nickname;
		
		try {
			registro = LocateRegistry.getRegistry(porta);
			server = (RemoteServer)registro.lookup("//"+host+":"+porta+"/Servidor");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Não foi possivel conectar com o servidor");
			e.printStackTrace();
			System.exit(0);
		}
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
					JOptionPane.showMessageDialog(null, "Você já está inscrito");
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public void notificaMensagem() throws RemoteException {
//		janela.recebeMensagensUsuarios();
	}
	
	public void notificaDesconexao() throws RemoteException {
		JOptionPane.showMessageDialog(null, "Você foi desconectado");
		System.exit(0);
	}
	
	public void setMensagemTopico(String mensagem) throws RemoteException {
//		janela.escreveMensagensTopico(mensagem);
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
}
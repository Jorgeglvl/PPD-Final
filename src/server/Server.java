package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import common.Utils;

public class Server {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 4444;

    private ServerSocket server;
    private Map<String, ClientListener> clients;
    private Spy spy;

	public Server() throws NotBoundException{
        try {
            String connection_info;
            clients = new HashMap<String, ClientListener>();
            server = new ServerSocket(PORT);
            spy = new Spy();
            JOptionPane.showMessageDialog(null,
            "Servidor iniciado! Use a janela do espião para determinas as mensagens suspeitas!");
            while(true){
                Socket connection = server.accept();
                connection_info = Utils.receiveMessage(connection);
                if(checkLogin(connection_info)){
                    ClientListener cl = new ClientListener(connection_info, connection, this);
                    clients.put(connection_info, cl);
                    Utils.sendMessage(connection, "SUCESS");
                    new Thread(cl).start();
                }else{
                    Utils.sendMessage(connection, "ERROR");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
            e.getMessage(),
            "Inane error",
            JOptionPane.ERROR_MESSAGE);
        }
    }

	public Map<String, ClientListener> getClients(){
        return clients;
    }
	
	public ArrayList<String> getSuspectList(){
		return spy.getSuspectList();
	}
	
	public void suspect(String message) throws RemoteException {
		System.out.println("Suspect -> Servidor");
		spy.notifySuspect();
	}

    private boolean checkLogin(String connection_info){
        String[] splited = connection_info.split(":");
        for(Map.Entry<String, ClientListener> pair: clients.entrySet()){
            String[] parts = pair.getKey().split(":");
            if(parts[0].toLowerCase().equals(splited[0].toLowerCase())){
                return false;
            } else if((parts[1] + parts[2]).equals((splited[1] + splited[2]))){
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) throws NotBoundException {
        Server server = new Server();
    }

}

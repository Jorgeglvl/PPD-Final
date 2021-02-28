package client;

import java.io.IOException;
import java.net.Socket;

import javax.print.DocFlavor.STRING;

import common.*;
public class ClientListener implements Runnable {

    private boolean running;
    private boolean isOpened;
    private Home home;
    private Spy spy;
    private Socket connection;
    private String connection_info;
    private Chat chat;

    public ClientListener(Home home, Socket connection) {
        this.running = false;
        this.isOpened = false;
        this.home = home;
        this.connection = connection;
        this.connection_info = null;
        this.chat = null;

    }

    @Override
    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(connection);
            if (message == null || message.equals("CHAT_CLOSE")) {
                if (isOpened) {
                    home.getOpened_games().remove(connection_info);
                    home.getConnected_listeners().remove(connection_info);
                    isOpened = false;
                    try {
                        connection.close();
                    } catch (IOException e) {
                        System.err.println("[ClientListener:Run] -> " + e.getMessage());
                    }
                    chat.dispose();
                }
                running = false;
            } else {
                String[] fields = message.split(";");
                if(fields.length > 1){
                    if(fields[0].equals("OPEN_CHAT")){
                        String[] splited = fields[1].split(":");
                        connection_info = fields[1];
                        if(!isOpened){
                            home.getOpened_games().add(connection_info);
                            home.getConnected_listeners().put(connection_info, this);
                            isOpened = true;
                            chat = new Chat(home, connection, connection_info, home.getConnection_info().split(":")[0]);
                        }
                    }
                    else if(fields[0].equals("MESSAGE")){
                        String msg = "";
                        for(int i=1;i<fields.length;i++){
                            msg += fields[i];
                            if(i>1) msg += ";";
                        }                        
                        home.verify(msg);
                        chat.append_message(msg);
                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    
}

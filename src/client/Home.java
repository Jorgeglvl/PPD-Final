package client;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import common.*;

public class Home extends JFrame {

    private ArrayList<String> opened_chats;
    private Map<String, ClientListener> connected_listeners;
    private ArrayList<String> connected_users;
    private ArrayList<String> suspect_words;
    private String connection_info;
    private Socket connection;
    private ServerSocket server;
    private boolean running;

    private JLabel jl_title;
    private JButton jb_get_connected, jb_openChat;
    private JList jlist;
    private JScrollPane scroll;

    public Home(Socket connection, String connection_info) {
        super("Home");
        this.connection_info = connection_info;
        this.connection = connection;
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        start();
    }

    private void initComponents() {
        server = null;
        running = false;
        opened_chats = new ArrayList<String>();
        connected_listeners = new HashMap<String, ClientListener>();
        connected_users = new ArrayList<String>();
        suspect_words = new ArrayList<String>();
        jl_title = new JLabel("<Usuario: " + connection_info.split(":")[0] + ">", SwingConstants.CENTER);
        jb_get_connected = new JButton("Atualizar Lista");
        jb_openChat = new JButton("Conversar");
        jlist = new JList();
        scroll = new JScrollPane(jlist);
    }

    private void configComponents() {
        this.setLayout(null);
        this.setMinimumSize(new Dimension(600, 480));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);

        jl_title.setBounds(5, 10, 370, 40);
        jl_title.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        jb_get_connected.setBounds(400, 10, 180, 40);
        jb_get_connected.setFocusable(false);

        jb_openChat.setBounds(5, 400, 575, 40);
        jb_openChat.setFocusable(false);

        jlist.setBorder(BorderFactory.createTitledBorder("Usuarios Online"));
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scroll.setBounds(5, 60, 575, 335);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
    }

    private void insertComponents() {
        this.add(jl_title);
        this.add(jb_get_connected);
        this.add(jb_openChat);
        this.add(scroll);
    }

    private void insertActions() {
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                Utils.sendMessage(connection, "QUIT");
                System.out.println("> Conexão encerrada");
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

        });

        jb_get_connected.addActionListener(event -> getConnectedUsers());
        jb_openChat.addActionListener(event -> openChat());
    }

    private void start() {
        this.pack();
        this.setVisible(true);
        this.startServer(this, Integer.parseInt(connection_info.split(":")[2]));
    }

    private void getConnectedUsers() {
        Utils.sendMessage(connection, "GET_CONNECTED_USERS");
        String response = Utils.receiveMessage(connection);
        jlist.removeAll();
        connected_users.clear();
        for (String info : response.split(";")) {
            if (!info.equals(connection_info)) {
                connected_users.add(info);
            }
        }
        jlist.setListData(connected_users.toArray());
    }
    
    private void getSuspectWords() {
        Utils.sendMessage(connection, "GET_SUSPECT_WORDS");
        String response = Utils.receiveMessage(connection);
        suspect_words.clear();
        for (String word : response.split(";")) {
        	suspect_words.add(word);
        }
        suspect_words.toArray();    	
    }
    
    public void verify(String message) {
    	this.getSuspectWords();    	
    	for(String word : suspect_words) {
    		if(message.toLowerCase().contains(word.toLowerCase())) {
    	        Utils.sendMessage(connection, "SUSPECT;" + message);
    	        break;
    		}
    	}
    }

    private void openChat() {
        int index = jlist.getSelectedIndex();
        if (index != -1) {
            String connection_info = jlist.getSelectedValue().toString();
            String[] splited = connection_info.split(":");
            if (!opened_chats.contains(connection_info)) {
                try {
                    Socket connection = new Socket(splited[1], Integer.parseInt(splited[2]));
                    Utils.sendMessage(connection, "OPEN_CHAT;" + this.connection_info);
                    ClientListener cl = new ClientListener(this, connection);
                    cl.setChat(new Chat(this, connection, connection_info, this.connection_info.split(":")[0]));
//                    cl.setGame(new Game(this, connection, connection_info, this.connection_info.split(":")[0], true));
                    cl.setOpened(true);
                    connected_listeners.put(connection_info, cl);
                    opened_chats.add(connection_info);
                    new Thread(cl).start();
                } catch (NumberFormatException e) {
                    System.err.println("[Home:openChat] -> " + e.getMessage());
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    System.err.println("[Home:openChat] -> " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("[Home:openChat] -> " + e.getMessage());
                }
            }
        }
    }

    private void startServer(Home home, int port){
        new Thread(){
            @Override
            public void run(){
                running = true;
                try {
                    server = new ServerSocket(port);
                    System.out.println("Servidor cliente iniciado na porta " + port + "...");
                    while(running){
                        Socket connection = server.accept();
                        ClientListener cl = new ClientListener(home, connection);
                        new Thread(cl).start();
                    }
                } catch (Exception e) {
                    System.err.println("[Home:startServer] -> " + e.getMessage());
                }
            }
        } .start();
    }

    public ArrayList<String> getOpened_games() {
        return opened_chats;
    }

    public Map<String, ClientListener> getConnected_listeners() {
        return connected_listeners;
    }

    public String getConnection_info() {
        return connection_info;
    }
    
}

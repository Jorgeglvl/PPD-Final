package server;

import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class Spy extends JFrame{
	
	private static final Spy INSTANCE = new Spy();

    private JLabel jl_title;
    private JEditorPane messages;
    private JTextField jt_message;
    private JButton jb_add, jb_remove;
    private JPanel panel, jp_buttons;
    private JScrollPane scroll;

    private String title;
    private String connection_info;
    private static ArrayList<String> message_list;

    private Spy(){
        super("Espião");
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        start();
    }

    private void initComponents(){
        message_list = new ArrayList<String>();
        jl_title = new JLabel("Palavras Suspeitas:", SwingConstants.CENTER);
        messages = new JEditorPane();
        scroll = new JScrollPane(messages);
        jt_message = new JTextField();
        jb_add = new JButton("Adicionar");
        jb_remove = new JButton("Remover");
        panel = new JPanel(new BorderLayout());
        jp_buttons = new JPanel(new BorderLayout());

    }

    private void configComponents(){
        this.setMinimumSize(new Dimension(480, 720));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        messages.setContentType("text/html");
        messages.setEditable(false);

        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jb_add.setSize(100, 40);
    }

    private void insertComponents(){
        this.add(jl_title, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);
        panel.add(jt_message, BorderLayout.CENTER);
        
        jp_buttons.add(jb_add, BorderLayout.WEST);
        jp_buttons.add(jb_remove, BorderLayout.EAST);
        
        panel.add(jp_buttons, BorderLayout.EAST);

    } 
    
    private void insertActions(){
        jb_add.addActionListener(event -> addSuspeita(jt_message.getText()));
        jb_remove.addActionListener(event -> removeSuspeita(jt_message.getText()));
        jt_message.addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){

            }
            public void keyPressed(KeyEvent e){
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                	addSuspeita(jt_message.getText());
                }
            }
            public void keyReleased(KeyEvent e){

            }
        });

    }

    private void addSuspeita(String text){
    	text = text.toUpperCase();
    	if(message_list.contains(text)) {
    		JOptionPane.showMessageDialog(null,"Essa palavra já é suspeita");
    	} else {
    		message_list.add(text);
    		String message = "";
		    for(String str : message_list){
		        message += str;
		        message += "<br>";
		    }
		    messages.setText(message);
    	}
    	jt_message.setText("");
    }
    
    private void removeSuspeita(String text){
    	text = text.toUpperCase();
    	if(message_list.contains(text)) {
    		message_list.remove(text);
    		String message = "";
		    for(String str : message_list){
		        message += str;
		        message += "<br>";
		    }
		    messages.setText(message);
    	} else {
    		JOptionPane.showMessageDialog(null,"Essa palavra não é suspeita");
    		}
    	jt_message.setText("");
    }

    private void start(){
        this.pack();
        this.setVisible(true);
    }
    
    public ArrayList<String> getMessage_list() {
    	return message_list;
    }
    
    public static Spy getINSTANCE() {
    	return INSTANCE;
    }
}

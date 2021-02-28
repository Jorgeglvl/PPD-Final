package RMI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class MediadorFrame extends JFrame{
	
    private JLabel jl_title;
    private JEditorPane messages;
    private JButton lb_clear;
    private JScrollPane scroll;
    private Mediador mediador;

    private static ArrayList<String> notification_list;

    public MediadorFrame(Mediador mediador){
        super("Mediador");
        this.mediador = mediador;
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        start();
    }
    
    private void initComponents(){
        notification_list = new ArrayList<String>();
        jl_title = new JLabel("Notifica��es:", SwingConstants.CENTER);
        messages = new JEditorPane();
        scroll = new JScrollPane(messages);
        lb_clear = new JButton("Limar Notifica��es");

    }

    private void configComponents(){
        this.setMinimumSize(new Dimension(320, 400));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        messages.setContentType("text/html");
        messages.setEditable(false);

        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    }
    
    private void insertComponents(){
        this.add(jl_title, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
        this.add(lb_clear, BorderLayout.SOUTH);        

    } 
    
    private void insertActions(){
        lb_clear.addActionListener(event -> clearNotifications());
    }
    
    public void addNotification(String text){
    		notification_list.add(text);
    		String message = "";
		    for(String str : notification_list){
		        message += str;
		        message += "<br>";
		    }
		    messages.setText(message);
    }
    
    public void clearNotifications() {
		notification_list.clear();
	    messages.setText("");
    	
    }
    
    private void start(){
        this.pack();
        this.setVisible(true);
    }

}
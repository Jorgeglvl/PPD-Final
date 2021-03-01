package RMI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.rmi.RemoteException;
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
    private MediadorFrame mediadorFrame;

    private static ArrayList<String> notification_list;

    public MediadorFrame(Mediador mediador){
        super("Mediador");
        mediadorFrame = this;
        this.mediador = mediador;
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        createRunnable();
//        start();
    }
    
    private void initComponents(){
        notification_list = new ArrayList<String>();
        jl_title = new JLabel("Notificações:", SwingConstants.CENTER);
        messages = new JEditorPane();
        scroll = new JScrollPane(messages);
        lb_clear = new JButton("Limar Notificações");

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
    
    public void addNotification() throws RemoteException{
		ArrayList<String> notificationsList = mediador.recebeMensagem("Monitoramento", false);
		System.out.println(notificationsList);
		while(!notificationsList.isEmpty()) {
//			textUsuarios.append("Recebido de "+notificationsList.remove(0)+"\n");
	    	System.out.println("Era pra ter mostrado");
    		notification_list.add(notificationsList.remove(0));
    		String message = "";
		    for(String str : notification_list){
		        message += str;
		        message += "<br>";
		    }
		    messages.setText(message);
		}
    }
    
    public void clearNotifications() {
		notification_list.clear();
	    messages.setText("");
    	
    }
    
//    private void start(){
//        this.pack();
//        this.setVisible(true);
//    }
    
	private void createRunnable() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mediadorFrame.pack();
					mediadorFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

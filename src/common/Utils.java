package common;

import java.io.*;
import java.net.Socket;

public class Utils {
    public static boolean sendMessage(Socket connection, String message) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            output.writeObject(message);
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR:sendMessage] -> " + e.getMessage());
        }
        return false;
    }

    public static String receiveMessage(Socket connection) {
        String response = null;
        try {
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
            response = (String) input.readObject();
        } catch (IOException e) {
            System.err.println("[ERROR:receivedMessage] -> " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR:receivedMessage] -> " + e.getMessage());
        }

        return response;
    }

}

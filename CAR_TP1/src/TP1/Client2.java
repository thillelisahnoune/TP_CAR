package TP2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client2 {
    public static void main(String[] args) {
// Declaration des identifiants 
    	String login = "miage";
    	String pass="car";
        try {
            Socket socket = new Socket("localhost", 2021); // Declarer le serveur socket 

            PrintWriter output2 = new PrintWriter(socket.getOutputStream(), true);  // Lire les flux i/o
            BufferedReader input2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            
            
            System.out.println("Server: " + input2.readLine());

            // Login
            output2.println("USER " + login);
            
            // reponse sur le login 
            System.out.println("Server: " + input2.readLine());

           
            // Pass
            output2.println("PASS " + pass);

           // Reponse sur le pass
            System.out.println("Server: " + input2.readLine());

           // Ping
            output2.println("PING");

            // Reponse sur le Ping 
            System.out.println("Server: " + input2.readLine());

            if (input2.readLine().equals("200 PING command ok")) {
               
                output2.println("PONG");
                
                // Reponse sur le Pong
                System.out.println("Server: " + input2.readLine());
            } else {
                System.out.println("Serveur ne peut pas repondre à la commande");
            }
           
            // Quit
            output2.println("QUIT");

           
            // Reponse sur le quit
            System.out.println("Server: " + input2.readLine());

           // Fermeture des i/o et du socket
            output2.close();
            input2.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package TP2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur{
	// Déclarations de sockets
    private static ServerSocket serveur;
    private static Socket client;

    public static void main(String[] args) {
    	// Déclaration des identifiants
        String login = "miage";
        String pass= "car";

        try {
            serveur = new ServerSocket(2021); // Ecouter le canal sur le port 2021
            System.out.println("Serveur prêt à accepter des connexions sur le port 2021");

            while (true) {
                client = serveur.accept(); // Accepter la connexion

                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream())); // Lire es flux de données i/o
                OutputStream output = client.getOutputStream();

                
                output.write("220 Service ready\r\n".getBytes());

                // Authentification
                String loginT = input.readLine();
                System.out.println("Client : " + loginT);

                // Login
                if (loginT.equals("USER " + login)) {
                    output.write("331 User valid\r\n".getBytes());
                } else {
                    output.write("530 User invalid\r\n".getBytes());
                    client.close();
                    continue;
                }

                // Pass
                String passT = input.readLine();
                System.out.println("Client : " + passT);

                // Pass
                if (passT.equals("PASS " + pass)) {
                    output.write("230 Pass valid\r\n".getBytes());
                } else {
                    output.write("530 Pass invalid\r\n".getBytes());
                    client.close();
                    continue;
                }

                // ping
                String pingT = input.readLine();
                System.out.println("Client : " + pingT);
                 if (pingT.equals("PING")) {
                       
                       output.write("200 PING command ok\r\n".getBytes());
                        String response = input.readLine();
                        if (response.equals("PONG")) {
                            System.out.println("Client: 200 PONG command ok\r\n");
                            output.write("PONG\r\n".getBytes());
                        
                        } else {
                            output.write("502 Unknown command\r\n".getBytes());
                        }
                        String quitT = input.readLine();
                        System.out.println("Client : " + quitT);
                     if (quitT.equals("QUIT")) {
                        output.write("221 User logged out\r\n".getBytes());
                        client.close();
                        break;
                    } else {
                        output.write("500 Unknown command\r\n".getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
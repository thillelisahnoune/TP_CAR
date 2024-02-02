package TP2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 {
    public static void main(String[] args) {
    	// Declaration des identifiants
    	String login = "miage";
    	String pass = "car";
       try {
           Socket socket = new Socket("localhost", 2021); // Socket client 

           PrintWriter output1 = new PrintWriter(socket.getOutputStream(), true); // Lire les flux de donnees i/o
           BufferedReader input1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));

              System.out.println("Serveur : " + input1.readLine());// Premiére reponse


           
           // login
           output1.println("USER " + login);

           // Reponse sur le login
           System.out.println("Serveur : " + input1.readLine());

           // Pass           
           output1.println("PASS " + pass);

           // Reponse sur le pass
           System.out.println("Serveur : " + input1.readLine());

           

           // Quit
           output1.println("QUIT");

           // Reponse sur le quit
           System.out.println("Serveur : " + input1.readLine());

           // Fermeture du i/o et du socket
           output1.close();
           input1.close();
           socket.close();

       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
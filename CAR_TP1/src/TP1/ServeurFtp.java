package TP1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServeurFtp {
	// Définir un objet Socket et  ServerSocket
	private static ServerSocket server;
	private static Socket client;
	 
	
	
	public static void main(String[] args) throws IOException{
		String login = "Thilleli";
		String pass = "Sahnoune";		
		
		// Initialiser notre serveur
		server = new ServerSocket(2021);
		System.out.println("serveur prêt à accepter des connexions sur le port 2021"); 
		client = server.accept(); // Accepter la connexion
		 
		 // Demander la requette du client 
		 InputStream req = client.getInputStream();
		 Scanner s = new Scanner(req);
		 
		 OutputStream output = client.getOutputStream();
		 String str = "220 Service ready\r\n" ;
		 output.write( str.getBytes());
		 
			 
			// Récupérer le login 
			 String loginT = s.nextLine();
			 System.out.println(loginT);
			 
			 if(loginT.equals("USER " +login)){
			 System.out.println("Login valid");
			 String strL = "331 Login valid\r\n";
			 output.write( strL.getBytes());
			 }else {
				 System.out.println("Login invalid");
				 String strL = "441 Login invalid\r\n";
				 output.write( strL.getBytes());
			 }
		 
			 
			// Récupérer le pass 
			 String passT = s.nextLine();
			 System.out.println(passT);
			 
			 
			 if(passT.equals("PASS " +pass)){
			 System.out.println("Pass valid");
			 String strP = "230 Pass valid\r\n" ;
			 output.write( strP.getBytes());
			 
			 }else {
				 System.out.println("Pass invalid");
				 String strL = "441 Pass invalid\r\n";
				 output.write( strL.getBytes());
			 }		 
			 
			 
			 while(true){
				// Récupérer le quit
				 String quitT = s.nextLine();
				 
				 if (quitT.equals("QUIT")) {
				 String strQ = "221 User logged out\r\n";
				 output.write(strQ.getBytes());
				 break;
				 } else {
				 String strOther = "502 Command not implemented\r\n";
				 output.write(strOther.getBytes());
				 }
			 }
	}

}

package TPCAR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerFTP {
	// Declarations 
	private static ServerSocket serveur;
    private static Socket client;
    private static boolean modeBinair = false; // Added to track the transfer mode
    private static ServerSocket serverSocketDonnee;
    private static int portDonnee;
    
    private static void sendFileSize(String filename, OutputStream out) throws IOException {
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            long fileSize = file.length();
            String sizeResponse = "213 " + fileSize + "\r\n";
            out.write(sizeResponse.getBytes());
        } else {
            String sizeNotFoundResponse = "550 Requested action not taken. File not found\r\n";
            out.write(sizeNotFoundResponse.getBytes());
        }
    	}
	
public static void main(String[] args) throws IOException{
	String login = "Thilleli";
	String pass = "Sahnoune";		
	
	// Initialiser notre serveur
	serveur = new ServerSocket(2021);
	System.out.println("serveur prêt à accepter des connexions sur le port 2021"); 
	client = serveur.accept(); // Accepter la connexion
	 
	 // Demander la requette du client 
	 InputStream req = client.getInputStream();
	 Scanner s = new Scanner(req);
	 
	 OutputStream output = client.getOutputStream();
	 String str = "220 Service ready\r\n" ;
	 output.write( str.getBytes());
	 
	 
	 		// Récupérer le login 
	 String loginT = s.nextLine();
	 System.out.println(loginT);
	 
	 while(true) {
		 if(loginT.equals("USER " +login)){
			 System.out.println("Login valid");
			 String strL = "331 Login valid\r\n";
			 output.write( strL.getBytes());
		 }
			 // Récupérer le pass
	 String passT = s.nextLine();
	 System.out.println(passT);
	 
	 if(passT.equals("PASS " +pass)){
		 System.out.println("Pass valid");
		 String strP = "230 Pass valid\r\n" ;
		 output.write( strP.getBytes());
		 
		 // Les prochaines commandes 
		 while(true) {
		 	// Mode binnaire ?
		 String com=s.nextLine();
		 if (com.equals("TYPE I")) {
                 modeBinair = true;
                 String strB = "200 Binary mode set\r\n";
                 output.write(strB.getBytes());
                 
             // Size 
		 } else if(com.startsWith("SIZE")) {
        	 String[] split = com.split("\\s+");
             if (split.length == 2) {
                 String fileName = split[1];
                 sendFileSize(fileName, output);
             }
             // OS
		 } else if(com.equals("SYST")) {	
            	 String strS = "215 UNIX Type: L8\r\n";
                 output.write(strS.getBytes());
             
             // Feature
		 } else if(com.equals("FEAT")) {
            	 String strF = "211-Features:\r\n PASV\r\n SIZE\r\n UTF8\r\n211 End\r\n";
                 output.write(strF.getBytes());
                 
             // EPSV
		 }else if(com.equals("EPSV")) {
            	 	// Nouveau Socket pour les donnees
                 serverSocketDonnee = new ServerSocket(0);
                 portDonnee = serverSocketDonnee.getLocalPort();
                 String strEPSV = "229 Entering Extended Passive Mode (|||" + portDonnee + "|)\r\n";
                 output.write(strEPSV.getBytes());
                 
                 	// RETR
		 } else if(com.startsWith("RETR")) { 
                 Socket dataSocket = serverSocketDonnee.accept();
                 output.write("150 Connexion Acceptée\r\n".getBytes());   

                 String[] splite = com.split("\\s+");
                 if (splite.length == 2) {
                     String file_name = splite[1];

                     // Lecture du fichier  
                     FileInputStream fileInputStream = new FileInputStream(file_name);
                     OutputStream dataOut = dataSocket.getOutputStream();

                     // envoi de fichier 
                     byte[] buffer = new byte[1024];
                     int bytesRead;
                     while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                         dataOut.write(buffer, 0, bytesRead);
                     } // while retr 
                     fileInputStream.close();
                     output.write("226 Closing data connection\r\n".getBytes());
                 
                 
                 } // if retr : fileName
                 // Fermer le socket 
                 serverSocketDonnee.close();
                 dataSocket.close();
             } // retr 
              
		 
		 } // while pass
		 
			 } // pass
	 
	 } // while main
	 
	 } // main
	
} // class
































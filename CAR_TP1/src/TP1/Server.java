package Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    // Declarations
    private static ServerSocket serveur;
    private static ServerSocket serverSocketDonnee;
    private static Socket client;
    private static boolean modeBinair = false; // Added to track the transfer mode
    private static int portDonnee;
    
    private static void sendFileSize(String fileName, OutputStream output) throws IOException {
        File file = new File(fileName);
        try (FileInputStream fis= new FileInputStream(file)){
        	long fileSize = file.length();
            String sizeResponse = "213 " + fileSize + "\r\n";
            output.write(("213 " + fileSize + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
		    // Le fichier n'existe pas
		    e.printStackTrace(); 
		} catch (IOException e) {
		    // Une erreur d'entrée/sortie s'est produite
		    e.printStackTrace(); 
		} catch (Exception e) {
		    // Autres exceptions générales
			e.printStackTrace();
		}
        }  // Récuperer la taille du fichier 
    
public static void main(String[] args) throws IOException{
    String login = "t";
    String pass = "s";       
    
    // Initialiser un serveur, ecoute sur le port 2021
    serveur = new ServerSocket(2020);
    System.out.println("Serveur prêt à accepter des connexions sur le port 2020");
    client = serveur.accept(); // Ecouter le canal et accepter la connexion
    
     // Lire le flux de donnée rentrant
     InputStream req = client.getInputStream();
     Scanner s = new Scanner(req);
    
     // Envoyer les données au client
     OutputStream output = client.getOutputStream();
     output.write( "220 Service ready\r\n".getBytes());
    
    
             // Authentification
while(true) {
     if(s.hasNext()){
     String loginT = s.nextLine();  // Récuperer le login
    
            if(loginT.equals("USER "+ login)){
                System.out.println("User valid");
                output.write( "331 User valid\r\n".getBytes());
            }else {
            	System.out.println("Login invalid");
            	output.write("530 Login invalid\r\n".getBytes());
            }

            String passT = s.nextLine();  // Récuperer le mot de passe
            if(passT.equals("PASS "+ pass)){
                System.out.println("Passe valide");
                output.write( "230 Pass valide\r\n".getBytes());
               
                while(true){
                     if (s.hasNextLine()) {  // Pour eviter line not found
                    	 
                     String nextT = s.nextLine();  // Traiter le quit    
                     if (nextT.equals("QUIT")) {
                     System.out.println("User loged out");
                     output.write( "221 User loged out\r\n".getBytes());
                     break;
                     
                     // Traiter la commande GET
                     } else if (nextT.equals("TYPE I")) {  // Activer le mode binnair
                                modeBinair = true;
                                output.write("200 Mode binnair activé pour le tranfére de données\r\n".getBytes());
                                
                     } else if (nextT.startsWith("SIZE")) {  // Récuperer la taille du fichier à envoyé
                            String[] split = nextT.split("\\s+");
                            if (split.length == 2) {
                                String filename = split[1];
                                sendFileSize(filename, output);                                
                            }
                            
                     		} else if (nextT.equals("SYST")) {  // Type de OS
                                output.write("215 UNIX Type: L8\r\n".getBytes());
                           
                            } else if (nextT.equals("FEAT")) {  // Feature, sinon elle plante la commande get
                                output.write( "211-Features:\r\n PASV\r\n SIZE\r\n UTF8\r\n211 End\r\n".getBytes());
                           
                            } else if (nextT.equals("PASV")) {  // Mode passive
    							serverSocketDonnee = new ServerSocket(0);
    							portDonnee = serverSocketDonnee.getLocalPort();
    							output.write(("229 Entering Passive Mode (|||" + portDonnee + "|)\r\n").getBytes());
    							
    						} else if (nextT.equals("EPSV")) {  // Mode de transfére de donnée passive                                
                                serverSocketDonnee = new ServerSocket(0); // Crer un nouveau serveur pour ecouter les donnée, ecouter sur un port aleatoire puis on le recupére                             
                                portDonnee = serverSocketDonnee.getLocalPort();
                                output.write(("229 Entré en mode Passive (|||" + portDonnee + "|)\r\n").getBytes());
                                
                            } else if (nextT.startsWith("RETR")) {
                                Socket socketDonnee = serverSocketDonnee.accept();  // Attendre la connexion de pour le transfére de donnée et l'acceptée
                                output.write("150 Connexion acceptée\r\n".getBytes());
                                
                                String[] splite = nextT.split("\\s+");
                                if (splite.length == 2) {
                                    String filename = splite[1];
                                    FileInputStream fileInputStream = new FileInputStream(filename);  // Ouvrire le fichier à envoyé
                                    OutputStream dataOut = socketDonnee.getOutputStream();

                                    byte[] buffer = new byte[2048];  // Envoyé le contenus du fichier 
                                    int bytesRead;
                                    while ((bytesRead = fileInputStream.read(buffer)) != -1) { // charger le fichier dans le buffer tant qu'il n'est pas encors terminé
                                        dataOut.write(buffer, 0, bytesRead);
                                    }

                                    fileInputStream.close();  // Fermer le ficheir, sinon on ne pourra pas le tranférer tant qu'il est ouvert
                                    output.write("Connexion fermé\r\n".getBytes());                                
                                	}
                                
                                serverSocketDonnee.close();  // Fermer le serveur et la socket, pour ecouter d'autre connexion si y'aura 
                                socketDonnee.close();   
                                
                            } else if(nextT.startsWith("LIST")) {
                            	String[] parts = nextT.split("\\s+");
    							String directoryPath = (parts.length > 1) ? parts[1] : ".";

    							Socket socketDonneeList = serverSocketDonnee.accept();  // Ecouter le canal et accepter la connexion
    							output.write("150 Connexion de données acceptée\r\n".getBytes());

    							
    							StringBuilder listeFiles = new StringBuilder();  // Initialiser la liste des fichiers 
    							File repC = new File(directoryPath);  // Repertoire courant pour commancer la navigation 
    							File[] files = repC.listFiles();  // Récuperer les fichiers dans le repertoire courant

    							if (files != null) {  // Tant qu'il ya des fichiers, on les parcours
    								for (File file : files) {
    									String fileDetails = String.format(" %s\r\n", file.getName());  // Lister les noms de fichiers 
    									listeFiles.append(fileDetails);
    								}
    							}
    							
    							OutputStream outputD = socketDonneeList.getOutputStream();
    							outputD.write(listeFiles.toString().getBytes());  // envoyer la liste des fichiers au client
    							
    							socketDonneeList.close();
    							outputD.write("226 Connexion fermée\r\n".getBytes());  // Fermer la connexion    							
    							
							
							

                            }
                     }//hasNext
                 }//while
            } //pass
            
     } //hasNext
} //while
} //main
} //class

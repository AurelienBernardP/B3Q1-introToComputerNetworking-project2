import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server
 */
public class Server {

   private static final int port = 2146;
   private static int nbThreads = 0;
   private static int maxThreads;
   public static void main(String argv[]) throws Exception {
      ServerSocket server;
      if(argv.length != 2){
         maxThreads = Integer.parseInt(argv[0])/2;
      }else{
         System.err.println("Expected 1 arguments but given "+ argv.length);
         return;
      }
      try {
         //Creating a server socket listening on a port
         server = new ServerSocket(port);
         while(true){
            //Handshaking with the client if thread pool not exceeded
            if(nbThreads < maxThreads){
               Socket socketClient = server.accept();
               //Create a new thread which delegates the connection
               ControlChannel w = new ControlChannel(socketClient);
               w.start();
               nbThreads++;
            }
            
         }
      } catch (Exception e) {
         System.err.println("Server died: "+ e);
      }
   }

   static void threadKilled(){
      if(nbThreads > 0){
         nbThreads--;
      }else{
         System.err.println("No thread to kill");
      }
   }
}
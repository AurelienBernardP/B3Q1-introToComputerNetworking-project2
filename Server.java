import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server
 */
public class Server {

   private static final int port = 2038;
   private static int nbThreads = 0;
   private int maxThreads;

   public static void main(String argv[]) throws Exception {

      ServerSocket server;

      if(argv[2]!= null){
         maxThreads = Integer.parseInt(argv[2]);
      }else{
         System.err.println("wrong use of arguments");
         return;
      }

      try {
         //Creating a server socket listening on a port
         server = new ServerSocket(port);

         while(true){
            //Handshaking with the client if thread pool not exceeded
            if(nbThreads < maxThreads){//argv[2]

               Socket socketClient = server.accept();
               //Create a new thread which delegates the connection
               Worker w = new Worker(socketClient,this);
               nbThreads++;
            }
            
         }
      } catch (Exception e) {
         System.err.println("Server died: "+ e);
      }
   }

   void threadKilled(){
      if(nbThreads > 0){
         nbThreads--;
      }else{
         System.err.println("No thread to kill");
      }
   }
}
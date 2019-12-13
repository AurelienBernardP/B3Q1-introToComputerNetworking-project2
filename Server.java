import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server
 */
public class Server {
   private static final Integer port = 2038;
   private static Integer nbThreads = 0;
   public static void main(String argv[]) throws Exception {
      ServerSocket server;
      try {
         //Creating a server socket listening on a port
         server = new ServerSocket(port);

         while(true){
            //Handshaking with the client if thread pool not exceeded
            if(nbThreads < 5){//argv[2]
               Socket socketClient = server.accept();
               nbThreads++;
            }
            //Creating a new thread which delegates the connection
            Worker w = new Worker(socketClient);
            if(!w.isAlive())
               nbThreads--;
         }
      } catch (Exception e) {
         System.err.println("Server died: "+ e);
      }
   }
}


class Worker extends Thread{
   private final Socket socketControl;
   private Socket socketData;
   private static final Integer timeOut = 7000;
   private boolean isActive;

   public Worker(Socket s){
      this.socketControl = s;
   }

   @Override
   public void run(){
      OutputStream out;
      InputStream in;
      BufferedReader reader;
      String request;
      try {
         //Setting a time limit
         this.socketControl.setSoTimeout(timeOut);
         this.socketControl.setTcpNoDelay(true);

         //Input and output stream of the socket
         out = socketControl.getOutputStream();
         in = socketControl.getInputStream();
         reader = new BufferedReader(new InputStreamReader(in));
         request = new String();

         while(true){
            //Reading the input stream of the socket
            request = reader.readLine();
            
            if(request != null){
               request = request + "\r\n";
               switch (requestProcessor(request)){
                  case 0:
                     
                     break;
                  case 1:
                     Integer portSocket = socketControl.getPort();
                     byte[] ipSocket = socketControl.getInetAddress().getAddress()
                     System.out.print("227 Entering Passive Mode (" +  + ")");
                     out.write(lower,0,lower.length);
                     break;
                  case 2:
                     break;
                  case 3:
                     break;
                  default:
                     break;
               }
            }

            //Socket closed from the client side
            else
               break;
         }
      } catch (Exception any) {
         System.err.println("Worker died: "+ any);
      } 

      //Closing the connection
      finally {
         try {
            if(socket != null)
               this.socket.close();
            System.out.println("Socket closed");
         } catch (Exception e) {
            System.err.println("Closing socket error: "+ e);
         }
      }
   }


   /* int requestProcessor(String request)
   * Checks the validity of the request and indicates the procedure to perform
   * 
   * Input: request, a string
   * 
   * Return:
   *       -1: The request is erroneous
   *        0: 
   *        1: The request is "PASV"
   *        2: 
   *        3: 
   */
   private int requestProcessor(String request){
      if(request.startsWith("PASV") && request.charAt(4) == '\r'
                                        && request.charAt(5) == '\n' ){
         this.isActive = false;
         return 1;
      }
      if(){
         this.isActive = true;
         return 1;
      }

}

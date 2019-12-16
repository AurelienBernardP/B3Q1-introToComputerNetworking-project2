
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class Worker extends Thread {

    private static final int TIMEOUT = 1000 * 70;
    private boolean isActive;
    private boolean isPassive;

    private final Socket socketControl;    
    private OutputStream outControl;
    private InputStream inControl;
    private BufferedReader readerControl;

    private Socket socketData;
    private OutputStream outData;
    private InputStream inData;
    private BufferedReader readerData;

    public Worker(Socket s) {
        this.socketControl = s;
    }

    @Override
    public void run() {
        String request;

        try {
            // Setting a time limit
            this.socketControl.setSoTimeout(TIMEOUT);
            this.socketControl.setTcpNoDelay(true);

            // Input and output stream of the socket
            outControl = socketControl.getOutputStream();
            inControl = socketControl.getInputStream();
            readerControl = new BufferedReader(new InputStreamReader(inControl));
            request = new String();

            while (true) {
                // Reading the input stream of the socket
                request = readerControl.readLine();

                if (request != null) {
                    processRequest(request);
                }

                // Socket closed from the client side
                else
                    Server.threadKilled();
                return;
            }
        } catch (Exception any) {
            System.err.println("Worker died: " + any);
            Server.threadKilled();
            return;
        }
    }

    private void processRequest(String request) {
        if (request == null)
            return;
    
        String[] words = request.split(" ");
    
        if (words.length <= 0) {
            //out.write
            return;
        }
    
        switch (words[0]) {
            case "PASV":
                if (words.length > 1)
                    // send errors
                    return;
                pasvConectionInit();
                return;
            case "PORT":
                requestPORT(words);
                break;
    
            case "CDUP"://go to parent directory, no arg
                break;
            case "CWD"://change working directory, 1 arg directory path
                break;

            case "LIST"://see current directory content, no arg( we dont have to handle the case where there is an arg)
                break;

            case "PWD"://gives path of current directory, no arg
                break;

            case "DELETE":// delete file in the current dirrectory, 1 arg, the file name
                break;

            case "GET":// dowload a file from working directory, 1 arg, the file name
                break;

            case "PUT":// put a file on the server, 1 arg the file name 
                break;

            case"QUIT":// no arg, disconnect from server
            case"BYE":
            case"EXIT":
            case"CLOSE":
            case"DISCONNECT":

                break;
            case"USER": //input user name, 1 arg, the user name
                break;
            case"PASS": //input pasword, 1 arg, the password(i think we have to decript it as we receive it cripted)
                break;
            case"RENAME":
                break; //rename a file in current directory , 2 args , current name of file, new filename.
                
            default: // error case
                return;
            }
    
        return;
    }

    private void pasvConectionInit(){

        Integer portSocket = socketControl.getPort();
        byte[] ipSocket = socketControl.getInetAddress().getAddress();
        //System.out.print("227 Entering Passive Mode (" + +")");
        //System.out.print("home address" + socketControl.getInetAddress().toString());
        int[] dataPort = getPassivePortAdrs(socketData.getPort());

        controlResponse("227 Entering Passive Mode (" + socketControl.getInetAddress().toString() + "."+ dataPort[0].toString() + "." + dataPort[1].toString() + ")\r\n");
        return;
    }

    private int[] getPassivePortAdrs(int portNb){
        return new int[]{portNb / 256, portNb % 256};
    }

    void initDataConnection(String ipClient, int portClient, int portData){
        try {
            //Creating a socket listening on a port
            socketData = new Socket(ipClient, portClient, InetAddress.getLocalHost(),portData);
            
            // Setting a time limit
            this.socketData.setSoTimeout(TIMEOUT);
            this.socketData.setTcpNoDelay(true);

            // Input and output stream of the socket
            outData = socketData.getOutputStream();
            inData = socketData.getInputStream();
            readerData = new BufferedReader(new InputStreamReader(inData));
        } catch (Exception e) {
            System.out.println("Error initialisation data connection: "+ e);
        }
    }

    void controlResponse(String response){
        try {
            outControl.write(response.getBytes());
        } catch (Exception e) {
            System.out.println("Reponse Control connection error: "+e);
        }
    }

    void dataResponse(String response){
        try {
            outData.write(response.getBytes());
        } catch (Exception e) {
            System.out.println("Reponse Data connection error: "+e);
        }
    }

    void requestPORT(String[] request){
        
        //Check length of request
        if(request.length != 2){
            //out.write(new String());
            return;
        }

        //Check if connection already init
        if(isActive == true || isPassive == true){
            //out.write(new String());
            return;
        }

        String[] interfaceClient = request[1].split(",");

        //Check if IP length is ok
        if(interfaceClient.length != 6){
            //out.write
            return;
        }

        //Check if IP is all number
        for (int i = 0; i < interfaceClient.length; i++) {
            try {
                Double.parseDouble(interfaceClient[i]);
            } catch (NumberFormatException e) {
                //out.write
                return;
            }
        }

        int portClient = transitionClientPort(Integer.parseInt(interfaceClient[4]), Integer.parseInt(interfaceClient[5]));



        isActive = true;
        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

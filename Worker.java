
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

class Worker extends Thread {

    private static final int TIMEOUT = 1000 * 7;
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

            // Input and output stream of the control socket
            outControl = socketControl.getOutputStream();
            inControl = socketControl.getInputStream();
            readerControl = new BufferedReader(new InputStreamReader(inControl));
            request = new String();

            while (true) {
                // Reading the input stream of the control socket
                request = readerControl.readLine();
                if (request != null)
                    processRequest(request);
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
        String[] words = request.split(" ");

        if (words.length <= 0) {
            controlResponse("502 Command Not Implemented");
            return;
        }

        switch (words[0]) {
            case "PASV":
                Integer portSocket = socketControl.getPort();
                byte[] ipSocket = socketControl.getInetAddress().getAddress();
                //System.out.print("227 Entering Passive Mode (" + +")");
                //System.out.print("home address" + socketControl.getInetAddress().toString());
                Integer[] dataPort = new Integer[2];

                dataPort[0] = socketData.getPort() / 256;
                dataPort[1] = socketData.getPort() % 256;

                controlResponse("227 Entering Passive Mode (" + socketControl.getInetAddress().toString() + "."+ dataPort[0].toString() + "." + dataPort[1].toString() + ")\r\n");
                break;

            case "PORT":
                requestPORT(words);
                break;

            case "":
                break;

            default:
                return;
            }

        return;
    }

    private boolean initDataConnection(String ipClient, int portClient, int portData){
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
            return true; 
        } catch (Exception e) {
            System.out.println("Error initialisation data connection: "+ e);
            controlResponse("425 Cannot Open Data Connection");
            return false;
        }
    }

    void closeDataConnection(){
        try {
            socketData.close();

            outData = null;
            inData = null;
            readerData = null;
        } catch (Exception e) {
            System.out.println("Closes data connection: "+ e);
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
            controlResponse("502 Command Not Implemented");
            return;
        }

        //Check if connection already init
        if(isActive == true || isPassive == true){
            controlResponse("503 Bad Sequence Of Command");
            return;
        }

        String[] interfaceClient = request[1].split(",");

        //Check if IP length is ok
        if(interfaceClient.length != 6){
            controlResponse("502 Command Not Implemented");
            return;
        }

        //Check if IP is all number
        for (int i = 0; i < interfaceClient.length; i++) {
            try {
                Double.parseDouble(interfaceClient[i]);
            } catch (NumberFormatException e) {
                controlResponse("501 Syntax Error In Parameters");
                return;
            }
        }

        int portClient = transitionClientPort(Integer.parseInt(interfaceClient[4]), Integer.parseInt(interfaceClient[5]));

        String ipClient = interfaceClient[0] +"."+ interfaceClient[1] +"."+ interfaceClient[2] +"."+ interfaceClient[3];
        if(initDataConnection(ipClient, portClient, 2001)){
            dataResponse("SYN");
            boolean isAcked = false;
            while(!isAcked){
                try {
                    String response = readerData.readLine();
                    if(response.equals("SYN, ACK\r\n")){
                        dataResponse("ACK");
                        isAcked = true;
                        controlResponse("200 PORT command successful\r\n");
                        isActive = true;
                    }
                } catch (Exception e) {
                    System.out.println("Error reading client from data channel: "+ e);
                    closeDataConnection();
                    controlResponse("425 Cannot Open Data Connection");
                    break;
                }
            }
        }

        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

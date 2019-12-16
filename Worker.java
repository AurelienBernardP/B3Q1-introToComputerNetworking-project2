
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import StringUtils;

class Worker extends Thread {
    
    private final Socket socketControl;
    private Socket socketData;
    private static final int TIMEOUT = 1000 * 70;
    private boolean isActive;
    private boolean isPassive;
    private Server parentServer;

    public Worker(Socket s, Server parent) {
        this.socketControl = s;
        parentServer = parent;
    }

    @Override
    public void run() {
        OutputStream out;
        InputStream in;
        BufferedReader reader;
        String request;

        try {
            // Setting a time limit
            this.socketControl.setSoTimeout(timeOut);
            this.socketControl.setTcpNoDelay(true);

            // Input and output stream of the socket
            out = socketControl.getOutputStream();
            in = socketControl.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            request = new String();

            while (true) {
                // Reading the input stream of the socket
                request = reader.readLine();

                if (request != null) {
                    processRequest(request);
                }

                // Socket closed from the client side
                else
                    parentServer.threadKilled();
                return;
            }
        } catch (Exception any) {
            System.err.println("Worker died: " + any);
            parentServer.threadKilled();
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
            Integer portSocket = socketControl.getPort();
            byte[] ipSocket = socketControl.getInetAddress().getAddress();
      //      System.out.print("227 Entering Passive Mode (" + +")");
        //    System.out.print("home address" + socketControl.getInetAddress().toString());
            int[] dataPort = new Int[2];

            dataPort[0] = socketData.getPort() / 256;
            dataPort[1] = socketData.getPort() % 256;

            out.write(new String("227 Entering Passive Mode (" + socketControl.getInetAddress().toString() + "."
                    + dataPort[0] + "." + dataPort[1] + ")\r\n").getBytes());
            break;

        case "PORT":
            requestActive(words);
            break;

        case "":
            break;

        default:
            return;
        }

        return;
    }

    void requestActive(String[] request){
        
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

        int portClient = transitionClientPort(Integer.parseInt(interfaceClient[4]), Integer.parseInt(interfaceClient[4]));

        isActive = true;
        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

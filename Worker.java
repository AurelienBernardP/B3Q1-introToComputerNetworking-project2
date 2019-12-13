
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Worker extends Thread {
    
    private final Socket socketControl;
    private Socket socketData;
    private static final int TIMEOUT = 1000 * 70;
    private boolean isActive;
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

        case "yas":
            break;

        case "":
            break;

        default:
            return;
        }

        return;
    }

}
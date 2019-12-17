
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeoutException;


class DataChannel extends Thread {

    ControlChannel controlChannel;

    private static final int TIMEOUT = 1000 * 7;
    private boolean isActive;

    private Socket socketData;
    private OutputStream outData;
    private InputStream inData;
    private BufferedReader readerData;

    public DataChannel(ControlChannel controlChannel, Socket s, String ipClient, int portClient, int portData){
        try {
            this.controlChannel = controlChannel;
            this.socketData = new Socket(ipClient, portClient, InetAddress.getLocalHost(),portData);
        } catch (Exception e) {
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Data channel died: " + e);            
        }
    } 

    @Override
    public void run(){
        String request;

        try {
            // Setting a time limit
            this.socketData.setSoTimeout(TIMEOUT);
            this.socketData.setTcpNoDelay(true);

            // Input and output stream of the control socket
            outData = socketData.getOutputStream();
            inData = socketData.getInputStream();
            readerData = new BufferedReader(new InputStreamReader(inData));
            request = new String();

            while (true) {
                // Reading the input stream of the control socket
                request = readerData.readLine();
                if (request != null)
                    processRequest(request);
            }
        } catch (Exception any) {
            socketData.close();
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.err.println("Data Channel died: " + any);
            return;
        }
    }

    private void processRequest(String request) {
        String[] words = request.split(" ");
    
        if (words.length <= 0) {
            controlChannel.controlResponse(FTPCode().getMessage(502));
            return;
        }
    
        switch (words[0]) {
            case"SYN":
                requestSyn(words);
                break;
            case"ACK":
                requestAck(words);
                break;
            case"SYN, ACK":
                requestSynAck(request.split(","));
                break;
            default:
                controlChannel.controlResponse(FTPCode().getMessage(502));
                return;
            }

        controlChannel.controlResponse(FTPCode().getMessage(502));
        return;
    }


    void closeDataChannel(){
        try {
            socketData.close();
            outData = null;
            inData = null;
            readerData = null;
        } catch (Exception e) {
            System.out.println("Closes data connection: "+ e);
        }
    }

    void dataResponse(String response){
        try {
            outData.write(response.getBytes());
        } catch (Exception e) {
            System.out.println("Reponse Data connection error: "+e);
        }
    }

    void responseSyn(){
        dataResponse("SYN");
        return;
    }


    private void requestAck(String[] request){
        return;
    }


    private void requestSyn(String[] request){
        dataResponse("SYN, ACK");
        return;
    }

    private void requestSynAck(String[] request){
        
        /*//Check length of request
        if(request.length != 2){
            controlResponse(FTPCode().getMessage(502));
            return;
        }

        //Check if request is all characters
        if(request[0] != "SYN" && request[1] != " ACK")
                controlResponse(FTPCode().getMessage(501));
                return;
            }
        }*/

        dataResponse("ACK");
        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

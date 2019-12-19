
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


class DataChannel extends Thread {
    private static final int TIMEOUT = 1000 * 7;

    private ServerSocket serverDataChannel;
    private Socket socketData;
    private OutputStream outData;
    private InputStream inData;
    private BufferedReader readerData;

    private Deque<String> requestInQueue;


    private ControlChannel controlChannel;

    public DataChannel(ControlChannel controlChannel){
        try {
            serverDataChannel = new ServerSocket(0);
            requestInQueue = new LinkedList<String>();
            this.controlChannel = controlChannel;

        } catch (Exception e) {
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Data channel died: " + e);            
        }
    }

    public void startListening(){
        try {
            this.socketData = serverDataChannel.accept();
            // Setting a time limit
            this.socketData.setSoTimeout(TIMEOUT);
            this.socketData.setTcpNoDelay(true);

            // Input and output stream of the control socket
            outData = socketData.getOutputStream();
            inData = socketData.getInputStream();
            readerData = new BufferedReader(new InputStreamReader(inData));
        } catch (Exception e) {
            try {
                socketData.close();                
            } catch (Exception a){}
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Data channel died: " + e);            

        }
    }

    @Override
    public void run(){
            while (requestInQueue.peek() != null)
                processRequest(requestInQueue.removeFirst());
    }

    public void addRequestInQueue(String request){
        if(request == null)
            return;
        
        this.requestInQueue.addLast(request);
        return;

    }

    public int getPort(){
        return serverDataChannel.getLocalPort();
    }

    private void processRequest(String request) {
        String[] words = request.split(" ");
    
        if (words.length <= 0) {
            controlChannel.controlResponse(new FTPCode().getMessage(502));
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
                controlChannel.controlResponse(new FTPCode().getMessage(502));
                return;
            }

        controlChannel.controlResponse(new FTPCode().getMessage(502));
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

    void responseAck(){
        dataResponse("ACK");
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
            controlResponse(new FTPCode().getMessage(502));
            return;
        }

        //Check if request is all characters
        if(request[0] != "SYN" && request[1] != " ACK")
                controlResponse(FTPCode().getMessage(501));
                return;
            }
        }*/

        dataResponse("ACK");
        closeDataChannel();
        controlChannel.dataChannelWorking = false;
        controlChannel.controlResponse(new FTPCode().getMessage(200));
        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

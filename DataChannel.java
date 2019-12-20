
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


class DataChannel extends Thread {
    private static final int TIMEOUT = 1000 * 60;

    private ServerSocket serverDataChannel;
    private Socket socketData;
    private OutputStream outData;
    private InputStream inData;
    private BufferedReader readerData;
    private Boolean isBin;
    private Deque<String> requestInQueue;


    private ControlChannel controlChannel;

    public DataChannel(ControlChannel controlChannel,int port){
        try {
            isBin = true;
            serverDataChannel = new ServerSocket(port);
            requestInQueue = new LinkedList<String>();
            this.controlChannel = controlChannel;
        } catch (Exception e) {
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Data channel died: " + e);
        }
    }

    @Override
    public void run(){
        try {

                //Starts listening
                this.socketData = serverDataChannel.accept();
                // Setting a time limit
                this.socketData.setSoTimeout(TIMEOUT);
                this.socketData.setTcpNoDelay(true);
                // Input and output stream of the control socket
                outData = socketData.getOutputStream();
                inData = socketData.getInputStream();
                readerData = new BufferedReader(new InputStreamReader(inData));
                while (requestInQueue.peek() != null)
                    processRequest(requestInQueue.removeFirst());
                socketData.close();
        } catch (Exception e) {
            try {
                socketData.close();
            } catch (Exception a){
                System.out.println("Socket Data channel closed");
            }
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Socket Data channel died: " + e);
        }
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
            case "TYPE":
                if(words[1].equals("I")){
                    this.isBin = true;
                    break;
                }
                if(words[1].equals("A")){
                    this.isBin = false;
                    break;
                }
                break;

            case "RETR":
                File retrievedFile = VirtualFileSystem.getInstance().getFile(controlChannel.currentFolder,words[1]);
                try {
                    outData.write(retrievedFile.getContent());
                    controlChannel.controlResponse(new FTPCode().getMessage(226));
                    try {
                        socketData.close();
                    } catch (Exception a){
                        System.out.println("Socket Data channel closed");
                    }

                } catch (Exception e) {
                    controlChannel.controlResponse(new FTPCode().getMessage(426));
                    System.out.println("Response Data connection error: "+e);
                }
                break;

            case "STOR":
                byte[] finalContent = new byte[0];
                String finalMsg = new String();
                Folder currentFolder = controlChannel.currentFolder;
                do{
                    int count = 1;
                    if(isBin){
                        
                        byte[] buffer = new byte[8192]; // or 4096, or more
                        count = inData.read(buffer);

                        byte[] tmpContent = new byte[finalContent.length + buffer.length];
                        System.arraycopy(finalContent, 0, tmpContent, 0, finalContent.length);
                        System.arraycopy(buffer, 0, c, finalContent.length, buffer.length);
                        finalContent = tmpContent;
                    }else{
                        String tmpMsg = readerData().readLine();
                        if(tmpMsg != null)
                            tmpMsg += "\n";
                    }
                }while(tmpMsg != null && count > 1);
                if(isBin){
                    currentFolder.addFile(new File(words[1],finalContent));
                }else{
                    currentFolder.addFile(new File(words[1],finalMsg));
                }
                try {
                    socketData.close();
                } catch (Exception a){
                    System.out.println("Socket Data channel closed");
                }
                break;
            case "LIST":
                String list =  VirtualFileSystem.getInstance().getLIST(controlChannel.currentFolder,controlChannel.isLoggedIn());
                dataResponse(list);
                controlChannel.controlResponse(new FTPCode().getMessage(226));
                return;
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

        return;
    }

    void dataResponse(String response){
        try {
            outData.write(response.getBytes());
        } catch (Exception e) {
            System.out.println("Response Data connection error: "+e);
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

    void setIsBin(Boolean isBin){
        this.isBin = isBin;
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
        controlChannel.controlResponse(new FTPCode().getMessage(200));
        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

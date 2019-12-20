
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

    public DataChannel(ControlChannel controlChannel,int port, String typeConnection, String ipClient, int portClient){
        try {
            isBin = true;
            serverDataChannel = new ServerSocket(port);
            requestInQueue = new LinkedList<String>();
            this.controlChannel = controlChannel;

            switch (typeConnection) {
                case "EPSV":
                    controlChannel.controlResponse(new FTPCode().getMessage(229) +" (|||" + getPort() + "|)\r\n");
                    this.socketData = serverDataChannel.accept();
                    break;
                case "PASV":
                    int[] dataPort = controlChannel.getPassivePortAdrs(getPort());
                    controlChannel.controlResponse(new FTPCode().getMessage(227) +" (" + serverDataChannel.getInetAddress().toString().replace('.', ',').replace("/","") +"," + Integer.toString(dataPort[0]) + "," + Integer.toString(dataPort[1]) + ")\r\n");    
                    this.socketData = serverDataChannel.accept();
                    break;
                case "PORT":
                    this.socketData = new Socket(ipClient, portClient, serverDataChannel.getInetAddress(), port);
                    controlChannel.controlResponse(new FTPCode().getMessage(200));
                    break;
                default:
                    break;
            }
            
            
        } catch (Exception e) {
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Data channel died: " + e);
        }
    }

    @Override
    public void run(){
        try {

                //Starts listening
                System.out.print("Before listening");
                System.out.print("After listening");

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
                serverDataChannel.close();
                System.out.print("Closed everything and out");
                return;
        } catch (Exception e) {
            try {
                socketData.close();
            } catch (Exception a){
                System.out.println("Socket Data channel closed");
            }
            controlChannel.controlResponse(new FTPCode().getMessage(425));
            System.out.println("Socket Data channel died: " + e);
            return;
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
                try {
                File retrievedFile = VirtualFileSystem.getInstance().getFile(controlChannel.currentFolder,words[1]);
                    outData.write(retrievedFile.getContent());
                    controlChannel.controlResponse(new FTPCode().getMessage(226));
                    try {
                        socketData.close();
                        serverDataChannel.close();
                    } catch (Exception a){
                        System.out.println("Socket Data channel closed");
                        return;
                    }//TO HANDLE EXEPCEPTION

                } catch (Exception e) {
                    controlChannel.controlResponse(new FTPCode().getMessage(426));
                    System.out.println("Response Data connection error: "+e);
                }
                break;

            case "STOR":
                Folder currentFolder = controlChannel.currentFolder;
                try{
                if(isBin){
                    byte[] finalContent = new byte[0];
                    int count = 0;
                    do{
                        count = 0;
                            byte[] buffer = new byte[8192]; // or 4096, or more
                            count = inData.read(buffer);

                            byte[] tmpContent = new byte[finalContent.length + buffer.length];
                            System.arraycopy(finalContent, 0, tmpContent, 0, finalContent.length);
                            System.arraycopy(buffer, 0, tmpContent, finalContent.length, buffer.length);
                            finalContent = tmpContent;
                    }while( count > 0);
                    currentFolder.addFile(new File(words[1],finalContent));
                }else{
                    String finalMsg = "";
                    String tmpMsg;
                    do{
                            tmpMsg = readerData.readLine();
                            System.out.println(tmpMsg); 
                            if(tmpMsg != null)
                                finalMsg += (tmpMsg + "\n");
                        
                    }while(tmpMsg != null);
                    controlChannel.currentFolder.addFile(new File(words[1],finalMsg));
                }
                }catch(IOException e){
                    System.out.println("Data Reader error: " + e); 
                }
                try {
                    socketData.close();
                }catch (Exception a){
                    System.out.println("Socket Data channel closed");
                }
                System.out.println("YOOOOOOO");
                controlChannel.controlResponse(new FTPCode().getMessage(226));
                break;
            case "LIST":
                String list =  VirtualFileSystem.getInstance().getLIST(controlChannel.currentFolder,controlChannel.isLoggedIn);
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

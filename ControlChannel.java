
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class ControlChannel extends Thread {

    private static final int TIMEOUT = 1000 * 20;
    private Deque<String> requestInQueue;
    private boolean isActive;
    boolean dataChannelWorking;

    private final Socket socketControl;    
    private OutputStream outControl;
    private InputStream inControl;
    private BufferedReader readerControl;

    private DataChannel dataChannel;

    private Folder currentFolder;
    private Boolean isLoggedIn;
    public ControlChannel(Socket s) {
        this.socketControl = s;
        requestInQueue = new LinkedList<String>();
        //currentFolder = VirtualFileSystem.getInstance().getRoot();  IL Y A un null exception ici!
        isLoggedIn = false; 
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
            controlResponse(new FTPCode().getMessage(200));//Welcome Message
            while (true) {
                // Reading the input stream of the control socket
                request = readerControl.readLine();
                System.out.println("Request = "+ request);

                if (request != null)
                    requestInQueue.addLast(request);
                if(requestInQueue.peek() != null)
                    processRequest(requestInQueue.removeFirst());

                if(request == null && requestInQueue.peek() == null){
                    Server.threadKilled();
                    return;
                }
                
            }
        } catch (Exception any) {
            System.err.println("Control Channel died: " + any);
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
            case "SYST":
                controlResponse("215 " + System.getProperty("os.name").toString() +"\n\r");
                break;

            case "FEAT":
                requestFEAT();
                break;
    
            case "MDTM":
                if(words.length != 2){
                    controlResponse("502 Command Not Implemented");
                    return;
                }
                try{
                    Integer lastModified = VirtualFileSystem.getInstance().getFile(currentFolder, words[1]).getLastModified();
                    controlResponse(new FTPCode().getMessage(253) +" " +lastModified.toString() +"\n\r"); 
                }catch(VirtualFileException e){
                    controlResponse(new FTPCode().getMessage(550));
                }

                break;

            case "PASV":
                if(words.length > 1){
                    controlResponse(new FTPCode().getMessage(502));
                    return;
                }
                requestPASV();
                return;
            case "PORT":
                requestPORT(words);
                break;
            
            case "CDUP"://go to parent directory, no arg
                try{
                    currentFolder = VirtualFileSystem.getInstance().doCDUP(currentFolder);
                    controlResponse( new FTPCode().getMessage(200));
                }catch(VirtualFileException e){
                    controlResponse( new FTPCode().getMessage(550));
                }
                break;
            case "CWD"://change working directory, 1 arg directory path
                if(words.length != 2){
                    controlResponse(new FTPCode().getMessage(504));
                    return;
                }
                try{
                    VirtualFileSystem.getInstance().doCWD(currentFolder,words[1],isLoggedIn);
                    controlResponse(new FTPCode().getMessage(200));
                }catch(VirtualFileException e){
                    controlResponse(new FTPCode().getMessage(504));
                }catch(NotAuthorizedException r){
                    controlResponse(new FTPCode().getMessage(550));
                }
                break;

            case "LIST"://see current directory content, no arg( we dont have to handle the case where there is an arg)
                    String list =  VirtualFileSystem.getInstance().getLIST(currentFolder);
                    controlResponse(new FTPCode().getMessage(200));
                    controlResponse(list + "\n\r");
                break;

            case "PWD"://gives path of current directory, no arg
                break;

            case "DELETE":// delete file in the current directory, 1 arg, the file name
                break;

            case "GET":// download a file from working directory, 1 arg, the file name
                break;

            case "PUT":// put a file on the server, 1 arg the file name 
                break;
            case"QUIT":// no arg, disconnect from server
            case"BYE":
            case"EXIT":
            case"CLOSE":
            case"DISCONNECT":
                controlResponse("Disconnecting, BYE!");
                return;
            case"USER": //input user name, 1 arg, the user name
                requestUSER(words);
                break;
            case"PASS": //input password, 1 arg, the password(i think we have to decription it as we receive it cripted)
                break;
            case"RENAME":
                break; //rename a file in current directory , 2 args , current name of file, new filename.
                
            default: // error case
                return;
            }
    
        return;
    }

    private void requestUSER(String[] request){
        if(request.length != 2){
            controlResponse(new FTPCode().getMessage(502));
            return;
        }

        if(request[1].equals("anonymous")){
            controlResponse(new FTPCode().getMessage(230));
            isLoggedIn = true;
            return;
        }
        if(request[1].equals("SAM")){
            controlResponse(new FTPCode().getMessage(331));
            isLoggedIn = true;
            return;
        }

        controlResponse(new FTPCode().getMessage(430));
        return;
    }

    private void requestFEAT(){
        String extensionsSupported = "211-Extensions supported:\r\n";
        controlResponse(extensionsSupported);

        extensionsSupported =  " PASV\r\n";
        extensionsSupported += " PORT\r\n";
        extensionsSupported += " CDUP\r\n";
        extensionsSupported += " LIST\r\n";
        extensionsSupported += " DELETE\r\n";
        extensionsSupported += " GET\r\n";
        extensionsSupported += " PUT\r\n";
        extensionsSupported += " QUIT\r\n";
        extensionsSupported += " BYE\r\n";
        extensionsSupported += " EXIT\r\n";
        extensionsSupported += " CLOSE\r\n";
        extensionsSupported += " DISCONNECT\r\n";
        extensionsSupported += " USER\r\n";
        extensionsSupported += " PASS\r\n";
        extensionsSupported += " RENAME\r\n";
        extensionsSupported += "211 END\r\n";

        controlResponse(extensionsSupported);
        return;
    }

    private void requestPASV(){

        if(!dataChannelWorking){
            Integer portClient = socketControl.getPort();
            String ipClient = socketControl.getInetAddress().getAddress().toString();

            dataChannel = new DataChannel(this, ipClient, portClient, 0);
            int[] dataPort = getPassivePortAdrs(dataChannel.getPort());
            controlResponse(new FTPCode().getMessage(227) + " (" + socketControl.getLocalAddress().toString() + Integer.toString(dataPort[0]) + "." + Integer.toString(dataPort[1]) + ")\r\n");    
        }
        else{
            requestInQueue.addFirst("PASV\r\n");
        }
        return;
    }

    private int[] getPassivePortAdrs(int portNb){
        return new int[]{(portNb-(portNb%256))/256, portNb % 256};
    }

    void controlResponse(String response){
        try {
            outControl.write(response.getBytes());
        } catch (Exception e) {
            System.out.println("Reponse Control connection error: "+e);
        }
    }

    void requestPORT(String[] request){

        //Check length of request
        if(request.length != 2){
            controlResponse(new FTPCode().getMessage(502));
            return;
        }

        String[] interfaceClient = request[1].split(",");

        //Check if IP length is ok
        if(interfaceClient.length != 6){
            controlResponse(new FTPCode().getMessage(502));
            return;
        }

        //Check if IP is all number
        for (int i = 0; i < interfaceClient.length; i++) {
            try {
                Double.parseDouble(interfaceClient[i]);
            } catch (NumberFormatException e) {
                controlResponse(new FTPCode().getMessage(501));
                return;
            }
        }

        //Check if connection already init
        if(dataChannelWorking){
            requestInQueue.addFirst(request[0] + request[1]);
            return;
        }

        int portClient = transitionClientPort(Integer.parseInt(interfaceClient[4]), Integer.parseInt(interfaceClient[5]));
        String ipClient = interfaceClient[0] +"."+ interfaceClient[1] +"."+ interfaceClient[2] +"."+ interfaceClient[3];

        dataChannelWorking = true;
        this.dataChannel = new DataChannel(this, ipClient, portClient, 2001);
        if(dataChannel != null)
            dataChannel.responseSyn();
        else
            controlResponse(new FTPCode().getMessage(502));
        return;
    }

    int transitionClientPort(int x, int y){
        return x*256+y;
    }
}

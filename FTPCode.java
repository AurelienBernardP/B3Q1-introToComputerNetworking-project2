public class FTPCode {


    String getMessage(int code){
        
        switch(code ) {
            case 125:
                return "Data connection already open; transfer starting.\r\n";
            case 150:
                return "File status okay; about to open data connection.\r\n";
            case 200:
                return "200 command successful\r\n";
            case 202:
                return "Command not implemented, superfluous at this site.\r\n";
            case 211:
                return "System status, or system, help reply."; // needs precision
            case 212:
                return "Directory status.\r\n";
            case 213:
                return "File status.\r\n";
            case 220: 
                return "Service ready for new user. \r\n";
            case 221:
                return "Service closing control connection.\r\n";
            case 225:
                return "Data connection open; no transfer in progress.\r\n";
            case 226:
                return "Closing data connection. Requested file action successful\r\n";
            case 227:
                return "227 Entering Passive Mode\r\n";
            case 228:
                return "Entering Long Passive Mode\r\n";
            case 229:
                return "229 Entering Extended Passive Mode\r\n";
            case 230:
                return "230 User logged in, proceed.\r\n";
            case 231:
                return "User logged out\r\n";
            case 232:
                return "Logout command noted, will complete when transfer done.\r\n";
            case 250:
                return "Requested file action okay, completed. \r\n";
            case 253:
                return "253 last modification data correctly retrieved \r\n";
            case 257:
                return "257 ";
            case 331:
                return "User name okay, need password.\r\n";
            case 332:
                return "Need account for login.\r\n";
            case 350:
                return "Requested file action pending further information \r\n";
            case 421:
                return "Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down. \r\n";
            case 425:
                return "Can't open data connection.\r\n";
            case 426:
                return "Connection closed; transfer aborted.\r\n";
            case 430:
                return "Invalid username or password \r\n";
            case 434:
                return "Requested host unavailable.\r\n";
            case 450:
                return "Requested file action not taken.\r\n";
            case 451:
                return "Requested action aborted. Local error in processing.\r\n";
            case 452:
                return "Requested action not taken.\r\n";
            case 500:
                return "Syntax error in parameters or arguments.\r\n";
            case 502:
                return "502 Command not implemented.\r\n";
            case 503:
                return "503 Bad sequence of commands.\r\n";
            case 504:
                return "504 Command not implemented for those parameter.\r\n";
            case 530:
                return "530 Not logged in.\r\n";
            case 550:
                return "550 Requested action not taken. \r\n";
            case 551:
                return"Requested action aborted. Page type unknown.\r\n";
            case 552:
                return"Requested file action aborted. Exceeded storage allocation\r\n";
            case 553:
                return "Requested action not taken. File name not allowed.\r\n";
            case 631:
                return "Integrity protected reply. \r\n";
            case 10068:
                return "Too many users, server is full.\r\n";

        }
        return "Error code not Found for this issue\r\n";

    }


}
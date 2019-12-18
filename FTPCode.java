public class FTPCode {


    String getMessage(int code){
        
        switch(code ) {
            case 125:
                return "Data connection already open; transfer starting.\n\r";
            case 150:
                return "File status okay; about to open data connection.\n\r";
            case 200:
                return "200 command successful\n\r";
            case 202:
                return "Command not implemented, superfluous at this site.\n\r";
            case 211:
                return "System status, or system, help reply."; // needs precision
            case 212:
                return "Directory status.\n\r";
            case 213:
                return "File status.\n\r";
            case 220: 
                return "Service ready for new user. \n\r";
            case 221:
                return "Service closing control connection.\n\r";
            case 225:
                return "Data connection open; no transfer in progress.\n\r";
            case 226:
                return "Closing data connection. Requested file action successful\n\r";
            case 227:
                return "227 Entering Passive Mode\n\r";
            case 228:
                return "Entering Long Passive Mode\n\r";
            case 229:
                return "229 Entering Extended Passive Mode\n\r";
            case 230:
                return "User logged in, proceed.\n\r";
            case 231:
                return "User logged out\n\r";
            case 232:
                return "Logout command noted, will complete when transfer done.\n\r";
            case 250:
                return "Requested file action okay, completed. \n\r";
            case 253:
                return "253 last modification data correctly retrieved \n\r";
            case 257:
                return " created.\n\r";
            case 331:
                return "User name okay, need password.\n\r";
            case 332:
                return "Need account for login.\n\r";
            case 350:
                return "Requested file action pending further information \n\r";
            case 421:
                return "Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down. \n\r";
            case 425:
                return "Can't open data connection.\n\r";
            case 426:
                return "Connection closed; transfer aborted.\n\r";
            case 430:
                return "Invalid username or password \n\r";
            case 434:
                return "Requested host unavailable.\n\r";
            case 450:
                return "Requested file action not taken.\n\r";
            case 451:
                return "Requested action aborted. Local error in processing.\n\r";
            case 452:
                return "Requested action not taken.\n\r";
            case 500:
                return "Syntax error in parameters or arguments.\n\r";
            case 502:
                return "502 Command not implemented.\n\r";
            case 503:
                return "503 Bad sequence of commands.\n\r";
            case 504:
                return "504 Command not implemented for those parameter.\n\r";
            case 530:
                return "530 Not logged in.\n\r";
            case 550:
                return "550 Requested action not taken. \n\r";
            case 551:
                return"Requested action aborted. Page type unknown.\n\r";
            case 552:
                return"Requested file action aborted. Exceeded storage allocation\n\r";
            case 553:
                return "Requested action not taken. File name not allowed.\n\r";
            case 631:
                return "Integrity protected reply. \n\r";
            case 10068:
                return "Too many users, server is full.\n\r";

        }
        return "Error code not Found for this issue\n\r";

    }


}
public class FTPCode {


    String getMessage(Integer code){
        
        switch(code ) {
            case 125:
                return code.toString() + " Data connection already open; transfer starting.\n\r";
            case 150:
                return code.toString() + " File status okay; about to open data connection.\n\r";
            case 200:
                return code.toString() + " Command successful\n\r";
            case 202:
                return code.toString() + " Command not implemented, superfluous at this site.\n\r";
            case 211:
                return code.toString() + " System status, or system, help reply."; // needs precision
            case 212:
                return code.toString() + " Directory status.\n\r";
            case 213:
                return code.toString() + " File status.\n\r";
            case 220: 
                return code.toString() + " Service ready for new user. \n\r";
            case 221:
                return code.toString() + " Service closing control connection.\n\r";
            case 225:
                return code.toString() + " Data connection open; no transfer in progress.\n\r";
            case 226:
                return code.toString() + " Closing data connection. Requested file action successful\n\r";
            case 227:
                return code.toString() + " Entering Passive Mode\n\r";
            case 228:
                return code.toString() + " Entering Long Passive Mode\n\r";
            case 229:
                return code.toString() + " Entering Extended Passive Mode\n\r";
            case 230:
                return code.toString() + " User logged in, proceed.\n\r";
            case 231:
                return code.toString() + " User logged out\n\r";
            case 232:
                return code.toString() + " Logout command noted, will complete when transfer done.\n\r";
            case 250:
                return code.toString() + " Requested file action okay, completed. \n\r";
            case 253:
                return code.toString() + " last modification data correctly retrieved \n\r";
            case 257:
                return code.toString() + " created.\n\r";
            case 331:
                return code.toString() + " User name okay, need password.\n\r";
            case 332:
                return code.toString() + " Need account for login.\n\r";
            case 350:
                return code.toString() + " Requested file action pending further information \n\r";
            case 421:
                return code.toString() + " Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down. \n\r";
            case 425:
                return code.toString() + " Can't open data connection.\n\r";
            case 426:
                return code.toString() + " Connection closed; transfer aborted.\n\r";
            case 430:
                return code.toString() + " Invalid username or password \n\r";
            case 434:
                return code.toString() + " Requested host unavailable.\n\r";
            case 450:
                return code.toString() + " Requested file action not taken.\n\r";
            case 451:
                return code.toString() + " Requested action aborted. Local error in processing.\n\r";
            case 452:
                return code.toString() + " Requested action not taken.\n\r";
            case 500:
                return code.toString() + " Syntax error in parameters or arguments.\n\r";
            case 502:
                return code.toString() + " Command not implemented.\n\r";
            case 503:
                return code.toString() + " Bad sequence of commands.\n\r";
            case 504:
                return code.toString() + " Command not implemented for those parameter.\n\r";
            case 530:
                return code.toString() + " Not logged in.\n\r";
            case 550:
                return code.toString() + " Requested action not taken. \n\r";
            case 551:
                return code.toString() + " Requested action aborted. Page type unknown.\n\r";
            case 552:
                return code.toString() + " Requested file action aborted. Exceeded storage allocation\n\r";
            case 553:
                return code.toString() + " Requested action not taken. File name not allowed.\n\r";
            case 631:
                return code.toString() + " Integrity protected reply. \n\r";
            case 10068:
                return code.toString() + " Too many users, server is full.\n\r";

        }
        return code.toString() + " Error code not Found for this issue\n\r";

    }


}
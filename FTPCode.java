public class FTPCode {


    String getMessage(int code){
        
        switch(code ) {
            case 125:
                return Integer.toString(code) + " Data connection already open; transfer starting.\r\n";
            case 150:
                return Integer.toString(code) + " File status okay; about to open data connection.\r\n";
            case 200:
                return Integer.toString(code) + " Command successful\r\n";
            case 202:
                return Integer.toString(code) + " Command not implemented, superfluous at this site.\r\n";
            case 211:
                return Integer.toString(code) + " System status, or system, help reply."; // needs precision
            case 212:
                return Integer.toString(code) + " Directory status.\r\n";
            case 213:
                return Integer.toString(code) + " File status.\r\n";
            case 220: 
                return Integer.toString(code) + " Service ready for new user. \r\n";
            case 221:
                return Integer.toString(code) + " Service closing control connection.\r\n";
            case 225:
                return Integer.toString(code) + " Data connection open; no transfer in progress.\r\n";
            case 226:
                return Integer.toString(code) + " Closing data connection. Requested file action successful\r\n";
            case 227:
                return Integer.toString(code) + " Entering Passive Mode ";
            case 228:
                return Integer.toString(code) + " Entering Long Passive Mode ";
            case 229:
                return Integer.toString(code) + " Entering Extended Passive Mode ";
            case 230:
                return Integer.toString(code) + " User logged in, proceed.\r\n";
            case 231:
                return Integer.toString(code) + " User logged out\r\n";
            case 232:
                return Integer.toString(code) + " Logout command noted, will complete when transfer done.\r\n";
            case 250:
                return Integer.toString(code) + " Requested file action okay, completed. \r\n";
            case 253:
                return Integer.toString(code) + " last modification data correctly retrieved \r\n";
            case 257:
                return Integer.toString(code) + " ";
            case 331:
                return Integer.toString(code) + " User name received, need password.\r\n";
            case 332:
                return Integer.toString(code) + " Need account for login.\r\n";
            case 350:
                return Integer.toString(code) + " Requested file action pending further information \r\n";
            case 421:
                return Integer.toString(code) + " Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down. \r\n";
            case 425:
                return Integer.toString(code) + " Can't open data connection.\r\n";
            case 426:
                return Integer.toString(code) + " Connection closed; transfer aborted.\r\n";
            case 430:
                return Integer.toString(code) + " Invalid username or password \r\n";
            case 434:
                return Integer.toString(code) + " Requested host unavailable.\r\n";
            case 450:
                return Integer.toString(code) + " Requested file action not taken.\r\n";
            case 451:
                return Integer.toString(code) + " Requested action aborted. Local error in processing.\r\n";
            case 452:
                return Integer.toString(code) + " Requested action not taken.\r\n";
            case 500:
                return Integer.toString(code) + " Syntax error in parameters or arguments.\r\n";
            case 501:
                return Integer.toString(code) + " Syntax error in parameters or arguments.\r\n";
            case 502:
                return Integer.toString(code) + " Command not implemented.\r\n";
            case 503:
                return Integer.toString(code) + " Bad sequence of commands.\r\n";
            case 504:
                return Integer.toString(code) + " Command not implemented for those parameter.\r\n";
            case 530:
                return Integer.toString(code) + " Not logged in.\r\n";
            case 550:
                return Integer.toString(code) + " Requested action not taken. \r\n";
            case 551:
                return Integer.toString(code) + " Requested action aborted. Page type unknown.\r\n";
            case 552:
                return Integer.toString(code) + " Requested file action aborted. Exceeded storage allocation\r\n";
            case 553:
                return Integer.toString(code) + " Requested action not taken. File name not allowed.\r\n";
            case 631:
                return Integer.toString(code) + " Integrity protected reply. \r\n";
            case 10068:
                return Integer.toString(code) + " Too many users, server is full.\r\n";

        }
        return Integer.toString(code) + " Error code not Found for this issue\r\n";

    }


}
public class FTPCode {


    String getMessage(int code){
        
        switch(code ) {
            case 125:
                return "Data connection already open; transfer starting.";
            case 150:
                return "File status okay; about to open data connection.";
            case 200:
                return "200 PORT command successful";
            case 202:
                return "Command not implemented, superfluous at this site.";
            case 211:
                return "System status, or system, help reply.";
            case 212:
                return "Directory status.";
            case 213:
                return "File status.";
            case 220: 
                return "Service ready for new user. ";
            case 221:
                return "Service closing control connection.";
            case 225:
                return "Data connection open; no transfer in progress.";
            case 226:
                return "Closing data connection. Requested file action successful";
            case 227:
                return "227 Entering Passive Mode";
            case 228:
                return "Entering Long Passive Mode";
            case 229:
                return "229 Entering Extended Passive Mode";
            case 230:
                return "User logged in, proceed.";
            case 231:
                return "User logged out";
            case 232:
                return "Logout command noted, will complete when transfer done.";
            case 250:
                return "Requested file action okay, completed. ";
            case 253:
                return "253 last modification data correctly retrieved ";
            case 257:
                return " created.";
            case 331:
                return "User name okay, need password.";
            case 332:
                return "Need account for login.";
            case 350:
                return "Requested file action pending further information ";
            case 421:
                return "Service not available, closing control connection. This may be a reply to any command if the service knows it must shut down. ";
            case 425:
                return "Can't open data connection.";
            case 426:
                return "Connection closed; transfer aborted.";
            case 430:
                return "Invalid username or password ";
            case 434:
                return "Requested host unavailable.";
            case 450:
                return "Requested file action not taken.";
            case 451:
                return "Requested action aborted. Local error in processing.";
            case 452:
                return "Requested action not taken.";
            case 500:
                return "Syntax error in parameters or arguments.";
            case 502:
                return "Command not implemented.";
            case 503:
                return "Bad sequence of commands.";
            case 504:
                return "Command not implemented for that parameter.";
            case 530:
                return "Not logged in.";
            case 550:
                return "550 Requested action not taken. File unavailable. \n\r";
            case 551:
                return"Requested action aborted. Page type unknown.";
            case 552:
                return"Requested file action aborted. Exceeded storage allocation";
            case 553:
                return "Requested action not taken. File name not allowed.";
            case 631:
                return "Integrity protected reply. ";
            case 10068:
                return "Too many users, server is full.";

        }
        return "Error code not Found for this issue";

    }


}
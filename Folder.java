import java.text.DateFormat; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.*;
class Folder{

    private Boolean isPrivate ;
    private ArrayList<Folder> subFolders;
    private ArrayList<File> files;
    private String name;
    private Folder parent;

    Folder(String name, Boolean isPrivate,Folder parent){

        this.name = name;
        this.isPrivate = isPrivate;
        this.parent = parent;
    }

    void addFile(File newFile){

        Integer nbOccurances = 0 ;

        for (int i = 0; i < files.size(); i++){
            if(newFile.getName().equals(files.get(i).getName())){
                nbOccurances ++;
            }
        }

        if(nbOccurances > 0 ){/// makes error anyway
            newFile.setName(newFile.getName() + "("+ nbOccurances.toString()+")");
        }

        files.add(newFile);
    }

    String getName(){
        return name;
    }

    void addFolder(String name, Boolean isPrivate){
        subFolders.add(new Folder(name,isPrivate, this));
    }

    Folder getChildFolder(String name){
        for (int i = 0; i < subFolders.size(); i++){
            if(name.equals(subFolders.get(i).getName())){
                return subFolders.get(i);
            }
        }
        return null;
    }

    File getFile(String name){
        for (int i = 0; i < files.size(); i++){
            if(name.equals(files.get(i).getName())){
                return files.get(i);
            }
        }
        return null;
    }

    String getList(){
        String list = "";

        for (int i = 0; i < subFolders.size(); i++){
            list += (subFolders.get(i).getName() + " ");
        }
        for (int i = 0; i < files.size(); i++){
            list += (files.get(i).getName() + " ");
        }
        return list;
    }

    Folder getParent(){
        return parent;
    }

    void deleteFile(String name)throws VirtualFileException{
        for (int i = 0; i < files.size(); i++){
            if(name.equals(files.get(i).getName())){
                files.remove(i);
                return;
            }
        }
        throw new VirtualFileException();
    }

    boolean isPrivate(){
        return isPrivate;
    }
   
}


class VirtualFileSystem{
    private Folder root;
    private Boolean isLoggedIn;
    private byte[] myImg = {66,  77,  70,  1,  0,  0,    0,   0,   0,   0,  62,   0,   0,  0,   40,   0,
            0,   0,  34,  0,  0,  0,   33,   0,   0,   0,   1,   0,   1,  0,    0,   0,
            0,   0,   8,  1,  0,  0,    0,   0,   0,   0,   0,   0,   0,  0,    0,   0,
            0,   0,   0,  0,  0,  0,    0,   0,   0,   0,  -1,  -1,  -1,  0,   -1,  -1,
        -1,  -1, -64,  0,  0,  0,   -1, -32,   0,   1, -64,   0,   0,  0, -128,  31,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -8, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -8, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
        -1,  -2, -64,  0,  0,  0, -128,  31,  -1,  -8, -64,   0,   0,  0,   -1, -17,
        -1,  -2, -64,  0,  0,  0,   -1,  -9,  -1,  -2, -64,   0,   0,  0,   -1,  -5,
        -1,  -2, -64,  0,  0,  0,   -1,  -3,  -2,   0, -64,   0,   0,  0,   -1,  -2,
        -3,  -1, -64,  0,  0,  0,   -1,  -1, 126,  -1, -64,   0,   0,  0,   -1,  -1,
        127, 127, -64,  0,  0,  0,   -1,  -1, -65, 127, -64,   0,   0,  0,   -1,  -1,
        -33, -65, -64,  0,  0,  0,   -1,  -1, -17, -65, -64,   0,   0,  0,   -1,  -1,
        -17, -33, -64,  0,  0,  0,   -1,  -1,  -9, -33, -64,   0,   0,  0,   -1,  -1,
        -9, -33, -64,  0,  0,  0,   -1,  -1,  -8,  31, -64,   0,   0,  0,   -1,  -1,
        -1,  -1, -64,  0,  0,  0
        
    };

    private static VirtualFileSystem instance = null;

    static VirtualFileSystem getInstance(){

        if(instance != null ){
            return instance;
        }else{
            instance = new VirtualFileSystem();
            return instance;
        }
    }

    private VirtualFileSystem(){
        root = new Folder("/", false, null);

        root.addFolder("private",true);
        root.addFile(new File("myText.txt","Irasshaimase"));
        root.addFile(new File("myimage.bmp",myImg));
        Folder privateFolder = root.getChildFolder("private");
        privateFolder.addFile(new File("secret.txt", "UPUPDOWNDOWNLEFTRIGHTLEFTRIGHTBASTART"));
        isLoggedIn = false;
    }

    String getPWD(Folder currentFolder){
        String path = "";
        Folder tmp = currentFolder;
        while(tmp.getParent() != null){
            path = "/" + tmp.getName() + path;
            tmp = currentFolder.getParent();
        }
        return "/" + path;
    }

    String getLIST(Folder currentFolder){
        return currentFolder.getList();
    }

    Folder doCWD(Folder currentFolder,String childFolder, Boolean isLoggedIn)throws VirtualFileException , NotAuthorizedException{
        Folder nextFolder = currentFolder.getChildFolder(childFolder);
        if(nextFolder.isPrivate() && !isLoggedIn){
            throw new NotAuthorizedException();
        }
        
        if(nextFolder != null){
            return nextFolder;
        }else{
            throw new VirtualFileException();
        }
    }

    Folder doCDUP(Folder currentFolder)throws VirtualFileException{
        if(currentFolder.getParent() != null)
            return currentFolder.getParent();
        else{
            throw new VirtualFileException();
        }

    }

    File getFile(Folder currentFolder,String name)throws VirtualFileException{
        File tmp = currentFolder.getFile(name);
        if (tmp != null)
            return tmp;

        throw new VirtualFileException();
    }

    void addFile(Folder currentFolder, File newFile){
        currentFolder.addFile(newFile);
    }

    void renameFile(Folder currentFolder, String oldName, String newName)throws VirtualFileException{
        File toChange = currentFolder.getFile(oldName);
        if(toChange != null){
            toChange.setName(newName);
        }else{
            throw new VirtualFileException();
        }
    }
    void deleteFile(Folder currentFolder,String name)throws VirtualFileException{
        currentFolder.deleteFile(name);
    }

    Folder getRoot(){
        return root;
    }

}

class File{

    private String name;
    private byte[] content;
    private long lastModified;
    File(String name, byte[] content){
        this.name = name;
        this.content = content;
        lastModified = System.currentTimeMillis();
    }

    File(String name, String content){
        this.name = name;
        this.content = content.getBytes();
    }

    String getName(){
        return name;
    }

    byte[] getContent(){
        return content;
    }

    void setName(String newName){
        name = newName;
        lastModified = System.currentTimeMillis();
    }
    //FORMAT YYYYMMDDhhmmss
    int getLastModified(){
        DateFormat simple = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(this.lastModified);

        return Integer.parseInt(simple.format(date).toString());
    }



}

class VirtualFileException extends Exception{

    public VirtualFileException()
    {
        super();
    }
    public VirtualFileException(String s)
    {
        super(s);
    }
}

class NotAuthorizedException extends Exception{

    public NotAuthorizedException()
    {
        super();
    }
    public NotAuthorizedException(String s)
    {
        super(s);
    }
}
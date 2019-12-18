class Folder{

    private Boolean isPrivate ;
    private List<Folder> subFolders;
    private List<File> files;
    private String name;
    private Folder parent;

    Folder(String name, Boolean isPrivate,Folder parent){

        this.name = name;
        this.isPrivate = isPrivate;
        this.isRoot = isRoot;
        this.parent = parent;
    }

    void addFile(File newFile){

        Integer nbOccurances = 0 ;

        for (int i = 0; i < files.size(); i++){
            if(newFile.getName().equals(files.get(i).getName())){
                nbOccurances ++;
            }
        }

        if(nbOccurances > 0 ){
            newFile.setName(newFile.getName() + "("+ nbOccurances.toString()+")");
        }

        files.add(newFile);
    }

    void addFolder(String name, Boolean isPrivate){
        subFolder.add(new Folder(name,isPrivate, this));
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
        String list = new String();

        for (int i = 0; i < subFolders.size(); i++){
            
        }
        for (int i = 0; i < files.size(); i++){

        }
    }
   
}


class VirtualFileSystem{
    private Folder root;
    private Folder currentFolder;
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
    VirtualFileSystem(){
        root = new Folder(root, false, null);

        root.addFolder("private",true);
        root.addFile(new File("myText.txt","Irasshaimase"));

        root.addFile(new File("myimage.bmp",myImg));
        Folder privateFolder = root.getChildFolder("private");
        privateFolder.addFile(new File("secret.txt", "UPUPDOWNDOWNLEFTRIGHTLEFTRIGHTBASTART"));
        
    }

    String getPWD(){

    }

    String getLs(){

    }

    String doCd(String childFolder){
        
    }

    File getFile(String name){
        return currentFolder.getFile(name);
    }

}

class File{

    private String name;
    private byte[] content;

    File(String name, byte[] content){
        this.name = name;
        this.content = content;
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
    }

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs277filemanager;
import java.io.File;

/**
 *
 * @author alexmelford
 */
public class MyFileNode {
    File file;
    String filename;
    
    public MyFileNode(String filename){
        this.file = new File(filename);
    }

    public MyFileNode(String name, File f) {
        this.filename = name;
        this.file = f;
    }
    
    public File getFile(){
        return file;
    }
    
    public String toString(){
        if(file.getName().equals(""))
            return file.getPath();
        return file.getName();
    }
    
    public boolean isDirectory(){
        return file.isDirectory();
    }
}

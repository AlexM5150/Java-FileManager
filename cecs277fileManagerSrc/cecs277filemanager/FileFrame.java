/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs277filemanager;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

/**
 *
 * @author alexmelford
 */
public class FileFrame extends JInternalFrame{
    public static  String currentSelected;
    private JSplitPane splitpane;
    private DirPanel dirPanel;
    private FilePanel filePanel;
    public FileFrame() throws IOException{
        dirPanel = new DirPanel();
        filePanel = new FilePanel();
        init();
    }
    
    public FileFrame(File f) throws IOException{
        dirPanel = new DirPanel(f);
        
        filePanel = new FilePanel();
        init();
    }
    
    private void init(){
        filePanel.setFileFFrame(this);
        dirPanel.setFilePanel(filePanel);
        dirPanel.setFileFFrame(this);
        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dirPanel, filePanel);
        this.setFrameTitle(currentSelected);
        this.getContentPane().add(splitpane);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setResizable(true);
        this.setIconifiable(true);
        this.setSize(850,650);
        this.setVisible(true);
    }
    
    public DirPanel getdirPanel(){
        return dirPanel;
    }
    
    public FilePanel getfilePanel(){
        return filePanel;
    }
    
    public void setFrameTitle(String absolutePath) {
        this.setTitle(absolutePath);
    }
    
    
}

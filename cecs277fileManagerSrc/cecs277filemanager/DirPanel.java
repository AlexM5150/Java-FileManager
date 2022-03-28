/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs277filemanager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 *
 * @author alexmelford
 */
public class DirPanel extends JPanel{
    private JScrollPane scrollPane = new JScrollPane();
    private JTree dirtree = new JTree();
    private DefaultTreeModel treemodel;
    private File rootFile;
    private FilePanel filePanel;
    private FileFrame fileFrame;
    
    public DirPanel() throws IOException{
        dirtree.setPreferredSize(new Dimension(300,300));
        dirtree.addTreeSelectionListener(new MyTreeSelectionListener());
        
        rootFile = new File(System.getProperty("user.home"));
        while(rootFile.getParentFile() != null)
            rootFile = rootFile.getParentFile();
        buildTree();
    }
    
    public DirPanel(File root) throws IOException{
        dirtree.setPreferredSize(new Dimension(300,300));
        dirtree.addTreeSelectionListener(new MyTreeSelectionListener());
        
        rootFile = root;
        //while(rootFile.getParentFile() != null)
        //    rootFile = rootFile.getParentFile();
        buildTree();
    }
    
    public void setRootFile(File rf){
        rootFile = rf;
    }
    
    public File getRootFile(){
        return rootFile;
    }
    
    public void setFilePanel(FilePanel fp){
        filePanel = fp;
    }
    
    public void setFileFFrame(FileFrame ff){
        fileFrame = ff;
    }
    
    public JTree getTree(){
        return dirtree;
    }
    
    private void buildTree() throws IOException{
        MyFileNode root = new MyFileNode(rootFile.getPath(), rootFile);
        DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(root);
        treemodel = new DefaultTreeModel(topNode);
        
        dirtree.setRootVisible(true);
        dirtree = new JTree(topNode);
        
        System.out.println(rootFile.getPath());
        dirtree.expandRow(0);
        
        FileFrame.currentSelected = rootFile.getAbsolutePath();
        createNodes(rootFile, topNode);
        dirtree.expandRow(0);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) dirtree.getCellRenderer();
        renderer.setLeafIcon(renderer.getClosedIcon());
        
        changeFilePanel();
        dirtree.addTreeSelectionListener(new MyTreeSelectionListener());        
        dirtree.setModel(treemodel);
        scrollPane.setViewportView(dirtree);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        dirtree.treeDidChange();
    }
    
    private void createNodes(File dir, DefaultMutableTreeNode topNode) throws IOException{
        File[] files;
        DefaultMutableTreeNode childNode = null;
        
        if(dir.isDirectory()){
            files = dir.listFiles();
            if(files == null)
                return;
            
            for(int i=0; i<files.length; i++){
                if(!files[i].isHidden() && files[i].isDirectory()){
                    MyFileNode mfn = new MyFileNode(files[i].getName(), files[i]);
                    childNode = new DefaultMutableTreeNode(mfn);
                    childNode.setAllowsChildren(true);
                    
                    topNode.add(childNode);                    
                }
            }
        }  
    }
    
    
    private void recurseNodes(DefaultMutableTreeNode topNode, File dir){
        File[] files;
        DefaultMutableTreeNode childNode = null;
        
        if(dir.isDirectory()){
            files = dir.listFiles();
            if(files == null)
                return;
            
            for(int i=0; i<files.length; i++){
                if(!files[i].isHidden() && files[i].isDirectory()){
                    MyFileNode mfn = new MyFileNode(files[i].getName(), files[i]);
                    childNode = new DefaultMutableTreeNode(mfn);
                    recurseNodes(childNode, files[i]);
                    childNode.setAllowsChildren(true);
                    topNode.add(childNode);
                }
            }
        }
    }
        
    
    public void changeFilePanel(){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) dirtree.getLastSelectedPathComponent();
        if(node == null)
            return;
        MyFileNode mfnInfo = (MyFileNode) node.getUserObject();
        System.out.println(mfnInfo.getFile().getAbsolutePath());
        filePanel.fillList(mfnInfo.getFile());
        fileFrame.setFrameTitle(mfnInfo.getFile().getAbsolutePath());
    }
    
    private void expandTree(){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) dirtree.getLastSelectedPathComponent();
        if(node == null)
            return;
        
        DefaultMutableTreeNode childNode = null;
        MyFileNode mfnInfo = (MyFileNode) node.getUserObject();
        
        File f = mfnInfo.getFile();
        File[] files = f.listFiles();
        for(int i=0; i<files.length; i++){
            if(!files[i].isHidden() && files[i].isDirectory()){
                MyFileNode mfn = new MyFileNode(files[i].getName(), files[i]);
                childNode = new DefaultMutableTreeNode(mfn);
                recurseNodes(childNode, files[i]);
                childNode.setAllowsChildren(true);
                node.add(childNode);
            }
        }
    }
    
    public class MyTreeSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e){
            changeFilePanel();
            expandTree();
        }

    }
}

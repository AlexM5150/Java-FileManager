package cecs277filemanager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;



/**
 *
 * @author alex
 */

public class FilePanel extends JPanel {
    private JList list = new JList();
    private DefaultListModel model;
    private JScrollPane scrollPane = new JScrollPane();
    private FileFrame ff;
    
    public FilePanel(){
        
        this.setDropTarget(new MyDropTarget());
        list.setDragEnabled(true);
        list.addMouseListener( new MySelectionListener());
        list.setCellRenderer( new FileRendererSimple() );
        buildModel();
 
        
    }
    private void buildModel(){
        model  = new DefaultListModel();
        list.setModel(model);
        scrollPane.setViewportView(list);
        list.setVisibleRowCount(-1);
        list.revalidate();
        this.setPreferredSize(new Dimension(600,600));
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        
    }

    void fillList(File dir) {
        File[] files;
        
        files = dir.listFiles();
        model.clear();
        list.removeAll();
        for(int i=0; i<files.length; i++){
            if(!files[i].isHidden() && files[i].isDirectory())
                model.addElement(files[i]);
        }
        for(int i=0; i<files.length; i++){
            if(!files[i].isHidden() && !files[i].isDirectory())
                model.addElement(files[i]);
        }
        list.setModel(model);
    }
    
    public void setFileFFrame(FileFrame fileFrame){
        ff = fileFrame;
    }
    
    public JList getJlist(){
        return list;
    }
    
    public  File getSelectedFile(){
        int minIndex = list.getMinSelectionIndex();
        int maxIndex = list.getMaxSelectionIndex();
        for (int i = minIndex; i <= maxIndex; i++) {
                if (list.isSelectedIndex(i)) {
                    File file;
                    file = (File) list.getModel().getElementAt(i);
                    return file;
                }
            }
        return null;
        }
    
    public void delete(){
        
        File f = getSelectedFile();
        //DefaultListModel model = (DefaultListModel) list.getModel();
        int a = JOptionPane.showConfirmDialog(ff,"Delete?  " + f.getAbsolutePath());  
        if(a == JOptionPane.YES_OPTION){
            f.delete();
            model.removeElement(f);
        }
        
    }
    
    public void rename() throws IOException{
        File f = getSelectedFile();
        DataBack db = new DataBack(null, true);
        db.setFromField(f.getName());
        db.setFrameTitle("Rename");
        db.setDirectory(ff.getTitle());
        db.setVisible(true);
        String to = db.getToField();
        if (to.length() > 0){
            Path source = Paths.get(f.getAbsolutePath());
            Files.move(source, source.resolveSibling(to));
            ff.getdirPanel().changeFilePanel();
        }
    }
    
    public void copy() throws IOException{
        
        File f = getSelectedFile();
        DataBack db1 = new DataBack(null, true);
        db1.setFromField(f.getName());
        db1.setFrameTitle("Copying");
        db1.setDirectory(ff.getTitle());
        db1.setVisible(true);
        String to = db1.getToField();
        if (to.length() > 0){
            MyFileNode mfn = new MyFileNode(to);
            String toPath = mfn.getFile().getAbsolutePath();
            Path source = Paths.get(f.getAbsolutePath());
            Path dest = Paths.get(toPath);
     
            Files.copy(source, dest);
            ff.getdirPanel().changeFilePanel();
        }
        
    }

    
    private class PopupActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("Rename")){
                System.out.println("Renaming File");
                try {
                    rename();
                } catch (IOException ex) {
                    Logger.getLogger(FilePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(e.getActionCommand().equals("Copy")){
		System.out.println("Copying Program");
                try {
                    copy();
                } catch (IOException ex) {
                    Logger.getLogger(FilePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(e.getActionCommand().equals("Paste")){
                System.out.println("Pasting File");
            }
            else if(e.getActionCommand().equals("Delete")){
                System.out.println("Deleting File");
                delete();
            }
        }
    }    
        
    public JPopupMenu buildPopup(){
        ActionListener actionListener = new PopupActionListener();
        JMenuItem rename, copy, delete, paste;
        rename = new JMenuItem("Rename");
        rename.addActionListener(actionListener);
        copy = new JMenuItem("Copy");
        copy.addActionListener(actionListener);
        delete = new JMenuItem("Delete");
        delete.addActionListener(actionListener);        
        paste = new JMenuItem("Paste");
        paste.addActionListener(actionListener);
        JPopupMenu popup = new JPopupMenu();
        popup.add(rename);
        popup.add(copy);
        popup.add(paste);
        popup.addSeparator();
        popup.add(delete);
        return popup;
    }
    
    private class MySelectionListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                Desktop desktop = Desktop.getDesktop();
                File f = getSelectedFile();
                try {
                        desktop.open(f);
                } catch (IOException ex) {

                    }
            }
            else if ( SwingUtilities.isRightMouseButton(e) ){
                JPopupMenu popup = buildPopup();
                
                popup.show(list, e.getX(), e.getY()); //and show the menu
            }
            
        }
        
    }
    
    
   
    /*************************************************************************
     * class MyDropTarget handles the dropping of files onto its owner
     * (whatever JList it is assigned to). As written, it can process two
     * types: strings and files (String, File). The String type is necessary
     * to process internal source drops from another FilePanel object. The
     * File type is necessary to process drops from external sources such 
     * as Windows Explorer or IOS.
     * 
     * Note: no code is provided that actually copies files to the target
     * directory. Also, you may need to adjust this code if your list model
     * is not the default model. JList assumes a toString operation is
     * defined for whatever class is used.
     */
    class MyDropTarget extends DropTarget {
    /**************************************************************************
     * 
     * @param evt the event that caused this drop operation to be invoked
     */    
        
        public void drop(DropTargetDropEvent evt){
            JTree tree = ff.getdirPanel().getTree();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if(node == null)
                return;
            MyFileNode mfnInfo = (MyFileNode) node.getUserObject();
            String selected = mfnInfo.getFile().getAbsolutePath() + File.separator;
            
            MyFileNode dropFile, srcFile;
            
            try {
                //types of events accepted
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                //storage to hold the drop data for processing
                List result = new ArrayList();
                //what is being dropped? First, Strings are processed
                if(evt.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)){     
                    String temp = (String)evt.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    //String events are concatenated if more than one list item 
                    //selected in the source. The strings are separated by
                    //newline characters. Use split to break the string into
                    //individual file names and store in String[]
                    String[] next = temp.split("\\n");
                    //add the strings to the listmodel
                    for(int i=0; i<next.length;i++){
                        dropFile = new MyFileNode(next[i]);
                        model.addElement(dropFile.getFile());
                        selected = selected + dropFile.getFile().getName();
                        Path src = Paths.get(dropFile.getFile().getAbsolutePath());
                        Path destPath = Paths.get(selected);
                        System.out.println(destPath);
                        Files.copy(src, destPath);
                    }    
                }
                else{ //then if not String, Files are assumed
                    result =(List)evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    //process the input
                    for(Object o : result){
                        System.out.println(o.toString());
                        model.addElement(o);
                        srcFile = new MyFileNode(o.toString());
                        Path src = Paths.get(srcFile.getFile().getAbsolutePath());
                        selected = selected + srcFile.getFile().getName();
                        Path destPath = Paths.get(selected);
                        System.out.println(destPath);
                        Files.copy(src, destPath);
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
    }

}


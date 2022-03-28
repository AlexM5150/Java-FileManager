package cecs277filemanager;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alex
 */
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

        
public class AppGUI extends JFrame {
    JPanel panel, topPanel;
    JMenuBar menubar;
    JToolBar toolbar, drivebar, statusbar;
    JDesktopPane desktop;
    FileFrame ff;
    JButton simple, details;
    String currentDrive;
    JComboBox combo;
    public AppGUI() throws IOException{
        panel = new JPanel();
        topPanel = new JPanel();
        menubar = new JMenuBar();
        toolbar = new JToolBar();
        drivebar = new JToolBar();
        statusbar = new JToolBar();
        desktop = new JDesktopPane();
        ff = new FileFrame();
        
        
    }
    public void go(){
        this.setTitle("CECS 277 File Manager");
        panel.setLayout(new BorderLayout());
        topPanel.setLayout(new BorderLayout());
        
    	buildMenu();
        topPanel.add(menubar, BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.NORTH);
        this.add(panel);
        
        desktop.add(ff);
        panel.add(desktop, BorderLayout.CENTER);
        
        buildToolBar();
        topPanel.add(toolbar, BorderLayout.SOUTH);
        
        buildStatusBar();
        panel.add(statusbar, BorderLayout.SOUTH);
        this.setSize(1000,850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
    }
    
    private void buildMenu() {
    	JMenu fileMenu, helpMenu, treeMenu, windowMenu;
    	fileMenu = new JMenu("File");
    	treeMenu = new JMenu("Tree");
    	windowMenu = new JMenu("Window");
    	helpMenu = new JMenu("Help");
    	
        JMenuItem rename = new JMenuItem("Rename");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem delete = new JMenuItem("Delete");
    	JMenuItem run = new JMenuItem("Run");
    	JMenuItem exit = new JMenuItem("Exit");
        
        JMenuItem Expand_Branch = new JMenuItem("Expand Branch");
        JMenuItem Collapse_Branch = new JMenuItem("Collapse Branch");
        
        JMenuItem New = new JMenuItem("New");
        JMenuItem cascade = new JMenuItem("Cascade");
        
    	JMenuItem about = new JMenuItem("About");
        JMenuItem help = new JMenuItem("Help");

    	run.addActionListener(new FileActionListener());
        copy.addActionListener(new FileActionListener());
        delete.addActionListener(new FileActionListener());
    	rename.addActionListener(new FileActionListener());
    	exit.addActionListener(new FileActionListener());
        
        Expand_Branch.addActionListener(new TreeActionListener());
        Collapse_Branch.addActionListener(new TreeActionListener());
        
        New.addActionListener(new WindowActionListener());
    	cascade.addActionListener(new WindowActionListener());

    	about.addActionListener(new HelpActionListener());
    	help.addActionListener(new HelpActionListener());
        
        fileMenu.add(rename);
    	fileMenu.add(copy);
        fileMenu.add(delete);
        fileMenu.add(run);
        fileMenu.add(exit);
        
        treeMenu.add(Expand_Branch);
        treeMenu.add(Collapse_Branch);
        
        windowMenu.add(New);
        windowMenu.add(cascade);

    	helpMenu.add(about);
        helpMenu.add(help);
    	

    	menubar.add(fileMenu);
        menubar.add(treeMenu);
        menubar.add(windowMenu);
    	menubar.add(helpMenu);
    }
    
    private void buildStatusBar(){
        statusbar.removeAll();

        File f = (File) combo.getSelectedItem();
        JLabel labels = new JLabel("Current Drive: " + f
                + "     Free Space: " + f.getFreeSpace()/(1024*1024*1024)
                + "GB     Used Space: " + (f.getTotalSpace() - f.getFreeSpace())/(1024*1024*1024)
                + "GB     Total Space: " + f.getTotalSpace()/(1024*1024*1024) + "GB");
        statusbar.add(labels);
    }
    
    private void buildToolBar(){
        JPanel p = new JPanel();
        combo = new JComboBox(getDrives());
        combo.addActionListener(new comboActionListener());
        details = new JButton("Details");
        simple = new JButton("Simple");
        details.addActionListener(new ToolBarActionListener());
        simple.addActionListener(new ToolBarActionListener());
        p.add(combo);
        p.add(details);
        p.add(simple);
        toolbar.add(p);
    }
    
    public File[] getDrives(){
        File[] paths;
        paths = File.listRoots();
        for(File path : paths) 
            System.out.println(path);
        return paths;
    }
    
    public FileFrame getFileFFrame(){
        return (FileFrame) desktop.getSelectedFrame();
    }

    private class comboActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            buildStatusBar();
            File f = (File) combo.getSelectedItem();
            FileFrame newff;
            try {
                newff = new FileFrame(f);
                desktop.add(newff).setLocation(0,100);
                newff.toFront();
            } catch (IOException ex) {
                Logger.getLogger(AppGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        
    }
    

    private class ToolBarActionListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    FileFrame frame = getFileFFrame();
                    FilePanel fp = frame.getfilePanel();
                    JList list = fp.getJlist();
                    if(e.getActionCommand().equals("Details")){
                            list.setCellRenderer( new FileRenderer() );
                        }
                        else if(e.getActionCommand().equals("Simple")){
				list.setCellRenderer( new FileRendererSimple() );
                        }
                    }
                }
    
    private class FileActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
                    FileFrame frame = getFileFFrame();
                    FilePanel fp = frame.getfilePanel();
                        if(e.getActionCommand().equals("Exit")){
                            System.exit(0);
                        }
                        else if(e.getActionCommand().equals("Run")){
				System.out.println("Running File");
                                Desktop desktop = Desktop.getDesktop();
                                File f = fp.getSelectedFile();
                                try {
                                        desktop.open(f);
                                } catch (IOException ex) {

                                    }
                        }
			else if(e.getActionCommand().equals("Delete")){
				System.out.println("Deleting File");
                                fp.delete();
                        }
                        else if(e.getActionCommand().equals("Rename")){
				System.out.println("Renaming File");
                                try {
                                    fp.rename();
                                } catch (IOException ex) {
                                    Logger.getLogger(AppGUI.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
			else if(e.getActionCommand().equals("Copy")){
				System.out.println("Copied File");
                                try {
                                    fp.copy();
                                } catch (IOException ex) {
                                    Logger.getLogger(AppGUI.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
		}
    }
    
    
    private class HelpActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
                        if(e.getActionCommand().equals("Help")){
                            HelpDlg dlg1 = new HelpDlg(null, true);
                            dlg1.setVisible(true);
                        }
                            
                        else if(e.getActionCommand().equals("About")){
                            AboutDlg dlg2 = new AboutDlg(null, true);
                            dlg2.setVisible(true);
                        }
		}
    }  
    
    private class TreeActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
                    System.out.println("Expanded Branch");
                    FileFrame frame = getFileFFrame();
                    JTree tree = frame.getdirPanel().getTree();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                        if(node == null)
                            return;
                        TreePath path = new TreePath(node.getPath());
                        if(e.getActionCommand().equals("Expand Branch")){
                            tree.expandPath(path);
                        }
                            
                        else if(e.getActionCommand().equals("Collapse Branch")){
                            System.out.println("Collapsed Branch");
                            tree.collapsePath(path);
                        }
		}
    }
    
    private class WindowActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
                        if(e.getActionCommand().equals("New")){
                            try {
                                FileFrame newff = new FileFrame();
                                desktop.add(newff).setLocation(0,100);
                                newff.toFront();
                            } catch (IOException ex) {
                                Logger.getLogger(AppGUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                            
                        else if(e.getActionCommand().equals("Cascade")){
                            System.out.println("Window cascading in progress");
                            int x = 0;
                            for (JInternalFrame f : desktop.getAllFrames()){
                                f.setLocation(x*30, x*30);
                                x++;
                            }
                        }
		}
    }  
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs277filemanager;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author alexmelford
 */
class FileRendererSimple extends DefaultListCellRenderer{ 
    String fileName;
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        File file = (File)value;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        
        setIcon(fsv.getSystemIcon(file));
   
        fileName = file.getName();
        
        setText(fileName);

        return this;
    }
    
}
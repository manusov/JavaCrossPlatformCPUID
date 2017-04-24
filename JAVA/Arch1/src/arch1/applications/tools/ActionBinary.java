//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// User interface dialogue window and target operation execution
// for SAVE BINARY, LOAD BINARY operations.

package arch1.applications.tools;

import arch1.kernel.IOPB;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ActionBinary {
private static final JFileChooser fc = new JFileChooser();
private static final String FILE_NAME = "cpuid.bin";  // MAKE THIS AS PARM.
private static FileNameExtensionFilter filter;

//---------- Entry point for LB = "Load binary" dialogue method, setup GUI -----
public boolean createDialogLB
    ( JFrame parentWin, long[] opb ) 
    {
    boolean loaded=false;
    fc.setDialogTitle("Load binary - select file");
    filter = new FileNameExtensionFilter("Binary dump file", "bin");
    fc.setFileFilter(filter);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setSelectedFile(new File(FILE_NAME));
    int select = fc.showOpenDialog(parentWin);
    if(select==JFileChooser.APPROVE_OPTION)
        {
        String s1 = fc.getSelectedFile().getPath();
        int status = loadBinary( parentWin, s1, opb );
        if ( status==0 ) { loaded=true; }
        }
    return loaded;
    }

//---------- Entry point for SB = "Save binary" dialogue method, setup GUI -----
public void createDialogSB
    ( JFrame parentWin, long[] opb )
    /*
    {
    fc.setDialogTitle("Save binary - select directory");
    filter = new FileNameExtensionFilter("*.bin","*.*");
    fc.setFileFilter(filter);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setSelectedFile(new File(fileName));
    int select = fc.showSaveDialog(parentWin);
    if(select==JFileChooser.APPROVE_OPTION)
        {
        String s1 = fc.getSelectedFile().getPath();
        saveBinary( parentWin, s1, opb );
        }
    }
    */
            
    {
    fc.setDialogTitle("Save binary - select directory");
    filter = new FileNameExtensionFilter ( "Binary files" , "bin" );
    fc.setFileFilter(filter);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setSelectedFile(new File(FILE_NAME));
    
    //--- (re)start dialogue ---
    boolean inDialogue = true;
    restartDialogue:
    while(inDialogue)
        {
        int select = fc.showSaveDialog(parentWin);
        //--- save file ---
        if(select==JFileChooser.APPROVE_OPTION)
            {
                
            String s1 = fc.getSelectedFile().getPath();
            int x0 = JOptionPane.YES_OPTION;
            //--- check file exist and warning message ---
            File file = new File(s1);
            if( file.exists() == true )
                {
                x0 = JOptionPane.showConfirmDialog
                    ( null, 
                    "File exist: " + s1 + "\noverwrite?" , "REPORT" ,
                    JOptionPane.YES_NO_CANCEL_OPTION ,
                    JOptionPane.WARNING_MESSAGE );  // or QUESTION_MESSAGE
                }
            //--- Select operation by user selection ---
            // if ( x0 == JOptionPane.YES_OPTION )     // reserved, no actions 
            if ( ( x0 == JOptionPane.NO_OPTION  ) |
                 ( x0 == JOptionPane.CLOSED_OPTION ) )
                { continue restartDialogue; }
            if ( x0 == JOptionPane.CANCEL_OPTION ) 
                { inDialogue = false; continue restartDialogue; }
            //-- save binary ---
            saveBinary( parentWin, s1, opb );
            //---
            inDialogue = false;
            }  else { inDialogue = false; }
        }    //--- End of save dialogue cycle ---
    }        //--- End of method ---

    
//---------- Helper method for save binary to file and visual status -----------
private static void saveBinary
                   ( JFrame parentWin, String filePath, long[] opbData )
    {
    long x = opbData[0];
    int opbLength = (int) ( x & (long)((long)-1>>>32) );
    opbLength *= 4;
    byte[] fileData = IOPB.receiveBytes( opbData, 4, opbLength );
    
    int status=0;
    try ( FileOutputStream writer = new FileOutputStream(filePath, false) )
        { writer.write(fileData); writer.flush(); }
    catch(Exception ex) { status=1; }
            
    if (status==0) {
                   JOptionPane.showMessageDialog
                   (parentWin, "Binary file saved: " + filePath, "SAVE BINARY",
                   JOptionPane.WARNING_MESSAGE); }
            else   {
                   JOptionPane.showMessageDialog
                   (parentWin, "Binary file write failed", "ERROR",
                   JOptionPane.ERROR_MESSAGE); }
    }

//---------- Helper method for load binary from file and visual status ---------
private static int loadBinary
                   ( JFrame parentWin, String filePath, long[] opbData )
    {
    int status=0;
    int availableBytes = 0;
    int opbBytes = opbData.length * 8;
    byte[] fileData = new byte[opbBytes];
    try { FileInputStream reader = new FileInputStream(filePath);
          availableBytes = reader.available();
          reader.read ( fileData, 0, availableBytes );
        } catch(IOException ex) { status=1; } 
    if (status==0) {
                   int opbPart = availableBytes / 8;
                   IOPB.transmitBytes(fileData, opbData, 4, opbPart );
                   opbData[0] = opbPart / 4;
                   opbData[1] = 0;
                   opbData[2] = 0;
                   opbData[3] = 0;
                   JOptionPane.showMessageDialog
                   (parentWin, "Binary file loaded: " + filePath, "LOAD BINARY",
                   JOptionPane.WARNING_MESSAGE); }
            else   {
                   JOptionPane.showMessageDialog
                   (parentWin, "Binary file read failed", "ERROR",
                   JOptionPane.ERROR_MESSAGE); }

    return status;
    }

}

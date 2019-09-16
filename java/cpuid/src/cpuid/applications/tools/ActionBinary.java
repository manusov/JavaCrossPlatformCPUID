/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
User interface dialogue window and target operation execution
for SAVE BINARY, LOAD BINARY operations.
*/

package cpuid.applications.tools;

import cpuid.kernel.IOPB;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ActionBinary 
{
private static final JFileChooser FC = new JFileChooser();
private static final String FILE_NAME = "cpuid.bin";  // MAKE THIS AS PARM.
private static FileNameExtensionFilter filter;

// Handler for LB = "Load binary" dialogue method, setup GUI
public boolean createDialogLB
    ( JFrame parentWin, long[] opb ) 
    {
    boolean loaded=false;
    FC.setDialogTitle("Load binary - select file");
    filter = new FileNameExtensionFilter("Binary dump file", "bin");
    FC.setFileFilter(filter);
    FC.setFileSelectionMode(JFileChooser.FILES_ONLY);
    FC.setSelectedFile(new File(FILE_NAME));
    int select = FC.showOpenDialog(parentWin);
    if(select==JFileChooser.APPROVE_OPTION)
        {
        String s1 = FC.getSelectedFile().getPath();
        int status = loadBinary( parentWin, s1, opb );
        if ( status==0 ) { loaded=true; }
        }
    return loaded;
    }

// Handler for SB = "Save binary" dialogue method, setup GUI
public void createDialogSB
    ( JFrame parentWin, long[] opb )
    {
    FC.setDialogTitle("Save binary - select directory");
    filter = new FileNameExtensionFilter ( "Binary files" , "bin" );
    FC.setFileFilter(filter);
    FC.setFileSelectionMode(JFileChooser.FILES_ONLY);
    FC.setSelectedFile(new File(FILE_NAME));
    
    // (re)start dialogue
    boolean inDialogue = true;
    restartDialogue:
    while(inDialogue)
        {
        int select = FC.showSaveDialog(parentWin);
        // save file
        if(select==JFileChooser.APPROVE_OPTION)
            {
            String s1 = FC.getSelectedFile().getPath();
            int x0 = JOptionPane.YES_OPTION;
            // check file exist and warning message
            File file = new File(s1);
            if( file.exists() == true )
                {
                x0 = JOptionPane.showConfirmDialog
                    ( null, 
                    "File exist: " + s1 + "\noverwrite?" , "REPORT" ,
                    JOptionPane.YES_NO_CANCEL_OPTION ,
                    JOptionPane.WARNING_MESSAGE );  // or QUESTION_MESSAGE
                }
            // select operation by user selection
            if ( ( x0 == JOptionPane.NO_OPTION  ) |
                 ( x0 == JOptionPane.CLOSED_OPTION ) )
                { continue restartDialogue; }
            if ( x0 == JOptionPane.CANCEL_OPTION ) 
                { inDialogue = false; continue restartDialogue; }
            // save binary
            saveBinary( parentWin, s1, opb );
            //---
            inDialogue = false;
            }  else { inDialogue = false; }
        }    // End of save dialogue cycle
    }        // End of method

    
// Helper method for save binary to file and visual status
// INPUT:   parentWin = parent GUI frame
//          filePath = saved file path string
//          fileData = saved file binary data as qwords
// OUTPUT:  None (void)
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

// Helper method for load binary from file and visual status
// INPUT:   parentWin = parent GUI frame
//          filePath = loaded file path string
//          fileData = qwords array for load binary data
// OUTPUT:  integer status: 0 = loaded OK, 1 = error
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
    if (status==0) {  // this executed if loaded OK
                   int opbPart = availableBytes / 8;  // div by 8 means LONG
                   IOPB.transmitBytes(fileData, opbData, 4, opbPart );
                   opbData[0] = opbPart / 4;  // div by 4, units 32 bytes entry
                   opbData[1] = 0;
                   opbData[2] = 0;
                   opbData[3] = 0;
                   JOptionPane.showMessageDialog
                   (parentWin, "Binary file loaded: " + filePath, "LOAD BINARY",
                   JOptionPane.WARNING_MESSAGE); }
            else   {  // this executed if load failed
                   JOptionPane.showMessageDialog
                   (parentWin, "Binary file read failed", "ERROR",
                   JOptionPane.ERROR_MESSAGE); }

    return status;
    }

}

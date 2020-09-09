/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Operations handlers for buttons - Load binary, Save binary.
Provides GUI windows for dialogues with
load and save binary files functionality.
*/

package cpuidrefactoring.tools;

import cpuidrefactoring.system.IOPB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class ActionBinary 
{
private static final String FILE_NAME = "cpuid.bin";
// chooser must be static to remember user-selected path
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;

public boolean loadBinaryDialogue( JFrame parentWin, long[] opb ) 
    {
    boolean loaded = false;
    CHOOSER.setDialogTitle( "Load binary - select file" );
    filter = new FileNameExtensionFilter( "Binary dump file", "bin" );
    CHOOSER.setFileFilter( filter );
    CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
    CHOOSER.setSelectedFile( new File(FILE_NAME) );
    int select = CHOOSER.showOpenDialog( parentWin );
    if(select==JFileChooser.APPROVE_OPTION)
        {
        String s1 = CHOOSER.getSelectedFile().getPath();
        int status = loadBinary( parentWin, s1, opb );
        if ( status == 0 ) { loaded = true; }
        }
    return loaded;
    }

public void saveBinaryDialogue( JFrame parentWin, long[] opb )
    {
    CHOOSER.setDialogTitle( "Save binary - select directory" );
    filter = new FileNameExtensionFilter ( "Binary files" , "bin" );
    CHOOSER.setFileFilter( filter );
    CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
    CHOOSER.setSelectedFile( new File(FILE_NAME) );
    // (re)start dialogue
    boolean inDialogue = true;
    while(inDialogue)
        {
        int select = CHOOSER.showSaveDialog(parentWin);
        // save file
        if( select == JFileChooser.APPROVE_OPTION )
            {
            String s1 = CHOOSER.getSelectedFile().getPath();
            int x0 = JOptionPane.YES_OPTION;
            // check file exist and warning message
            File file = new File(s1);
            if( file.exists() == true )
                {
                x0 = JOptionPane.showConfirmDialog
                    ( null, "File exist: " + s1 + "\noverwrite?", "REPORT",
                    JOptionPane.YES_NO_CANCEL_OPTION ,
                    JOptionPane.WARNING_MESSAGE );  // or QUESTION_MESSAGE
                }
            // select operation by user selection
            if ( ( x0 == JOptionPane.NO_OPTION  ) |
                 ( x0 == JOptionPane.CLOSED_OPTION ) )
                { 
                continue; 
                }
            if ( x0 == JOptionPane.CANCEL_OPTION ) 
                { 
                inDialogue = false;
                continue; 
                }
                // save binary
                saveBinary( parentWin, s1, opb );
                inDialogue = false;
                }
            else 
                {
                inDialogue = false; 
                }
        }    // End of save dialogue cycle
    }        // End of method


// Helpers for communication between byte[] and long[] arrays.

/*
Helper method for save binary to file and visual status
INPUT:   parentWin = parent GUI frame
         filePath = saved file path string
         fileData = saved file binary data as qwords
OUTPUT:  None (void)
*/
private void saveBinary( JFrame parentWin, String filePath, long[] opbData )
    {
    long x = opbData[0];
    int opbLength = (int) ( x & (long)((long)-1>>>32) );
    opbLength *= 4;
    byte[] fileData = IOPB.receiveBytes( opbData, 4, opbLength );
    int status=0;
    try ( FileOutputStream writer = new FileOutputStream(filePath, false) )
        {
        writer.write( fileData );
        writer.flush(); 
        }
    catch( Exception ex ) 
        { 
        status=1; 
        }
    if ( status == 0 )
        {
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file saved: " + filePath, "SAVE BINARY",
              JOptionPane.WARNING_MESSAGE ); 
        }
    else
        {
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file write failed", "ERROR",
              JOptionPane.ERROR_MESSAGE ); 
        }
    }

/*
Helper method for load binary from file and visual status
INPUT:   parentWin = parent GUI frame
         filePath = loaded file path string
         fileData = qwords array for load binary data
OUTPUT:  integer status: 0 = loaded OK, 1 = error
*/
private int loadBinary( JFrame parentWin, String filePath, long[] opbData )
    {
    int status=0;
    int availableBytes = 0;
    int opbBytes = opbData.length * 8;
    byte[] fileData = new byte[opbBytes];
    try { FileInputStream reader = new FileInputStream(filePath);
          availableBytes = reader.available();
          reader.read ( fileData, 0, availableBytes );
        } 
    catch( IOException ex ) 
        {
        status=1; 
        } 
    if ( status == 0 ) 
        {  // this executed if loaded OK
        int opbPart = availableBytes / 8;  // div by 8 means LONG
        IOPB.transmitBytes( fileData, opbData, 4, opbPart );
        opbData[0] = opbPart / 4;  // div by 4, units 32 bytes entry
        opbData[1] = 0;
        opbData[2] = 0;
        opbData[3] = 0;
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file loaded: " + filePath, "LOAD BINARY",
              JOptionPane.WARNING_MESSAGE ); 
        }
    else   
        {  // this executed if load failed
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file read failed", "ERROR",
              JOptionPane.ERROR_MESSAGE ); 
        }
    return status;
    }
}

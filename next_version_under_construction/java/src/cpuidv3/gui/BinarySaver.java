/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Handler for "Save binary" item at root menu.
One of operations handlers for buttons - Load binary, Save binary (this file).
Provides GUI windows for dialogues with load and save binary files
functionality ( This file - for save ).

*/

package cpuidv3.gui;

import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

final class BinarySaver 
{
private static final String FILE_NAME = "cpuid.bin";
// chooser must be static to remember user-selected path
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;

    void saveBinaryDialogue( JFrame parentWin, long[] data )
    {
        CHOOSER.setDialogTitle( "Save binary - select directory" );
        filter = new FileNameExtensionFilter ( "Binary files" , "bin" );
        CHOOSER.setFileFilter( filter );
        CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
        CHOOSER.setSelectedFile( new File( FILE_NAME ) );
        // (Re)start dialogue.
        boolean inDialogue = true;
        while(inDialogue)
        {
            int select = CHOOSER.showSaveDialog(parentWin);
            // Interpreting selection at save file dialogue.
            if( select == JFileChooser.APPROVE_OPTION )
            {
                String s1 = CHOOSER.getSelectedFile().getPath();
                int x0 = JOptionPane.YES_OPTION;
                // Check file exist and warning message.
                File file = new File(s1);
                if( file.exists() == true )
                {
                    x0 = JOptionPane.showConfirmDialog
                        ( parentWin, 
                        "File exist: " + s1 + "\noverwrite?", "Save binary",
                        JOptionPane.YES_NO_CANCEL_OPTION ,
                        JOptionPane.WARNING_MESSAGE );  // or QUESTION_MESSAGE
                }
                // Select operation by user selection.
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
                // Save binary file.
                saveBinary( parentWin, s1, data );
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

    private void saveBinary( JFrame parent, String filePath, long[] data )
    {
        byte[] fileData = new byte[data.length * 8];
        int byteIndex = 0;
        for( int i=0; i<data.length; i++ )
        {
            long a = data[i];
            for( int j=0; j<8; j++ )
            {
                fileData[ byteIndex++ ] = (byte)( ( a >> (j * 8) ) & 0xFFL );
            }
        }
        
        boolean status = true;
        try ( FileOutputStream writer =
                new FileOutputStream( filePath, false ) )
        {
            writer.write( fileData );
            writer.flush(); 
        }
        catch( Exception ex ) 
        { 
            status = false;
        }
        
        if ( status )
        {
            JOptionPane.showMessageDialog
                ( parent, "Binary file saved: " + filePath  + ".",
                "Save binary", JOptionPane.WARNING_MESSAGE ); 
        }
        else
        {
            JOptionPane.showMessageDialog
                ( parent, "Binary file write failed.", "ERROR",
                JOptionPane.ERROR_MESSAGE ); 
        }
    }
}

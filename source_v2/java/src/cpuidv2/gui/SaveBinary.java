/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
----------------------------------------------------------------------------
Handler for "Save binary" item at root menu.
Legacy comments:
Operations handlers for buttons - Load binary, Save binary (this file).
Provides GUI windows for dialogues with
load and save binary files functionality (This file - save).
*/

package cpuidv2.gui;

import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class SaveBinary 
{
private static final String FILE_NAME = "cpuid.bin";
// chooser must be static to remember user-selected path
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;

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
                    ( null, "File exist: " + s1 + "\noverwrite?", "Save binary",
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
    byte[] fileData = receiveBytes( opbData, 4, opbLength );
    int status=0;
    try ( FileOutputStream writer = new FileOutputStream( filePath, false ) )
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
            ( parentWin, "Binary file saved: " + filePath  + ".",
              "Save binary", JOptionPane.WARNING_MESSAGE ); 
        }
    else
        {
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file write failed.", "ERROR",
              JOptionPane.ERROR_MESSAGE ); 
        }
    }

/*
Unpack QWORD array to BYTE array, receive data from OPB after native call
Each source QWORD unpacked to destination 8 BYTEs
Result = Destination BYTE array
INPUT:   Parm#1 = longipb = Source QWORD array
         Parm#2 = base = Source array base, units = qwords
         Parm#3 = length = Source array length, units = qwords
OUTPUT:  Destination BYTE array, bytes unpacked from qwords
*/
private byte[] receiveBytes( long[] longopb, int base, int length )
    {
    int n = length;
    int m = n*8;
    long x, y;
    int k = 0;
    byte[] bytearray = new byte[m];
    for ( int i=0; i<m; i++ )
        {
        bytearray[i] = 0; 
        }
    for ( int i=0; i<n; i++ )
        { 
        x = longopb[base+i];
        for ( int j=0; j<8; j++ )
            {
            y = x & 0xFF;
            x = x >>> 8;
            bytearray[k] = (byte)y;
            k++;
            }
        }
    return bytearray;
    }
}

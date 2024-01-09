/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Handler for "Load binary" item at root menu.
Legacy comments:
Operations handlers for buttons - Load binary (this file), Save binary.
Provides GUI windows for dialogues with
load and save binary files functionality (This file - load).
*/

package cpuidv2.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class LoadBinary 
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
    if( select == JFileChooser.APPROVE_OPTION )
        {
        String s1 = CHOOSER.getSelectedFile().getPath();
        int status = loadBinary( parentWin, s1, opb );
        if ( status == 0 ) { loaded = true; }
        }
    return loaded;
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
    int status = 0;
    int availableBytes = 0;
    int opbBytes = opbData.length * 8;
    byte[] fileData = new byte[opbBytes];
    try ( FileInputStream reader = new FileInputStream( filePath ) )
        {
        availableBytes = reader.available();
        reader.read ( fileData, 0, availableBytes );
        } 
    catch( IOException ex ) 
        {
        status = 1; 
        } 
    if ( status == 0 ) 
        {  // this executed if loaded OK
        int opbPart = availableBytes / 8;  // div by 8 means LONG
        transmitBytes( fileData, opbData, 4, opbPart );
        opbData[0] = opbPart / 4;  // div by 4, units 32 bytes entry
        opbData[1] = 0;
        opbData[2] = 0;
        opbData[3] = 0;
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file loaded: " + filePath + ".",
              "Load binary", JOptionPane.WARNING_MESSAGE ); 
        }
    else   
        {  // this executed if load failed
        JOptionPane.showMessageDialog
            ( parentWin, "Binary file read failed.", "ERROR",
              JOptionPane.ERROR_MESSAGE ); 
        }
    return status;
    }

/*
Pack BYTE array to QWORD array, transmit data to IPB before native call
Each 8 source BYTES packed to one destination QWORD
INPUT:   Parm#1 = bytearray = Source BYTE array
         Parm#2 = longipb = Destination QWORD array
         Parm#3 = base = Destination array base, units = qwords
         Parm#4 = length Destination array length, units = qwords
OUTPUT:  None (void)
*/
private void transmitBytes
    ( byte[] bytearray, long[] longipb, int base, int length )
    {
    int n = length;
    long x, y = 0;
    int k = 0;
    for ( int i=0; i<n; i++ ) 
        {
        longipb[base+i] = 0; 
        }
    for ( int i=0; i<n; i++ )
        {
        for( int j=0; j<8; j++ )
           {
           x = bytearray[k] & 0xFF;
           k++;
           x = x << 56;
           y = y >>> 8;
           y = y + x;
           }
        longipb[base+i] = y;
        y = 0;
        }
    }
}

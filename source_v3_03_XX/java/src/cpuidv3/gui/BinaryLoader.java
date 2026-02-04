/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Handler for "Load binary" item at root menu.
One of operations handlers for buttons - Load binary (this file) and 
Save binary. Provides GUI windows for dialogues with load and save
binary files functionality ( This file - for load ).
Provides errors handling during user dialogue, file load and file parsing.
Returns array of longs as groups of 4 longs (4 x 64-bit values = 32 bytes):
1) Two 32-bit values: entry id = 0, cpuid function.
2) Two 32-bit values: cpuid subfunction, cpuid pass number (not used).
3) Two 32-bit values: cpuid results registers: EAX, EBX.
4) Two 32-bit values: cpuid results registers: ECX, EDX.

*/

package cpuidv3.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

final class BinaryLoader 
{
    private static final String FILE_NAME = "cpuid.bin";
    // Chooser must be static to remember user-selected path.
    private static final JFileChooser CHOOSER = new JFileChooser();

    long[] loadBinaryDialogue( JFrame parentWin )
    {
        long[] result = null;
        CHOOSER.setDialogTitle( "Load binary - select file" );
        FileNameExtensionFilter filter = new FileNameExtensionFilter
            ( "Binary dump file", "bin" );
        CHOOSER.setFileFilter( filter );
        CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
        CHOOSER.setSelectedFile( new File(FILE_NAME) );
        int select = CHOOSER.showOpenDialog( parentWin );
        if( select == JFileChooser.APPROVE_OPTION )
        {
            String s1 = CHOOSER.getSelectedFile().getPath();
            result = loadBinary( parentWin, s1 );
        }
        return result;
    }

    // Helper method for load binary from file and visual status.
    // INPUT:   parentWin = parent GUI frame.
    //          filePath = loaded file path string.
    //          fileData = qwords array for load binary data.
    // OUTPUT:  long[] data array if loaded OK, null = error.
    private long[] loadBinary( JFrame parentWin, String filePath )
    {
        byte[] fileData;
        try ( FileInputStream reader = new FileInputStream( filePath ) )
        {
            int availableBytes = reader.available();
            fileData = new byte[ availableBytes ];
            reader.read ( fileData, 0, availableBytes );
        }
        catch( IOException ex )
        {
            fileData = null;
        }

        long[] resultData = null;
        if ( ( fileData != null )&&( ( fileData.length % 32 ) == 0) )
        {   // This executed if load OK and file size correct.
            
            resultData = new long[ fileData.length / 8 ];
            int byteIndex = 0;
            for( int i=0; i<resultData.length; i++ )
            {
                long a = 0;
                for( int j=0; j<8; j++ )
                {
                    long b = fileData[ byteIndex++ ];
                    a |= ( ( b & 0xFFL ) << ( j * 8 ) );
                }
                resultData[i] = a;
            }
            
            JOptionPane.showMessageDialog
             ( parentWin, "Binary file loaded: " + filePath + ".",
             "Load binary", JOptionPane.INFORMATION_MESSAGE ); 
        }
        else if ( fileData != null )
        {   // This executed if load OK but file size incorrect.
            JOptionPane.showMessageDialog
             ( parentWin, "Incorrect file size (CPUID binary dump required).", 
               "Error", JOptionPane.ERROR_MESSAGE ); 
        }
        else
        {   // This executed if load failed.
            JOptionPane.showMessageDialog
             ( parentWin, "File read failed (CPUID binary dump required).", 
               "Error", JOptionPane.ERROR_MESSAGE );
        }
        return resultData;
    }
}

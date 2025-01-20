/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Functions library, used by handlers for "Save hex all CPUs", "Save hex one CPU"
items at root menu and "Save hex" down button. 
Provides GUI windows for dialogues with save dump files.

*/

package cpuidv3.gui;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HexSaver 
{
private static final String FILE_NAME = "cpuid_hex_dump.txt";
// Chooser must be static to remember user-selected path.
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;

    // This for single logical CPU dump.
    public void saveHexDialogue( JFrame parentWin, long[] data,
        String reportStr, String appStr , String webStr )
    {
        helperSaveHexDialogue
            ( parentWin, reportStr, appStr, webStr, buildHexDump( data, 0 ) );
    }
    
    // This for multiple logical CPUs dump.
    public void saveHexDialogue( JFrame parentWin, long[][] data,
        String reportStr, String appStr , String webStr )
    {
        helperSaveHexDialogue
            ( parentWin, reportStr, appStr, webStr, buildHexDump( data ) );
    }
    
    private void helperSaveHexDialogue( JFrame parentWin,
        String reportStr, String appStr , String webStr, String dumpStr )
    {
        CHOOSER.setDialogTitle( "Save CPUID dump - select directory" );
        filter = new FileNameExtensionFilter ( "Text files" , "txt" );
        CHOOSER.setFileFilter( filter );
        CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
        CHOOSER.setSelectedFile( new File( FILE_NAME ) );
        // (Re)start dialogue.
        boolean inDialogue = true;
        while( inDialogue )
        {
            int select = CHOOSER.showSaveDialog( parentWin );
            // Interpreting selection at save file dialogue.
            if( select == JFileChooser.APPROVE_OPTION )
            {
                String pathString = CHOOSER.getSelectedFile().getPath();
                int x0 = JOptionPane.YES_OPTION;
                // Check file exist and warning message.
                File file = new File( pathString );
                if( file.exists() == true )
                {
                    x0 = JOptionPane.showConfirmDialog
                        ( parentWin, 
                          "File exist: " + pathString + "\noverwrite?" ,
                          "REPORT",
                          JOptionPane.YES_NO_CANCEL_OPTION,
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
                // Continue prepare for save file.
                saveHexDumpReport( parentWin, pathString,
                    reportStr + "\r\n\r\n" + appStr + webStr + 
                    dumpStr + "\r\n" );
                inDialogue = false;
            }  
            else
            {
                inDialogue = false; 
            }
        }
    }

/*
Patterns for strings generation.
*/
    private final static String TABLE_START = 
        "\r\nCPUID Registers (CPU #%d):\r\n";
    private final static String TABLE_UP = 
        "Function        EAX      EBX      ECX      EDX\r\n";
    private final static String TABLE_DATA =
        "CPUID %08X: %08X-%08X-%08X-%08X\r\n";
    private final static int TABLE_WIDTH = 52;
    private final static char TABLE_CHAR = '-';
    
    private void helperLine( StringBuilder sb )
    {
        for( int j=0; j<TABLE_WIDTH; j++ ) { sb.append( TABLE_CHAR ); }
        sb.append( "\r\n" );
    }

/*
Helper methods for build InstLatX64-style hex dump text from binary data.
INPUT:   data = binary dump. long[][] for all CPUs, long[] for one CPU.
OUTPUT:  String = text report.
*/
    private String buildHexDump( long[][] data )
    {
        if ( ( data != null )&&( data.length > 0) )
        {
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<data.length; i++ )
            {
                sb.append( buildHexDump( data[i], i ) );
            }
            return sb.toString();
        }
        else
        {
            return "No data available.\r\n";
        }
    }

    private String buildHexDump( long[] data, int index )
    {
        if (( data != null )&&( data.length > 0)&&(( data.length % 4 ) == 0 ))
        {
            StringBuilder sb = new StringBuilder();
            sb.append( String.format( TABLE_START, index ) );
            helperLine( sb );
            sb.append( TABLE_UP );
            helperLine( sb );
            for( int i=0; i<data.length; i+=4 )
            {
                int function = (int)( data[i] >>> 32 );
                int eax = (int)( data[i + 2] & 0xFFFFFFFFL );
                int ebx = (int)( data[i + 2] >>> 32 );
                int ecx = (int)( data[i + 3] & 0xFFFFFFFFL );
                int edx = (int)( data[i + 3] >>> 32 );
                sb.append( String.format( TABLE_DATA, 
                        function, eax, ebx, ecx, edx ));
            }
            helperLine( sb );
            return sb.toString();
        }
        else if (( data != null )&&( data.length > 0))
        {
            return "Incorrect dump size for this CPU.\r\n";
        }
        else
        {
            return "No data available for this CPU.\r\n";
        }
    }
    
/*
Helper method for save string to file and visual status.
INPUT:   parentWin = parent GUI frame.
         filePath = saved file path string.
         text = saved file data as text.
OUTPUT:  None (void).
*/
    private void saveHexDumpReport
        ( JFrame parent, String filePath, String fileData )
    {
        boolean status = true;
        try ( FileWriter writer = new FileWriter( filePath, false ) )
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
                ( parent, "Hex dump saved: " + filePath  + ".",
                  "Hex dump save", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
            JOptionPane.showMessageDialog
                ( parent, "Write hex dump failed", 
                  "ERROR", JOptionPane.ERROR_MESSAGE );
        }
    }
}

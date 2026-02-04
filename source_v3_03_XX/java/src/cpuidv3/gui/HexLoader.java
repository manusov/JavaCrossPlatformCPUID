/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Special thanks to:
https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html
https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string
used at unit test for this class.

https://stackoverflow.com/questions/960431/how-can-i-convert-listinteger-to-int-in-java
used for convert array of Integer to array of int.

Handler for "Load InstLatX64 CPUID" item at root menu and "Load hex" button
at window down. Provides file selection dialogue, load text file with hex dump,
parsing this file and returns array of integers as groups with 5 integers.
CPUID function , EAX , EBX , ECX , EDX.
Provides errors handling during user dialogue, file load and file parsing.

*/

package cpuidv3.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

final class HexLoader 
{
    private static final String FILE_NAME = "cpuid.txt";
    // Chooser must be static to remember user-selected path.
    private static final JFileChooser CHOOSER = new JFileChooser();
    
    // Handler for "Load hex" dialogue method, setup GUI.
    int[] loadTextDialogue( JFrame parentWin )
    {
        int[] result = null;
        CHOOSER.setDialogTitle( "Load hex - select file" );
        FileNameExtensionFilter filter = new FileNameExtensionFilter
            ( "InstLatx64 compatible report file", "txt" );
        CHOOSER.setFileFilter( filter );
        CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
        CHOOSER.setSelectedFile( new File( FILE_NAME ) );
        int select = CHOOSER.showOpenDialog( parentWin );
        if( select == JFileChooser.APPROVE_OPTION )
        {
            String s1 = CHOOSER.getSelectedFile().getPath();
            result = loadText( parentWin, s1 );
        }
        return result;
    }
    
    // Patterns for strings detection.
    private final static String ALL_S   =
        "CPUID 00000000: 00000000-00000000-00000000-00000000";
    private final static String START_S  = "CPUID";
    private final static String SPLIT_S1 = ":|\t";  // Separate by ":" or TAB.
    private final static String SPLIT_S2 = "-| ";   // Separate by "-" or " ".
    
    // Load and parse operation status representation.
    private enum LOAD_STATUS { RESULT_OK, READ_FAILED, NO_DATA, BAD_SIZE };
    
    // Helper method for load text file, parse it and visual status
    // INPUT:   parentWin = parent GUI frame
    //          filePath = loaded file path string
    // OUTPUT:  int[][] array contains data if loaded OK, otherwise null.
    private int[] loadText( JFrame parentWin, String filePath )
    {
        LOAD_STATUS loadStatus = LOAD_STATUS.RESULT_OK;
        int count = 0;
        File file = new File( filePath );
        ArrayList<Integer> dump = new ArrayList<>();
    
        try ( FileReader fr = new FileReader( file );
              BufferedReader reader = new BufferedReader( fr ) )
        {
            String line = reader.readLine();
            while( line != null )
            {
                line = line.trim().toUpperCase();
                if ( ( line.length() >= ALL_S.length() ) && 
                     ( line.startsWith( START_S )      ) )
                {
                    line = line.substring( START_S.length() ).trim();
                    String[] words = line.split( SPLIT_S1 );
                    if ( ( words != null ) && ( words.length >= 2 ) )
                    {
                        for( int i=0; i<words.length; i++ )
                        {   // Reject spaces and tabs at start and at end.
                            words[i] = words[i].replace("\t", " ").trim();
                        }

                        String function = words[0];
                        String[] values = 
                                //words[words.length - 1].split( SPLIT_S2 );
                                words[1].split( SPLIT_S2 );
                        if ( ( function.length() == 8 ) &&
                                ( values.length >= 4 ) )
                        {
                            try
                            {
                                int f = Integer.parseUnsignedInt
                                    ( function, 16 );
                                dump.add( f );
                                count++;
                                for( int i=0; i<4; i++ )
                                {
                                    int r = Integer.parseUnsignedInt
                                                ( values[i], 16 );
                                    dump.add( r );
                                    count++;
                                }
                            }
                            catch( NumberFormatException e ) { }
                        }
                    }
                }
                line = reader.readLine();
            }
            if( count <= 0 )
            {
                loadStatus = LOAD_STATUS.NO_DATA;
            }
            else if ( ( count % 5 ) != 0)
            {
                loadStatus = LOAD_STATUS.BAD_SIZE;
            }
        }
        catch( IOException ex )
        {
            loadStatus = LOAD_STATUS.READ_FAILED;
        }
    
        // Check load and parse results.
        switch ( loadStatus )
        {
            case RESULT_OK:
                // This executed if loaded OK.
                JOptionPane.showMessageDialog( parentWin, 
                 "Text file loaded: " + filePath  + ".",
                 "Load hex file", JOptionPane.INFORMATION_MESSAGE );
                break;
            
            case READ_FAILED:
                // This executed if load failed = file I/O error.
                JOptionPane.showMessageDialog( parentWin, 
                 "File read failed (InstLatx64 compatible report required).", 
                 "Error", JOptionPane.ERROR_MESSAGE );
                break;
            
            case NO_DATA:
                // This executed if load failed = no valid data found.
                JOptionPane.showMessageDialog( parentWin, 
                 "No valid data found (InstLatx64 compatible report required).",
                 "Error", JOptionPane.ERROR_MESSAGE );
                break;
            
            case BAD_SIZE:
                // This executed if load failed = incorrect dump size.
                JOptionPane.showMessageDialog( parentWin, 
                 "Incorrect dump size (InstLatx64 compatible report required).",
                 "Error", JOptionPane.ERROR_MESSAGE );
                break;
            
            default:
                // This executed if load failed = unknown error.
                JOptionPane.showMessageDialog( parentWin, 
                 "Unknown error.", 
                 "Error", JOptionPane.ERROR_MESSAGE );
                break;
        }
        
        if( dump.isEmpty() )
        {
            return null;
        }
        else
        {
            return dump.stream().mapToInt( Integer::intValue ).toArray();
        }
    }
}

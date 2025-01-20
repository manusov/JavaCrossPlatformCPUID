/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Handler for "Load InstLatX64 CPUID" item at root menu
and "Load hex" button at window down.

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

class HexLoader 
{
private static final String FILE_NAME = "cpuid.txt";
// Chooser must be static to remember user-selected path.
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;
    
    // Handler for "Load hex" dialogue method, setup GUI
    int[] loadTextDialogue( JFrame parentWin )
    {
        int[] result = null;
        CHOOSER.setDialogTitle( "Load hex - select file" );
        filter = new FileNameExtensionFilter
            ( "InstLatx64 compatible report file", "txt" );
        CHOOSER.setFileFilter( filter );
        CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
        CHOOSER.setSelectedFile( new File(FILE_NAME) );
        int select = CHOOSER.showOpenDialog( parentWin );
        if( select == JFileChooser.APPROVE_OPTION )
        {
            String s1 = CHOOSER.getSelectedFile().getPath();
            result = loadText( parentWin, s1 );
        }
        return result;
    }

/*
Patterns for strings detection.
*/
    private final static String ALL_S   =
        "CPUID 00000000: 00000000-00000000-00000000-00000000";
    private final static String START_S  = "CPUID";
    private final static String SPLIT_S1 = ":|\t";  // Separate by ":" or TAB.
    private final static String SPLIT_S2 = "-| ";   // Separate by "-" or " ".

/*
Helper method for load text file, parse it and visual status
INPUT:   parentWin = parent GUI frame
         filePath = loaded file path string
OUTPUT:  int[][] array contains data if loaded OK, otherwise null.
*/
    private int[] loadText( JFrame parent, String filePath )
    {
        int status = 0;
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
                        String[] values = words[1].split( SPLIT_S2 );
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
                            catch( Exception e ) { }
                        }
                    }
                }
                line = reader.readLine();
            }
            if( count <= 0 )
            {
                status = 2;  // Error = no data found.
            }
            else if ( ( count % 5 ) != 0)
            {
                status = 3;  // Error = incorrect dump size.
            }
        }
        catch( IOException ex )
        {
            status = 1;  // Error = file read failed.
        }
    
        // check results
        switch ( status )
        {
            case 0:
                // this executed if loaded OK
                JOptionPane.showMessageDialog
                    ( parent, 
                    "Text file loaded: " + filePath  + ".",
                    "Load hex", JOptionPane.WARNING_MESSAGE );
                break;
            
            case 1:
                // this executed if load failed = file I/O error
                JOptionPane.showMessageDialog
                    ( parent, 
                  "File read failed (InstLatx64 compatible report required).", 
                    "ERROR", JOptionPane.ERROR_MESSAGE );
                break;
            
            case 2:
                // this executed if load failed = no valid data found
                JOptionPane.showMessageDialog
                    ( parent, 
                "No valid data found (InstLatx64 compatible report required).",
                    "ERROR", JOptionPane.ERROR_MESSAGE );
                break;
            
            case 3:
                // this executed if load failed = incorrect dump size
                JOptionPane.showMessageDialog
                    ( parent, 
                  "Incorrect dump size (InstLatx64 compatible report required).",
                    "ERROR", JOptionPane.ERROR_MESSAGE );
                break;
            
            default:
                // this executed if load failed = unknown error
                JOptionPane.showMessageDialog
                    ( parent, 
                    "Unknown error.", 
                    "ERROR", JOptionPane.ERROR_MESSAGE );
                break;
        }
        
        if( dump.isEmpty() )
        {
            return null;
        }
        else
        {
            Integer[] a = dump.toArray( new Integer[dump.size()] );
            int[] b = new int[a.length];
            for(int i=0; i<a.length; i++)
            {
                b[i] = a[i];
            }
            return b;
        }
    }
}

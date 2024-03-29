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
Handler for "Load InstLatX64 CPUID" item at root menu.
*/

package cpuidv2.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class LoadInstLatX64 
{
private static final String FILE_NAME = "cpuid.txt";
// chooser must be static to remember user-selected path
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;
    
// Handler for "Load text" dialogue method, setup GUI
public boolean loadTextDialogue( JFrame parentWin, long[] opb )
    {
    boolean loaded = false;
    CHOOSER.setDialogTitle( "Load text - select file" );
    filter = new FileNameExtensionFilter
        ( "InstLatx64 compatible report file", "txt" );
    CHOOSER.setFileFilter( filter );
    CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
    CHOOSER.setSelectedFile( new File(FILE_NAME) );
    int select = CHOOSER.showOpenDialog( parentWin );
    if( select == JFileChooser.APPROVE_OPTION )
        {
        String s1 = CHOOSER.getSelectedFile().getPath();
        int status = loadText( parentWin, s1, opb );
        if ( status == 0 ) { loaded = true; }
        }
    return loaded;
    }

/*
Patterns for string detection.
*/
private final static String ALL_S   =
    "CPUID 00000000: 00000000-00000000-00000000-00000000";
private final static String START_S  = "CPUID";
private final static String SPLIT_S1 = ": ";
private final static String SPLIT_S2 = "-| ";

/*
Helper method for load text file, parse it and visual status
INPUT:   parentWin = parent GUI frame
         filePath = loaded file path string
         fileData = qwords array for return binary data after parse
OUTPUT:  integer status: 0 = loaded OK, 1 = error
*/
private int loadText( JFrame parent, String filePath, long[] opb )
    {
    int status = 0;
    int count = 1;
    int cpus = 0;

    File file = new File( filePath );
    
    try ( FileReader fr = new FileReader( file );
          BufferedReader reader = new BufferedReader( fr ) )
        {
        for( int i=0; i<opb.length; i++ )
            {
            opb[i] = 0;
            }
        
        int subfunction = 0;
        long functionChanged = -1;
        boolean parsed1, parsed2 = false;
        String line = reader.readLine();
        
        while( ( line != null ) && ( cpus < 2 ) )
            {
            if ( count >= opb.length / 4 )
                {
                status = 3;  // error = buffer overflow
                break;
                }
            parsed1 = false;
            line = line.trim().toUpperCase();
            if ( ( line.length() >= ALL_S.length() ) && 
                 ( line.startsWith( START_S )      ) )
                {
                line = line.substring( START_S.length() ).trim();
                String[] words = line.split( SPLIT_S1 );
                if ( ( words != null ) && ( words.length >= 2 ) )
                    {
                    String function = words[0];
                    String[] values = words[1].split( SPLIT_S2 );
                    if ( ( function.length() == 8 ) && ( values.length >= 4 ) )
                        {
                        try
                            {
                            long x = Integer.parseUnsignedInt( function, 16 );
                            x &= 0xFFFFFFFFL;
                            if ( ( x != 0 ) || ( cpus == 0 ) )
                                {
                                long[] y = new long[4];
                                for( int i=0; i<4; i++ )
                                    {
                                    y[i] = Integer.parseUnsignedInt
                                            ( values[i], 16 );
                                    y[i] &= 0xFFFFFFFFL;
                                    }
                                if ( x != functionChanged )
                                    {
                                    subfunction = 0;
                                    }
                                functionChanged = x;
                                opb[count * 4    ] = x << 32;
                                opb[count * 4 + 1] = subfunction;
                                opb[count * 4 + 2] = y[0] + ( y[1] << 32 );
                                opb[count * 4 + 3] = y[2] + ( y[3] << 32 );
                                count++;
                                subfunction++;
                                parsed1 = true;
                                }
                            if ( x == 0 )
                                {
                                cpus++;
                                }
                            }
                        catch( Exception e )
                            {
                            }
                        }
                    }
                }
            if (( ! parsed1 )&&( parsed2 ))
                {
                break;
                }
            parsed2 = parsed1;
            line = reader.readLine();
            }
        if( count <= 1 )
            {
            status = 2;  // error = no data found
            }
        }
    catch( IOException ex )
        {
        status = 1;  // error = file read failed
        }
    
    // check results
    switch ( status )
        {
        case 0:
            // this executed if loaded OK
            opb[0] = count - 1;
            JOptionPane.showMessageDialog
                ( parent, 
                  "Text file loaded: " + filePath  + ".",
                  "Load text", JOptionPane.WARNING_MESSAGE );
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
            // this executed if load failed = buffer overflow
            JOptionPane.showMessageDialog
                ( parent, 
                  "Buffer overflow (InstLatx64 compatible report required).",
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
    
    return status;
    }
}

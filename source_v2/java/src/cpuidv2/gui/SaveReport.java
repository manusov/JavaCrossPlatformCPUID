/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
----------------------------------------------------------------------------
Functions library, used by handler for "Save report" item at root menu.
Legacy comments:
Operations handlers for buttons - Report this, Report full.
Provides GUI windows for dialogues with save report files.
Report this - for current visualized table,
Report full - for all information.
*/

package cpuidv2.gui;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

public final class SaveReport 
{
private static final String FILE_NAME = "report.txt";
private static final int MAXCOL_DEFAULT = 13;
private static final int MAXCOL_LIMIT = 120;
// chooser must be static to remember user-selected path
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;
    
// Handler for "Report full" dialogue method, setup GUI.
public void reportThisDialogue
    ( JFrame parentWin, 
      AbstractTableModel model1, AbstractTableModel model2,
      String longName , String vendorVersion )
    {
    CHOOSER.setDialogTitle( "Report this - select directory" );
    filter = new FileNameExtensionFilter ( "Text files" , "txt" );
    CHOOSER.setFileFilter( filter );
    CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
    CHOOSER.setSelectedFile( new File( FILE_NAME ) );
    // (Re)start dialogue.
    boolean inDialogue = true;
    while( inDialogue )
        {
        int select = CHOOSER.showSaveDialog( parentWin );
        // Save file.
        if( select == JFileChooser.APPROVE_OPTION )
            {
            String s1 = CHOOSER.getSelectedFile().getPath();
            int x0 = JOptionPane.YES_OPTION;
            // Check file exist and warning message.
            File file = new File( s1 );
            if( file.exists() == true )
                {
                x0 = JOptionPane.showConfirmDialog
                    ( null, "File exist: " + s1 + "\noverwrite?" , "REPORT",
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
            String s2 = "Report file.\r\n" + 
                         longName + "\r\n" + vendorVersion + "\r\n\r\n";
            String s3 = "" , s4 = "";
            // Make and save report.
            if ( model1 != null ) { s3 = tableReport( model1 ); }
            if ( model2 != null ) { s4 = tableReport( model2 ); }
            StringBuilder sb = new StringBuilder( s2 );
            sb.append( s3 );
            sb.append( "\r\n" );
            sb.append( s4 );
            saveReport( parentWin, s1, sb.toString() );
            inDialogue = false;
            }  
        else
            {
            inDialogue = false; 
            }
        }    // End of save dialogue cycle.
    }        // End of method.

public void reportFullDialogue
    ( JFrame parentWin, 
      AbstractTableModel[] models1, AbstractTableModel[] models2,
      String longName , String vendorVersion ) 
    {
    CHOOSER.setDialogTitle( "Report full - select directory" );
    filter = new FileNameExtensionFilter ( "Text files" , "txt" );
    CHOOSER.setFileFilter( filter );
    CHOOSER.setFileSelectionMode( JFileChooser.FILES_ONLY );
    CHOOSER.setSelectedFile( new File( FILE_NAME ));
    // (Re)start dialogue.
    boolean inDialogue = true;
    while( inDialogue )
        {
        int select = CHOOSER.showSaveDialog( parentWin );
        // Save file.
        if( select == JFileChooser.APPROVE_OPTION )
            {
            String s1 = CHOOSER.getSelectedFile().getPath();
            int x0 = JOptionPane.YES_OPTION;
            // Check file exist and warning message.
            File file = new File(s1);
            if( file.exists() == true )
                {
                x0 = JOptionPane.showConfirmDialog
                    ( null, "File exist: " + s1 + "\noverwrite?" , "REPORT" ,
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
            // Continue prepare for save file.
            String s2 = "Report file.\r\n" +
                         longName + "\r\n" + vendorVersion + "\r\n\r\n";
            StringBuilder sb3 = new StringBuilder ( "" );
            // Make and save report.
            if ( models1 != null )
                {
                int n = models1.length;
                for ( int i=0; i<n; i++ )
                    {
                    if ( models1[i] != null )
                        { 
                        sb3.append( tableReport( models1[i] ) ); 
                        sb3.append( "\r\n" );
                        }
                    if ( ( models2 != null ) && ( models2[i] != null ) )
                        {
                        sb3.append( tableReport( models2[i] ) );
                        sb3.append( "\r\n" );
                        }
                    }
                }
            saveReport( parentWin, s1, s2 + sb3.toString() );
            inDialogue = false;
            }
        else
            {
            inDialogue = false; 
            }
        }    // End of save dialogue cycle.
    }        // End of method.


// Helpers methods
// Helper method for convert table model to string
// INPUT:   atm = abstract table model
// OUTPUT:  string.
private String tableReport ( AbstractTableModel atm ) 
    {
    StringBuilder report = new StringBuilder( "" );
    if ( atm == null ) { return report.toString(); }
    // Continue if table exist,get geometry.
    int m = atm.getColumnCount();
    int n = atm.getRowCount();
    String s;
    int a;
    int[] maxcols = new int[m];
    int maxcol = MAXCOL_DEFAULT;
    // Get column names lengths.
    for (int i=0; i<m; i++)
        { maxcols[i] = atm.getColumnName(i).length(); }
    // Get column maximum lengths.    
    for (int j=0; j<n; j++)
        {
        for (int i=0; i<m; i++)
            {
            s = getShortString( atm, j, i );
            a = s.length();
            if (a>maxcols[i]) { maxcols[i]=a; }
            }
        }
    for ( int i=0; i<maxcols.length; i++ ) { maxcol += maxcols[i]; }
    // Write table up.
    for (int i=0; i<m; i++)
        {
        s = atm.getColumnName(i);
        report.append( " " );
        report.append( s );
        a = maxcols[i] - s.length() + 1;
        for ( int k=0; k<a; k++ ) { report.append( " " ); }
        }
    // Write horizontal line.        
    report.append( "\r\n" );
    for ( int i=0; i<maxcol; i++ ) { report.append( "-" ); }
    report.append( "\r\n" );
    // Write table content.
    for (int j=0; j<n; j++)      // this cycle for rows , n = rows count.
        {
        for (int i=0; i<m; i++)  // this cycle for columns , m = columns count.
            {
            s = getShortString( atm, j, i );
            report.append( s );
            a = maxcols[i] - s.length() + 2;
            for ( int k=0; k<a; k++ ) { report.append( " " ); }
            }
            report.append( "\r\n" );    
        }
    // Write horizontal line and return.
    for ( int i=0; i<maxcol; i++ ) { report.append( "-" ); }
    report.append( "\r\n" );
    return report.toString(); 
    }

/*
Helper method for get short version of strings for report
INPUT:   atm = abstract table model
         j = string vertical position in the table
         i = string horizontal position in the table
OUTPUT:  string, size limited.
*/
private String getShortString ( AbstractTableModel model, int j, int i )
    {
    StringBuilder sb1 = new StringBuilder( " " );
    sb1.append( (String)model.getValueAt( j, i ) );
    StringBuilder sb2 = sb1;
    int n = MAXCOL_LIMIT;
    if ( sb1.length() > n ) 
        {
        sb2.append( sb1.substring( 0, n-2 ) );
        sb2.append( "..." );
        }
    return sb2.toString();
    }

/*
Helper method for save string to file and visual status
INPUT:   parentWin = parent GUI frame
         filePath = saved file path string
         fileData = saved file data as single string contains separators
OUTPUT:  None (void).
*/
private void saveReport( JFrame parentWin, String filePath, String fileData )
    {
    int status = 0;
    try ( FileWriter writer = new FileWriter( filePath, false ) )
        {
        writer.write( fileData );
        writer.flush();
        }
    catch( Exception ex )
        {
        status = 1; 
        }
    if ( status == 0 )  
        {
        JOptionPane.showMessageDialog
            ( parentWin, "Report saved: " + filePath  + ".",
             "Report", JOptionPane.WARNING_MESSAGE );
        }
    else
        {
        JOptionPane.showMessageDialog
            ( parentWin, "Write report failed", "ERROR",
              JOptionPane.ERROR_MESSAGE);
        }
    }
}

/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Functions library, used by handler for "Save report" item at root menu.
Two operations handlers for buttons - Report this and Report full.
Provides GUI windows for dialogues with save report files.
Report this - for current visualized table (GUI selected tab),
Report full - for all information.

*/

package cpuidv3.gui;

import cpuidv3.sal.ChangeableTableModel;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import cpuidv3.sal.ReportData;
import static java.lang.Integer.max;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

final class ReportSaver
{
    private static final String FILE_NAME = "report.txt";
    // Chooser must be static to remember user-selected path.
    private static final JFileChooser CHOOSER = new JFileChooser();
    
    void saveReportDialogue( JFrame parentWin, 
        String nameStr, String appStr, String webStr, String reportStr, 
        ReportData reportData )
    {
        helperSaveReportDialogue( parentWin, nameStr, appStr, webStr, reportStr,
            buildReport( reportData ).toString() );
    }
        
    void saveReportDialogue( JFrame parentWin,  
        String nameStr, String appStr, String webStr, String reportStr, 
        ReportData[] reportData )
    {
        helperSaveReportDialogue( parentWin, nameStr, appStr, webStr, reportStr, 
            buildReport( reportData ).toString() );
    }
    
    private void helperSaveReportDialogue( JFrame parentWin, 
        String nameStr, String appStr, String webStr, String reportStr,
        String dataStr )
    {
        CHOOSER.setDialogTitle( reportStr + " - select directory" );
        FileNameExtensionFilter filter = 
            new FileNameExtensionFilter ( "Text files" , "txt" );
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
                          JOptionPane.QUESTION_MESSAGE );
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
                saveReport( parentWin, pathString,
                    nameStr + ". " + appStr + ".\r\n" + webStr +
                    reportStr + ".\r\n" + dataStr + "\r\n" );
                inDialogue = false;
            }  
            else
            {
                inDialogue = false; 
            }
        }
    }
        
    private StringBuilder buildReport( ReportData data )
    {
        StringBuilder sb = new StringBuilder();
        if ( data == null )
        {
            sb.append( "No data." );
            return sb;
        }
        
        if ( data.panelName != null )
        {
                sb.append( "\r\n[ " );
                sb.append( data.panelName );
                sb.append( " ]\r\n" );
        }
        
        int count1 = ( data.tablesPairsNames != null ) ?
            data.tablesPairsNames.length : 0;
        int count2 = ( data.upTables != null ) ? 
            data.upTables.length : 0;
        int count3 = ( data.downTables != null ) ? 
            data.downTables.length : 0;
        int count = max( count1, count2 );
        count = max( count, count3 );
        
        for( int i=0; i<count; i++ )
        {
            String tableName = null;
            ChangeableTableModel model1 = null;
            ChangeableTableModel model2 = null;
            if ( i < count1 ) { tableName = data.tablesPairsNames[i]; }
            if ( i < count2 ) { model1 = data.upTables[i]; }
            if ( i < count3 ) { model2 = data.downTables[i]; }
           
            if( tableName != null )
            {
                sb.append( "\r\n[ " );
                sb.append( tableName );
                sb.append( " ]\r\n" );
            }

            if( model1 != null )
            {
                sb.append( "\r\n" );
                sb.append( tableReport( model1 ) );
            }

            if( model2 != null )
            {
                sb.append( "\r\n" );
                sb.append( tableReport( model2 ) );
            }
        }

        return sb;
    }

    private StringBuilder buildReport( ReportData[] data )
    {
        StringBuilder sb = new StringBuilder();
        if ( data == null )
        {
            sb.append( "No data." );
            return sb;
        }
        
        for ( ReportData d : data) 
        {
            sb.append( buildReport( d ) );
        }
        return sb;
    }
    
    // Helper method for save string to file and visual status.
    // INPUT:   parentWin = parent GUI frame.
    //          filePath = saved file path string.
    //          fileData = saved file data as single string contains separators.
    // OUTPUT:  None (void).
    private void saveReport( JFrame parent, String filePath, String fileData )
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
            JOptionPane.showMessageDialog( parent, 
                "Report saved: " + filePath  + ".",
                "Report", JOptionPane.INFORMATION_MESSAGE );
        }
        else
        {
            JOptionPane.showMessageDialog( parent, 
                "Write report failed", "ERROR", JOptionPane.ERROR_MESSAGE );
        }
    }
}

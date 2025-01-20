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

import static cpuidv3.services.HelperTableToReport.tableReport;
import static java.lang.Integer.max;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

class ReportSaver
{
private static final String FILE_NAME = "report.txt";
// Chooser must be static to remember user-selected path.
private static final JFileChooser CHOOSER = new JFileChooser();
private FileNameExtensionFilter filter;
    
    public void saveReportDialogue
    ( JFrame parentWin, 
      AbstractTableModel model1, AbstractTableModel model2,
      String reportStr, String appStr , String webStr )
    {
        helperSaveReportDialogue( parentWin, reportStr, appStr, webStr, 
            buildReport( model1, model2 ) );
    }

    public void saveReportDialogue
    ( JFrame parentWin, 
      AbstractTableModel model1[], AbstractTableModel model2[],
      String reportStr, String appStr , String webStr )
    {
        helperSaveReportDialogue( parentWin, reportStr, appStr, webStr, 
            buildReport( model1, model2 ) );
    }
    
    private void helperSaveReportDialogue( JFrame parentWin,
        String reportStr, String appStr , String webStr, String dataStr )
    {
        CHOOSER.setDialogTitle( reportStr + " - select directory" );
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
                saveReport( parentWin, pathString,
                    reportStr + ".\r\n\r\n" + appStr + webStr + 
                    dataStr + "\r\n" );
                inDialogue = false;
            }  
            else
            {
                inDialogue = false; 
            }
        }
    }
    
    private String buildReport
        ( AbstractTableModel model1[], AbstractTableModel model2[] )
    {
        int m1 = 0;
        int m2 = 0;
        if ( model1 != null ) { m1 = model1.length; }
        if ( model2 != null ) { m2 = model2.length; }
        int count = max( m1, m2 );
        if ( count > 0 )
        {
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<count; i++ )
            {
                AbstractTableModel tableModel1 = null;
                AbstractTableModel tableModel2 = null;
                if ( i < m1 ) { tableModel1 = model1[i]; }
                if ( i < m2 ) { tableModel2 = model2[i]; }
                sb.append( buildReport( tableModel1, tableModel2 ) );
                if ( i != ( count - 1 ) ) { sb.append( "\r\n" ); }
            }
            return sb.toString();
        }
        else
        {
            return "No data available.\r\n";
        }
    }

    private String buildReport
        ( AbstractTableModel model1, AbstractTableModel model2 )
    {
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }
    
    /*
    Helper method for save string to file and visual status.
    INPUT:   parentWin = parent GUI frame.
             filePath = saved file path string.
             fileData = saved file data as single string contains separators.
    OUTPUT:  None (void).
    */
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
            JOptionPane.showMessageDialog
                ( parent, "Report saved: " + filePath  + ".",
                "Report", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
            JOptionPane.showMessageDialog
            ( parent, "Write report failed", 
              "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}

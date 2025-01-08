package dumploader.gui;

import static dumploader.gui.HelperTableToReport.tableReport;
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

/*
    // Handler for "Report this" dialogue method, setup GUI.
    public void reportThisDialogue
    ( JFrame parentWin, 
      AbstractTableModel model1, AbstractTableModel model2,
      String appStr , String webStr )
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
                        ( parentWin, "File exist: " + s1 + "\noverwrite?" ,
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
                String s2 = "Report file.\r\n" + appStr + webStr + "\r\n" ;
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
*/

    // Handler for "System report" from root menu or
    // "Report full" from down buttons dialogue methods, setup GUI.
//  public void reportSystemOrFullDialogue
    public void reportDialogue
    ( JFrame parentWin, // boolean reportType, 
      AbstractTableModel[] models1, AbstractTableModel[] models2,
      String appStr , String webStr ) 
    {
/*
        String reportTypeString;
        if(reportType)
            reportTypeString = "Report full";
        else
            reportTypeString = "Complex system report";
        CHOOSER.setDialogTitle( reportTypeString + " - select directory" );
*/
        CHOOSER.setDialogTitle( "Report - select directory" );
//        
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
                        ( parentWin, "File exist: " + s1 + "\noverwrite?" ,
                        "REPORT" ,
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
                /*
                String s2 = "Report file.\r\n" +
                         longName + "\r\n" + vendorVersion + "\r\n\r\n";
                */
                String s2 = "Report file.\r\n" + appStr + webStr + "\r\n" ;
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

    /*
    Helper method for save string to file and visual status.
    INPUT:   parentWin = parent GUI frame.
             filePath = saved file path string.
             fileData = saved file data as single string contains separators.
    OUTPUT:  None (void).
    */
    private void saveReport( JFrame parentWin, 
            String filePath, String fileData )
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

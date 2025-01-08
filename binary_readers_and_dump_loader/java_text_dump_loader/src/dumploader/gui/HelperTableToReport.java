/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class with static helpers methods for convert tables to text strings.
Used for unified report generation method for Console and GUI modes.

Table model is preferred solution than arrays of strings, because report
can be saved after table interactive edit at GUI mode.
Even if complex non-optimal conversion sequence:
String[][] --> Table models --> String[][] is possible.

*/

package dumploader.gui;

import javax.swing.table.AbstractTableModel;

class HelperTableToReport 
{
    // private static final int MAXCOL_DEFAULT = 13;
    private static final int MAXCOL_DEFAULT = 1;
    //
    
    private static final int MAXCOL_LIMIT = 120;
    
    /*
    Helpers methods
    Helper method for convert table model to string
    INPUT:   atm = abstract table model
    OUTPUT:  string.
    */

    public static String tableReport
        ( AbstractTableModel atm ) 
    {
        return tableReport( atm, false );
    }


    public static String tableReport
        ( AbstractTableModel atm, boolean strictSpaces ) 
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
        
        // for ( int i=0; i<maxcols.length; i++ ) { maxcol += maxcols[i]; }
        for ( int i=0; i<maxcols.length; i++ ) { maxcol += maxcols[i] + 2; }
        //
        
        // Write table up.
        for (int i=0; i<m; i++)
        {
            s = atm.getColumnName(i);
            
            if( strictSpaces )
            {
                s = s.trim();
            }
            else
            {
                report.append( " " );
            }
            
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
                
                if( strictSpaces )
                {
                    s = s.trim();
                }
                
                report.append( s );
                a = maxcols[i] - s.length() + 2;
                for ( int k=0; k<a; k++ ) 
                { 
                    report.append( " " ); 
                }
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
    INPUT:  atm = abstract table model
            j = string vertical position in the table
            i = string horizontal position in the table
    OUTPUT:  string, size limited.
    */
    private static String getShortString ( AbstractTableModel model, int j, int i )
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
}

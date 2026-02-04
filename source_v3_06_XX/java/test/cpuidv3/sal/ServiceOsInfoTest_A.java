/*
Unit test for class: ServiceOsInfo.java.
Get Operating System and environment information 
by Java methods in the tested class, output results to console.
*/

package cpuidv3.sal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceOsInfoTest_A 
{

/*
Test template methods.    
*/    
    
    public ServiceOsInfoTest_A() 
    {
        System.out.println( "ServiceOsInfoTest() runs." );
    }
    
    @BeforeClass
    public static void setUpClass() 
    {
        System.out.println( "setUpClass() runs." );
    }
    
    @AfterClass
    public static void tearDownClass() 
    {
        System.out.println( "tearDownClass() runs." );
    }
    
    @Before
    public void setUp() 
    {
        System.out.println( "setUp() runs." );
    }
    
    @After
    public void tearDown() 
    {
        System.out.println( "tearDown() runs." );
    }

    @Test
    public void testTarget() 
    {
        System.out.println( "Test class ServiceOsInfo.java." );
        SAL sal = new SAL();
        ServiceOsInfo service = new ServiceOsInfo( sal );
        HelperPrint helperPrint = new HelperPrint();
        
        String tableName = service.getTableName();
        String[] tableUp = service.getTableUp();
        String[][] tableData = service.getTableData();
        if (( tableName != null )&&( tableUp != null)&&( tableData != null ))
        {
            System.out.println
                ( "\r\n---------- Get parameters ----------\r\n" );
            helperPrint.printTable( tableName, tableUp, tableData );
            System.out.println
                ( "\r\n---------- Summary report ----------\r\n" );
            service.printSummaryReport();
        }
        else
        {
            fail( "Get data FAILED." );
        }
    }

/*
Helpers.    
*/
    
    final class HelperPrint
    {
        // Build formatted report as [internal] single string with sub-strings.
        // Report format is text table.
        // Table can be used for text console output or saved to text file.

        void printTable
            ( String tableName, String[] tableUp, String[][] tableData )
            {
                printTable( tableName, tableUp, tableData, false );
            }
            
        void printTable( String[] tableUp, String[][] tableData )
        {
            printTable( null, tableUp, tableData, true );
        }
        
        void printTable
            ( String tableName, String[] tableUp, String[][] tableData, 
              boolean f )
        {
            // Verify report data consistency.
            if (( ( !f )&&( tableName == null ) )||( tableUp == null )||
                ( tableData == null ))
            {
                System.out.println
                    ( "Report FAILED: null object detected." );
                return;
            }
            if (( ( !f )&&( tableName.length() == 0 ) )||
                ( tableUp.length == 0 )||( tableData.length == 0 )||
                ( tableData[0] == null )||( tableData.length == 0))
            {
                System.out.println
                    ( "Report FAILED: null or zero-length object detected." );
                return;
            }
            // Additional verify report data consistency, count format values.
            int maxLengths[] = new int[tableUp.length];
            boolean consistency = true;
            for( int i=0; i<tableUp.length; i++ )
            {
                if( tableUp[i] == null )
                {
                    consistency = false;
                    break;
                }
                else
                {
                    maxLengths[i] = tableUp[i].length();
                }
            }
            for( int i=0; ( i<tableData.length ) && consistency; i++ )
            {
                if(( tableData[i] == null )||
                   ( tableData[i].length != maxLengths.length ))
                {
                    consistency = false;
                    break;
                }
                else
                {
                    for( int j=0; j<tableData[i].length; j++ )
                    {
                        if( tableData[i][j] == null )
                        {
                            consistency = false;
                            break;
                        }
                        else
                        {
                            int n = tableData[i][j].trim().length();
                            if( maxLengths[j] < n )
                            {
                                maxLengths[j] = n;
                            }
                        }
                    }
                }
            }
            if( !consistency )
            {
                System.out.println
                    ( "Report FAILED: inconsistent table data detected." );
                return;
            }
            // Consistency verification PASSED, now start build report.
            StringBuilder report = new StringBuilder( "" );
            int tableWidth = 1;
            for( int i=0; i<maxLengths.length; i++ )
            {
                tableWidth += maxLengths[i];
                tableWidth += 3;
            }
            StringBuilder sb = new StringBuilder( "" );
            for( int i=0; i<tableWidth; i++ )
            {
                sb.append( "-" );
            }
            // Table name.
            String tableLine = sb.toString();
            if ( !f )
            {
                String sName = String.format
                    ( "%s.\r\n", helperStringCell( tableName, 0 ) );
                report.append( sName );
                report.append( tableLine );
                report.append( "\r\n" );
            }
            // Table up.
            for( int i=0; i<maxLengths.length; i++ )
            {
                String s = ( i > 0 ) ? "  " : "";
                report.append( s );
                int cellWidth = maxLengths[i];
                report.append( helperStringCell( tableUp[i], cellWidth ) );
            }
            report.append( "\r\n" );
            report.append( tableLine );
            report.append( "\r\n" );
            // Table content.
            for ( String[] td : tableData ) 
            {
                for ( int j = 0; j<maxLengths.length; j++ )
                {
                    String s = ( j > 0 ) ? "  " : "";
                    report.append( s );
                    int cellWidth = maxLengths[j];
                    report.append( helperStringCell( td[j], cellWidth ) );
                }
                report.append( "\r\n" );
            }
        report.append( tableLine );
        System.out.println( report.toString() ); 
        }
    
        private static final int MAX_CELL = 120;
        private String helperStringCell( String s, int cellWidth )
        {
            s = s.trim();
            StringBuilder sb1 = new StringBuilder( " " );
            sb1.append( s );
            // Add spaces if required for formatting.
            if( cellWidth > 1 )
            {
                int count = cellWidth + 1 - s.length();
                for( int i=1; i<count; i++ )
                {
                    sb1.append( " " );
                }
            }
            // Limit string length by MAX_CELL constant.
            StringBuilder sb2 = sb1;
            if ( sb1.length() > MAX_CELL ) 
            {
                sb2.append( sb1.substring( 0, MAX_CELL - 2 ) );
                sb2.append( "..." );
            }
            return sb2.toString();
        }
    }
}

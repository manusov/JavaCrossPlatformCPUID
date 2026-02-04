/*
Test for class DecoderOsMpLinux.java.
This is not pure class-level unit test, because
depends on other classes and parent class.
This test must be runned under LINUX, failed under WINDOWS.
*/

package cpuidv3.servicemplinux;

import cpuidv3.pal.PAL;
import cpuidv3.sal.ChangeableTableModel;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceMpLinuxTest 
{
    ServiceMpLinux service;
    
    public ServiceMpLinuxTest()
    {
        System.out.println( "ServiceMpLinuxTest()() runs." );
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
        service = new ServiceMpLinux();
    }
    
    @After
    public void tearDown()
    {
        System.out.println( "tearDown() runs." );
        service = null;
    }
    
    @Test
    public void testTarget()
    {
        System.out.println( "Test class ServiceMpLinux.java.\r\n" );
        
        final int NOT_USED = 0;
        // Change this depend on platform.
        final PAL.OS_TYPE osType = PAL.OS_TYPE.LINUX64;
        
        // Test 1.
        if ( !service.initBinary( osType, NOT_USED ) )
        {
            fail( "Topology decoder initialization failed " + 
                  "(Linux required for this test)." );
        }
        
        if ( !service.parseBinary() )
        {
            fail( "Binary topology data parsing failed." );
        }
        
        String[] up = { "Parameter", "Value" };
        String[][] data = service.getSummaryAddStrings();
        if ( data == null )
        {
            fail( "Get topology summary strings failed." );
        }
            
        ChangeableTableModel tableModel = new ChangeableTableModel( up, data );
        String tableData = tableReport( tableModel );
        System.out.println( tableData );
        
        // Test 2.
        if ( !service.initBinary( osType, NOT_USED ) )
        {
            fail( "Topology decoder initialization failed." );
        }
        
        if ( !service.parseBinary() )
        {
            fail( "Binary topology data parsing failed." );
        }

        String[] shortNames = service.getShortNames();
        String[] longNames = service.getLongNames();
        String[][] listsUps = service.getListsUps();
        String[][][] lists =  service.getLists();
        
        if ( ( shortNames == null )||( longNames == null )||
             ( listsUps == null )||( lists == null ) )
        {
            fail( "Get topology tables failed" );
            return;  // This for next warning supress.
        }
        
        if( ( shortNames.length != longNames.length )||
            ( listsUps.length != lists.length )||
            ( shortNames.length != lists.length ) )
        {
            fail( "Topology data length mismatch." );
        }
        
        for( int i=0; i<shortNames.length; i++ )
        {
            System.out.println( shortNames[i] + " = " + longNames[i] + "." );
            
            up = listsUps[i];
            data = lists[i];
            if (( up == null )||( data == null ))
            {
                fail( "Get topology first (object) table failed." );
            }
            ChangeableTableModel model = 
                new ChangeableTableModel( up, data );
            String table = tableReport( model );
            System.out.println( table );
        }
        System.out.println();
        
        String[][] dumpUps = service.getDumpsUps();
        String[][][] dumps = service.getDumps();
        if( ( dumpUps == null )||( dumps == null )||
            ( dumpUps[0] == null )||( dumps[0] == null ) )
        {
            fail( "Get topology second (dump) table failed." );
        }
        ChangeableTableModel model = 
            new ChangeableTableModel( dumpUps[0], dumps[0] );
        String table = tableReport( model );
        System.out.println( table );
        System.out.println();
    }
}

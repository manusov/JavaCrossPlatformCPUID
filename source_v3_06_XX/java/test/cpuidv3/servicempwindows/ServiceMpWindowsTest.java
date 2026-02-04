/*
Test for class DecoderOsMpWindows.java.
This is not pure class-level unit test, because
depends on other classes and parent class.
This test must be runned under WINDOWS, failed under LINUX.
*/

package cpuidv3.servicempwindows;

import cpuidv3.pal.PAL;
import cpuidv3.sal.ChangeableTableModel;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceMpWindowsTest 
{
    ServiceMpWindows service;
    
    public ServiceMpWindowsTest()
    {
        System.out.println( "ServiceMpWindowsTest()() runs." );
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
        service = new ServiceMpWindows();
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
        System.out.println( "Test class ServiceMpWindows.java.\r\n" );
        
        // Test 1.
        long[] topologyData = TopologyEmulator.getEmulateData();
        if ( topologyData == null )
        {
            fail( "Internal error: topology data emulator failed." );
        }
        
        service.setBinary( topologyData );
        
        final int STANDARD_TOPOLOGY = 0;
        if ( !service.initBinary( PAL.OS_TYPE.WIN32, STANDARD_TOPOLOGY ) )
        {
            fail( "Topology decoder initialization failed." );
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
        topologyData = TopologyEmulator.getEmulateData();
        if ( topologyData == null )
        {
            fail( "Internal error: topology data emulator failed." );
        }
        
        service.setBinary( topologyData );
        
        if ( !service.initBinary( PAL.OS_TYPE.WIN32, STANDARD_TOPOLOGY ) )
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
        // System.out.println();
    }
}

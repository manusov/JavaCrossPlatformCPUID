/*
Test for class DecoderOsMpLinux.java.
This is not pure class-level unit test, because
depends on other classes and parent class.
*/

package cpuidv3.servicemplinux;

import cpuidv3.pal.PAL;
import cpuidv3.sal.ChangeableTableModel;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DecoderOsMpLinuxTest 
{
    public DecoderOsMpLinuxTest() { }
    
    DecoderOsMpLinux decoder;
    
    @Before public void setUp() 
    {
        decoder = new DecoderOsMpLinux();
    }
    
    @After public void tearDown() 
    {
        decoder = null;
    }

    @Test public void testSetBinary() 
    {
        final int NOT_USED = 0;
        final PAL.OS_TYPE osType = PAL.OS_TYPE.LINUX64;  // Change this depend on platform
        
        if ( !decoder.initBinary( osType, NOT_USED ) )
        {
            fail( "Topology decoder initialization failed." );
        }
        
        if ( !decoder.parseBinary() )
        {
            fail( "Binary topology data parsing failed." );
        }
        
        String[] up = { "Parameter", "Value" };
        String[][] data = decoder.getSummaryAddStrings();
        if ( data == null )
        {
            fail( "Get topology summary strings failed." );
        }
            
        ChangeableTableModel tableModel = new ChangeableTableModel( up, data );
        String tableData = tableReport( tableModel );
        System.out.println( tableData );
    }

    @Test public void testTopologyObjectsTables()
    {
        final int NOT_USED = 0;
        final PAL.OS_TYPE osType = PAL.OS_TYPE.LINUX64;  // Change this depend on platform
        
        if ( !decoder.initBinary( osType, NOT_USED ) )
        {
            fail( "Topology decoder initialization failed." );
        }
        
        if ( !decoder.parseBinary() )
        {
            fail( "Binary topology data parsing failed." );
        }

        String[] shortNames = decoder.getShortNames();
        String[] longNames = decoder.getLongNames();
        String[][] listsUps = decoder.getListsUps();
        String[][][] lists =  decoder.getLists();
        
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
            
            String[] up = listsUps[i];
            String[][] data = lists[i];
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
        
        String[][] dumpUps = decoder.getDumpsUps();
        String[][][] dumps = decoder.getDumps();
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

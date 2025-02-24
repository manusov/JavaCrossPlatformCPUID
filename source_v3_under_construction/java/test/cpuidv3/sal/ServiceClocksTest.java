/*
Test for class ServiceClocks.java.
This is not pure class-level unit test, because depends on PAL and SAL classes,
native libraries, helpers, other classes and gets path to resource package.
Note PAL = Platform Abstraction Layer.
Note SAL = Services Abstraction Layer.
*/

package cpuidv3.sal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceClocksTest 
{
    public ServiceClocksTest() { }
    
    SAL sal = null;
    
    @Before public void setUp() 
    {
        String libPath = getResourcePackage();
        sal = SAL.getInstance( libPath );
        System.out.println( "SAL initialized." );
    }
    
    @After public void tearDown() 
    {
        sal.clearAllBinaryData();
        System.out.println( "SAL de-initialized.\r\n" );
    }

    @Test public void testGetTable()
    {
        System.out.println( "Test strings get and table build." );
        Service service = sal.getService(SAL.SERVICE_ID.CLOCKS );
        if ( service == null )
        {
            fail( "Service object is null." );
        }
        
        String tableName = service.getTableName();
        if ( tableName == null )
        {
            fail( "Table name string is null." );
        }
        
        ChangeableTableModel tableModel = sal.getClockTable();
        if ( tableModel == null )
        {
            fail( "Table model is null." );
        }

        System.out.println( "\r\n[ " + tableName + "]\r\n" );
        String tableData = tableReport( tableModel );
        System.out.println( tableData );
    }
}

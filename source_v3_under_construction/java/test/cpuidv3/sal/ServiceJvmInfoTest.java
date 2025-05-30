/*
Test for class ServiceJvmInfo.java.
This is not pure class-level unit test, because depends on PAL and SAL classes,
native libraries, helpers, other classes and gets path to resource package.
Note PAL = Platform Abstraction Layer.
Note SAL = Services Abstraction Layer.
*/

package cpuidv3.sal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import cpuidv3.sal.SAL.SERVICE_ID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceJvmInfoTest 
{
    public ServiceJvmInfoTest() {  }
    
    SAL sal = null;
    
    @Before public void setUp() 
    {
        String libPath = getResourcePackage();
        sal = SALHW.getInstance( libPath );
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
        Service service = sal.getService( SERVICE_ID.JVM_INFO );
        if ( service == null )
        {
            fail( "Service object is null." );
        }
        
        String tableName = service.getTableName();
        if ( tableName == null )
        {
            fail( "Table name string is null." );
        }
        
        ChangeableTableModel tableModel = sal.getJvmTable();
        if ( tableModel == null )
        {
            fail( "Table model is null." );
        }

        System.out.println( "\r\n[ " + tableName + "]\r\n" );
        String tableData = tableReport( tableModel );
        System.out.println( tableData );
    }
}

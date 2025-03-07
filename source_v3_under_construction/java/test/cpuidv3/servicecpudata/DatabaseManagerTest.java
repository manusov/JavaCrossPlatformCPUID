/*
Test for class DatabaseManager.java.
This is not pure class-level unit test, because depends on PAL class,
native libraries, helpers, other classes and gets path to resource package.
Note PAL = Platform Abstraction Layer.
Note SAL = Services Abstraction Layer.
*/

package cpuidv3.servicecpudata;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseManagerTest 
{
    public DatabaseManagerTest() { }
    
    private final static String PHYSICAL_VENDOR = "GenuineIntel";
    private final static String VIRTUAL_VENDOR = "KVMKVMKVM";
    
    DatabaseManager manager;
    
    @Before public void setUp() 
    {
        manager = new DatabaseManager( PHYSICAL_VENDOR, VIRTUAL_VENDOR );
    }
    
    @After public void tearDown() 
    {
        manager = null;
    }

    @Test public void testPart() 
    {
        manager.buildStash();
        
        String s = manager.getPhysicalVendor();
        if ( s == null )
        {
            fail( "Get physical vendor string failed." );
        }
        System.out.println( s );

        s = manager.getVirtualVendor();
        if ( s == null )
        {
            fail( "Get virtual vendor string failed." );
        }
        System.out.println( s );
    }
}

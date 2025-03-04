/*
Test for class SAL.java.
This is not pure class-level unit test, because depends on PAL class,
native libraries, helpers, other classes and gets path to resource package.
Note PAL = Platform Abstraction Layer.
Note SAL = Services Abstraction Layer.
*/

package cpuidv3.sal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import cpuidv3.pal.PAL;
import cpuidv3.pal.PAL.PAL_STATUS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SALTest 
{
    public SALTest() {  }
    
    SALHW sal = null;
    
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

    @Test public void testGetNativeStatus() 
    {
        System.out.println( "Test sal.getNativeStatus()." );
        PAL_STATUS status = sal.getPalStatus();
        if ( status == null )
        {
            fail( "SAL status is null." );
        }
        if ( status != PAL_STATUS.SUCCESS )
        {
            fail( "SAL initialization failed." );
        }
        System.out.println( "Status = " + status + "." );
    }

    @Test
    public void testGetRuntimeName() 
    {
        System.out.println( "Test sal.getRuntimeName()." );
        String runtimeName = sal.getRuntimeName();
        if ( runtimeName == null )
        {
            fail( "Runtime name string is null." );
        }
        System.out.println( "Runtime = " + runtimeName + "." );
    }

    @Test public void testGetPal() 
    {
        System.out.println( "Test sal.getPal()." );
        PAL pal = sal.getPal();
        if ( pal == null )
        {
            fail( "PAL object is null." );
        }
        System.out.println( "PAL object = " + pal + "." );
    }

    @Test
    public void testGetService() 
    {
        System.out.println( "Test sal.getService()." );
        Service service = sal.getService( SALHW.SERVICE_ID.CLOCKS );
        if ( service == null )
        {
            fail( "Service object is null." );
        }
        System.out.println( "One of service objects = " + service + "." );
    }

    @Test
    public void testConsoleSummary() 
    {
        System.out.println("Test sal.consoleSummary()." );
        sal.consoleSummary();
    }
    
}

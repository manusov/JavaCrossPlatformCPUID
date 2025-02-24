/*
Test for class PAL.java.
This is not pure class-level unit test, because depends on
native libraries, helpers and gets path to resource package.
Note PAL = Platform Abstraction Layer.
Note SAL = Services Abstraction Layer.
Note PALTest.java and HelperDetectorTest.java yet same functionality.
*/

package cpuidv3.pal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import cpuidv3.pal.PAL.OS_TYPE;
import cpuidv3.pal.PAL.PAL_STATUS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PALTest 
{
    public PALTest() { }
    
    PAL pal = null;
    
    @Before public void setUp() 
    {
        String libPath = getResourcePackage();
        pal = PAL.getInstance( libPath );
        System.out.println( "PAL initialized." );
    }
    
    @After public void tearDown() 
    {
        int a = pal.resetPAL();
        String s = String.format( "PAL de-initialized, return = %08Xh.", a );
        System.out.println( s );
    }
    
    @Test public void testState() 
    {
        PAL_STATUS ps = pal.getPalStatus();
        System.out.println( "PAL status   = " + ps + "." );
        OS_TYPE ot = pal.getOsType();
        System.out.println("OS type      = " + ot + "." );
        String rn = pal.getRuntimeName();
        System.out.println("Runtime name = " + rn + "." );
        
        if (( ps == null )||( ps != PAL_STATUS.SUCCESS ))
        {
            fail( "Bad PAL status." );
        }
        
        if (( ot == null )||( ot == OS_TYPE.UNKNOWN ))
        {
            fail( "OS detection failed." );
        }

        if ( rn == null )
        {
            fail( "Runtime name is null." );
        }
    }
}

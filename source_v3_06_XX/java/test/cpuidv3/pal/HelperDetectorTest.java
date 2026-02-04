
package cpuidv3.pal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HelperDetectorTest 
{
    PAL pal = null;
    
    public HelperDetectorTest()
    {
        System.out.println( "HelperDetectorTest() runs." );
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
        String libPath = getResourcePackage();
        pal = PAL.getInstance( libPath );
        System.out.println( "PAL initialized." );
    }
    
    @After
    public void tearDown()
    {
        System.out.println( "tearDown() runs." );
        int a = pal.resetPAL();
        String s = String.format( "PAL de-initialized, return = %08Xh.", a );
        System.out.println( s );

    }

    @Test
    public void testTarget()
    {
        System.out.println( "Test class HelperDetector.java.\r\n" );
        
        PAL.PAL_STATUS ps = pal.getPalStatus();
        System.out.println( "PAL status   = " + ps + "." );
        PAL.OS_TYPE ot = pal.getOsType();
        System.out.println("OS type      = " + ot + "." );
        String rn = pal.getRuntimeName();
        System.out.println("Runtime name = " + rn + ".\r\n" );
        
        if (( ps == null )||( ps != PAL.PAL_STATUS.SUCCESS ))
        {
            fail( "Bad PAL status." );
        }
        
        if (( ot == null )||( ot == PAL.OS_TYPE.UNKNOWN ))
        {
            fail( "OS detection failed." );
        }

        if ( rn == null )
        {
            fail( "Runtime name is null." );
        }
    }   
}

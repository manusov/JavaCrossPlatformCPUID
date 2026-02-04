/*
Test for class HelperTscMeasurer.java.
This is not pure class-level unit test, because depends on PAL,
native libraries, helpers and gets path to resource package.
Note PAL = Platform Abstraction Layer.
*/

package cpuidv3.pal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HelperTscMeasurerTest 
{
    PAL pal = null;
    
    public HelperTscMeasurerTest()
    {
        System.out.println( "HelperTscMeasurerTest() runs." );
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
        System.out.println( "Test class HelperTscMeasurer.java.\r\n" );
        
        System.out.println( "Test pal.measureTscFrequency()." );
        long[] result = pal.measureTscFrequency();
        if ( result == null )
        {
            fail( "No data." );
        }
        else if ( result.length != 1 )
        {
            fail( "Bad data size." );
        }
        else
        {
            long hz = result[0];
            if ( hz <= 0 )
            {
                fail( "Bad frequency value." );
            }
            
            double frequency = hz / 1000000.0;
            System.out.println
                ( String.format( "TSC frequency hex = %016Xh.", hz ) );
            System.out.println
                ( String.format( "Frequency value   = %.2f MHz.", frequency ) );
        }
        System.out.println();
    }   
}

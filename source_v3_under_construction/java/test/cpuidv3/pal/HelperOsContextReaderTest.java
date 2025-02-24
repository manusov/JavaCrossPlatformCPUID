/*
Test for class HelperOsContextReader.java.
This is not pure class-level unit test, because depends on PAL,
native libraries, helpers and gets path to resource package.
Note PAL = Platform Abstraction Layer.
*/

package cpuidv3.pal;

import static cpuidv3.CPUIDv3.getResourcePackage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HelperOsContextReaderTest 
{
    public HelperOsContextReaderTest() { }
    
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

    @Test public void testGetOsContext() 
    {
        System.out.println( "Test pal.getOsContext()." );
        long[] result = pal.getOsContext();
        if ( result == null )
        {
            fail( "No data." );
        }
        else if ( result.length <= 0 )
        {
            fail( "Bad data size." );
        }
        else
        {
            long cpuMap = result[0];
            long osMap = result[1];
            System.out.println
                ( String.format( "CPU supported bitmap = %016X", cpuMap ) );
            System.out.println
                ( String.format( "OS enabled bitmap    = %016X", osMap ) );
        }
    }
}

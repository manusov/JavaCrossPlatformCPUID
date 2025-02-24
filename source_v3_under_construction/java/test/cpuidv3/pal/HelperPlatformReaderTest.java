/*
Test for class HelperPlatformReader.java.
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

public class HelperPlatformReaderTest 
{
    public HelperPlatformReaderTest() {  }
    
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

    @Test public void testGetPlatformInfo() 
    {
        System.out.println( "Test pal.getPlatformInfo()." );
        long[] result = pal.getPlatformInfo();
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
            int cpuCount = (int)( result[0] & 0xFFFFFFFFL );
            
            System.out.println( String.format( 
                "Logical CPU count = %d.", 
                cpuCount ) );
            
            System.out.println( String.format( 
                "Affinity mask size = %d bytes.",
                ( result.length - 1 ) * 8 ) );
            
            for( int i=1; i<result.length; i++ )
            {
                System.out.print( String.format( "%016Xh", result[i] ) );
                if ( i < ( result.length - 1 ) )
                {
                    System.out.print(", ");
                }
                else
                {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }
}

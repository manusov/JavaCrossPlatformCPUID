/*
Test for class HelperTopologyReader.java.
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

public class HelperTopologyReaderTest 
{
    public HelperTopologyReaderTest() {  }
    
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

    @Test public void testGetTopology() 
    {
        System.out.println( "Test pal.getTopology()." );
        long[] result = pal.getTopology();
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
            helperHexDump( result );
        }
    }

    @Test public void testGetExtendedTopology() 
    {
        System.out.println( "Test pal.getExtendedTopology()." );
        long[] result = pal.getExtendedTopology();
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
            helperHexDump( result );
        }
    }
    
    private void helperHexDump( long[] data )
    {
        final String tableString = 
            " Offset(h)  x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 xA xB xC xD xE xF";
        final int TAB_LINE_LENGTH = tableString.length() + 1;
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<TAB_LINE_LENGTH; i++ ) { sb.append( "-" ); }
        String tabLine = sb.toString();
        System.out.println( tabLine );
        System.out.println( tableString );
        System.out.println( tabLine );
        int index = 0;
        while( index < data.length )
        {
            System.out.print( String.format( " %08X   ", index * 8 ));
            StringBuilder sbLine = new StringBuilder();
            for( int i=0; i<2; i++ )
            {
                long a = data[ index++ ];
                for( int j=0; j<8; j++ )
                {
                    int b = (int)(a & 0xFFL);
                    sbLine.append( String.format( "%02X ", b ));
                    a >>>= 8;
                }
                if ( index >= data.length )
                {
                    break;
                }
            }
            System.out.println( sbLine.toString() );
        }
        System.out.println( tabLine );
    }
}

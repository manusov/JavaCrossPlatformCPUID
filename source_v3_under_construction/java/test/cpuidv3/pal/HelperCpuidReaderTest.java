/*
Test for class HelperCpuReader.java.
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

public class HelperCpuidReaderTest 
{
    public HelperCpuidReaderTest() { }
    
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
        System.out.println( s + "\r\n" );
    }

    @Test public void testGetCpuid() 
    {
        System.out.println( "pal.getCpuid()." );
        helperPrintTable( pal.getCpuid() );

    }

    @Test public void testGetCpuidSubfunction() 
    {
        final String formatString = "pal.getCpuidSubfunction( %08Xh, %08Xh ).";
        
        final int FUNCTION = 4;  // Edit this by platform config or use pal.getPlatformInfo().
        final int SUBF_0 = 0;    // Edit this by platform config or use pal.getPlatformInfo().
        String s = String.format( formatString, FUNCTION, SUBF_0 );
        System.out.println( s );
        helperPrintTable( pal.getCpuidSubfunction( FUNCTION, SUBF_0 ) );
        
        final int SUBF_1 = 1;    // Edit this by platform config or use pal.getPlatformInfo().
        s = String.format( formatString, FUNCTION, SUBF_1 );
        System.out.println( s );
        helperPrintTable( pal.getCpuidSubfunction( FUNCTION, SUBF_1 ) );
    }

    @Test public void testGetCpuidAffinized() 
    {
        System.out.println( "pal.getCpuidAffinized()." );
        final int TEST_CPU_COUNT = 3;  // Edit this by platform config or use pal.getPlatformInfo().
        for( int i=0; i<TEST_CPU_COUNT; i++ )
        {
            System.out.println( String.format( " CPU #%d", i ) );
            helperPrintTable( pal.getCpuidAffinized( i ) );
        }
    }

    @Test public void testGetCpuidSubfunctionAffinized() 
    {
        final String formatString = 
            "pal.getCpuidSubfunctionAffinized( %08Xh, %08Xh, %d ).";

        // Edit this by platform config or use pal.getPlatformInfo().
        int function = 0x1;
        int subfunction = 0;
        int cpuIndex = 2;
        String s = String.format
            ( formatString, function, subfunction, cpuIndex );
        System.out.println( s );
        helperPrintTable( pal.getCpuidSubfunctionAffinized
            ( function, subfunction, cpuIndex ) );
        
        // Edit this by platform config or use pal.getPlatformInfo().
        function = 0x0B;
        subfunction = 0;
        cpuIndex = 5;
        s = String.format( formatString, function, subfunction, cpuIndex );
        System.out.println( s );
        helperPrintTable( pal.getCpuidSubfunctionAffinized
            ( function, subfunction, cpuIndex ) );

        // Edit this by platform config or use pal.getPlatformInfo().
        function = 0x0B;
        subfunction = 1;
        cpuIndex = 5;
        s = String.format( formatString, function, subfunction, cpuIndex );
        System.out.println( s );
        helperPrintTable( pal.getCpuidSubfunctionAffinized
            ( function, subfunction, cpuIndex ) );
    }
    
    private void helperPrintTable( long[] result )
    {
        if( result == null )
        {
            final String s = "No array returned.";
            System.out.println( s );
            fail( s );
        }
        else if (( result.length < 4 )||( ( result.length % 4 ) != 0 ))
        {
            final String s = "Bad array size.";
            System.out.println( s );
            fail( s );
        }
        else
        {
            final String tableString = 
                " CPUID    subfunction  pass      " +
                "EAX      EBX      ECX      EDX";
            final int TAB_LINE_LENGTH = tableString.length() + 6;
            
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<TAB_LINE_LENGTH; i++ ) { sb.append( "-" );  }
            String tabLine = sb.toString();
            
            System.out.println( tabLine );
            System.out.println( tableString );
            System.out.println( tabLine );
            final int ROW_LONGS = 4;
            final int ROWS = result.length / ROW_LONGS;
            for( int i=0; i<ROWS; i++ )
            {
                int fnc    = (int)( result[i * ROW_LONGS] >>> 32 );
                int subfnc = (int)( result[i * ROW_LONGS + 1] & 0xFFFFFFFFL );
                int pass   = (int)( result[i * ROW_LONGS + 1] >>> 32 );
                int eax    = (int)( result[i * ROW_LONGS + 2] & 0xFFFFFFFFL );
                int ebx    = (int)( result[i * ROW_LONGS + 2] >>> 32 );
                int ecx    = (int)( result[i * ROW_LONGS + 3] & 0xFFFFFFFFL );
                int edx    = (int)( result[i * ROW_LONGS + 3] >>> 32 );
                String line = String.format
                    ( " %08X %08X     %08X  %08X %08X %08X %08X", 
                      fnc, subfnc, pass, eax, ebx, ecx, edx );
                System.out.println( line );
            }
            System.out.println( tabLine + "\r\n");
        }
    }
}

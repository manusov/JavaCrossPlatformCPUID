/*
Unit test for class BinaryLoader.java.
*/

package cpuidv3.gui;

import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BinaryLoaderTest 
{
    public BinaryLoaderTest()
    {
        System.out.println( "BinaryLoaderTest() runs." );
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
    }
    
    @After
    public void tearDown()
    {
        System.out.println( "tearDown() runs." );
    }

    @Test
    public void testTarget() 
    {
        final String titleString = 
            "Test class BinaryLoader.java, method loadBinaryDialogue().";
        System.out.println( titleString );
        final JFrame parentWin = null;
        final BinaryLoader instance = new BinaryLoader();
        final long[] result = instance.loadBinaryDialogue( parentWin );
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
            final int TAB_LINE_LENGTH = Integer.max
                ( titleString.length(), tableString.length() + 6 );
            
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<TAB_LINE_LENGTH; i++ ) { sb.append( "-" ); }
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
            System.out.println( tabLine );
        }
    }
}

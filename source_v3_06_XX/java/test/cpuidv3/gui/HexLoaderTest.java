/*
Unit test for class HexLoader.java.
*/

package cpuidv3.gui;

import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HexLoaderTest 
{
    public HexLoaderTest() 
    {
        System.out.println( "HexLoaderTest() runs." );
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
            "Test class HexLoader.java, method loadTextDialogue().";
        System.out.println( titleString );
        final JFrame parentWin = null;
        final HexLoader instance = new HexLoader();
        final int[] result = instance.loadTextDialogue( parentWin );
        if( result == null )
        {
            final String s = "No array returned.";
            System.out.println( s );
            fail( s );
        }
        else if (( result.length < 5 )||( ( result.length % 5 ) != 0 ))
        {
            final String s = "Bad array size.";
            System.out.println( s );
            fail( s );
        }
        else
        {
            final String tableString = 
                    " CPUID    EAX      EBX      ECX      EDX";
            final int TAB_LINE_LENGTH = Integer.max
                ( titleString.length(), tableString.length() + 6 );
            
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<TAB_LINE_LENGTH; i++ ) { sb.append( "-" ); }
            String tabLine = sb.toString();
            
            System.out.println( tabLine );
            System.out.println( tableString );
            System.out.println( tabLine );
            final int COLUMNS = 5;
            final int ROWS = result.length / COLUMNS;
            for( int i=0; i<ROWS; i++ )
            {
                int cpuid = result[i * COLUMNS ];
                int eax   = result[i * COLUMNS + 1];
                int ebx   = result[i * COLUMNS + 2];
                int ecx   = result[i * COLUMNS + 3];
                int edx   = result[i * COLUMNS + 4];
                String line = String.format
                    ( " %08X %08X %08X %08X %08X", cpuid, eax, ebx, ecx, edx );
                System.out.println( line );
            }
            System.out.println( tabLine );
        }
    }
}

/*
Unit test for class BinarySaver.java.
*/

package cpuidv3.gui;

import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinarySaverTest 
{
    public BinarySaverTest()
    {
        System.out.println( "BinarySaverTest() runs." );
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
        System.out.println
            ( "Test class BinarySaver.java, method saveBinaryDialogue()." );
        final JFrame parentWin = null;
        final long[] data = new long[]{ 1, 2, 3, 4, -1, -2, -3, -4 };
        final BinarySaver instance = new BinarySaver();
        instance.saveBinaryDialogue( parentWin, data );
    }
}

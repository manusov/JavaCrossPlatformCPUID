/*
Unit test for class HexSaver.java.
*/

package cpuidv3.gui;

import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HexSaverTest 
{
    public HexSaverTest()
    {
        System.out.println( "HexSaverTest() runs." );
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
            ( "Test class HexSaver.java, method saveHexDialogue() #1." );
        JFrame parentWin = null;
        String nameStr = "Emulate application name string";
        String appStr = "Emulate application mode string";
        String webStr = "Emulate web link string.\r\n";
        String reportStr = "Emulate SINGLE-CPU HEX DUMP name string";
        long[] data = new long[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
        HexSaver instance = new HexSaver();
        instance.saveHexDialogue
            ( parentWin, nameStr, appStr, webStr, reportStr, data );
        System.out.println( "Done #1." );

        System.out.println
            ( "Test class HexSaver.java, method saveHexDialogue() #2." );
        parentWin = null;
        nameStr = "Emulate application name string";
        appStr = "Emulate application mode string";
        webStr = "Emulate web link string.\r\n";
        reportStr = "Emulate MULTI-CPU HEX DUMP name string";
        long[][] mData = new long[][]{ { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
        instance = new HexSaver();
        instance.saveHexDialogue
            ( parentWin, nameStr, appStr, webStr, reportStr, mData );
        System.out.println( "Done #2." );
    }
}

/*
Unit test for class HexSaver.java.
*/

package cpuidv3.gui;

import javax.swing.JFrame;
import org.junit.Test;

public class HexSaverTest 
{    
    public HexSaverTest() { }

    @Test public void testSaveHexDialogue_6args_1() 
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
    }

    @Test public void testSaveHexDialogue_6args_2() 
    {
        System.out.println
            ( "Test class HexSaver.java, method saveHexDialogue() #2." );

        JFrame parentWin = null;
        String nameStr = "Emulate application name string";
        String appStr = "Emulate application mode string";
        String webStr = "Emulate web link string.\r\n";
        String reportStr = "Emulate MULTI-CPU HEX DUMP name string";
        long[][] data = new long[][]{ { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };

        HexSaver instance = new HexSaver();
        instance.saveHexDialogue
            ( parentWin, nameStr, appStr, webStr, reportStr, data );

        System.out.println( "Done #2." );
    }
}

/*
Unit test for class BinarySaver.java.
*/

package cpuidv3.gui;

import javax.swing.JFrame;
import org.junit.Test;

public class BinarySaverTest 
{
    public BinarySaverTest() { }

    @Test public void testSaveBinaryDialogue() 
    {
        System.out.println
            ( "Test class BinarySaver.java, method saveBinaryDialogue()." );
        final JFrame parentWin = null;
        final long[] data = new long[]{ 1, 2, 3, 4, -1, -2, -3, -4 };
        final BinarySaver instance = new BinarySaver();
        instance.saveBinaryDialogue( parentWin, data );
    }
}

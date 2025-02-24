/*
Unit test for class CpuSelector.java.
*/

package cpuidv3.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class CpuSelectorTest 
{
    public CpuSelectorTest() { }

    private final static int TEST_CPU_COUNT = 4;
    private CpuSelector selector = null;
    
    @Before public void setUp() 
    {
        System.out.println( "Start test: class CpuSelector.java." );
        selector = new CpuSelector( TEST_CPU_COUNT );
    }
    
    @After public void tearDown() 
    {
        System.out.println( "End test: class CpuSelector.java." );
        selector = null;
    }

    @Test public void testCreateDialog() 
    {
        System.out.println( "Test method: createDialog() and getters." );
        JFrame parentWin = null;
        final JDialog dialog = selector.createDialog( parentWin );
        
        if ( dialog == null )
        {
            fail( "Dialog object is null." );
        }
        else
        {
            dialog.setLocationRelativeTo( parentWin );
            dialog.setVisible( true );

            boolean gms = selector.getMakeSelection();
            System.out.println( "getMakeSelection = " + gms + "." );
            if ( gms )
            {
                boolean gae = selector.getAffEnabled();
                System.out.println( "getAffEnabled = " + gae + "." );
                int gcs = selector.getCpuSelected();
                System.out.println( "getCpuSelected = " + gcs + "." );
            
                if ( gae && gcs >= TEST_CPU_COUNT )
                {
                    fail( "Selected processor number is too big." );
                }
                else if ( gae && ( gcs < 0 ) )
                {
                    fail( "Selected processor number is negative." );
                }
            }
        }
    }
}

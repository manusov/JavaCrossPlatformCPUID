/*
Unit test for class About.java.
*/

package cpuidv3.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import static org.junit.Assert.fail;
import org.junit.Test;

public class AboutTest 
{
    public AboutTest() {  }
    
    @Test public void testCreateDialog() 
    {
        System.out.println( "Test class About.java." );
        JFrame parentWin = null;
        About about = new About();
        final JDialog dialog = about.createDialog( null );
        
        if ( dialog == null )
        {
            fail( "Dialog object is null." );
        }
        else
        {
            dialog.setLocationRelativeTo( parentWin );
            dialog.setVisible( true );    
            System.out.println( "Done." );
        }
    }
}

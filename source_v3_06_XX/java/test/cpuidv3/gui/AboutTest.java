/*
Unit test for class: About.java.
Gets dialogue window class from tested class About.java,
runs GUI dialogue.
*/

package cpuidv3.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class AboutTest 
{
    public AboutTest() 
    {
        System.out.println( "AboutTest() runs." );
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

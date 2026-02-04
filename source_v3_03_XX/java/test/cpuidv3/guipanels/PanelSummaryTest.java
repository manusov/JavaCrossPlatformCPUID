/*
Test for class PanelSummary.java.
This is not pure class-level unit test, 
because depends on templates,  helpers and other classes.
Plus, replaces some functionality of tested class.
*/

package cpuidv3.guipanels;

import static cpuidv3.CPUIDv3.getResourcePackage;
import cpuidv3.sal.SAL;
import cpuidv3.sal.SALHW;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PanelSummaryTest 
{
    private final String TEST_NAME = "Test class PanelSummary.java";
    private final int X_SIZE = 700;
    private final int Y_SIZE = 700;
    private final int WAIT_QUANT_MS = 100;
    private PanelSummary testedPanel = null;
    private boolean termination = false;

    public PanelSummaryTest() { }


    @Before public void setUp() throws Exception 
    {
        SAL localSAL = SALHW.getInstance( getResourcePackage() );
        testedPanel = new PanelSummary( localSAL );
    }

    @After public void tearDown() throws Exception 
    {
        testedPanel = null;
    }
    
    class TestFrame extends JFrame
    {
        public TestFrame() 
        {
            super( TEST_NAME );

            setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
            addWindowListener( new WindowAdapter() 
            {
                @Override public void windowClosing( WindowEvent e ) 
                {
                    int confirm = JOptionPane.showOptionDialog( null,
                    "Close?", "Exit", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if ( confirm == JOptionPane.YES_OPTION ) 
                    {
                        termination = true;
                        dispose();
                    }
                }
            });
            
            SpringLayout layout = new SpringLayout();
            JPanel panel = new JPanel( layout );
            boolean physical = true;
            testedPanel.rebuildPanel( physical );
            JPanel tablePanel = testedPanel.getPanel();
            panel.add( tablePanel );
            getContentPane().add( panel );
            setSize( X_SIZE, Y_SIZE );
            setVisible( true );
        }
    }
    
    @Test public void testGetPanelGUI()
    {
        System.out.println( TEST_NAME + "." );
        new TestFrame();
        while( !termination )
        {
            try { Thread.sleep( WAIT_QUANT_MS ); }
            catch ( InterruptedException e ) { }
        }
        System.out.println( "Done." );
    }
    
    @Test public void testGetPanelName() 
    {
        System.out.println( "--- getPanelName() ---" );
        String result = testedPanel.getPanelName();
        System.out.println( result );
    }

    @Test public void testGetPanelIcon() 
    {
        System.out.println( "--- getPanelIcon() ---" );
        String result = testedPanel.getPanelIcon();
        System.out.println( result );
    }

    @Test public void testGetPanel() 
    {
        System.out.println( "--- getPanel() ---" );
        JPanel result = testedPanel.getPanel();
        System.out.println( "" + result );
    }

    @Test public void testGetPanelActive() 
    {
        System.out.println( "--- getPanelActive() ---" );
        boolean result = testedPanel.getPanelActive();
        System.out.println( "" + result );
    }
    
    @Test public void testGetPanelTip() 
    {
        System.out.println( "--- getPanelTip() ---" );
        String result = testedPanel.getPanelTip();
        System.out.println( result );
    }

    @Test public void testRebuildPanel() 
    {
        System.out.println( "--- rebuildPanel() ---" );
        boolean physical = true;
        testedPanel.rebuildPanel( physical );
    }
}

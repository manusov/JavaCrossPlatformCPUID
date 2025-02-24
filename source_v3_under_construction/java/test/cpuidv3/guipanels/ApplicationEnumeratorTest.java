/*
Test for class ReportSaver.java.
This is not pure class-level unit test, 
because depends on templates,  helpers and other classes.
*/

package cpuidv3.guipanels;

import static cpuidv3.CPUIDv3.getResourcePackage;
import cpuidv3.sal.SAL;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationEnumeratorTest 
{
    private ApplicationEnumerator apenum = null;
    
    public ApplicationEnumeratorTest() {  }
    
    @Before public void setUp() 
    {  
        System.out.println
            ( "[ Start test: class ApplicationEnumerator.java. ]" );
        SAL sal = SAL.getInstance( getResourcePackage() );
        apenum = new ApplicationEnumerator( sal );
    }
    
    @After public void tearDown() 
    {
        System.out.println
            ( "[ End test: class ApplicationEnumerator.java. ]\r\n" );
        apenum = null;
    }

    @Test public void testGetTabNames()
    {
        System.out.println( "--- getTabNames() ---" );
        String[] result = apenum.getTabNames();
        for ( String s : result) 
        {
            System.out.println( s );
        }
    }
    
    @Test public void testGetTabIcons() 
    {
        System.out.println( "--- getTabIcons() ---" );
        Icon[] result = apenum.getTabIcons();
        for ( Icon i : result )
        {
            System.out.println( "" + i );
        }
    }

    @Test public void testGetTabPanels() 
    {
        System.out.println( "--- getTabPanels() ---" );
        JPanel[] result = apenum.getTabPanels();
        for ( JPanel p : result )
        {
            System.out.println( "" + p );
        }
    }

    @Test public void testGetTabActives() 
    {
        System.out.println( "--- getTabActives() ---" );
        boolean[] result = apenum.getTabActives();
        for ( boolean b : result )
        {
            System.out.println( "" + b );
        }
    }

    @Test public void testGetTabTips() 
    {
        System.out.println( "--- getTabTips() ---" );
        String[] result = apenum.getTabTips();
        for ( String s : result )
        {
            System.out.println( s );
        }
    }

    @Test public void testBuildTabPanels() 
    {
        System.out.println( "--- buildTabPanels() ---" );
        boolean physical = true;
        apenum.buildTabPanels( physical );
    }
}

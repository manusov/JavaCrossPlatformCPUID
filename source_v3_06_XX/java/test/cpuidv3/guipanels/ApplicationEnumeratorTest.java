/*
Test for class ApplicationEnumerator.java.
This is not pure class-level unit test, 
because depends on templates,  helpers and other classes.
*/

package cpuidv3.guipanels;

import static cpuidv3.CPUIDv3.getResourcePackage;
import cpuidv3.sal.SAL;
import cpuidv3.sal.SALHW;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationEnumeratorTest 
{
    private ApplicationEnumerator apenum = null;
    
    public ApplicationEnumeratorTest()
    {
        System.out.println( "ApplicationEnumeratorTest() runs." );
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
        System.out.println
            ( "[ Start test: class ApplicationEnumerator.java. ]" );
        SAL sal = SALHW.getInstance( getResourcePackage() );
        apenum = new ApplicationEnumerator( sal );
    }
    
    @After
    public void tearDown()
    {
        System.out.println( "tearDown() runs." );
        System.out.println
            ( "[ End test: class ApplicationEnumerator.java. ]\r\n" );
        apenum = null;
    }

    @Test
    public void testTarget()
    {
        System.out.println( "Test class ApplicationEnumerator.java." );
        System.out.println( "\r\n--- getTabNames() ---" );
        String[] resultStrings = apenum.getTabNames();
        for ( String s : resultStrings ) 
        {
            System.out.println( s );
        }
        
        System.out.println( "\r\n--- getTabIcons() ---" );
        Icon[] resultIcon = apenum.getTabIcons();
        for ( Icon i : resultIcon )
        {
            System.out.println( "" + i );
        }

        System.out.println( "\r\n--- getTabPanels() ---" );
        JPanel[] resultPanel = apenum.getTabPanels();
        for ( JPanel p : resultPanel )
        {
            System.out.println( "" + p );
        }

        System.out.println( "\r\n--- getTabActives() ---" );
        boolean[] resultBool = apenum.getTabActives();
        for ( boolean b : resultBool )
        {
            System.out.println( "" + b );
        }

        System.out.println( "\r\n--- getTabTips() ---" );
        resultStrings = apenum.getTabTips();
        for ( String s : resultStrings )
        {
            System.out.println( s );
        }
        
        System.out.println( "\r\n--- buildTabPanels() ---" );
        boolean physical = true;
        apenum.buildTabPanels( physical );
        
        System.out.println();
    }    
}

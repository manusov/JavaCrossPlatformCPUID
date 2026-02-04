/*
Test for class ReportSaver.java.
This is not pure class-level unit test, 
because depends on templates,  helpers and other classes.
*/

package cpuidv3.gui;

import cpuidv3.sal.ChangeableTableModel;
import cpuidv3.sal.ReportData;
import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReportSaverTest 
{
    public ReportSaverTest()
    {
        System.out.println( "ReportSaverTest() runs." );
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
            ( "Test class ReportSaver.java, method saveReportDialogue() #1." );

        JFrame parentWin = null;
        String nameStr = "Emulate application name string";
        String appStr = "Emulate application mode string";
        String webStr = "Emulate web link string.\r\n";
        String reportStr = "Emulate TEXT REPORT name string";

        String screenName = "Screen name";
        String functionName = "Function 0";
        String[] up1 = new String[]{ "A", "B" };
        String[][] data1 = new String[][]{ { "A1", "B1" }, { "A2", "B2" } };
        ChangeableTableModel model1 = new ChangeableTableModel( up1, data1 );
        String[] up2 = new String[]{ "C", "D" };
        String[][] data2 = 
            new String[][]{ { "C1", "D1" }, { "C2", "D2" }, { "C3", "D3" } };
        ChangeableTableModel model2 = new ChangeableTableModel( up2, data2 );
        
        ReportData data = new ReportData
            (   screenName, new String[] { functionName },
                new ChangeableTableModel[] { model1 } ,
                new ChangeableTableModel[] { model2 } );

        ReportSaver instance = new ReportSaver();
        instance.saveReportDialogue( parentWin,
            nameStr, appStr, webStr, reportStr, data );

        System.out.println( "Done #1." );
        
        System.out.println
            ( "Test class ReportSaver.java, method saveReportDialogue() #2." );

        parentWin = null;
        nameStr = "Emulate application name string";
        appStr = "Emulate application mode string";
        webStr = "Emulate web link string.\r\n";
        reportStr = "Emulate TEXT REPORT name string";

        String screen0name = "Screen 0 name";
        functionName = "Function 0";
        up1 = new String[]{ "A", "B" };
        data1 = new String[][]{ { "A1", "B1" }, { "A2", "B2" } };
        model1 = new ChangeableTableModel( up1, data1 );
        up2 = new String[]{ "C", "D" };
        data2 = 
            new String[][]{ { "C1", "D1" }, { "C2", "D2" }, { "C3", "D3" } };
        model2 = new ChangeableTableModel( up2, data2 );
        ReportData dataScreen0 = new ReportData
            (   screen0name, new String[] { functionName },
                new ChangeableTableModel[] { model1 } ,
                new ChangeableTableModel[] { model2 } );

        String screen1name = "Screen 1 name";
        String itemName = "Item 1";
        String[] up3 = new String[]{ "E", "F" };
        String[][] data3 = new String[][]{ { "E1", "F1" }, { "E2", "F2" } };
        ChangeableTableModel model3 = new ChangeableTableModel( up3, data3 );
        String[] up4 = new String[]{ "G", "H" };
        String[][] data4 = 
            new String[][]{ { "G1", "H1" }, { "G2", "H2" }, { "G3", "H3" } };
        ChangeableTableModel model4 = new ChangeableTableModel( up4, data4 );
        ReportData dataScreen1 = new ReportData
            (   screen1name, new String[] { itemName },
                new ChangeableTableModel[] { model3 } ,
                new ChangeableTableModel[] { model4 } );

        instance = new ReportSaver();
        instance.saveReportDialogue( parentWin,
            nameStr, appStr, webStr, reportStr,
            new ReportData[]{ dataScreen0, dataScreen1} );

        System.out.println( "Done #2." );
    }
}

/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Sub-application class for show CPUID instruction information,
it read by native layer.
Based on MVC ( Model, View, Controller ) pattern.
Contains Controller, supports interface with data model and GUI view.
*/

package cpuidrefactoring.applications;

import cpuidrefactoring.About;
import cpuidrefactoring.CpuidRefactoring;
import cpuidrefactoring.database.VendorDetectPhysical;
import cpuidrefactoring.database.VendorDetectVirtual;
import cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T;
import static cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T.*;
import cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import cpuidrefactoring.rootmenu.*;
import cpuidrefactoring.system.Registry;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ApplicationCpuid extends ApplicationController
{
public ApplicationCpuid()
    {
    BuildModel model = new BuildModel();
    BuildView view = new BuildView
        ( model, About.getX2size(), About.getY2size() );
    setModel( model );
    setView( view );
    }
    
private class BuildModel extends ApplicationModel
    {
/*
Support early CPU and VMM vendor detection.
Database usage 1 of 3 = Early vendor detection.
See also: DeviceCpuid.java , CpuidSummary.java.
Early detect CPU vendor, this operation reserved for repeat receive CPUID
binary data with added vendor-specific functions. Note cannot add this
functions for all CPUs at first pass, because hardware failures and wrong
results possible if some incompatible vendor functions used.
Additionally, static classes store detection results and results can be
read later without re-detection.
*/
    private void earlyVendorDetect( boolean physical )
        {
        String       scpu = earlyExtractVendorString( STANDARD_KEY, opb );
        String       svmm = earlyExtractVendorString( VIRTUAL_KEY, opb );
        VENDOR_T     vcpu = VendorDetectPhysical.earlyDetect( scpu );
        HYPERVISOR_T vvmm = VendorDetectVirtual.earlyDetect( svmm );
/*            
reserved for secondary read binary data (with vendor-specific functions),
if vendors match patterns
physical flag:
true  = means physical or virtual CPU detection, secondary read possible
false = means load dump from file, secondary read not possible
*/
        if ( physical && vcpu == VENDOR_CYRIX )
            {
            // reserved for secondary read with vendor-specific additions
            }
        else if ( physical && vcpu == VENDOR_TRANSMETA )
            {
            // reserved for secondary read with vendor-specific additions
            }
          
        if ( physical && vvmm == HYPERVISOR_MICROSOFT )
            {
            // reserved for secondary read with vendor-specific additions
            }
        }
    
    // helper for extract CPU and VMM vendor strings from binary dump
    // possible optimization, see also ReservedFunctionCpuid.java,
    // same functionality duplication
    
    private final static int STANDARD_KEY = 0;
    private final static int VIRTUAL_KEY  = 0x40000000;
    private String earlyExtractVendorString( int key, long[] data )
        {
        boolean b = false;
        StringBuilder sb = new StringBuilder( "" );
        int n = data.length / 4;
        int m = ( int )( data[0] & 0x3FF );
        for( int i=1; ( i<m )&&( i<n ); i++ )
            {
            int function = (int)( data[ i*4 ] >> 32 );
            if ( function == key )
                {
                int ebx = (int)( data[ i*4+2 ] >> 32 );
                int ecx = (int)( data[ i*4+3 ] & (long)0xFFFFFFFF );
                int edx = (int)( data[ i*4+3 ] >> 32 );
                int[] signature;
                 // for virtual function 40000000h order is EBX-ECX-EDX
                if ( key == VIRTUAL_KEY )
                    signature = new int[]{ ebx, ecx, edx };
                // for functions 00000000h, 80000000h order is EBX-EDX-ECX
                else
                    signature = new int[]{ ebx, edx, ecx };
                // cycle for convert 3 integer numbers to 12-char string
                for( int j=0; j<3; j++ )
                    {
                    int d = signature[j];
                    for( int k=0; k<4; k++ )  // cycle convert int to 4 chars
                        {
                        char c = (char)( d & 0xFF );
                        if ( c != 0 )
                            {
                            if ( ( c < ' ' )||( c > '}' ) ) c = '.';
                            sb.append( c );
                            b = true;
                            }
                        d = d >>> 8;
                        }
                    }
                }
            }
        return b ? sb.toString() : null;
        }
/*
End of database usage 1 of 3 = Early vendor detection.
*/                

/*
Initializing Application Model.
*/
    
    // private final static String NAME = "CPUID Results";
    private final static int OPB_SIZE = 2048;  // IPB not used for this funct.
    private final static int FUNCTION_CPUID = 0;
    private final int jniStatus;
    private ViewSet[] viewSets;
    private boolean statusInit, statusParse;
    
    private BuildModel()
        {
        // Initializing hardware support objects
        opb = new long[ OPB_SIZE ];    // no clear, use fact: all elements = 0
        Registry r = CpuidRefactoring.getRegistry();
        // Receive CPUID binary data,
        // call function 0 = Get CPUID dump,
        // IPB = null, this means third parameter = function code = 0
        jniStatus = r.binaryGate( null, opb, FUNCTION_CPUID, OPB_SIZE );
        // Initialize status
        statusInit = false;
        statusParse = false;
        // Load driver
        device = r.loadDriver( Registry.CPR.DRIVER_CPUID );
        // Send binary data from hardware to CPR module
        device.setBinary( opb );
        // Analyzing data: verify array size and alignment restrictions
        if ( jniStatus > 0 ) { statusInit = device.initBinary();   }
        
        // Early vendor detection, 
        // parse binary array and generating CPUID data entries
        if ( statusInit )    
            {  // this usage of vendor detector when application starts
            earlyVendorDetect( true );
            statusParse = device.parseBinary(); 
            }
        
        // Build text data
        if ( statusInit & statusParse ) 
            {
            viewSets = buildStrings();
            }
        else
            {
            viewSets = null;
            }
        }
    
    @Override public int getCount() 
        {
        if ( viewSets != null ) return viewSets.length;
        else return 0;
        }
    
    @Override public ViewSet getSelectedModel( int i ) 
        {
        return viewSets[i]; 
        }
    
    @Override public long[] getBinary()
        {
        return device.getBinary(); 
        }

    @Override public boolean setBinary( long[] x )
        {  // this call for load dump, no physical platform, physical = false
        return setBinaryHelper( x, false );
        }

    @Override public boolean redetectPlatform()
        {
        opb = new long[ OPB_SIZE ];    // no clear, use fact: all elements = 0
        Registry r = CpuidRefactoring.getRegistry();
        int status = r.binaryGate( null, opb, FUNCTION_CPUID, OPB_SIZE );
        if ( status > 0 )
            {  // this call for re-detect physical platform, physical = true
            return setBinaryHelper( opb, true );
            }
        else
            {
            return false;
            }
        }
    
    // Helper method for redetect, include early detect vendor by database
    private boolean setBinaryHelper( long [] x, boolean physical )
        {
        boolean b1, b2 = false;
        device.setBinary( x );
        b1 = device.initBinary();
        if ( b1 )
            {
            earlyVendorDetect( physical );
            b2 = device.parseBinary(); 
            }
        if ( b1 & b2 == false ) return false;
        viewSets = buildStrings();
        return true;
        }
    
    private final static int SUMMARY_SCREEN_ID = 0;
    private final static int DUMP_SCREEN_ID = 1;
    private final static int TREE_SCREEN_ID = 2;
    private final static int F_SCREEN_ID = 3;
    
    private ViewSet[] buildStrings()
        {
        // initializing text data and detected functions
        String[][] listsUp = device.getScreensListsUp();
        String[][][] lists = device.getScreensLists();
        String[] shortNames = device.getScreensShortNames();
        // get lengths and verify it
        int n = listsUp.length;
        if ( ( lists.length != n ) || ( lists.length != n ) ||
             ( lists.length != n ) ) // || ( m != n - 3 ) )
            return null;
        // array of view sets of GUI objects
        ViewSet[] vs = new ViewSet[n];
        // Build CPUID summary single table panel
        String shortName = shortNames[ SUMMARY_SCREEN_ID ];
        String[] upText = listsUp[ SUMMARY_SCREEN_ID ];
        String[][] centerText = lists[ SUMMARY_SCREEN_ID ];
        ChangeableTableModel model1 = 
            new ChangeableTableModel( upText, centerText );
        ChangeableTableModel model2;
        vs[ SUMMARY_SCREEN_ID ] = new ViewSetSingleTable( shortName, model1 );
        // Build CPUID dump single table panel
        shortName = shortNames[ DUMP_SCREEN_ID ];
        upText = listsUp[ DUMP_SCREEN_ID ];
        centerText = lists[ DUMP_SCREEN_ID ];
        model1 = new ChangeableTableModel( upText, centerText );
        vs[ DUMP_SCREEN_ID ] = new ViewSetSingleTable( shortName, model1 );
        
        // Build CPUID tree panel
        ArrayList<DefaultMutableTreeNode> al1 = new ArrayList();
        ListEntry le1 = new ListEntry( "CPUID", "", "", true, false );
        DefaultMutableTreeNode dmtn1 = new DefaultMutableTreeNode( le1, true );
        al1.add( dmtn1 );
        
        ListEntry le2 =  // Child node 1 = Standard CPUID
            new ListEntry( "Standard functions", "", "", true, false );
        DefaultMutableTreeNode dmtn2 = new DefaultMutableTreeNode( le2, true );
        dmtn1.add( dmtn2 );
        
        ListEntry le3 =  // Child node 2 = Extended CPUID
            new ListEntry( "Extended functions", "", "", true, false );
        DefaultMutableTreeNode dmtn3 = new DefaultMutableTreeNode( le3, true );
        dmtn1.add( dmtn3 );
        
        ListEntry le4 =  // Child node 3 = Vendor CPUID
            new ListEntry( "Vendor functions", "", "", true, false );
        DefaultMutableTreeNode dmtn4 = new DefaultMutableTreeNode( le4, true );
        dmtn1.add( dmtn4 );

        ListEntry le5 =  // Child node 4 = Virtual CPUID
            new ListEntry( "Virtual functions", "", "", true, false );
        DefaultMutableTreeNode dmtn5 = new DefaultMutableTreeNode( le5, true );
        dmtn1.add( dmtn5 );
        
        // Build CPUID functions double tables panels and tree branches
        String[][] dumpsUp = device.getScreensDumpsUp();
        String[][][] dumps = device.getScreensDumps();
        String[] longNames = device.getScreensLongNames();
        String[] upDump;
        String[][] centerDump;
        String longName;
        for ( int i = F_SCREEN_ID; i<n; i++ )
            {
            // support tables panels
            shortName  = shortNames[i];
            longName   = longNames [i];
            upText     = listsUp   [i];
            centerText = lists     [i];
            upDump     = dumpsUp   [i];
            centerDump = dumps     [i];
            model1 = new ChangeableTableModel( upText, centerText );
            model2 = new ChangeableTableModel( upDump, centerDump );
            // support functions screens
            vs[i] = new ViewSetDualTable
                ( shortName, model1, model2 );
            // support tree branches
            ListEntryTable let = new ListEntryTable
                ( shortName , longName , "" , true , true , model1 , model2 );
                DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode
                    ( let , false );
                
                char c1 = shortName.charAt(0);
                String s1 = "";
                if ( shortName.length() > 4 )
                    s1 = shortName.substring( 0, 4 ); 
/*

Select function group (tree branch) for current analused function

00000000h - 7FFFFFFFh  = Range for Standard CPUID functions
80000000h - FFFFFFFFh  = Range for Extended CPUID functions
8FFFFFFFh              = Special signature for AMD
C0000000h - CFFFFFFFh  = Range for VIA/IDT Vendor CPUID functions
80860000h - 8086FFFFh  = Range for Transmeta Vendor CPUID functions
20000000h - 2FFFFFFFh  = Range for Intel Xeon Phi Vendor CPUID functions
40000000h - 4FFFFFFFh  = Range for Virtual CPUID functions

Note about checks sequence is important, because 8086xxxx can match 8xxxxxxx.
Note about ranges reduced by this check algorithm:

00000000h - 0FFFFFFFh  = Range for Standard CPUID functions
80000000h - 8FFFFFFFh  = Range for Extended CPUID functions
                
*/
                if ( ( c1 == '2' ) || ( c1 == 'C' ) || ( s1.equals( "8086" )) ) 
                                       dmtn4.add( dmtn );
                else if ( c1 == '0' )  dmtn2.add( dmtn );
                else if ( c1 == '8' )  dmtn3.add( dmtn );
                else if ( c1 == '4' )  dmtn5.add( dmtn );
            }
        
        // store CPUID tree and done
        DefaultTreeModel treeModel = new DefaultTreeModel( al1.get(0) , true );
        vs[ TREE_SCREEN_ID ] = new ViewSetTree( "CPUID Tree", treeModel );
        return vs;
        }
    }

/*
Initializing Application View.
*/

private class BuildView extends ApplicationView
    {
    private final String[] BUTTONS_NAMES = 
        { "Redetect", "Load binary", "Save binary", 
          "Report full", "Report this",
          "About", "Cancel" };
    private final String[] BUTTONS_TIPS =
        { "Repeat CPUID restores current platform data after load binary",
          "Load binary file with CPUID data instead current platform results",
          "Save binary file with CPUID data, can be loaded later",
          "CPUID instruction results full report",
          "CPUID instruction this leaf only report",
          null, null };
    private final int[] BUTTONS_KEYS = { 'D', 'L', 'S', 'F', 'R', 'A', 'C' }; 
    private BuildView( BuildModel z , int x, int y )
        {
        model = z;
        xsize = x;
        ysize = y;
        // Build GUI
        t = new JTabbedPane();
        buildGUI();
        // Built buttons, add listeners, common for all tabs
        actionsListeners = new ActionListener[] 
            { new BRedetect(), new BLoadBinary(), new BSaveBinary(),
              new BReportFull(), new BReportThis(), new BAbout(),
              new BCancel() };
        sl1 = new SpringLayout();
        p = new JPanel(sl1);
        p.add( t );
        HelperLayout.springCenter ( sl1, p, t, T_UP, T_DOWN, T_LEFT, T_RIGHT );
        buttons = new JButton[BUTTONS_NAMES.length];
        // BUTTONS_TIPS include overloaded methods downButtons
        HelperButton.downButtons
            ( p, buttons, BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS,
              BUTTONS_NAMES.length, 
              actionsListeners, sl1, 
              B_DOWN, B_RIGHT, B_INTERVAL, DB );
        }
    
    // Built JTabbedPane, make callable because redetect

    @Override protected final void buildGUI()
        {
        int n = model.getCount();
        viewSets = new ViewSet[n];
        viewPanels = new ViewPanel[n];
        int j=0;
        for( int i=0; i<n; i++ )
            {
            ViewSet x = (ViewSet) model.getSelectedModel( i );
            if ( x instanceof ViewSetTree )                   // tree panel
                {
                Object[] y = ( (ViewSet)(x) ).getGuiObjects();
                String z1 = (String) y[0];
                DefaultTreeModel z2 = (DefaultTreeModel) y[1];
                ViewPanelTree v = new ViewPanelTree( xsize, ysize, z2 );
                t.add( v.getP(), z1 );
                viewSets[j] = x;
                viewPanels[j++] = v;
                }

            if ( x instanceof ViewSetSingleTable )  // summary, dump panels
                {
                Object[] y = ( (ViewSet)(x) ).getGuiObjects();
                String z1 = (String) y[0];
                ChangeableTableModel z2 = (ChangeableTableModel) y[1];
                ViewPanelSingleTable v =
                    new ViewPanelSingleTable( xsize, ysize, z2, true );
                t.add( v.getP(), z1 );
                viewSets[j] = x;
                viewPanels[j++] = v;
                }

            if ( x instanceof ViewSetDualTable )  // cpuid functions panels
                {
                Object[] y = ( (ViewSet)(x) ).getGuiObjects();
                String z1 = (String) y[0];
                ChangeableTableModel z2 = (ChangeableTableModel) y[1];
                ChangeableTableModel z3 = (ChangeableTableModel) y[2];
                ViewPanelDualTable v = 
                    new ViewPanelDualTable( xsize, ysize, z2, z3 );
                t.add( v.getP(), z1 );
                viewSets[j] = x;
                viewPanels[j++] = v;
                }
            }
        }
    }
}

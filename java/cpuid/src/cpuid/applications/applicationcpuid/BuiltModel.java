//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// MVC "Model" module for ApplicationCpuid,
// Note MVC is Model, View, Controller paradigm.
// Application: CPUID info.
// Model provide data model, use PAL and local builder.

package cpuid.applications.applicationcpuid;

import cpuid.CpuId;
import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ListEntry;
import cpuid.applications.guimodels.ListEntryTables;
import cpuid.applications.guimodels.ModelBuilder;
import cpuid.applications.guimodels.ViewableModel;
import cpuid.applications.guimodels.VM2;
import cpuid.applications.guimodels.VM3;
import cpuid.applications.guimodels.VM4;
import cpuid.applications.mvc.BMA;
import cpuid.drivers.cpr.Device;
import cpuid.kernel.Registry;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class BuiltModel extends BMA   //  implements BM
{
public BuiltModel()
    {
    // Initialize CPUID model builder
    Registry r = CpuId.getRegistry();
    pal = r.getPAL();
    cmb = new CpuidModelBuilder();
    }

// Model builder for CPUID data source declaration

protected class CpuidModelBuilder implements ModelBuilder 
    {
    private final Device device;
    private final long[] ipb, opb;
    private final static int IPBQWORDS = 1, OPBQWORDS = 2048;
    private final static int GETCPUID = 0; // 3;
    private final int jniStatus;
    private boolean statusInit, statusParse;
    private ViewableModel[] vms;
    private int n, m;

    public CpuidModelBuilder()
        {
        // Initializing hardware support objects
        ipb = new long[IPBQWORDS]; 
        for ( int i=0; i<IPBQWORDS; i++ ) { ipb[i]=0; }
        opb = new long[OPBQWORDS];
        for ( int i=0; i<OPBQWORDS; i++ ) { opb[i]=0; }
        Registry r = CpuId.getRegistry();
        // Receive CPUID binary data, kernel call
        // call function 0 = Get CPUID dump,
        // IPB = null, this means third parameter = function code = 0
        jniStatus = r.binaryGate( null, opb, GETCPUID, OPBQWORDS );
        // Initialize CPR
        statusInit = false;
        statusParse = false;
        // Load driver
        device = r.loadDriver(Registry.CPR.driverCPUID);
        // Send binary data from hardware to CPR module
        device.setBinary(opb);
        // Analysing data
        if ( jniStatus > 0 ) { statusInit = device.initBinary();   }
        if ( statusInit )    { statusParse = device.parseBinary(); }
        // Built text data
        if ( statusInit & statusParse ) 
            {
            builtStrings();
            }
        }

    @Override public int getCount()
        { return n; }

    @Override public ViewableModel getValue(int i)
        { return vms[i]; }

    @Override public long[] getBinary()
        { return device.getBinary(); }

    @Override public boolean setBinary( long[] x )
        {
        boolean b1 = false, b2 = false;
        device.setBinary(x);
        b1 = device.initBinary();
        if (b1) { b2 = device.parseBinary(); }
        if ( b1&b2==false ) return false;
        builtStrings();
        return true;
        }

// Helper method for redetect

    private void builtStrings()
        {
        m = device.getCommandsCount();
        n = m + 3;
        vms = new ViewableModel[n];
        // summary single table panel
        String s1 = device.getSummaryName();
        String s2 = "";
        String[] sa1 = device.getSummaryUp();
        String[][] sa2 = device.getSummaryText();
        ChangeableTableModel m1 = new ChangeableTableModel( sa1, sa2 );
        ChangeableTableModel m2 = null;
        vms[0] = new VM3( s1, m1 );
        // dump single table panel
        s1 = device.getDumpName();
        sa1 = device.getDumpUp();
        sa2 = device.getDumpText();
        m1 = new ChangeableTableModel( sa1, sa2 );
        vms[1] = new VM3( s1, m1 );
        // cpuid tree panel
        // Root node = CPUID
        ListEntry le1 = 
            new ListEntry( "CPUID", "", "", true, false );
        DefaultMutableTreeNode dmtn1 = 
            new DefaultMutableTreeNode( le1, true );
        ArrayList<DefaultMutableTreeNode> al1 = new ArrayList();
        al1.add(dmtn1);
        // Child node 1 = Standard CPUID
        ListEntry le2 = 
            new ListEntry( "Standard functions", "", "", true, false );
        DefaultMutableTreeNode dmtn2 = 
            new DefaultMutableTreeNode( le2, true );
        dmtn1.add(dmtn2);
        // Child node 2 = Extended CPUID
        ListEntry le3 = 
            new ListEntry( "Extended functions", "", "", true, false );
        DefaultMutableTreeNode dmtn3 = 
            new DefaultMutableTreeNode( le3, true );
        dmtn1.add(dmtn3);
        // Child node 3 = Virtual CPUID
        ListEntry le4 = 
            new ListEntry( "Virtual functions", "", "", true, false );
        DefaultMutableTreeNode dmtn4 = 
            new DefaultMutableTreeNode( le4, true );
        dmtn1.add(dmtn4);
        // cpuid functions double tables panels and tree branches
        for (int i=0; i<m; i++)
            {
            if ( device.getCommandSupported(i) )
                {
                // support tables panels
                s1 = device.getCommandShortName(i);
                s2 = device.getCommandLongName(i);
                sa1 = device.getCommandUp1(i);
                sa2 = device.getCommandText1(i);
                m1 = new ChangeableTableModel( sa1, sa2 );
                sa1 = device.getCommandUp2(i);
                sa2 = device.getCommandText2(i);
                m2 = new ChangeableTableModel( sa1, sa2 );
                vms[i+3] = new VM4( s1, m1, m2 );
                // support tree branches
                ListEntryTables let = new ListEntryTables 
                    ( s1 , s2 , "" , true , true , m1 , m2 );
                DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode
                    ( let , false );
                char c1 = s1.charAt(0);
                if (c1=='0') { dmtn2.add(dmtn);  }
                if (c1=='8') { dmtn3.add(dmtn);  }
                if (c1=='4') { dmtn4.add(dmtn);  }
                }
            }
        DefaultTreeModel rm1 = new DefaultTreeModel( al1.get(0) , true );
        vms[2] = new VM2( "CPUID Tree", rm1 );
        }
    }

}

/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
System Tree Builder for Left Panel.
Create tree of sub-applications:
cpuid, cpuclk, cpucontext, jvminfo, jvmenvironment.
*/

package cpuid.applications.rootmenus;

import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ListEntryApplications;
import cpuid.applications.guimodels.ModelBuilder;
import cpuid.applications.guimodels.VM3;
import cpuid.applications.guimodels.VM4;
import cpuid.applications.mvc.BCA;
import cpuid.applications.mvc.BMA;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SystemTreeBuilder 
{
private static final String[] APPS =
    {
    "cpuid.applications.applicationcpuid.BuiltController" ,
    "cpuid.applications.applicationcpuclk.BuiltController" ,
    "cpuid.applications.applicationcpucontext.BuiltController" ,
    "cpuid.applications.applicationjvminfo.BuiltController" ,
    "cpuid.applications.applicationjvmenvr.BuiltController"
    };
private static final int APPS_COUNT = APPS.length;

// define entry per sub-application format: class with class constructor
class X
    {
    protected String name;
    protected boolean leaf;
    protected int id;
    protected int parent;
    protected ListEntryApplications value;
    protected DefaultMutableTreeNode node;
    
    X( String s, boolean b, int n1, int n2 )
        {
        name = s;
        leaf = b;
        id = n1;
        parent = n2;
        value = null;
        node = null;
        }
    }

// data for create tree: nodes and leafs = sub-applications
private final X[] treeData =
    {
    new X ( "Platform"    , false , -1 , -1 ) ,
    new X ( "CPU"         , false , -1 ,  0 ) ,
    new X ( "JVM"         , false , -1 ,  0 ) ,
    new X ( "CPUID"       , true  ,  0 ,  1 ) ,
    new X ( "CPUCLK"      , true  ,  1 ,  1 ) ,
    new X ( "Context"     , true  ,  2 ,  1 ) ,
    new X ( "JVM info"    , true  ,  3 ,  2 ) ,
    new X ( "Environment" , true  ,  4 ,  2 )
    };
private final int treeCount = treeData.length;

private JPanel[] pApps;                    // view panels for applications pool
private final ChangeableTableModel[] rApps;   // reports for applications pool
private final DefaultTreeModel mRoot;         // tree model for left panel
    
protected SystemTreeBuilder()
    {
    pApps = new JPanel[APPS_COUNT];
    LinkedHashSet<ChangeableTableModel> reports = new LinkedHashSet();
    
    // cycle for create sub-applications by Model, View, Controller set
    for ( int i=0; i<APPS_COUNT; i++ )
        {
        String s = APPS[i];
        Class c = null;
        Object o = null;
        try
            {
            c = Class.forName(s); 
            if (c!=null)
                {
                o = c.newInstance();
                if ( o instanceof BCA )
                    {
                    // Store applications panels
                    pApps[i] = ((BCA)o).getView().getP();
                    // Store applications models
                    Object ob1 = ((BCA)o).getModel();
                    if ( ob1 instanceof BMA )
                        {
                        Object ob2 = ((BMA)ob1).getModel();
                        if ( ob2 instanceof ModelBuilder )
                            {
                            int n = ((ModelBuilder)ob2).getCount();
                            for ( int j=0; j<n; j++ )
                                {
                                Object ob3 = ((ModelBuilder)ob2).getValue(j);
                                Object ob10 = null;
                                Object ob11 = null;
                                if ( ob3 instanceof VM3 )
                                    {
                                    Object[] ob4 = ((VM3)ob3).getValue();
                                    if ( ( ob4 != null ) && ( ob4.length >= 2) )
                                        { ob10 = ob4[1]; }
                                    }
                                if ( ob3 instanceof VM4 )
                                    {
                                    Object[] ob4 = ((VM4)ob3).getValue();
                                    if ( ( ob4 != null ) && ( ob4.length >= 3) )
                                        { ob10 = ob4[1]; ob11 = ob4[2]; }
                                    }
                                if ( ob10 instanceof ChangeableTableModel )
                                    { reports.add((ChangeableTableModel)ob10); }
                                if ( ob11 instanceof ChangeableTableModel )
                                    { reports.add((ChangeableTableModel)ob11); }
                                }
                            }
                        }
                    // End of store
                    }
                }
            }
        catch (Exception e)
            { pApps[i] = null; }
        }

    // cycle for create array of report data
    Iterator it = reports.iterator();
    int ns = reports.size();
    rApps = new ChangeableTableModel[ns];
    for ( int i=0; i<ns; i++ )
        { rApps[i] = (ChangeableTableModel) it.next(); }
    
    // cycle for create sub-applications entries, tree node,
    // connects parents and childs (nodes and leafs) at tree
    for( int i=0; i<treeCount; i++ )
        {
        treeData[i].value = new ListEntryApplications
            ( treeData[i].name , treeData[i].leaf , treeData[i].id );
        treeData[i].node = new DefaultMutableTreeNode
            ( treeData[i].value , !treeData[i].leaf );
        int j = treeData[i].parent;
        if ( j >= 0 )
            { ( treeData[j].node ).add( treeData[i].node );  }
        }

    // assign tree model
    mRoot = new DefaultTreeModel( treeData[0].node , true );
    }

// Get array of panels
protected JPanel[] getApps()
    { return pApps; }

// Get array of table models
protected ChangeableTableModel[] getReports()
    { return rApps; }

// Get tree
protected DefaultTreeModel getTree()
    { return mRoot; }
    
}

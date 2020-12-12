/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Build set of sub-applications by constant list.
Sub-applications provided as sets of panels, table models and tree models.
*/

package cpuidrefactoring.rootmenu;

import cpuidrefactoring.applications.ApplicationCpuid;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

class SystemTreeBuilder 
{
private static final String[] APPS =
    { "cpuidrefactoring.applications.ApplicationCpuid",
      "cpuidrefactoring.applications.ApplicationCpuClk",
      "cpuidrefactoring.applications.ApplicationCpuContext",
      "cpuidrefactoring.applications.ApplicationJvmInfo",
      "cpuidrefactoring.applications.ApplicationOsInfo" };

// define entry per sub-application format: class with class constructor
class X
    {
    final String name;
    final boolean leaf;
    final int id;
    final int parent;
    ListEntryApplication value;
    DefaultMutableTreeNode node;
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
    new X ( "Platform" , false , -1 , -1 ) ,
    new X ( "CPU"      , false , -1 ,  0 ) ,
    new X ( "JVM/OS"   , false , -1 ,  0 ) ,
    new X ( "CPUID"    , true  ,  0 ,  1 ) ,
    new X ( "CPUCLK"   , true  ,  1 ,  1 ) ,
    new X ( "Context"  , true  ,  2 ,  1 ) ,
    new X ( "JVM info" , true  ,  3 ,  2 ) ,
    new X ( "OS info"  , true  ,  4 ,  2 )
    };
private final int treeCount = treeData.length;

private JPanel[] applicationPanels;
private final ChangeableTableModel[] applicationReports;
private final DefaultTreeModel applicationTrees;

SystemTreeBuilder()
    {
    applicationPanels = new JPanel[ APPS.length ];
    LinkedHashSet<ChangeableTableModel> reports = new LinkedHashSet();
    
    // cycle for create sub-applications by Model, View, Controller set
    for ( int i=0; i<APPS.length; i++ )
        {
        String s = APPS[i];
        Class c;
        Object o;
        try
            {
            c = Class.forName( s ); 
            if ( c != null )
                {
                o = c.newInstance();
                if ( o instanceof ApplicationController )
                    {
                    // Store CPUID application for special usage at root menu
                    if ( o instanceof ApplicationCpuid )
                        {
                        appCpuid = (ApplicationCpuid) o;
                        }
                    // Store applications panels
                    ApplicationView av = ( (ApplicationController)o ).getView();
                    if ( av != null )
                        {
                        applicationPanels[i] = av.getPanel();
                        }
                    // Store applications models
                    Object am = ( (ApplicationController)o ).getModel();
                    if ( am instanceof ApplicationModel )
                        {  // start of store
                        int n = ( (ApplicationModel)am ).getCount();
                        for ( int j=0; j<n; j++ )
                            {
                            Object viewable = 
                                ( (ApplicationModel)am ).getSelectedModel(j);
                            Object tab1 = null;
                            Object tab2 = null;
                            if ( viewable instanceof ViewSetSingleTable )
                                {
                                Object[] vts =( (ViewSetSingleTable)viewable ).
                                    getGuiObjects();
                                if ( ( vts != null )&&( vts.length >= 2) )
                                   { tab1 = vts[1]; }
                                }
                            if ( viewable instanceof ViewSetDualTable )
                                {
                                Object[] vtd = ( (ViewSetDualTable)viewable ).
                                    getGuiObjects();
                                if ( ( vtd != null )&&( vtd.length >= 3) )
                                    {
                                    tab1 = vtd[1]; 
                                    tab2 = vtd[2]; 
                                    }
                                }
                            if ( tab1 instanceof ChangeableTableModel )
                                {
                                reports.add((ChangeableTableModel)tab1); 
                                }
                            if ( tab2 instanceof ChangeableTableModel )
                                { 
                                reports.add((ChangeableTableModel)tab2); 
                                }
                            }
                        }
                    // end of store
                    }
                }
            }
        catch ( ClassNotFoundException | IllegalAccessException |
                InstantiationException e )
            { applicationPanels[i] = null; }
        }

    // cycle for create array of report data
    Iterator it = reports.iterator();
    int ns = reports.size();
    applicationReports = new ChangeableTableModel[ns];
    for ( int i=0; i<ns; i++ )
        { applicationReports[i] = (ChangeableTableModel) it.next(); }
    
    // cycle for create sub-applications entries, tree node,
    // connects parents and childs (nodes and leafs) at tree
    for( int i=0; i<treeCount; i++ )
        {
        treeData[i].value = new ListEntryApplication
            ( treeData[i].name , treeData[i].leaf , treeData[i].id );
        treeData[i].node = new DefaultMutableTreeNode
            ( treeData[i].value , !treeData[i].leaf );
        int j = treeData[i].parent;
        if ( j >= 0 )
            { ( treeData[j].node ).add( treeData[i].node );  }
        }

    // assign tree model
    applicationTrees = new DefaultTreeModel( treeData[0].node , true );
    }    

// Get array of panels
JPanel[] getApplicationPanels() { return applicationPanels; }

// Get array of table models
ChangeableTableModel[] getApplicationReports()  { return applicationReports; }

// Get tree
DefaultTreeModel getApplicationTrees() { return applicationTrees; }

// Special method for get ApplicationCpuid, used for application-specific
// operations from root menu, 
// for example load InstLatx64 compatible text report.
private ApplicationCpuid appCpuid = null;
ApplicationCpuid getAppCpuid() { return appCpuid; }

}

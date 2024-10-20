/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
CPUID instruction results visualization application, 
provides content for "Summary", "Dump", "Details" leafs at tabbed pane.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import cpuidv2.CPUIDv2;
import cpuidv2.gui.HelperTable;
import cpuidv2.services.Device;
import cpuidv2.services.ServiceCpuid;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

class InfoCpuid extends Application 
{
private final JPanel summaryPanel;
private final JPanel dumpPanel;
private final JPanel detailsPanel;

private final static String SUMMARY_TIP = 
    "Processor and platform summary data by CPUID instruction.";
private final static String DUMP_TIP = 
    "CPUID instruction results hex dump.";
private final static String DETAILS_TIP = 
    "CPUID functions detail bitmaps.";

private ChangeableTableModel summaryModel;
private ChangeableTableModel dumpModel;
private ChangeableTableModel[] fncListModels;
private ChangeableTableModel[] fncDumpModels;

private JTable summaryTable;
private JTable dumpTable;

private JTree fncTree;
private JTable[] fncListTables;
private JTable[] fncDumpTables;

private JSplitPane dualPanelH;
private JSplitPane dualPanelV;

InfoCpuid()
    {
    summaryPanel = new JPanel();
    dumpPanel = new JPanel();
    detailsPanel = new JPanel();
    }
    
@Override String[] getPanelNames()
    { return new String[] { "Summary", "Dump", "Details" }; }
@Override String[] getPanelIcons()
    { 
    return new String[] 
        { "tab_summary.png", "tab_dump.png", "tab_details.png" }; 
    }
@Override JPanel[] getPanels()
    {
    return new JPanel[] { summaryPanel, dumpPanel, detailsPanel };
    }
@Override boolean[] getPanelActives() 
    { 
    return new boolean[] { true, true, true }; 
    }
@Override String[] getPanelTips()
    {
    return new String[] { SUMMARY_TIP, DUMP_TIP, DETAILS_TIP };
    }

// Get system information and build GUI panel for three CPUID data tabs : 
// "Summary", "Dump", "Details".
@Override void rebuildPanels()
    {
    ServiceCpuid service = CPUIDv2.getServiceCpuid();
    if( service.loadBinary() )
        {
        refreshPanels();
        }
    }

private final static int F_SCREEN_ID = 3;

@Override void refreshPanels()
    {
    previousIndex = -1;
    ServiceCpuid service = CPUIDv2.getServiceCpuid();
    Device device = service.getCpuidDevice();
    String[][] ups = device.getScreensListsUp();
    String[][][] lists = device.getScreensLists();
    String[] shortNames = device.getScreensShortNames();
    String[] longNames = device.getScreensLongNames();

    if( ( ups != null )&&( lists != null )&&
            ( lists.length == ups.length )&&( ups.length > 0 )&&
            ( shortNames != null )&&( shortNames.length == ups.length )&&
            ( longNames != null )&&( longNames.length == ups.length ) )
        {
        summaryModel = new ChangeableTableModel( ups[0], lists[0] );
        summaryTable = new JTable( summaryModel );
        HelperTable.optimizeSingleTable
            ( summaryPanel, summaryTable, 0, true );

        if( ups.length > 1 )
            {
            dumpModel = new ChangeableTableModel( ups[1], lists[1] );
            dumpTable = new JTable( dumpModel );
            HelperTable.optimizeSingleTable
                ( dumpPanel, dumpTable, 0, true );
            }
            
        String[][] dumpUps = device.getScreensDumpsUp();
        String[][][] dumpLists = device.getScreensDumps();

        if( ( dumpUps != null )&&( dumpLists != null )&&
            ( dumpUps.length == ups.length )&&
            ( dumpLists.length == dumpUps.length )&&
            ( dumpUps.length > 3 )&&( ups.length > 3) )
            {
            int count = ups.length - 3;
            fncListModels = new ChangeableTableModel[count];
            fncDumpModels = new ChangeableTableModel[count];
            fncListTables = new JTable[count];
            fncDumpTables = new JTable[count];

            for(int i=0; i<count; i++) 
                {
                fncListModels[i] = new ChangeableTableModel
                    ( ups[i + 3], lists[i + 3] );
                fncListTables[i] = new JTable( fncListModels[i] );
                    
                fncDumpModels[i] = new ChangeableTableModel
                    ( dumpUps[i + 3], dumpLists[i + 3] );
                fncDumpTables[i] = new JTable( fncDumpModels[i] );
                }
            
            // Build CPUID tree panel.
            ArrayList<DefaultMutableTreeNode> al1 = new ArrayList();
            ListEntry entryRootCpuid =
                new ListEntry( "CPUID", "", "", true, false );
            DefaultMutableTreeNode dmtnRootCpuid = 
                new DefaultMutableTreeNode( entryRootCpuid, true );
            al1.add( dmtnRootCpuid );
            
            ListEntry entryStandard =  // Child node 1 = Standard CPUID
                new ListEntry( "Standard functions", "", "", true, false );
            DefaultMutableTreeNode dmtnStandard = 
                new DefaultMutableTreeNode( entryStandard, true );
            dmtnRootCpuid.add( dmtnStandard );
        
            ListEntry entryExtended =  // Child node 2 = Extended CPUID
                new ListEntry( "Extended functions", "", "", true, false );
            DefaultMutableTreeNode dmtnExtended =
                new DefaultMutableTreeNode( entryExtended, true );
            dmtnRootCpuid.add( dmtnExtended );
        
            ListEntry entryVendor =  // Child node 3 = Vendor CPUID
                new ListEntry( "Vendor functions", "", "", true, false );
            DefaultMutableTreeNode dmtnVendor = 
                new DefaultMutableTreeNode( entryVendor, true );
            dmtnRootCpuid.add( dmtnVendor );

            ListEntry entryVirtual =  // Child node 4 = Virtual CPUID
                new ListEntry( "Virtual functions", "", "", true, false );
            DefaultMutableTreeNode dmtnVirtual = 
                new DefaultMutableTreeNode( entryVirtual, true );
            dmtnRootCpuid.add( dmtnVirtual );
            
            for ( int i = F_SCREEN_ID; i<ups.length; i++ )
                {
                ListEntry entry = new ListEntry
                    ( shortNames[i], longNames[i], "", true, true,
                      i - F_SCREEN_ID );
                DefaultMutableTreeNode node = new DefaultMutableTreeNode
                    ( entry , false );
                char c1 = shortNames[i].charAt(0);
                String s1 = "";
                if ( shortNames[i].length() > 4 )
                    {
                    s1 = shortNames[i].substring( 0, 4 );
                    }
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
                                       dmtnVendor.add( node );
                else if ( c1 == '0' )  dmtnStandard.add( node );
                else if ( c1 == '8' )  dmtnExtended.add( node );
                else if ( c1 == '4' )  dmtnVirtual.add( node );
                }
            
            // Create CPUID tree model, create tree, add selection listener.
            DefaultTreeModel treeModel =
                new DefaultTreeModel( al1.get(0) , true );
            fncTree = new JTree( treeModel );
            // Add selection listener
            fncTree.addTreeSelectionListener( new SelectionListener() );
            
            // Create left component of dual panel: CPUID functions tree.
            JScrollPane leftPanel = new JScrollPane( fncTree );

            // Create right component of dual panel: funct. + dump tables pair.
            JPanel rightPanel = helperNodeSelection( 0 );

            // Create dual panel : tree and (function + dump) tables.
            dualPanelH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
            dualPanelH.setOneTouchExpandable( true );
            dualPanelH.setDividerSize( 9 );
            dualPanelH.setLeftComponent( leftPanel );     // Tree.
            dualPanelH.setRightComponent( rightPanel );   // Function and dump.
            // Layout for dual panel : tree and (function + dump) tables.
            BoxLayout hLayout = new BoxLayout( detailsPanel, BoxLayout.X_AXIS );
            detailsPanel.setLayout( hLayout );
            detailsPanel.add( dualPanelH );
            }
        }
    }

// Selection listener for CPUID tree.

private int previousIndex = -1;
private class SelectionListener implements TreeSelectionListener
    {
    @Override public void valueChanged( TreeSelectionEvent e )
        {
        Object tree = (JTree)e.getSource();
        if ( !( tree instanceof JTree ) ) return;
        Object path = ( (JTree)tree ).getSelectionPath();
        if ( !( path instanceof TreePath ) ) return;
        Object node = ( (TreePath)path ).getLastPathComponent();
        if ( !(node instanceof DefaultMutableTreeNode ) ) return;
        Object entry = ( (DefaultMutableTreeNode)(node) ).getUserObject();
        if ( !( entry instanceof ListEntry ) ) return;
        
        int index = ((ListEntry)entry).getIndex();
        if(( index >= 0 )&&( index != previousIndex ))
            {
            previousIndex = index;
            int dividerH = dualPanelH.getDividerLocation();
            int dividerV = dualPanelV.getDividerLocation();
            JPanel rightPanel = helperNodeSelection( index );
            dualPanelH.setRightComponent( rightPanel );
            dualPanelH.setDividerLocation(dividerH);
            dualPanelV.setDividerLocation(dividerV);
            dualPanelH.revalidate();
            dualPanelH.repaint();
            dualPanelV.revalidate();
            dualPanelV.repaint();
            }
        }
    }

// Helper for create right component of dual panel: funct. + dump tables pair.

private JPanel helperNodeSelection( int index )
    {
    JTable fncListTable = fncListTables[index];
    JTable fncDumpTable = fncDumpTables[index];
    // Create right component of dual panel: funct. + dump tables pair.
    JPanel rightPanel = new JPanel();
    HelperTable.optimizeColumnsWidths( fncListTable, 500 );
    HelperTable.optimizeColumnsWidths( fncDumpTable, 500 );
    JScrollPane upTable = new JScrollPane( fncListTable );
    JScrollPane downTable = new JScrollPane( fncDumpTable );
    // Centering tables.
    DefaultTableCellRenderer mRenderer1 = new DefaultTableCellRenderer();
    DefaultTableCellRenderer mRenderer2 = new DefaultTableCellRenderer();
    mRenderer1.setHorizontalAlignment( SwingConstants.CENTER );
    mRenderer2.setHorizontalAlignment( SwingConstants.CENTER );
    for (int i=0; i<fncListTable.getColumnCount(); i++)
        {
        fncListTable.getColumnModel().getColumn(i).
            setCellRenderer( mRenderer1 );
        }
    for (int i=0; i<fncDumpTable.getColumnCount(); i++)
        {
        fncDumpTable.getColumnModel().getColumn(i).
            setCellRenderer( mRenderer2 ); 
        }
    // Built split panel with function table and dump table vertically.
    dualPanelV = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true );
    dualPanelV.setOneTouchExpandable( true );
    dualPanelV.setDividerSize( 11 );
    dualPanelV.setDividerLocation( 350 );
    dualPanelV.setTopComponent( upTable );
    dualPanelV.setBottomComponent( downTable );
    BoxLayout vLayout = new BoxLayout( rightPanel, BoxLayout.X_AXIS );
    rightPanel.setLayout( vLayout );
    rightPanel.add( dualPanelV );
    return rightPanel;
    }

@Override AbstractTableModel[] getPanelModels() 
    {
    int count = 0;
    if ( summaryModel  != null ) count ++;
    if ( dumpModel     != null ) count ++;
    if ( fncListModels != null ) count += fncListModels.length;
    if ( fncDumpModels != null ) count += fncDumpModels.length;
    AbstractTableModel[] models = new AbstractTableModel[count];
    
    models[0] = summaryModel;
    models[1] = dumpModel;
    int j = 0;
    for( int i = 2; i < count; i += 2 )
        {
        models[i] = fncListModels[j];
        models[i + 1] = fncDumpModels[j];
        j++;
        }
    
    return models;
    }

@Override AbstractTableModel[] getReportThisModels( int subIndex )
    { 
    AbstractTableModel[] models = null;
    int treeIndex = previousIndex;
    if( ( treeIndex < 0 )||
        ( treeIndex >= fncListModels.length )||
        ( treeIndex >= fncDumpModels.length ) )
        {
        treeIndex = 0;
        }
    
    switch( subIndex )
        {
        case 0:
            models = new AbstractTableModel[] { summaryModel };
            break;
        case 1:
            models = new AbstractTableModel[] { dumpModel };
            break;
        case 2:
            models = new AbstractTableModel[] 
                { fncListModels[treeIndex] , fncDumpModels[treeIndex] };

            break;
        default:
            break;
        }
    return models;    
    }

}


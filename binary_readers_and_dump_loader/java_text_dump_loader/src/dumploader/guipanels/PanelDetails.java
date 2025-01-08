/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

CPUID instruction results detail information panel (as tree),
provides content for "Details" leaf at tabbed pane.

*/

package dumploader.guipanels;

import dumploader.DumpLoader;
import dumploader.cpuenum.ChangeableTableModel;
import dumploader.cpuenum.CpuRootEnumerator;
import dumploader.cpuenum.EntryCpuidSubfunction;
import dumploader.cpuenum.EntryLogicalCpu;
import dumploader.cpuid.HybridReturn;
import dumploader.gui.HelperTable;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class PanelDetails extends ApplicationPanel
{
    private final JPanel detailsPanel;    
    private JTree fncTree;
    private JSplitPane dualPanelH;
    private JSplitPane dualPanelV;
    
    private ChangeableTableModel[] listModels;
    private ChangeableTableModel[] dumpModels;
    private JTable[] listTables;
    private JTable[] dumpTables;

    private final static int PANEL_DIVIDER_HORIZONTAL = 420;
    private final static int PANEL_DIVIDER_VERTICAL = 510;
    
    PanelDetails()
    {
        detailsPanel = new JPanel();
    }

    @Override String getPanelName()     { return "Details";         }
    @Override String getPanelIcon()     { return "tab_details.png"; }
    @Override JPanel getPanel()         { return detailsPanel;      }
    @Override boolean getPanelActive()  { return true;              }
    @Override String getPanelTip()      { return 
            "Logical processors and CPUID functions."; }
    
    // Get system information and build GUI panel for tab : "Details".            
    @Override void rebuildPanel( boolean physical )
    {
        previousIndex = -1;
        int globalIndex = 0;
        
        ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList();
        CpuRootEnumerator rootEnum = DumpLoader.getCpuRootEnumerator();
        EntryLogicalCpu[] cpuEntries;
        
        if( ( rootEnum != null )&&
          ( ( cpuEntries = rootEnum.getProcessorsList() ) != null )&&
            ( cpuEntries.length > 0) )
        {                                    // TODO. Minimize memory usage: get strings and create tables by clicks. ? Performance ?
            rootEnum.getSummaryTable();      // TODO. This duplicated for data prepare, optimize data preparing sequence.
            
            ListEntry entryRoot = new ListEntry
                ( rootEnum.getEnumeratorName(), 
                  "", "", true, false, globalIndex++ );
            DefaultMutableTreeNode dmtnRoot = 
                new DefaultMutableTreeNode( entryRoot, true );
            nodeList.add( dmtnRoot );
            
            int rootCount = cpuEntries.length;
            ArrayList<ChangeableTableModel> aTableModels = new ArrayList<>();
            ArrayList<ChangeableTableModel> aDumpModels = new ArrayList<>();
            ArrayList<JTable> aTables = new ArrayList<>();
            ArrayList<JTable> aDumps = new ArrayList<>();
            
            aTableModels.add( rootEnum.getEnumeratorFirstTable() );
            aDumpModels.add( rootEnum.getEnumeratorSecondTable() ); 
            aTables.add( new JTable( rootEnum.getEnumeratorFirstTable() ) );
            aDumps.add( new JTable( rootEnum.getEnumeratorSecondTable() ) );
            
            for( int i=0; i<rootCount; i++ )
            {
                EntryLogicalCpu ecp = cpuEntries[i];
                EntryCpuidSubfunction[] ecs = ecp.sunfunctionsList;
                int functionCount = rootEnum.setAndParsePerCpuEntriesDump( ecs );
                
                aTableModels.add( rootEnum.getProcessorFirstTable( i ) );
                aDumpModels.add( rootEnum.getProcessorSecondTable( i ) ); 
                aTables.add( new JTable( rootEnum.getProcessorFirstTable( i ) ) );
                aDumps.add( new JTable( rootEnum.getProcessorSecondTable( i ) ) );

                HybridReturn hr = rootEnum.getProcessorHybrid( i );
                
                ListEntry entryCpu = new ListEntry
                    ( rootEnum.getProcessorName( i ), hr.hybridName,
                      "", true, false, globalIndex++, hr.hybridCpu );
                DefaultMutableTreeNode dmtnCpu = 
                    new DefaultMutableTreeNode( entryCpu, true );
                
                ListEntry entryStandard =  // Child node 1 = Standard CPUID
                    new ListEntry( "Standard functions", "", "", false, false );
                DefaultMutableTreeNode dmtnStandard = 
                    new DefaultMutableTreeNode( entryStandard, true );
                dmtnCpu.add( dmtnStandard );
        
                ListEntry entryExtended =  // Child node 2 = Extended CPUID
                    new ListEntry( "Extended functions", "", "", false, false );
                DefaultMutableTreeNode dmtnExtended =
                    new DefaultMutableTreeNode( entryExtended, true );
                dmtnCpu.add( dmtnExtended );
        
                ListEntry entryVendor =  // Child node 3 = Vendor CPUID
                    new ListEntry( "Vendor functions", "", "", false, false );
                DefaultMutableTreeNode dmtnVendor = 
                    new DefaultMutableTreeNode( entryVendor, true );
                dmtnCpu.add( dmtnVendor );

                ListEntry entryVirtual =  // Child node 4 = Virtual CPUID
                    new ListEntry( "Virtual functions", "", "", false, false );
                DefaultMutableTreeNode dmtnVirtual = 
                    new DefaultMutableTreeNode( entryVirtual, true );
                dmtnCpu.add( dmtnVirtual );
                
                for( int j=0; j< functionCount; j++ )
                {
                    aTableModels.add( rootEnum.getFunctionFirstTable( j ) );
                    aDumpModels.add( rootEnum.getFunctionSecondTable( j ) ); 
                    aTables.add
                        ( new JTable( rootEnum.getFunctionFirstTable( j ) ) );
                    aDumps.add
                        ( new JTable( rootEnum.getFunctionSecondTable( j ) ) );

                    ListEntry entry = new ListEntry
                        ( rootEnum.getFunctionShortName( j ),
                          rootEnum.getFunctionLongName( j ),
                          "", false, true, globalIndex++ );
                    DefaultMutableTreeNode node = 
                            new DefaultMutableTreeNode( entry , false );
                    char c1 = rootEnum.getFunctionShortName( j ).charAt(0);
                    String s1 = "";
                    if ( rootEnum.getFunctionShortName( j ).length() > 4 )
                    {
                        s1 = rootEnum.getFunctionShortName( j ).substring( 0, 4 );
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
                    if ( ( c1 == '2' ) || ( c1 == 'C' ) ||
                            ( s1.equals( "8086" )) ) 
                                            { dmtnVendor.add( node );   }
                    else if ( c1 == '0' )   { dmtnStandard.add( node ); }
                    else if ( c1 == '8' )   { dmtnExtended.add( node ); }
                    else if ( c1 == '4' )   { dmtnVirtual.add( node );  }
                }

                dmtnRoot.add( dmtnCpu );
            }
            
            listModels = aTableModels.toArray
                ( new ChangeableTableModel[aTableModels.size()] );
            dumpModels = aDumpModels.toArray
                ( new ChangeableTableModel[aDumpModels.size()] );

            listTables = aTables.toArray( new JTable[aTables.size()] );
            dumpTables = aDumps.toArray( new JTable[aDumps.size()] );
        }
        else
        {
            String[] up = new String[] { "Parameter", "Value" };
            String[][] data = new String[][] {{ "dump not loaded", "dump not loaded" }};
            ChangeableTableModel model = new ChangeableTableModel( up, data );
            
            listModels = new ChangeableTableModel[1];
            dumpModels = new ChangeableTableModel[1];
            listTables = new JTable[1];
            dumpTables = new JTable[1];
            listModels[0] = model;
            dumpModels[0] = model;
            listTables[0] = new JTable( model );
            dumpTables[0] = new JTable( model );
            
            ListEntry entryRoot = new ListEntry
                ( " [ Dump not loaded. ] ", "", "", false, false );
            DefaultMutableTreeNode dmtnRoot = 
                new DefaultMutableTreeNode( entryRoot, true );
            nodeList.add( dmtnRoot );
        }
        
        DefaultTreeModel treeModel =
            new DefaultTreeModel( nodeList.get(0), true );
        fncTree = new JTree( treeModel );
        fncTree.addTreeSelectionListener( new SelectionListener() );
        JScrollPane leftPanel = new JScrollPane( fncTree );
        JPanel rightPanel = helperNodeSelection( 0 );
        dualPanelH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
        dualPanelH.setOneTouchExpandable( true );
        dualPanelH.setDividerSize( 9 );
        dualPanelH.setDividerLocation( PANEL_DIVIDER_HORIZONTAL );
        dualPanelH.setLeftComponent( leftPanel );
        dualPanelH.setRightComponent( rightPanel );
        BoxLayout hLayout = new BoxLayout( detailsPanel, BoxLayout.X_AXIS );
        detailsPanel.setLayout( hLayout );
        detailsPanel.add( dualPanelH );
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
                dualPanelH.setDividerLocation( dividerH );
                dualPanelV.setDividerLocation( dividerV );
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
        JTable fncListTable = listTables[ index ];
        JTable fncDumpTable = dumpTables[ index ];
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
        // Build split panel with function table and dump table vertically.
        dualPanelV = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true );
        dualPanelV.setOneTouchExpandable( true );
        dualPanelV.setDividerSize( 11 );
        dualPanelV.setDividerLocation( PANEL_DIVIDER_VERTICAL );
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
        if ( listModels != null ) count += listModels.length;
        if ( dumpModels != null ) count += dumpModels.length;
        AbstractTableModel[] models = new AbstractTableModel[count];
    
        int j = 0;
        for( int i = 0; i < count; i += 2 )
        {
            models[i] = listModels[j];
            models[i + 1] = dumpModels[j];
            j++;
        }
        return models;
    }
    
}

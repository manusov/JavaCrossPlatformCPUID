/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Multiprocessor topology by OS API information panel, 
provides content for "SMP" leaf at tabbed pane.

*/

package cpuidv3.guipanels;

import cpuidv3.gui.HelperTable;
import cpuidv3.sal.ChangeableTableModel;
import cpuidv3.sal.ReportData;
import cpuidv3.sal.SAL;
import cpuidv3.sal.TreeEntry;
import cpuidv3.sal.TreeScreenModel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class PanelSmp extends ApplicationPanel
{
    private final static int H_DIVIDER_SIZE = 9;
    private final static int V_DIVIDER_SIZE = 11;
    private final static int H_DIVIDER_LOCATION = 400;
    private final static int V_DIVIDER_LOCATION = 192;

    private final JPanel smpPanel;
    private JTree topologyTree;
//  private JTable[] topologyListTables;
//  private JTable[] topologyDumpTables;
    private JSplitPane dualPanelH;
    private JSplitPane dualPanelV;
    
    private TreeScreenModel treeScreenModel;
    private int previousIndex = -1;

    PanelSmp( SAL sal ) 
    { 
        super( sal );
        smpPanel = new JPanel(); 
    }

    @Override String getPanelName()     { return "SMP";         }
    @Override String getPanelIcon()     { return "tab_smp.png"; }
    @Override JPanel getPanel()         { return smpPanel;      }
    @Override boolean getPanelActive()  { return true;          }
    @Override String getPanelTip()      { return 
            "Symmetrical multiprocessor topology by OS API.";   }
    
    @Override void rebuildPanel( boolean physical )
    {
        previousIndex = -1;
        treeScreenModel = salRef.getSmpTree();
        if ( treeScreenModel == null ) return;
        
// Memory load optimization. See alternative at v3.03.11.
// Replace variant with pre-build all tables to variant with dynamically create
// required JTable(table model) when click associated node of tree.
// See alternative at v3.03.11 ( use HashMap, cache and not replicate equal
// tables ).
//        
//      int count = treeScreenModel.upTables.length;
//      topologyListTables = new JTable[count];
//      topologyDumpTables = new JTable[count];
//      for( int i=0; i<count; i++ )
//      {
//          topologyListTables[i] = new JTable( treeScreenModel.upTables[i] );
//          topologyDumpTables[i] = new JTable( treeScreenModel.downTables[i] );
//      }
//        
        topologyTree = new JTree( treeScreenModel.treeModel );
        topologyTree.addTreeSelectionListener( new SelectionListener() );
        JScrollPane leftPanel = new JScrollPane( topologyTree );
        JPanel rightPanel = helperNodeSelection( 0 );
        dualPanelH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
        dualPanelH.setOneTouchExpandable( true );
        dualPanelH.setDividerSize( H_DIVIDER_SIZE );
        dualPanelH.setDividerLocation( H_DIVIDER_LOCATION );
        dualPanelH.setLeftComponent( leftPanel );
        dualPanelH.setRightComponent( rightPanel );
        BoxLayout hLayout = new BoxLayout( smpPanel, BoxLayout.X_AXIS );
        smpPanel.setLayout( hLayout );
        smpPanel.add( dualPanelH );
    }
    
    // Selection listener for SMP topology tree.
    private class SelectionListener implements TreeSelectionListener
    {
        @Override public void valueChanged( TreeSelectionEvent e )
        {
            Object tree = ( JTree ) e.getSource();
            if ( !( tree instanceof JTree ) ) return;
            Object path = ( ( JTree )tree ).getSelectionPath();
            if ( !( path instanceof TreePath ) ) return;
            Object node = ( ( TreePath )path ).getLastPathComponent();
            if ( !(node instanceof DefaultMutableTreeNode ) ) return;
            Object entry = 
                ( ( DefaultMutableTreeNode )( node ) ).getUserObject();
            if ( !( entry instanceof TreeEntry ) ) return;
        
            int index = ( ( TreeEntry ) entry ).getIndex();
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
    
    // Helper for create right component of dual panel, pair of tables:
    // topology object table + dump table.
    private JPanel helperNodeSelection( int index )
    {
//      JTable fncListTable = topologyListTables[index];
//      JTable fncDumpTable = topologyDumpTables[index];
        JTable fncListTable = new JTable( treeScreenModel.upTables[index] );
        JTable fncDumpTable = new JTable( treeScreenModel.downTables[index] );
//        
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
        dualPanelV.setDividerSize( V_DIVIDER_SIZE );
        dualPanelV.setDividerLocation( V_DIVIDER_LOCATION );
        dualPanelV.setTopComponent( upTable );
        dualPanelV.setBottomComponent( downTable );
        BoxLayout vLayout = new BoxLayout( rightPanel, BoxLayout.X_AXIS );
        rightPanel.setLayout( vLayout );
        rightPanel.add( dualPanelV );
        return rightPanel;
    }

    @Override ReportData getReportThis()
    {
        int treeIndex = previousIndex;
        if( ( treeIndex < 0 )||
            ( treeIndex >= treeScreenModel.tablesPairsNames.length ) ||
            ( treeIndex >= treeScreenModel.upTables.length )         ||
            ( treeIndex >= treeScreenModel.downTables.length )       )
        {
            treeIndex = 0;
        }
        
        String tablesPairName = 
            treeScreenModel.tablesPairsNames[ treeIndex ];
        ChangeableTableModel upModel =
            treeScreenModel.upTables[ treeIndex ];
        ChangeableTableModel downModel = 
            treeScreenModel.downTables[ treeIndex ];
        
        return new ReportData( getPanelName(), new String[]{ tablesPairName },
            new ChangeableTableModel[]{ upModel }, 
            new ChangeableTableModel[]{ downModel } );
    }
    
    @Override ReportData[] getReportAll()
    {
        ChangeableTableModel[] rejectedDownTables = 
                new ChangeableTableModel[ treeScreenModel.downTables.length ];
        rejectedDownTables[0] = treeScreenModel.downTables[0];
        
        ReportData data = new ReportData
            ( getPanelName(), treeScreenModel.tablesPairsNames,
                treeScreenModel.upTables, rejectedDownTables );
        return new ReportData[] { data };
    }
}

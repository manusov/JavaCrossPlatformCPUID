/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

CPUID instruction results detail information panel (as tree),
provides content for "Details" leaf at tabbed pane.

*/

package cpuidv3.guipanels;

import cpuidv3.CPUIDv3;
import cpuidv3.gui.HelperTable;
import cpuidv3.services.ChangeableTableModel;
import cpuidv3.services.TreeEntry;
import cpuidv3.services.TreeScreenModel;
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
import javax.swing.tree.TreePath;

public class PanelDetails extends ApplicationPanel
{
    private final static int H_DIVIDER_SIZE = 9;
    private final static int V_DIVIDER_SIZE = 11;
    private final static int H_DIVIDER_LOCATION = 420;
    private final static int V_DIVIDER_LOCATION = 510;

    private final JPanel detailsPanel;
    private JTree detailsTree;
    private JTable[] fncListTables;
    private JTable[] fncDumpTables;
    private JSplitPane dualPanelH;
    private JSplitPane dualPanelV;
    
    private TreeScreenModel treeScreenModel;
    private int previousIndex = -1;
    
    PanelDetails() { detailsPanel = new JPanel(); }

    @Override String getPanelName()     { return "Details";         }
    @Override String getPanelIcon()     { return "tab_details.png"; }
    @Override JPanel getPanel()         { return detailsPanel;      }
    @Override boolean getPanelActive()  { return true;              }
    @Override String getPanelTip()      { return 
            "CPUID instruction functions tree and details.";        }
    
    @Override void rebuildPanel( boolean physical )
    {
        previousIndex = -1;
        treeScreenModel = CPUIDv3.getSal().getDetailsTree();
        if ( treeScreenModel == null ) return;
        
        int count = treeScreenModel.upTables.length;
        fncListTables = new JTable[count];
        fncDumpTables = new JTable[count];
        for( int i=0; i<count; i++ )
        {
            fncListTables[i] = new JTable( treeScreenModel.upTables[i] );
            fncDumpTables[i] = new JTable( treeScreenModel.downTables[i] );
        }
        
        detailsTree = new JTree( treeScreenModel.treeModel );
        detailsTree.addTreeSelectionListener( new SelectionListener() );
        JScrollPane leftPanel = new JScrollPane( detailsTree );
        JPanel rightPanel = helperNodeSelection( 0 );
        dualPanelH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
        dualPanelH.setOneTouchExpandable( true );
        dualPanelH.setDividerSize( H_DIVIDER_SIZE );
        dualPanelH.setDividerLocation( H_DIVIDER_LOCATION );
        dualPanelH.setLeftComponent( leftPanel );
        dualPanelH.setRightComponent( rightPanel );
        BoxLayout hLayout = new BoxLayout( detailsPanel, BoxLayout.X_AXIS );
        detailsPanel.setLayout( hLayout );
        detailsPanel.add( dualPanelH );
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

    @Override AbstractTableModel[] getPanelModels() 
    {
        int count = 0;
        if ( treeScreenModel.upTables != null )
        {
            count += treeScreenModel.upTables.length;
        }
        if ( treeScreenModel.downTables != null )
        {
            count += treeScreenModel.downTables.length;
        }
        AbstractTableModel[] models = new AbstractTableModel[count];
        int j = 0;
        for( int i = 0; i < count; i += 2 )
        {
            models[i] = treeScreenModel.upTables[j];
            models[i + 1] = treeScreenModel.downTables[j];
            j++;
        }
        return models;
    }
    
    @Override AbstractTableModel[] getReportThisModels()
    { 
        int treeIndex = previousIndex;
        if( ( treeIndex < 0 )||
            ( treeIndex >= treeScreenModel.upTables.length )||
            ( treeIndex >= treeScreenModel.downTables.length ) )
        {
            treeIndex = 0;
        }
        AbstractTableModel[] atms = new ChangeableTableModel[2];
        atms[0] = treeScreenModel.upTables[ treeIndex ];
        atms[1] = treeScreenModel.downTables[ treeIndex ];
        return atms;
    }

}

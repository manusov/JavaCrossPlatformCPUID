/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

CPUID instruction results and OS topology summary information panel,
provides content for "Summary" leaf at tabbed pane.

*/

package cpuidv3.guipanels;


import cpuidv3.CPUIDv3;
import cpuidv3.gui.HelperTable;
import cpuidv3.services.ChangeableTableModel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class PanelSummary extends ApplicationPanel
{
    private final JPanel panel;
    private ChangeableTableModel model;
    private JTable table;
    
    PanelSummary() { panel = new JPanel(); }

    @Override String getPanelName()     { return "Summary";         }
    @Override String getPanelIcon()     { return "tab_summary.png"; }
    @Override JPanel getPanel()         { return panel;             }
    @Override boolean getPanelActive()  { return true;              }
    @Override String getPanelTip()      { return 
            "Platform and CPUID instruction summary info.";         }
    
    @Override void rebuildPanel( boolean physical )
    {
        model = CPUIDv3.getSal().getSummaryTable();
        table = new JTable( model );
        final int X_SCALE = 200;
        HelperTable.optimizeSingleTable( panel, table, X_SCALE, true );
    }
    
    @Override AbstractTableModel[] getPanelModels()
    { 
        return new AbstractTableModel[]{ model };
    }

    @Override AbstractTableModel[] getReportThisModels()
    { 
        return getPanelModels();
    }
    
}

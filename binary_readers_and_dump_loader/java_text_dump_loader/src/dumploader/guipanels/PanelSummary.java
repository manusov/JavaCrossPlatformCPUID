/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

CPUID instruction results and OS topology summary information panel,
provides content for "Summary" leaf at tabbed pane.

*/

package dumploader.guipanels;

import dumploader.DumpLoader;
import dumploader.cpuenum.ChangeableTableModel;
import dumploader.cpuenum.CpuRootEnumerator;
import dumploader.cpuenum.EntryLogicalCpu;
import dumploader.gui.HelperTable;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class PanelSummary extends ApplicationPanel
{
    private final JPanel panel;
    private AbstractTableModel modelSummary;
    private JTable table;
    
    PanelSummary()
    {
        panel = new JPanel();
    }

    @Override String getPanelName()     { return "Summary";         }
    @Override String getPanelIcon()     { return "tab_summary.png"; }
    @Override JPanel getPanel()         { return panel;             }
    @Override boolean getPanelActive()  { return true;              }
    @Override String getPanelTip()      { return 
            "CPUID and topology summary."; }
    
    private final static int X_SCALE = 200;
    
    // Get information and build GUI panel for tab : "Summary".
    @Override void rebuildPanel( boolean physical )
    {
        CpuRootEnumerator rootEnum = DumpLoader.getCpuRootEnumerator();
        if( rootEnum != null )
        {
            modelSummary = rootEnum.getSummaryTable();
        }
        else
        {
            String[] upSummary = new String[] { "Parameter", "Value" };
            String[][] dataSummary = new String[][] {{ "dump not loaded", "dump not loaded" }};
            modelSummary = new ChangeableTableModel( upSummary, dataSummary );
        }
        table = new JTable( modelSummary );
        HelperTable.optimizeSingleTable( panel, table, X_SCALE, true );
    }

    @Override AbstractTableModel[] getPanelModels()
    { 
        return new AbstractTableModel[]{ modelSummary };
    }

}

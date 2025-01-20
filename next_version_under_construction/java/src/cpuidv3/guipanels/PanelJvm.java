/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

JVM (Java Virtual Machine) information panel,
provides content for "JVM" leaf at tabbed pane.

*/

package cpuidv3.guipanels;

import cpuidv3.CPUIDv3;
import cpuidv3.gui.HelperTable;
import cpuidv3.services.ChangeableTableModel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class PanelJvm extends ApplicationPanel
{
    private final JPanel panel;
    private ChangeableTableModel model;
    private JTable table;
    
    PanelJvm() { panel = new JPanel(); }

    @Override String getPanelName()     { return "JVM";         }
    @Override String getPanelIcon()     { return "tab_jvm.png"; }
    @Override JPanel getPanel()         { return panel;         }
    @Override boolean getPanelActive()  { return true;          }
    @Override String getPanelTip()      { return 
            "Java Virtual Machine properties.";                 }
    
    @Override void rebuildPanel(  boolean physical )
    {
        model = CPUIDv3.getSal().getJvmTable();
        table = new JTable( model );
        final int X_SCALE = 700;
        HelperTable.optimizeSingleTable( panel, table, X_SCALE, false );
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

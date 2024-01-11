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
CPU/OS context management information application, provides content for
"Context" leaf at tabbed pane.
Note "XCR" means Extended Control Register, declares OS capability for save
and restore processor context during task switch.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import cpuidv2.CPUIDv2;
import cpuidv2.gui.HelperTable;

class InfoContext extends Application 
{
private final JPanel panel;
private final static String TIP = 
    "OS processor context management bitmap by XGETBV instruction.";
private ChangeableTableModel model;
private JTable table;

InfoContext()
    {
    panel = new JPanel();
    }

@Override String[] getPanelNames() { return new String[] { "Context"     }; }
@Override String[] getPanelIcons() 
    { return new String[] { "tab_context.png" }; }
@Override JPanel[] getPanels() { return new JPanel[]     { panel         }; }
@Override boolean[] getPanelActives()
    { return new boolean[] { true }; }
@Override String[] getPanelTips()  { return new String[]  { TIP }; }

// Get system information and build GUI panel for one tab : "Context".
@Override void rebuildPanels()
    {
    String[] tableUp = { "Feature", "Bit", "CPU validated", "OS validated" };
    String[][] tableData = CPUIDv2.getServiceContext().getCpuContextInfo();
    model = new ChangeableTableModel( tableUp, tableData );
    table = new JTable( model );
    HelperTable.optimizeSingleTable( panel, table, 550, true );
    }

@Override AbstractTableModel[] getPanelModels() 
    { 
    return new AbstractTableModel[] { model };
    }

@Override AbstractTableModel[] getReportThisModels( int subIndex )
    { 
    return getPanelModels();
    }

}

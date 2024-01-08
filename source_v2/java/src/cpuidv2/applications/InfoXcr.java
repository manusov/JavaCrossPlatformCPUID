/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
---------------------------------------------------------------------------
CPU/OS context management information application, 
provides content for "Context" tab at tabbed pane.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import cpuidv2.CPUIDv2;
import cpuidv2.gui.HelperTable;

class InfoXcr extends Application 
{
private final JPanel panel;
private final static String TIP = 
    "OS processor context management bitmap by XGETBV instruction.";
private ChangeableTableModel model;
private JTable table;

InfoXcr()
    {
    panel = new JPanel();
    }

@Override String[] getPanelNames() { return new String[] { "Context"     }; }
@Override String[] getPanelIcons() { return new String[] { "tab_xcr.png" }; }
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

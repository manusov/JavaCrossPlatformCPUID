/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
---------------------------------------------------------------------------
OS information application, provides content for "OS" tab at tabbed pane.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import cpuidv2.CPUIDv2;
import cpuidv2.gui.HelperTable;

class InfoOs extends Application 
{
private final JPanel panel;
private final static String TIP = "Operating system properties and features.";
private ChangeableTableModel model;
private JTable table;
    
InfoOs()
    {
    panel = new JPanel();
    }

@Override String[] getPanelNames() { return new String[] { "OS"         }; }
@Override String[] getPanelIcons() { return new String[] { "tab_os.png" }; }
@Override JPanel[] getPanels() { return new JPanel[]     { panel      }; }
@Override boolean[] getPanelActives()
    { return new boolean[] { true }; }
@Override String[] getPanelTips()  { return new String[]  { TIP }; }

// Get system information and build GUI panel for one tab : "OS".
@Override void rebuildPanels()
    {
    String[] tableUp = { "Parameter" , "Value" };
    String[][] tableData = CPUIDv2.getServiceOsInfo().getOsInfo();
    model = new ChangeableTableModel( tableUp, tableData );
    table = new JTable( model );
    HelperTable.optimizeSingleTable( panel, table, 0, false );
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

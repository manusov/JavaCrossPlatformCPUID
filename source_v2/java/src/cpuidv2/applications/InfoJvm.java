/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
---------------------------------------------------------------------------
JVM information application, provides content for "JVM" tab at tabbed pane.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import cpuidv2.CPUIDv2;
import cpuidv2.gui.HelperTable;

class InfoJvm extends Application 
{
private final JPanel panel;
private final static String TIP = "Java virtual machine properties.";
private ChangeableTableModel model;
private JTable table;
    
InfoJvm()
    {
    panel = new JPanel();
    }

@Override String[] getPanelNames() { return new String[] { "JVM"         }; }
@Override String[] getPanelIcons() { return new String[] { "tab_jvm.png" }; }
@Override JPanel[] getPanels() { return new JPanel[]     { panel         }; }
@Override boolean[] getPanelActives()
    { return new boolean[] { true }; }
@Override String[] getPanelTips()  { return new String[] { TIP }; }

// Get system information and build GUI panel for one tab : "JVM".
@Override void rebuildPanels()
    {
    String[] tableUp = { "Parameter" , "Value" };
    String[][] tableData = CPUIDv2.getServiceJvmInfo().getJvmInfo();
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

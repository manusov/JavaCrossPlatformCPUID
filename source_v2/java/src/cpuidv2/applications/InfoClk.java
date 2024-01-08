/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
---------------------------------------------------------------------------
Processor clock information application, 
provides content for "Clocks" tab at tabbed pane.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import cpuidv2.CPUIDv2;
import cpuidv2.gui.HelperTable;

class InfoClk extends Application 
{
private final JPanel panel;
private final static String CLOCK_TIP = 
    "CPU clock measurement by RDTSC instruction.";
private ChangeableTableModel model;
private JTable table;
    
InfoClk()
    {
    panel = new JPanel();
    }

@Override String[] getPanelNames() { return new String[]  { "Clock"       }; }
@Override String[] getPanelIcons() { return new String[]  { "tab_clk.png" }; }
@Override JPanel[] getPanels()     { return new JPanel[]  { panel         }; }
@Override boolean[] getPanelActives() 
    { return new boolean[] { true }; }
@Override String[] getPanelTips()  { return new String[]  { CLOCK_TIP }; }

// Get system information and build GUI panel for one tab : "Clocks".
@Override void rebuildPanels()
    {
    String[] tableUp = { "Parameter" , "Value" };
    String[][] tableData = CPUIDv2.getServiceClocks().getClocksInfo();
    model = new ChangeableTableModel( tableUp, tableData );
    table = new JTable( model );
    HelperTable.optimizeSingleTable( panel, table, 0, true );
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

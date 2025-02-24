/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

CPU/OS context management information panel, provides content for
"Context" leaf at tabbed pane.
Note "XCR" means Extended Control Register, declares OS capability for save
and restore processor context during task switch.

*/

package cpuidv3.guipanels;

import cpuidv3.gui.HelperTable;
import cpuidv3.sal.ChangeableTableModel;
import cpuidv3.sal.ReportData;
import cpuidv3.sal.SAL;
import javax.swing.JPanel;
import javax.swing.JTable;

public class PanelContext extends ApplicationPanel
{
    private final JPanel panel;
    private ChangeableTableModel model;
    private JTable table;
    
    PanelContext( SAL sal ) 
    { 
        super( sal );
        panel = new JPanel(); 
    }

    @Override String getPanelName()     { return "Context";         }
    @Override String getPanelIcon()     { return "tab_context.png"; }
    @Override JPanel getPanel()         { return panel;             }
    @Override boolean getPanelActive()  { return true;              }
    @Override String getPanelTip()      { return 
            "OS processor context management bitmap by XGETBV instruction."; }
    
    @Override void rebuildPanel( boolean physical )
    {
        model = salRef.getContextTable();
        table = new JTable( model );
        final int X_SCALE = 600;
        HelperTable.optimizeSingleTable( panel, table, X_SCALE, true );
    }

    @Override ReportData getReportThis()
    {
        return new ReportData( getPanelName(), new String[]{ getPanelTip() }, 
            new ChangeableTableModel[]{ model }, null );
    }
    
    @Override ReportData[] getReportAll()
    {
        return new ReportData[] { getReportThis() };
    }
}

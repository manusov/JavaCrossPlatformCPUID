/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

OS (Operating System) information panel,
provides content for "OS" leaf at tabbed pane.

*/

package cpuidv3.guipanels;

import cpuidv3.sal.ChangeableTableModel;
import cpuidv3.sal.ReportData;
import cpuidv3.sal.SAL;
import javax.swing.JPanel;
import javax.swing.JTable;

class PanelOs extends ApplicationPanel
{
    private final JPanel panel;
    private ChangeableTableModel model;
    private JTable table;
    
    PanelOs( SAL sal )
    { 
        super( sal );
        panel = new JPanel(); 
    }

    @Override String getPanelName()     { return "OS";         }
    @Override String getPanelIcon()     { return "tab_os.png"; }
    @Override JPanel getPanel()         { return panel;        }
    @Override boolean getPanelActive()  { return true;         }
    @Override String getPanelTip()      { return 
            "Operating System properties.";       }
    
    @Override void rebuildPanel( boolean physical )
    {
        model = salRef.getOsTable();
        if ( model != null )
        {
            table = new JTable( model );
            final int X_SCALE = 700;
            HelperTable.optimizeSingleTable( panel, table, X_SCALE, false );
        }
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

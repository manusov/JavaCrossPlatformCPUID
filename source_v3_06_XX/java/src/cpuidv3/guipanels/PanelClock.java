/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Processor clock information panel, 
provides content for "Clock" leaf at tabbed pane.

*/

package cpuidv3.guipanels;

import cpuidv3.sal.ChangeableTableModel;
import cpuidv3.sal.ReportData;
import cpuidv3.sal.SAL;
import javax.swing.JPanel;
import javax.swing.JTable;

class PanelClock extends ApplicationPanel
{
    private final JPanel panel;
    private ChangeableTableModel model;
    private JTable table;
    
    PanelClock( SAL sal )
    {
        super( sal );
        panel = new JPanel(); 
    }
    
    @Override String getPanelName()     { return "Clock";         }
    @Override String getPanelIcon()     { return "tab_clock.png"; }
    @Override JPanel getPanel()         { return panel;           }
    @Override boolean getPanelActive()  { return true;            }
    @Override String getPanelTip()      { return 
            "CPU clock measurement by RDTSC instruction.";        }
    
    @Override void rebuildPanel(  boolean physical )
    {
        model = salRef.getClockTable();
        if ( model != null )
        {
            table = new JTable( model );
            final int X_SCALE = 600;
            HelperTable.optimizeSingleTable( panel, table, X_SCALE, true );
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

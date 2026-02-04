/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent template class for application functionality panel,
provided as leafs of tabbed pane.

*/

package cpuidv3.guipanels;

import cpuidv3.sal.ReportData;
import cpuidv3.sal.SAL;
import javax.swing.JPanel;

public class ApplicationPanel 
{
    protected final SAL salRef;
    
    public ApplicationPanel( SAL sal )
    {
        salRef = sal;
    }
    
    String getPanelName()     { return null;  }
    String getPanelIcon()     { return null;  }
    JPanel getPanel()         { return null;  }
    boolean getPanelActive()  { return false; }
    String getPanelTip()      { return null;  }

    ReportData getReportThis()  { return null; }
    ReportData[] getReportAll() { return null; }

    // This method for application restart.
    void rebuildPanel( boolean physical ) { }
    // This mrthod for dynamical revisual.
    void refreshPanel() { }
}

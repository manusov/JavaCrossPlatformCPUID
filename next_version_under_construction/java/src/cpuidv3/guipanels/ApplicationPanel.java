/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent template class for application functionality panel,
provided as leafs of tabbed pane.

*/

package cpuidv3.guipanels;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

public class ApplicationPanel 
{
    String getPanelName()     { return null;  }
    String getPanelIcon()     { return null;  }
    JPanel getPanel()         { return null;  }
    boolean getPanelActive()  { return false; }
    String getPanelTip()      { return null;  }

    AbstractTableModel[] getPanelModels()      { return null; }
    AbstractTableModel[] getReportThisModels() { return null; }

    // This method for application restart.
    void rebuildPanel( boolean physical ) { }
    // This mrthod for dynamical revisual.
    void refreshPanel() { }
}

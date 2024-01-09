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
Parent template class for applications. "Applications" here means functions
of Java CPUID application, provided as leafs of tabbed pane.
*/

package cpuidv2.applications;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

class Application 
{
String[] getPanelNames()    { return null; }
String[] getPanelIcons()    { return null; }
JPanel[] getPanels()        { return null; }
boolean[] getPanelActives() { return null; }
String[] getPanelTips()     { return null; }

AbstractTableModel[] getPanelModels()                     { return null; }
AbstractTableModel[] getReportThisModels( int subIndex )  { return null; }

void rebuildPanels() { }   // This method for application restart.
void refreshPanels() { }   // This mrthod for dynamical revisual.

}

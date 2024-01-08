/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
---------------------------------------------------------------------------
Parent template class for applications, provided as panels at tabbed pane.
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

void rebuildPanels() { }   // This methods pair for app. restart.
void refreshPanels() { }   // This mrthods pair for dynamical

}

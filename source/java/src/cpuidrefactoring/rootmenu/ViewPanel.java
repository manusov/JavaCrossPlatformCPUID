/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Parent class for sets of GUI panels and components.
*/

package cpuidrefactoring.rootmenu;

import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class ViewPanel 
{
abstract public JPanel getP();                 // get panel
abstract public JComponent[] getComponents();  // get GUI components list
}

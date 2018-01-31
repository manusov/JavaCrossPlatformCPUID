/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Base abstract class for view panels (viewers).
Declares methods for get panel and get components list in the panel.
*/

package cpuid.applications.guipanels;

import javax.swing.*;

abstract public class ViewPanel 
{
abstract public JPanel getP();                 // get panel
abstract public JComponent[] getComponents();  // get GUI components list
}

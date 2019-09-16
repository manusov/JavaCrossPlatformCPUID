/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Interface for BuiltView module.
Declares public methods:
get GUI sub-application panel with GUI components,
get x-size, get y-size of GUI object
*/

package cpuid.applications.mvc;

import javax.swing.JPanel;

public interface BV 
{
public JPanel getP(); 
public int getXsize();
public int getYsize();
}

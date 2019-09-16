/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
GUI panel model builder unified interface.
Declare component model capabilities:
get models count, get selected model, 
get data as binary dump, set data as binary dump.
*/

package cpuid.applications.guimodels;

public interface ModelBuilder 
{
public int getCount();                  // get number of models
public ViewableModel getValue(int i);   // get selected model
public long[] getBinary();              // get data as binary dump
public boolean setBinary( long[] x );   // set data as binary dump
}

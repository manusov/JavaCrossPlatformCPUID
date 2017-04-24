//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// GUI panel model builder unified interface.

package arch1.applications.guimodels;

public interface ModelBuilder 
{
public int getCount();
public ViewableModel getValue(int i);
public long[] getBinary();
public boolean setBinary( long[] x );
}

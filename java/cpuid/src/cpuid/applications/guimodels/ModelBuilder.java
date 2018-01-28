//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// GUI panel model builder unified interface.

package cpuid.applications.guimodels;

public interface ModelBuilder 
{
public int getCount();
public ViewableModel getValue(int i);
public long[] getBinary();
public boolean setBinary( long[] x );
}

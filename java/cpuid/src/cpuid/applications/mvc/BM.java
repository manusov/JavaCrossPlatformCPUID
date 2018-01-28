//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Interface for BuiltModel module.

package cpuid.applications.mvc;

import cpuid.kernel.PAL;
import cpuid.applications.guimodels.ModelBuilder;

public interface BM 
{
public PAL getPal(); 
public ModelBuilder getModel();
}

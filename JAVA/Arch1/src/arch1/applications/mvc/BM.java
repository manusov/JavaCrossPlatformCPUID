//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Interface for BuiltModel module.

package arch1.applications.mvc;

import arch1.kernel.PAL;
import arch1.applications.guimodels.ModelBuilder;

public interface BM 
{
public PAL getPal(); 
public ModelBuilder getModel();
}

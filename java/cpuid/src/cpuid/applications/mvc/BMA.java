/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Built Model Adapter.
Implements public methods:
get platform abstraction layer and get data model(s) for GUI component(s).
Note, constructor assigns variables values pal, cmb located at child class, 
for flexibility.

*/

package cpuid.applications.mvc;

import cpuid.applications.guimodels.ModelBuilder;
import cpuid.kernel.PAL;

public class BMA implements BM
{
protected PAL pal;
protected ModelBuilder cmb;

@Override public PAL getPal() 
    {
    return pal; 
    }

@Override public ModelBuilder getModel() 
    {
    return cmb; 
    }
    
}

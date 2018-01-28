//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Built Model Adapter.

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

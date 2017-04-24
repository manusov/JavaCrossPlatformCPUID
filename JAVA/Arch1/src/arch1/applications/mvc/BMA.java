//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Built Model Adapter.

package arch1.applications.mvc;

import arch1.applications.guimodels.ModelBuilder;
import arch1.kernel.PAL;

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

//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// MVC "Controller" module for ApplicationCPUcontext,
// Note MVC is Model, View, Controller paradigm.
// Application: CPU Context Management info.
// Controller interconnects model and view.

package arch1.applications.applicationcpucontext;

import arch1.About;
import arch1.applications.mvc.*;

public class BuiltController extends BCA
{
public BuiltController()
    {
    //--- MVC initialization ---    
    bm = new BuiltModel();
    bv = new BuiltView( bm, About.getX2size(), About.getY2size() );
    }
}

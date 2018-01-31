/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
MVC "Controller" module for ApplicationCPUcontext,
Note MVC is Model, View, Controller paradigm.
Application: CPU Context Management info.
Controller interconnects model and view.
Note, variables bm (BuildModel), bv (BuiltView) located at parent class.
*/

package cpuid.applications.applicationcpucontext;

import cpuid.applications.mvc.BCA;
import cpuid.About;

public class BuiltController extends BCA
{
public BuiltController()
    {
    // MVC initialization
    bm = new BuiltModel();
    bv = new BuiltView( bm, About.getX2size(), About.getY2size() );
    }
}

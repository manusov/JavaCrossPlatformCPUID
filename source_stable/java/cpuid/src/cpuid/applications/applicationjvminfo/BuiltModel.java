/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
MVC "Model" module for ApplicationJVMinfo,
note MVC is Model, View, Controller paradigm.
Application: Java Virtual Machine (JVM) info.
Model provide data model, use PAL and local builder.
Note, variables, initialized in constructor:
pal (platform abstraction layer), cmb (component model builder)
located at parent class.
*/

package cpuid.applications.applicationjvminfo;

import cpuid.CpuId;
import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ModelBuilder;
import cpuid.applications.guimodels.ViewableModel;
import cpuid.applications.guimodels.VM3;
import cpuid.applications.mvc.BMA;
import cpuid.drivers.cpr.Device;
import cpuid.kernel.Registry;

public class BuiltModel extends BMA   //  implements BM 
{
    
public BuiltModel()
    {
    // Initialize JVM info model builder
    Registry r = CpuId.getRegistry();
    pal = r.getPAL();
    cmb = new JVMcontrolModelBuilder();
    }

// Model builder for JVM information data source declaration

protected class JVMcontrolModelBuilder implements ModelBuilder 
    {
    private final static String NAME = "JVM information";
    private final ViewableModel vm;
    private final Device device;
    
    public JVMcontrolModelBuilder()
        {
        // Load driver
        Registry r = CpuId.getRegistry();
        device = r.loadDriver(Registry.CPR.driverJVMINFO);
        String[] sa1 = device.getSummaryUp();
        String[][] sa2 = device.getSummaryText();
        ChangeableTableModel m1 = new ChangeableTableModel(sa1, sa2);
        vm = new VM3(NAME, m1);
        }

    @Override public int getCount()
        { return 1; }

    @Override public ViewableModel getValue(int i)
        { return vm; }

    @Override public long[] getBinary()
        { return null; }  // not used for this model

    @Override public boolean setBinary( long[] x )
        { return false; }  // not used for this model
            
    }

}

//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// MVC "Model" module for ApplicationCpuClk,
// Note MVC is Model, View, Controller paradigm.
// Application: CPU Clock info.
// Model provide data model, use PAL and local builder.

package arch1.applications.applicationcpuclk;

import arch1.Arch1;
import arch1.applications.guimodels.ModelBuilder;
// import arch1.applications.mvc.BM;
import arch1.applications.guimodels.ChangeableTableModel;
import arch1.applications.guimodels.ViewableModel;
import arch1.applications.guimodels.VM3;
import arch1.applications.mvc.BMA;
import arch1.drivers.cpr.Device;
// import arch1.kernel.PAL;
import arch1.kernel.Registry;

public class BuiltModel extends BMA   //  implements BM
{
// private final PAL pal;
// private final ModelBuilder cmb;
    
public BuiltModel()
    {
    //--- Initialize CPUID model builder ---
    Registry r = Arch1.getRegistry();
    pal = r.getPAL();
    cmb = new CpuclkModelBuilder();
    }

/*
@Override public PAL getPal() 
    {
    return pal; 
    }

@Override public ModelBuilder getModel() 
    {
    return cmb; 
    }
*/

//---------- Model builder for CPU Clock data source declaration ---------------

protected class CpuclkModelBuilder implements ModelBuilder
    {
    private final static String NAME = "CPU Clocks";
    private final ViewableModel vm;
    private final long[] IPB, OPB;
    private final int IPB_SIZE = 4096, OPB_SIZE = 4096;
    private final Device device;
    
    protected CpuclkModelBuilder()
        {
        IPB = new long[IPB_SIZE];
        OPB = new long[OPB_SIZE];
        for ( int i=0; i<IPB_SIZE; i++ ) { IPB[i]=0; }
        for ( int i=0; i<OPB_SIZE; i++ ) { OPB[i]=0; }
        
        Registry r = Arch1.getRegistry();
        
        if ( ( r.binaryChannel( null, OPB, 4, OPB_SIZE ) ) == 0 )
            {
            OPB[0]=0;  // clear result if invalid 
            }
        //--- Load driver ---
        device = r.loadDriver(Registry.CPR.driverCPUCLK);
        //--- Send binary data from hardware to CPR module ---
        device.setBinary(OPB);
        //--- Get primary model strings for built secondary model ---
        String[] sa1 = device.getSummaryUp();
        String[][] sa2 = device.getSummaryText();
        //--- Built secondary model --- 
        ChangeableTableModel m1 = new ChangeableTableModel(sa1, sa2);
        vm = new VM3(NAME, m1);
        }

    @Override public int getCount()
        {
        return 1;
        }

    @Override public ViewableModel getValue(int i)
       {
       return vm;
       }
    
    @Override public long[] getBinary()
        {
        return null;  // not used for this model
        }

    @Override public boolean setBinary( long[] x )
        {
        return false;  // not used for this model
        }
    }

}

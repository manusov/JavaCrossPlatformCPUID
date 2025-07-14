/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Decoder class for interpreting CPUID information.
Text arrays = F ( CPUID binary dump ).

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.VendorDetectPhysical;
import cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T;
import cpuidv3.servicecpudata.VendorDetectVirtual;
import cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T.HYPERVISOR_XEN;
import cpuidv3.servicecpuid.IHybrid.HYBRID_CPU;
import cpuidv3.sal.EntryCpuidSubfunction;
import java.util.ArrayList;

public class DecoderCpuid 
{

private final static ReservedFunctionCpuid[] STANDARD_FUNCTIONS =
    {
    new Cpuid00000000(),
    new Cpuid00000001(),
    new Cpuid00000002(),
    new Cpuid00000003(),
    new Cpuid00000004(),
    new Cpuid00000005(),
    new Cpuid00000006(),
    new Cpuid00000007(),
    new Cpuid00000008(),
    new Cpuid00000009(),
    new Cpuid0000000A(),
    new Cpuid0000000B(),
    new Cpuid0000000C(),
    new Cpuid0000000D(),
    new Cpuid0000000E(),
    new Cpuid0000000F(),
    new Cpuid00000010(),
    new Cpuid00000011(),
    new Cpuid00000012(),
    new Cpuid00000013(),
    new Cpuid00000014(),
    new Cpuid00000015(),
    new Cpuid00000016(),
    new Cpuid00000017(),
    new Cpuid00000018(),
    new Cpuid00000019(),
    new Cpuid0000001A(),
    new Cpuid0000001B(),
    new Cpuid0000001C(),
    new Cpuid0000001D(),
    new Cpuid0000001E(),
    new Cpuid0000001F(),
    new Cpuid00000020(),
    new Cpuid00000021(),
    new Cpuid00000022(),
    new Cpuid00000023(),
    new Cpuid00000024(),
    new Cpuid00000025(),
    new Cpuid00000026(),
    new Cpuid00000027(),
    new Cpuid00000028()
    };
    
private final static ReservedFunctionCpuid[] EXTENDED_FUNCTIONS =
    {
    new Cpuid80000000(),
    new Cpuid80000001(),
    new Cpuid80000002(),
    new Cpuid80000003(),
    new Cpuid80000004(),
    new Cpuid80000005(),
    new Cpuid80000006(),
    new Cpuid80000007(),
    new Cpuid80000008(),
    new Cpuid80000009(),
    new Cpuid8000000A(),
    new Cpuid8000000B(),
    new Cpuid8000000C(),
    new Cpuid8000000D(),
    new Cpuid8000000E(),
    new Cpuid8000000F(),
    new Cpuid80000010(),
    new Cpuid80000011(),
    new Cpuid80000012(),
    new Cpuid80000013(),
    new Cpuid80000014(),
    new Cpuid80000015(),
    new Cpuid80000016(),
    new Cpuid80000017(),
    new Cpuid80000018(),
    new Cpuid80000019(),
    new Cpuid8000001A(),
    new Cpuid8000001B(),
    new Cpuid8000001C(),
    new Cpuid8000001D(),
    new Cpuid8000001E(),
    new Cpuid8000001F(),
    new Cpuid80000020(),
    new Cpuid80000021(),
    new Cpuid80000022(),
    new Cpuid80000023(),
    new Cpuid80000024(),
    new Cpuid80000025(),
    new Cpuid80000026(),
    new Cpuid80000027(),
    new Cpuid80000028(),
    new Cpuid8FFFFFFF()
    };

private final static ReservedFunctionCpuid[] VENDOR_FUNCTIONS =
    {
    new Cpuid20000000(),
    new Cpuid20000001(),
    new Cpuid80860000(),
    new Cpuid80860001(),
    new Cpuid80860002(),
    new Cpuid80860003(),
    new Cpuid80860004(),
    new Cpuid80860005(),
    new Cpuid80860006(),
    new Cpuid80860007(),
    new CpuidC0000000(),
    new CpuidC0000001(),
    new CpuidC0000002(),
    new CpuidC0000003(),
    new CpuidC0000004(),
    new CpuidC0000005()
    };

private final static int REPLACE_BY_XEN = 1;
private final static ReservedFunctionCpuid[] VIRTUAL_FUNCTIONS =
    {
    new Cpuid40000000(),
    new Cpuid40000001(),   // start range replaced for Xen
    new Cpuid40000002(),
    new Cpuid40000003(),
    new Cpuid40000004(),
    new Cpuid40000005(),   // end range replaced for Xen
    new Cpuid40000006(),
    new Cpuid40000007(),
    new Cpuid40000008(),
    new Cpuid40000009(),
    new Cpuid4000000A(),
    new Cpuid40000010()    // generic function
    };

private void restoreDefault()
    {
    ReservedFunctionCpuid[] defaultFunctions =
        {
        new Cpuid40000001(),
        new Cpuid40000002(),
        new Cpuid40000003(),
        new Cpuid40000004(),
        new Cpuid40000005()
        };
    int n = defaultFunctions.length;
    System.arraycopy( defaultFunctions, 0, VIRTUAL_FUNCTIONS, REPLACE_BY_XEN, n );
    }

private void replaceByXen( int index )
    {
    ReservedFunctionCpuid[] xenFunctions =
        {
        new Cpuid40000X01( index ),
        new Cpuid40000X02( index ),
        new Cpuid40000X03( index ),
        new Cpuid40000X04( index ),
        new Cpuid40000X05( index )
        };
    int n = xenFunctions.length;
    System.arraycopy( xenFunctions, 0, VIRTUAL_FUNCTIONS, REPLACE_BY_XEN, n );
    }

    private final ContainerCpuid container = new ContainerCpuid();
    public void setEntriesDump( EntryCpuidSubfunction[] e )
    {
        container.setEntriesDump( e );
        ReservedFunctionCpuid.setContainer( container );
        HelperEarlyVendor.earlyVendorDetect( e, false );
    }

    public int parseEntriesDump()
    {
        accumVendorName = "n/a";
        accumCpuName = "n/a";
        accumApicId = "n/a";
        accumX2ApicId = "n/a";
        accumHybrid = "n/a";
        accumHybridReturn = new HybridReturn( HYBRID_CPU.DEFAULT, "n/a", 1 );

/*
Database usage 2 of 3 = Get results of previous early vendor detection.
See also: ApplicationCpuid.java , CpuidSummary.java.
*/        
        VENDOR_T     vcpu = VendorDetectPhysical.getSavedResult();
        HYPERVISOR_T vvmm = VendorDetectVirtual.getSavedResult();
        container.setCpuVendor( vcpu );
        container.setVmmVendor( vvmm );
/*
End of database usage 2 of 3 = Get results of previous early vendor detection.
*/                

/*
Vendor-specific initialization for functions, 
can replace functions numbers 40000001h ... 40000005h
or co-exist with other hypervisor, 
if co-exist, Xen functions starts from 40000X01h, X=index.
Yet supported only replace mode with index = 0.
*/
        if ( vvmm == HYPERVISOR_XEN )
        {
            replaceByXen( 0 );  // Yet supported only replace mode with index = 0
        }
        else
        {
            restoreDefault();   // Restore required after Xen support
        }
/*
End of vendor-specific initialization
*/
        ArrayList<ReservedFunctionCpuid> allFnc = new ArrayList<>();
        for( ReservedFunctionCpuid item : STANDARD_FUNCTIONS )
        {
            helperInitAndAddFunction( item, allFnc );
        }

        for( ReservedFunctionCpuid item : VIRTUAL_FUNCTIONS )
        {
            helperInitAndAddFunction( item, allFnc );
        }
            
        for( ReservedFunctionCpuid item : EXTENDED_FUNCTIONS )
        {
            helperInitAndAddFunction( item, allFnc );
        }
            
        for( ReservedFunctionCpuid item : VENDOR_FUNCTIONS )
        {
            helperInitAndAddFunction( item, allFnc );
        }
            
        ReservedFunctionCpuid[] result =  allFnc.isEmpty() ? null :
            allFnc.toArray( new ReservedFunctionCpuid[allFnc.size()] );
        container.setDetectedFunctions( result );

        int detectedFunctionsCount = 0;
        if( result != null )
        {
            detectedFunctionsCount = result.length;
        }
        
        CpuidSummary summary = new CpuidSummary();
        CpuidDump dump = new CpuidDump();
        container.setCpuidSummary( summary );
        container.setCpuidDump( dump );
        
        return detectedFunctionsCount;
    }
    
    private void helperInitAndAddFunction( ReservedFunctionCpuid function,
            ArrayList<ReservedFunctionCpuid> accumFunctions )
    {
        int functionNumber = function.getFunction();
        EntryCpuidSubfunction[] ec = container.buildEntries( functionNumber );
        function.initData( ec );
        if ( function.isShow() )
        {
            accumFunctions.add( function );
        }

        if ( function instanceof IVendorName )
        {
            accumVendorName = ((IVendorName) function).getVendorName();
        }

        if ( function instanceof ICpuName )
        {
            accumCpuName = ((ICpuName) function).getCpuName();
        }
        
        if ( function instanceof IApicId )
        {
            accumApicId = ((IApicId) function).getApicId();
        }
        
        if ( function instanceof IX2ApicId )
        {
            accumX2ApicId = ((IX2ApicId) function).getX2ApicId();
        }
        
        if ( function instanceof IHybrid )
        {
            HybridReturn hybridReturn = ((IHybrid) function).getHybrid();
            accumHybrid = hybridReturn.hybridName;
            
            HYBRID_CPU hc = hybridReturn.hybridCpu;
            String hn = "?";
            if ( null != hc ) switch (hc) 
            {
                case P_CORE:
                    hn = "[P]";
                    break;
                case E_CORE:
                    hn = "[E]";
                    break;
                case LP_E_CORE:
                    hn = "[LP E]";
                    break;
                default:
                    hn = "";
                    break;
            }
            accumHybridReturn = 
                    new HybridReturn( hc, hn, hybridReturn.hybridSmt );
        }
    }

    // Values, accumulated after parsing entries dump.
    // Note parsing per one logical CPU, its entries dump set before parsing.
    private String accumVendorName = "n/a";
    private String accumCpuName = "n/a";
    private String accumApicId = "n/a";
    private String accumX2ApicId = "n/a";
    private String accumHybrid = "n/a";
    private HybridReturn accumHybridReturn = 
        new HybridReturn( HYBRID_CPU.DEFAULT, "n/a", 1 );
    
    public String[] getSummaryTableUp()
    {
        return container.getCpuidSummary().getParametersListUp();
    }
    
    public String[][] getSummaryTable()
    {
        return container.getCpuidSummary().getParametersList();
    }
    
    public int getSummarySmt()
    {
        return container.getCpuidSummary().getSummarySmt();
    }
    
    public String getEnumeratorName()
    {
        return " [ " + accumVendorName + " ] [ "  + accumCpuName + " ]";
    }
    
    public String[] getEnumeratorFirstTableUp()
    {
        return new String[] { "Parameter", "Value" };
    }
    
    public String[][] getEnumeratorFirstTable()
    {
        return new String[][] 
        {
            { "Processor vendor" , accumVendorName },
            { "Processor model"  , accumCpuName }
        };
    }

    public String[] getEnumeratorSecondTableUp()
    {
        return new String[] { "Parameter", "Value" };
    }
    
    public String[][] getEnumeratorSecondTable()
    {
        return new String[][] {{ "-", "-" }};
    }

    public String[] getProcessorFirstTableUp( int cpuIndex )
    {
        return new String[] { "Parameter", "Value" };
    }
    
    public String[][] getProcessorFirstTable( int cpuIndex )
    {
        return new String[][] 
        {
            { "CPU index at report" , "" + cpuIndex   },
            { "Local APIC ID"       , accumApicId     },
            { "Local x2APIC ID"     , accumX2ApicId   },
            { "Hybrid part"         , accumHybrid     }
        };
    }

    public String[] getProcessorSecondTableUp( int cpuIndex )
    {
        return container.getCpuidDump().getParametersListUp();
    }
    
    public String[][] getProcessorSecondTable( int cpuIndex )
    {
        return container.getCpuidDump().getParametersList();
    }
    
    public HybridReturn getProcessorHybrid( int cpuIndex )
    {
        return accumHybridReturn;
    }
    
    public String getFunctionShortName( int fncIndex )
    {
        ReservedFunctionCpuid[] fns = container.getDetectedFunctions();
        String s = "?";
        if( fncIndex < fns.length )
        {
            return fns[fncIndex].getShortName();
        }
        return s;
    }
    
    public String getFunctionLongName( int fncIndex )
    {
        ReservedFunctionCpuid[] fns = container.getDetectedFunctions();
        String s = "?";
        if( fncIndex < fns.length )
        {
            return fns[fncIndex].getLongName();
        }
        return s;
    }
    
    public String[] getFunctionFirstTableUp( int fncIndex )
    {
        ReservedFunctionCpuid[] fns = container.getDetectedFunctions();
        String[] s = new String[]{ "?", "?" };
        if( fncIndex < fns.length )
        {
            return fns[fncIndex].getParametersListUp();
        }
        return s;
    }
    
    public String[][] getFunctionFirstTable( int fncIndex )
    {
        ReservedFunctionCpuid[] fns = container.getDetectedFunctions();
        String[][] s = new String[][]{{ "?", "?" }};
        if( fncIndex < fns.length )
        {
            return fns[fncIndex].getParametersList();
        }
        return s;
    }

    public String[] getFunctionSecondTableUp( int fncIndex )
    {
        ReservedFunctionCpuid[] fns = container.getDetectedFunctions();
        String[] s = new String[]{ "?", "?" };
        if( fncIndex < fns.length )
        {
            return fns[fncIndex].getRegistersDumpUp();
        }
        return s;
    }
    
    public String[][] getFunctionSecondTable( int fncIndex )
    {
        ReservedFunctionCpuid[] fns = container.getDetectedFunctions();
        String[][] s = new String[][]{{ "?", "?" }};
        if( fncIndex < fns.length )
        {
            return fns[fncIndex].getRegistersDump();
        }
        return s;
    }
}

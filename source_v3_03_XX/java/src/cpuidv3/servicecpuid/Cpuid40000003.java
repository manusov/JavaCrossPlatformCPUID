/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000003h =
Virtual CPUID: hypervisor features.

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T.HYPERVISOR_MICROSOFT;
import static cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T.HYPERVISOR_ORACLE;
import java.util.ArrayList;

class Cpuid40000003 extends ParameterFunctionCpuid
{
Cpuid40000003()
    { setFunction( 0x40000003 ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor feature identification";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "VPRT"    , "VP run time"                        } ,
      { "PRCNT"   , "Partition reference counter"        } ,
      { "BSMSR"   , "Basic synIC MSRs"                   } ,
      { "STMSR"   , "Synthetic timer MSRs"               } ,
      { "APICMSR" , "APIC access MSRs"                   } ,
      { "HCMSR"   , "Hypercall MSRs"                     } ,
      { "AVPMSR"  , "Access virtual processor index MSR" } ,
      { "VSRMSR"  , "Virtual system reset MSR"           } ,
      { "SPMSR"   , "Access statistics pages MSR"        } ,
      { "REFTSC"  , "Reference TSC access"               } ,
      { "GISMSR"  , "Guest idle state MSR"               } ,
      { "TAFMSR"  , "TSC/APIC frequency MSRs"            } ,
      { "GDMSR"   , "Guest debugging MSRs"               } };
private final static String[][] DECODER_EBX =
    { { "CRPART" , "CreatePartitions"                  } ,
      { "ACPART" , "AccessPartitionId"                 } ,
      { "ACMP"   , "AccessMemoryPool"                  } ,
      { "ADJMB"  , "AdjustMessageBuffers"              } ,
      { "PSTMSG" , "PostMessages"                      } ,
      { "SGEV"   , "SignalEvents"                      } ,
      { "CRTPRT" , "CreatePort"                        } ,
      { "CONPRT" , "ConnectPort"                       } ,
      { "ACST"   , "AccessStats"                       } ,
      { "x"      , "Reserved"                          } ,
      { "x"      , "Reserved"                          } ,
      { "DBG"    , "Debugging"                         } ,
      { "CPUM"   , "CPUManagement"                     } ,
      { "CPROF"  , "ConfigureProfiler"                 } ,
      { "STWALK" , "EnableExpandedStackwalking"        } ,  // bit 14
      { "x"      , "Reserved"                          } ,
      { "ACVSM"  , "AccessVSM"                         } ,
      { "ACREG"  , "AccessVpRegisters"                 } ,
      { "x"      , "Reserved"                          } ,
      { "x"      , "Reserved"                          } ,
      { "EXHYPC" , "EnableExtendedHypercalls"          } ,
      { "STVP"   , "StartVirtualProcessor"             } };
private final static Object[][] DECODER_ECX =
    { { "HPET is required to enter C3"  , 4 , 4 } ,
      { "Maximum processor power state" , 3 , 0 } };
private final static String[][] DECODER_EDX =
    { { "MWAIT"   , "MWAIT available"                             } ,  // bit 0
      { "GDBG"    , "Guest debugging support available"           } ,
      { "PERFM"   , "Performance monitor support available"       } ,
      { "CPUDPE"  , "CPU dynamic partitioning events available"   } ,
      { "HYPXMM"  , "Hypercall XMM input parameters available"    } ,
      { "VGIDL"   , "Virtual guest idle state available"          } ,
      { "HYPSLP"  , "Hypervisor sleep state available"            } ,
      { "QNUMA"   , "Query NUMA distance available"               } ,
      { "DTIM"    , "Determine timer frequency available"         } ,
      { "INJMC"   , "Inject synthetic machine check available"    } ,
      { "GCRMSR"  , "Guest crash MSRs available"                  } ,
      { "DBGMSR"  , "Debug MSRs available"                        } ,
      { "NPIEP"   , "NPIEP available"                             } ,
      { "DISHYP"  , "Disable hypervisor available"                } ,
      { "EXTGVA"  , "Extended GVA ranges for flush virt address"  } ,
      { "HYXMMR"  , "Hypercall XMM register return available"     } ,
      { "x"       , "Reserved"                                    } ,  // bit 16
      { "SINTP"   , "Sint polling mode available"                 } ,
      { "HMSRLC"  , "Hypercall MSR lock available"                } ,
      { "DSTIM"   , "Use direct synthetic timers"                 } ,  // bit 19
      { "PAT"     , "PAT register available for VSM"              } ,
      { "BNDCFGS" , "BNDCFGS register available for VSN"          } ,
      { "x"       , "Reserved"                                    } ,  // bit 22
      { "STIM"    , "Synthetic time unhalted timer available"     } ,
      { "x"       , "Reserved"                                    } ,  // bit 24
      { "x"       , "Reserved"                                    } ,  // bit 25
      { "LBR"     , "Intel Last Branch Recording available"       } }; // bit 26

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        DecodeReturn dr;
        String[] interval = new String[] { "", "", "", "", "" };
        ArrayList<String[]> strings;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
            a.addAll( strings );
            a.add( interval );
            // EBX
            strings = decodeBitmap( "EBX", DECODER_EBX, entries[0].ebx );
            a.addAll( strings );
            a.add( interval );
            // ECX
            dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
            int x = dr.values[1];
            String s;
            if ( x <= 3 ) s = String.format( "C%d", x );
            else          s = "?";
            dr.strings.get(1)[4] = s;
            a.addAll( dr.strings );
            a.add( interval );
            // EDX
            strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
            a.addAll( strings );
            }
        return a.isEmpty() ? 
            super.getParametersList() : a.toArray( new String[a.size()][] );
        }
    else
        {
        return super.getParametersList();
        }
    }
}

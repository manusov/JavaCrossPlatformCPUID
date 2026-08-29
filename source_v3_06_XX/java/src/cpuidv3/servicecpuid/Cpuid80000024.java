/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000024h =
AMD secure virtualization features, provided by
AMD Platform Security Processor (PSP).

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid80000024 extends ParameterFunctionCpuid
{
Cpuid80000024() { setFunction( 0x80000024 ); }

@Override String getLongName()
    { return "AMD secure virtualization and PSP features"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Maximum sub-leaf number" , 31 , 0 } }; 
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "SEVLEG"   , "SEV legacy commands (CPU-oriented)"        } ,
      { "SEVTIO"   , "SEV TIO commands (bus-master IO-oriented)" } ,
      { "VIOMMU"   , "Secure virtual IOMMU"                      } ,
      { "TIODKU"   , "SEV-TIO IDE key update host command"       } };
private final static String[][] DECODER_ECX_SUBFUNCTION_0 =
    { { "VLEK"     , "Versioned loaded endorsement key"         } ,
      { "X86SNSH"  , "X86 SNP shutdown allowed"                 } ,
      { "RAPLDIS"  , "RAPL disable function"                    } ,
      { "CIPHIDN"  , "Cipher text hiding for DRAM"              } ,
      { "AES256X"  , "AES 256 XTS guest policy requirement"     } ,
      { "CXLGPOL"  , "CXL guest policy requirement"             } ,
      { "ECCMEMAT" , "ECC memory attestation"                   } ,
      { "PINSGRMP" , "Pre-init non-segmented RMP"               } ,
      { "PISEGRMP" , "Pre-init segmented RMP"                   } ,
      { "VALSCHK"  , "Validating alias check mitigation"        } ,
      { "x"        , "Reserved" } ,   // bit 10 reserved
      { "HGATT"    , "Host request to obtain guest attestation" } ,
      { "SECTSC"   , "Secure time stamp counter info command"   } ,
      { "VERMIT"   , "Verify mitigation command"                } ,
      { "x"        , "Reserved" } ,   // bit 14 reserved
      { "CSVCEK"   , "Chip-secret VCEK derivation"              } ,
      { "PGSWAP"   , "Guest page swap commands"                 } ,
      { "SEVESSNP" , "SEV-ES and SNP at the same time"          } ,
      { "PKITR"    , "Public key infrastructure transfer"       } ,
      { "ETCB"     , "Extended trusted computing base"          } ,
      { "GETINFO"  , "Get TPM info host command"                } ,
      { "x"        , "Reserved" } ,   // bit 21 reserved
      { "PGSWAP"   , "Page swap disable guest policy"           } ,
      { "x"        , "Reserved" } ,   // bit 23 reserved
      { "x"        , "Reserved" } ,   // bit 24 reserved
      { "x"        , "Reserved" } ,   // bit 25 reserved
      { "x"        , "Reserved" } ,   // bit 26 reserved
      { "x"        , "Reserved" } ,   // bit 27 reserved
      { "x"        , "Reserved" } ,   // bit 28 reserved
      { "x"        , "Reserved" } ,   // bit 29 reserved
      { "x"        , "Reserved" } ,   // bit 30 reserved
      { "x"        , "Reserved" } };  // bit 31 reserved
private final static String[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "CPUIDRQ"   , "CPUID request guest SNP message"         } ,
      { "KEYREQ"    , "Key request guest SNP message"           } ,
      { "REPREQ"    , "Report request guest SNP message"        } ,
      { "EXPREQ"    , "Export request guest SNP message"        } ,
      { "IMPREQ"    , "Import request (reserved)"               } ,
      { "ABSREQ"    , "Absorb request (reserved)"               } ,
      { "VMRKREQ"   , "VMRK request guest SNP message"          } ,
      { "ABSNREQ"   , "Absorb NOMA request guest SNP message"   } ,
      { "TSCIREQ"   , "TSC info request guest SNP message"      } ,
      { "TDIIREQ"   , "TDI info request guest TIO message"      } ,
      { "MMIOVREQ"  , "MMIO validate request guest TIO message" } ,
      { "MMIOCREQ"  , "MMIO config request guest TIO message"   } ,
      { "SDTEWREQ"  , "SDTE write request guest TIO message"    } ,
      { "CVIOMREQ"  , "Config VIOMMU request guest SNP message" } ,
      { "SETMPREQ"  , "Setup mapping request guest TIO message" } ,
      { "GETCSREQ"  , "Get CSR request guest SNP message"       } ,
      { "x"        , "Reserved" } ,   // bit 16 reserved
      { "x"        , "Reserved" } ,   // bit 17 reserved
      { "TPMIREQ"   , "TPM info request guest SNP message"      } ,
      { "x"        , "Reserved" } ,   // bit 19 reserved
      { "x"        , "Reserved" } ,   // bit 20 reserved
      { "x"        , "Reserved" } ,   // bit 21 reserved
      { "x"        , "Reserved" } ,   // bit 22 reserved
      { "x"        , "Reserved" } ,   // bit 23 reserved
      { "x"        , "Reserved" } ,   // bit 24 reserved
      { "x"        , "Reserved" } ,   // bit 25 reserved
      { "x"        , "Reserved" } ,   // bit 26 reserved
      { "x"        , "Reserved" } ,   // bit 27 reserved
      { "x"        , "Reserved" } ,   // bit 28 reserved
      { "x"        , "Reserved" } ,   // bit 29 reserved
      { "x"        , "Reserved" } ,   // bit 30 reserved
      { "x"        , "Reserved" } };  // bit 31 reserved

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    // Subfunction 0.
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0.
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        int maxSubFunction = dr.values[0];
        a.addAll( dr.strings );
        a.add( interval );
        // EBX, subfunction 0.
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        a.add( interval );
        // ECX, subfunction 0.
        strings = decodeBitmap
            ( "ECX", DECODER_ECX_SUBFUNCTION_0, entries[0].ecx );
        a.addAll( strings );
        // subfunction 1
        if ( ( entries.length > 1 )&&( maxSubFunction > 0 )&&
             ( entries[1].subfunction == 1 ) )
            {
            // EAX, subfunction 1.
            strings = decodeBitmap
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.add( interval );
            a.addAll( strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}

/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000X04h =
Xen hypervisor APIC and IOMMU configuration
(Xen hypervisor-specific Virtual CPUID).
Note this class must be dynamically add to functions list if hypervisor
vendor = Xen, otherwise function number can conflict with existed.
Note function number can be selected from base 40000004h with 100h increments,
for example 40000104h, 40000204h, ...
to prevent conflict with other hypervisors.

*/

package dumploader.cpuid;

import java.util.ArrayList;

class Cpuid40000X04 extends ParameterFunctionCpuid
{
Cpuid40000X04( int index )
    { 
    setFunction( 0x40000004 + ( index << 8 ) ); 
    }

@Override String getLongName()
    { return "Xen hypervisor APIC and IOMMU configuration"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "V APIC"    , "Virtualized APIC registers" } ,
      { "V X2APIC"  , "Virtualized x2APIC access" } ,
      { "IOMMU"     , "IOMMU mappings for other domain memory" } ,
      { "VCPUID"    , "VCPU ID is valid" } ,
      { "DOMID"     , "Domain ID is valid" } };
private final static Object[][] DECODER_EBX =
    { { "VCPU ID"   , 31 , 0 } };
private final static Object[][] DECODER_ECX =
    { { "Domain ID"   , 31 , 0 } };

@Override String[][] getParametersList()
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
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}

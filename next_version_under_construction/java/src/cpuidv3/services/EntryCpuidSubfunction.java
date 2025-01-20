/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

28-byte entry definition for binary CPUID dump interpreting.
Original size is 32 bytes, at Java CPUID.

*/

package cpuidv3.services;

public class EntryCpuidSubfunction
{
    public final int function, subfunction, pass, eax, ebx, ecx, edx;

    public EntryCpuidSubfunction
        ( int f, int s, int p, int a, int b, int c, int d )
    {
        function = f;
        subfunction = s;
        pass = p;
        eax = a;
        ebx = b;
        ecx = c;
        edx = d;
    }
}

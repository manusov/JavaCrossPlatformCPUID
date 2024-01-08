/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
32-byte entry definition for binary CPUID dump interpreting.
*/

package cpuidv2.cpuidfunctions;

class EntryCpuid 
{
int id, function, subfunction, pass, eax, ebx, ecx, edx;
}

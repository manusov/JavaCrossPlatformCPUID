/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000019h = AES Key Locker feature enumeration.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid00000019 extends ParameterFunctionCpuid
{
Cpuid00000019()
    { setFunction( 0x00000019 ); }

@Override String getLongName()
    { return "AES Key Locker feature enumeration"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "KL AT CPL0" , "KL restriction of CPL0 only" } ,
      { "KL NO ENC"  , "KL restriction of no-encrypt" } ,
      { "KL NO DEC"  , "KL restriction of no-decrypt" } };
private final static String[][] DECODER_EBX =
    { { "KL ENABLE"  , "AES KL instructions are fully enabled by firmware or OS" } ,
      { "x"          , "Reserved" } ,
      { "WIDE KL"    , "AES wide Key Locker instructions" } ,
      { "x"          , "Reserved" } ,
      { "IWK BACK"   , "IWKeyBackup MSR, can backing up the internal wrapping key" } };
private final static String[][] DECODER_ECX =
    { { "NO BACK"    , "Can use NoBackup parameter for LOADIWKEY" } ,
      { "IWK RND"    , "Can use KeySource=1, internal wrapping key randomization" } };

@Override String[][] getParametersList()
    {
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        strings = decodeBitmap
            ( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( strings );
        a.add( interval );
        // EBX
        strings = decodeBitmap
            ( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( strings );
        a.add( interval );
        // ECX
        strings = decodeBitmap
            ( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}

//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Kernel part:
// Input-Output Parameters Blocks (IOPB) support, data conversion methods
// between QWORD array and BYTE array, between QWORD array and STRING.

// For REMOTE and FILE modes as alternative of LOCAL mode:
// this static library module still used during REMOTE/FILE modes.

package arch1.kernel;

public class IOPB 
{

// Pack BYTE array to QWORD array, transmit data to IPB before native call
// Each 8 source BYTES packed to one destination QWORD
// Parm#1 = Source BYTE array
// Parm#2 = Destination QWORD array
// Parm#3 = Destination array base, units = qwords
// Parm#4 = Destination array length, units = qwords
public static void transmitBytes
    ( byte[] bytearray, long[] longipb, int base, int length )
    {
    int n = length;
    long x, y=0;
    int k=0;
    for (int i=0; i<n; i++) { longipb[base+i]=0; }
    for (int i=0; i<n; i++)
        {
        for(int j=0; j<8; j++)
           {
           x = bytearray[k] & 0xFF;
           k++;
           x = x << 56;
           y = y >>> 8;
           y = y + x;
           }
        longipb[base+i]=y;
        y=0;
        }
    }

// Unpack QWORD array to BYTE array, receive data from OPB after native call
// Each source QWORD unpacked to destination 8 BYTEs
// Result = Destination BYTE array
// Parm#1 = Source QWORD array
// Parm#2 = Source array base, units = qwords
// Parm#4 = Source array length, units = qwords
public static byte[] receiveBytes
    ( long[] longopb, int base, int length )
    {
    int n = length;
    int m = n*8;
    long x, y;
    int k=0;
    byte[] bytearray = new byte[m];
    for (int i=0; i<m; i++) { bytearray[i]=0; }
    for (int i=0; i<n; i++)
        { 
        x = longopb[base+i];
        for (int j=0; j<8; j++)
            {
            y = x & 0xFF;
            x = x >>> 8;
            bytearray[k] = (byte)y;
            k++;
            }
        }
    return bytearray;
    }
    
// Pack STRING to QWORD array, transmit data to IPB before native call
// Each 8 source ASCII chars packed to one destination QWORD
// Support only 8 low bits at UNICODE chars
// Parm#1 = Source STRING
// Parm#2 = Destination QWORD array
// Parm#3 = Destination array base, units = qwords
// Parm#4 = Destination array length, units = qwords
public static void transmitString( String s, long[] ipb, int base, int length )
    {
    int sl = s.length();
    byte[] array = new byte[1024];
    for ( int i=0; i<1024; i++ ) { array[i]=0;}
    for ( int i=0; i<sl; i++  ) { array[i] = (byte) s.charAt(i); }
    IOPB.transmitBytes( array, ipb, base, length );
    }

// Unpack QWORD array to BYTE array, receive data from OPB after native call
// Each source QWORD unpacked to 8 destination ASCII chars
// Support only 8 low bits at UNICODE chars
// Result = Destination String
// Parm#1 = Source QWORD array
// Parm#2 = Source array base, units = qwords
// Parm#4 = Source array length, units = qwords
public static String receiveString( long[] opb, int base, int length )
    {
    String s1;
    StringBuilder s2 = new StringBuilder();
    byte[] array = IOPB.receiveBytes(opb, base, length);
    for (int i=0; i<array.length; i++)
        {
        if ( array[i]==0 ) break;
        s2.append( (char)array[i] );
        }
    s1 = s2.toString();
    return s1;        
    }

}

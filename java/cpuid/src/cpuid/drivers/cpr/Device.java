/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Interface for CPR model, entire Device submodel.
Declares CPR device interface.
TODO: 
add separate brief and full (short and long) names for pins and registers.
*/

package cpuid.drivers.cpr;

public interface Device 
{

void setBinary(long[] x);       // copy binary dump from device to CPR module
long[] getBinary();             // copy binary dump from CPR module to device
boolean initBinary();           // initialize binary level and return validity
boolean parseBinary();          // parse binary dump and create text data

String getSummaryName();              // get name for device summary table
String[] getSummaryUp();              // get up string for device summary table
String[][] getSummaryText();          // get content for device summary table

String getDumpName();                 // get name for device dump table
String[] getDumpUp();                 // get up string for device dump table
String[][] getDumpText();             // get content for device dump table

int getCommandsCount();               // get number of commands per device
String getCommandShortName(int x);    // get short name of selected command
String getCommandLongName(int x);     // get long name of selected command
boolean getCommandSupported(int x);   // check selected command supported
String[] getCommandUp1(int x);        // get command result table 1 up string
String[][] getCommandText1(int x);    // get command result table 1 content
String[] getCommandUp2(int x);        // get command result table 2 up string
String[][] getCommandText2(int x);    // get command result table 2 content

int getPinsCount();                   // get number of pins per device
String getPinName(int x);             // get name of selected pin
boolean getPinSupported(int x);       // check selected pin supported
String[] getPinUp1(int x);            // get pin state table 1 up string
String[][] getPinText1(int x);        // get pin state table 1 content
String[] getPinUp2(int x);            // get pin state table 2 up string
String[][] getPinText2(int x);        // get pin state table 2 content

int getRegistersCount();              // get number of registers per device
String getRegisterName(int x);        // get name of selected register
boolean getRegisterSupported(int x);  // check selected register supported
String[] getRegisterUp1(int x);       // get register state table 1 up string
String[][] getRegisterText1(int x);   // get register state table 1 content
String[] getRegisterUp2(int x);       // get register state table 2 up string
String[][] getRegisterText2(int x);   // get register state table 2 content
    
}

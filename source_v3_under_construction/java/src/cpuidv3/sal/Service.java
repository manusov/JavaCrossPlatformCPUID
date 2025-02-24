/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for services, make services functionality regular.

*/

package cpuidv3.sal;

class Service 
{
    final SAL sal;
    Service( SAL s ) { sal = s; }
    
    String getTableName()                { return null;  }
    String[] getTableUp()                { return null;  }
    String[][] getTableData()            { return null;  }
    String[][] getSummaryTablePart()     { return null;  }
    
    boolean internalLoadBinaryData()     { return true;  }
    boolean internalLoadNonAffinized()   { return false; }
    void clearBinaryData()               {               }
    long[][] getBinaryData()             { return null;  }
    void setBinaryData( long[][] data )  {               }

    void printSummaryReport()            {               }
}


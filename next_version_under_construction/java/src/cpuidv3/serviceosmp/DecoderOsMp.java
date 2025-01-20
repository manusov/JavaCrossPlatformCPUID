/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for Decoder classes, used for interpreting topology information,
provided by OS API. WINDOWS and Linux topology interpreter variants is CHILD
classes.

*/

package cpuidv3.serviceosmp;

import cpuidv3.services.BinaryToTextDecoder;

public class DecoderOsMp extends BinaryToTextDecoder
{
    protected String[]     shortNames;
    protected String[]     longNames;
    protected String[][]   listsUps;
    protected String[][][] lists;
    protected String[][]   dumpsUps;
    protected String[][][] dumps;
    
    /*
    BinaryToTextDecoder fields usage for this class:
    shortNames = sorting keys and tree leafs left names.
    longNames  = tree leafs right names.
    listsUp    = table up lines for tables, selected depend on tree selection.
    lists      = table data lines for tables, selected depend on tree selection.
    dumpsUp    = dump up line, here common dump table for all lists.
    dumpsUp    = dump data lines, here common dump table for all lists.
    */
        
    @Override public String[] getShortNames()  { return shortNames; }
    @Override public String[] getLongNames()   { return longNames;  }
    @Override public String[][] getListsUps()  { return listsUps;   }
    @Override public String[][][] getLists()   { return lists;      }
    @Override public String[][] getDumpsUps()  { return dumpsUps;   }
    @Override public String[][][] getDumps()   { return dumps;      }
    
    public String[][] getSummaryAddStrings()         { return null; }
    
    protected final static String[] DUMP_UP = 
        { "Offset",  "x0", "x1", "x2", "x3", "x4", "x5", "x6", "x7", 
                     "x8", "x9", "xA", "xB", "xC", "xD", "xE", "xF"  };
    protected final static String[] LIST_UP = { "Parameter" , "Value" };
    protected final static String[] LIST_TABLE_BEGIN_WINDOWS =
        { "Offset",  "Raw index", "Object type", "Affinity mask" };
    protected final static String[] LIST_TABLE_BEGIN_LINUX =
        { "Raw index", "Object type", "Affinity mask" };
    
    // SN = Short names. Public access because also used at PanelSmp.java.
    public final static String SN_PROCESSOR_CORE    = "CORE#";
    public final static String SN_NUMA_NODE         = "NODE#";
    public final static String SN_CACHE             = "CACHE#";
    public final static String SN_PROCESSOR_PACKAGE = "PACKAGE#";
    public final static String SN_PROCESSOR_GROUP   = "GROUP#";
    public final static String SN_PROCESSOR_DIE     = "DIE#";
    public final static String SN_NUMA_NODE_EX      = "NODEEX#";
    public final static String SN_PROCESSOR_MODULE  = "MODULE#";
    public final static String SN_UNKNOWN           = "?";
    
    // LN = Long names.
    protected final static String LN_PROCESSOR_CORE    = "Processor core";
    protected final static String LN_NUMA_NODE         = "Numa node";
    protected final static String LN_CACHE             = "Cache";
    protected final static String LN_PROCESSOR_PACKAGE = "Processor package";
    protected final static String LN_PROCESSOR_GROUP   = "Processor group";
    protected final static String LN_PROCESSOR_DIE     = "Processor die";
    protected final static String LN_NUMA_NODE_EX      = "Numa node extended";
    protected final static String LN_PROCESSOR_MODULE  = "Processor module";
    protected final static String LN_UNKNOWN           = "Unknown";

    protected final static String[] N_CACHE_TYPES_WINDOWS =
        { "Unified", "Instruction", "Data", "Trace", "Unknown type" };

    protected final static String[] N_CACHE_TYPES_LINUX =
        { "Data", "Instruction", "Unified", "Trace", "Unknown type" };
    
    protected final static String S_METHOD_NAME =
        "Platform enumeration method";
    
    protected final static String S_CPU = "Cores and threads by OS API";
    protected final static String S_L1D = "L1 data";
    protected final static String S_L1I = "L1 instruction";
    protected final static String S_L1T = "L1 trace";
    protected final static String S_L2U = "L2 unified";
    protected final static String S_L3U = "L3 unified";
    protected final static String S_DOMAINS = "NUMA domains";
    protected final static String S_GROUPS = "Processor groups";
    protected final static String S_PACKAGES = "Processor packages";
    
    protected int nCore;
    protected int nNode;
    protected int nCache;
    protected int nPackage;
    protected int nGroup;
    protected int nDie;
    protected int nNodeEx;
    protected int nModule;
    
    protected int nThread;
}


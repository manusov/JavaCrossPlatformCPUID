/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Interconnector class for application functionality panels.
Panels provided as leafs of tabbed pane, panels data is decentralized.
Decentralization of panels data is better for scalability. 
If panels quantity too big, one central class must contain too many data.
For decentralized model, classes sizes balance is better. (?)

*/

package dumploader.guipanels;

import dumploader.DumpLoader;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;

public class ApplicationEnumerator 
{
    // List of applications.  
    // THIS REJECTED: Note some applications can has >1 leafs.
    private final ApplicationPanel[] PANELS = 
    { 
        new PanelSummary(),
        new PanelDetails(),
    };
//    
    // Order control array for right accumulation of system information.
    private final int[] PANEL_SYSINFO_ORDER =
    {
        1, // Details first, because multiprocessing enumeration.
        0
    };
//    
    private final int PANELS_COUNT = PANELS.length;
    
    public String[] getTabNames()
    {
        String[] names = new String[PANELS_COUNT];
        for(int i=0; i<PANELS_COUNT; i++)
        {
            names[i] = PANELS[i].getPanelName();
        }
        return names;
    }

    public Icon[] getTabIcons()
    {
        boolean status = true;
        String path = DumpLoader.getResourcePackage();
        Icon[] icons = new Icon[PANELS_COUNT];
        for(int i=0; (i < PANELS_COUNT) && status; i++)
        {
            try 
            {
                Icon icon = new javax.swing.ImageIcon( getClass().
                        getResource( path + PANELS[i].getPanelIcon() ) );
                icons[i] = icon;
            }
            catch ( Exception e ) 
            {
                status = false; 
            }
        }
        
        if( status )
        {
            return icons;
        }
        else
        {
            return new Icon[PANELS_COUNT];
        }
    }

    public JPanel[] getTabPanels()
    {
        JPanel[] panels = new JPanel[PANELS_COUNT];
        for(int i=0; i<PANELS_COUNT; i++)
        {
            panels[i] = PANELS[i].getPanel();
        }
        return panels;
    }
    
    public boolean[] getTabActives()
    {
        boolean[] flags = new boolean[PANELS_COUNT];
        for(int i=0; i<PANELS_COUNT; i++)
        {
            flags[i] = PANELS[i].getPanelActive();
        }
        return flags;
    }
    
    public String[] getTabTips()
    {
        String[] tips = new String[PANELS_COUNT];
        for(int i=0; i<PANELS_COUNT; i++)
        {
            tips[i] = PANELS[i].getPanelTip();
        }
        return tips;
        
    }
    
    public void buildTabPanels( boolean physical )
    {
/*        
        for( ApplicationPanel ap : PANELS )
        {
            ap.rebuildPanel( physical );
        }
*/
//
        for( int i=0; i<PANELS_COUNT; i++ )
        {
            int j = PANEL_SYSINFO_ORDER[i];
            PANELS[j].rebuildPanel( physical );
        }
//        
    }

    private final static int INDEX_CPUID_SUMMARY = 0;

    public AbstractTableModel[] getTabModels() 
    { 
        ArrayList<AbstractTableModel> models = new ArrayList<>();
        for( int i=0; i<PANELS_COUNT; i++ )
        {
            ApplicationPanel ap = PANELS[i];
            AbstractTableModel[] groupModels = ap.getPanelModels();
            if( groupModels != null )
            {
                for( AbstractTableModel tabModel : groupModels )
                {
                    if( tabModel != null )
                    {
                        models.add( tabModel );
                    }
                }
            }
        }
        return models.isEmpty() ? null : 
            models.toArray( new AbstractTableModel[models.size()] );
    }
/* 
    public AbstractTableModel[] getReportThisTabModels( int index )
    {
        AbstractTableModel[] thisReportModels = null;
        if (( index < PANELS_COUNT )&&( index >= 0 ))
        {
            ApplicationPanel ap = PANELS[index];
            thisReportModels = ap.getReportThisModels();
        }
        return thisReportModels;
    }
*/    
    public void rebuildAfterCpuidReload
        ( JTabbedPane tabbedPane, boolean getPhysical )
    {
        JPanel[] panels = getTabPanels();
        
        // Remove current GUI objects from each panel.
        for( JPanel panel : panels )
        {
            panel.removeAll();
        }
        
        // Create new GUI objects for each panel.
/*        
        for( ApplicationPanel ap : PANELS )
        {
            ap.rebuildPanel( getPhysical );
        }
*/
//
        for( int i=0; i<PANELS_COUNT; i++ )
        {
            int j = PANEL_SYSINFO_ORDER[i];
            PANELS[j].rebuildPanel( getPhysical );
        }
//
        // Select panel 0 = Summary.
        tabbedPane.setSelectedIndex( INDEX_CPUID_SUMMARY );

        // Refresh GUI after changes, for panels.
        for( JPanel panel : panels )
        {
            panel.revalidate();
            panel.repaint();
        }

        // Refresh GUI after changes, for tabbed pane.
        tabbedPane.revalidate();
    }
}

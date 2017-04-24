//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Viewer model 3, one table.

package arch1.applications.guipanels;

import javax.swing.*;
import javax.swing.table.*;

public class Viewer3 extends ViewPanel 
{
private final JPanel p;                   // complex panel
private final JScrollPane sp1;            // scroll panel
private final JTable table;               // table
private final AbstractTableModel atm;     // tables models
private final BoxLayout bl;               // layout manager
// private final SpringLayout sl1, sl2;
// REJECTED: replaced from BoxLayout to SpringLayout
private static DefaultTableCellRenderer mRenderer;  // this for centering

public Viewer3
    ( int x, int y, AbstractTableModel z1, boolean centeringOption )
    {
    atm=z1;
//---------- Built panel components ----------
    table = new JTable(atm);
    SetupTableSize.optimizeColumnsWidths( table, x );
    sp1 = new JScrollPane(table);
//---------- Centering table(s) ----------
    if(centeringOption)
        {
        mRenderer = new DefaultTableCellRenderer();
        mRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i=0; i<table.getColumnCount(); i++)
            { table.getColumnModel().getColumn(i).setCellRenderer(mRenderer); }
        }

//---------- Built panel and set layout ----------
    p = new JPanel();
    bl = new BoxLayout(p, BoxLayout.X_AXIS);
    p.setLayout(bl);
    // sl1 = new SpringLayout();
    // p = new JPanel(sl1);
    // SetupLayout.springCenter( sl1, p, sp1, 0, 0, 0, 0 );
    // REJECTED: replaced from BoxLayout to SpringLayout
    p.add(sp1);
    
    //sl2 = new SpringLayout();
    //sp1.setLayout(sl2);
    //SetupLayout.springCenter( sl2, sp1, table, 0, 0, 0, 0 );
    
    }

//---------- Return panel ----------    
@Override public JPanel getP()
    {
    return p;
    }

//---------- Return components array ----------
@Override public JComponent[] getComponents()
    {
    JComponent[] x = new JComponent[1];
    x[0] = table;
    return x;    
    }

}


/*
// BEFORE CHANGE BOX LAYOUT TO SPRING LAYOUT

package arch1.applications.guipanels;

import javax.swing.*;
import javax.swing.table.*;

public class Viewer3 extends ViewPanel 
{
private final JPanel p;                   // complex panel
private final JScrollPane sp1;            // scroll panel
private final JTable table;               // table
private final AbstractTableModel atm;     // tables models
private final BoxLayout bl;               // layout manager
// private final SpringLayout sl;
// replaced from BoxLayout to SpringLayout
private static DefaultTableCellRenderer mRenderer;  // this for centering

public Viewer3
    ( int x, int y, AbstractTableModel z1, boolean centeringOption )
    {
    atm=z1;
//---------- Built panel components ----------
    table = new JTable(atm);
    SetupTableSize.optimizeColumnsWidths( table, x );
    sp1 = new JScrollPane(table);
//---------- Centering table(s) ----------
    if(centeringOption)
        {
        mRenderer = new DefaultTableCellRenderer();
        mRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i=0; i<table.getColumnCount(); i++)
            { table.getColumnModel().getColumn(i).setCellRenderer(mRenderer); }
        }

//---------- Built panel and set layout ----------
    p = new JPanel();
    bl = new BoxLayout(p, BoxLayout.X_AXIS);
    p.setLayout(bl);
    // sl = new SpringLayout();
    // p = new JPanel(sl);
    // SetupLayout.springCenter( sl, p, sp1, 0, 0, 0, 0 );
    // replaced from BoxLayout to SpringLayout
    p.add(sp1);
    }

//---------- Return panel ----------    
@Override public JPanel getP()
    {
    return p;
    }

//---------- Return components array ----------
@Override public JComponent[] getComponents()
    {
    JComponent[] x = new JComponent[1];
    x[0] = table;
    return x;    
    }

}


*/
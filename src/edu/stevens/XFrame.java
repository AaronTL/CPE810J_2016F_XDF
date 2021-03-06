
/**
 * @author: Lei Tang， Sihan Wang, Jingting Zhang
 * 
 * add Menu to realize basic functions CREATE, OPEN, SAVE, EXIT. 
 * add Button to realize the basic functions above. 
 * In addition, add undo, cut, paste, searchButton,replaceButton if time permits.
 *
 */



package edu.stevens;
 import java.awt.*;
 import java.awt.event.*;
 import java.awt.datatransfer.*;
 import javax.swing.*;
import javax.swing.plaf.FontUIResource;
 import javax.swing.undo.*;
 import javax.swing.event.*;
 import java.util.*;
 import java.io.*;
 
// import java.text.*;
// import javax.swing.border.*;
// import javax.swing.text.*;
 
 
 

//define the external features of XDF(window/frame/UI)
public class XFrame extends JFrame implements ActionListener,DocumentListener
{
	//define menu bar
	JMenu fileMenu,editMenu,formatMenu,viewMenu,helpMenu,pageMenu,setMenu;
	//Right click item 
	JPopupMenu popupMenu;
	JMenuItem popupMenu_Undo,popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,popupMenu_Delete,popupMenu_SelectAll;
	//items of FILE
	JMenuItem fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,fileMenu_Print,fileMenu_Exit;
	//items of EDIT
	JMenuItem editMenu_Undo,editMenu_Cut,editMenu_Copy,editMenu_Paste,editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_Replace,editMenu_GoTo,editMenu_SelectAll,editMenu_TimeDate;
	//items of FORMAT
	JCheckBoxMenuItem formatMenu_LineWrap;
	JMenuItem formatMenu_Font;
	//item of VIEW
	JCheckBoxMenuItem viewMenu_Status;
	//item of HELP
	JMenuItem helpMenu_HelpTopics,helpMenu_AboutXFrame;
	//item of SET	
	JMenuItem C1,C2,C3,C4,C7,I1,I2,I3,I4;
	//item of PAGE
	JMenuItem pageMenu_pageSetUp;
	//text area
	JTextArea editArea, editArea1, editArea2;
	//JPanel to contain the textarea in double page option
	JPanel doublePanel;
	//Pane to contain the textarea in single page option
	JScrollPane singlePane;
	//status label
	JLabel statusLabel;
	//default Pagesetup
	public pageSetup PageSetup;
	//clipboard
	Toolkit toolkit=Toolkit.getDefaultToolkit();
	Clipboard clipBoard=toolkit.getSystemClipboard();
	//Create undo manager 
	protected UndoManager undo=new UndoManager();
	protected UndoableEditListener undoHandler=new UndoHandler();
	//other things
	String oldValue,oldValue1,oldValue2;//store the original content of the edit area for comparing text changes 
	boolean isNewFile=true;//Whether the new file (not saved) 
	File currentFile;//Current file name 
	//Constructor start 
	public XFrame()
	{	
		super("eXtreme Document Format");
		//Change system default font 
		Font font = new Font("Dialog",Font.BOLD,14);
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, font);
			}
		}
		//Create menu bar 
		JMenuBar menuBar=new JMenuBar();
	
		//Create the file menu and menu items and register the event listener
		fileMenu=new JMenu("FILE");
		fileMenu.setMnemonic('F');//shortcut keys 

		fileMenu_New=new JMenuItem("NEW");
		fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
		fileMenu_New.addActionListener(this);

		fileMenu_Open=new JMenuItem("OPEN");
		fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		fileMenu_Open.addActionListener(this);

		fileMenu_Save=new JMenuItem("SAVE");
		fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		fileMenu_Save.addActionListener(this);

		fileMenu_SaveAs=new JMenuItem("SAVE AS");
		fileMenu_SaveAs.addActionListener(this);
		

		fileMenu_Print=new JMenuItem("PRINT");
		fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK)); 
		fileMenu_Print.addActionListener(this);

		fileMenu_Exit=new JMenuItem("EXIT");
		fileMenu_Exit.addActionListener(this);

		//Create Edit menu and menu item and register the event listener 
		editMenu=new JMenu("EDIT");
		editMenu.setMnemonic('E');//shortcut key
		
		//When selecting the edit menu, set the availability of cut, copy, paste, delete and other functions 
		editMenu.addMenuListener(new MenuListener()
		{	public void menuCanceled(MenuEvent e)//Call to cancel the menu 
			{	checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete and other functions 
			}
			public void menuDeselected(MenuEvent e)//Call to cancel the selection of a menu
			{	checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete and other functions 
			}
			public void menuSelected(MenuEvent e)//Call when selecting a menu 
			{	checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete and other functions 
			}
		});

		editMenu_Undo=new JMenuItem("UNDO");
		editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
		editMenu_Undo.addActionListener(this);
		editMenu_Undo.setEnabled(false);

		editMenu_Cut=new JMenuItem("CUT");
		editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
		editMenu_Cut.addActionListener(this);

		editMenu_Copy=new JMenuItem("COPY");
		editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		editMenu_Copy.addActionListener(this);

		editMenu_Paste=new JMenuItem("PASTE");
		editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
		editMenu_Paste.addActionListener(this);

		editMenu_Delete=new JMenuItem("DELETE");
		editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		editMenu_Delete.addActionListener(this);

		editMenu_Find=new JMenuItem("FIND");
		editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
		editMenu_Find.addActionListener(this);

		editMenu_FindNext=new JMenuItem("FIND NEXT");
		editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
		editMenu_FindNext.addActionListener(this);

		editMenu_Replace = new JMenuItem("REPLACE",'R'); 
		editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK)); 
		editMenu_Replace.addActionListener(this);

		editMenu_GoTo = new JMenuItem("GOTO",'G'); 
		editMenu_GoTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK)); 
		editMenu_GoTo.addActionListener(this);

		editMenu_SelectAll = new JMenuItem("SELECT ALL",'A'); 
		editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK)); 
		editMenu_SelectAll.addActionListener(this);

		editMenu_TimeDate = new JMenuItem("TIME/DATE",'D');
		editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));
		editMenu_TimeDate.addActionListener(this);

		//Create the format menu and menu item and register the event listener
		formatMenu=new JMenu("FORMAT");
		formatMenu.setMnemonic('O');//shortcut key ALT+O

		formatMenu_LineWrap=new JCheckBoxMenuItem("LINE WRAP");
		formatMenu_LineWrap.setMnemonic('W');//shortcut key ALT+W
		formatMenu_LineWrap.setState(true);
		formatMenu_LineWrap.addActionListener(this);

		formatMenu_Font=new JMenuItem("FONT");
		formatMenu_Font.addActionListener(this);

		//Create the view menu and menu item and register the event listener
		viewMenu=new JMenu("VIEW");
		viewMenu.setMnemonic('V');//shortcut key ALT+V

		viewMenu_Status=new JCheckBoxMenuItem("STATUS");
		viewMenu_Status.setMnemonic('S');//shortcut key ALT+S
		viewMenu_Status.setState(true);
		viewMenu_Status.addActionListener(this);

		//Create a help menu and menu item and register the event listener 
		helpMenu = new JMenu("HELP");
		helpMenu.setMnemonic('H');//shortcut key ALT+H

		helpMenu_HelpTopics = new JMenuItem("HELP DETAILS"); 
		helpMenu_HelpTopics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
		helpMenu_HelpTopics.addActionListener(this);

		helpMenu_AboutXFrame = new JMenuItem("ABOUT"); 
		helpMenu_AboutXFrame.addActionListener(this);
		

		/****************************************/
		//Create a set menu and menu item and register the event listener
		ChangeColorAction action = new ChangeColorAction();
		ChangeFontAction action1 = new ChangeFontAction();
		setMenu = new JMenu("SETTINGS");
		setMenu.setMnemonic('s');//shortcut key
		
		JMenu setMenu_Color = new JMenu("BGCOLOR");
		JMenu setMenu_Font = new JMenu("MENUFONT");
		setMenu_Color.addActionListener(this);
		setMenu_Font.addActionListener(this);
		
		C1 = new JMenuItem("Candy Pink");
		C1.setBackground(Color.pink);
		C1.addActionListener(action);//Haven't finished yet,tbc
		C2 = new JMenuItem("Grass Green");
		C2.setBackground(Color.getHSBColor(238,238,238));
		C2.addActionListener(action);
		C3 = new JMenuItem("Fairy Blue");
		C3.setBackground(Color.getHSBColor(91,91,91));
		C3.addActionListener(action);
		C4 = new JMenuItem("Default");
		C4.setBackground(Color.getHSBColor(202,86,99));
		C4.addActionListener(action);
		
		//Haven't finished yet,tbc
		I1 = new JMenuItem("BOLD");
		I1.addActionListener(action1);
		I2 = new JMenuItem("ITALIC");
		I2.addActionListener(action1);
		I3 = new JMenuItem("ROMAN_BASELINE");
		I3.addActionListener(action1);
		I4 = new JMenuItem("DEFAULT");
		I4.addActionListener(action1);
		/*****************************************************/
		

		//Create a Page menu
		pageMenu =new JMenu("PAGE");
		pageMenu.setBackground(Color.white);
		pageMenu_pageSetUp=new JMenuItem("PAGE SET");
		pageMenu_pageSetUp.addActionListener(this);


		//Add the "file" menu and menu item to the menu bar 
		menuBar.add(fileMenu); 
		fileMenu.add(fileMenu_New); 
		fileMenu.add(fileMenu_Open); 
		fileMenu.add(fileMenu_Save); 
		fileMenu.add(fileMenu_SaveAs); 
		fileMenu.addSeparator();		//gap line
		fileMenu.add(fileMenu_Print); 
		fileMenu.addSeparator();		//gap line
		fileMenu.add(fileMenu_Exit); 

		//Add the "edit" menu and menu item to the menu bar
		menuBar.add(editMenu); 
		editMenu.add(editMenu_Undo);  
		editMenu.addSeparator();		//gap line
		editMenu.add(editMenu_Cut); 
		editMenu.add(editMenu_Copy); 
		editMenu.add(editMenu_Paste); 
		editMenu.add(editMenu_Delete); 
		editMenu.addSeparator(); 		//gap line
		editMenu.add(editMenu_Find); 
		editMenu.add(editMenu_FindNext); 
		editMenu.add(editMenu_Replace);
		editMenu.add(editMenu_GoTo); 
		editMenu.addSeparator();  		//gap line
		editMenu.add(editMenu_SelectAll); 
		editMenu.add(editMenu_TimeDate);

		//Add the "format" menu and menu item to the menu bar		
		menuBar.add(formatMenu); 
		formatMenu.add(formatMenu_LineWrap); 
		formatMenu.add(formatMenu_Font);

		//Add the "view" menu and menu item to the menu bar
		menuBar.add(viewMenu); 
		viewMenu.add(viewMenu_Status);

		//Add the "page" menu and menu item to the menu bar
		menuBar.add(pageMenu); 
		pageMenu.add(pageMenu_pageSetUp); 
		
		//Add the "set" menu and menu item to the menu bar
		menuBar.add(setMenu);
		setMenu_Color.add(C1);
		setMenu_Color.add(C2);
		setMenu_Color.add(C3);
		setMenu_Color.add(C4);
		setMenu_Font.add(I1);
		setMenu_Font.add(I2);
		setMenu_Font.add(I3);
		setMenu_Font.add(I4);
		
		setMenu.add(setMenu_Color);
		setMenu.addSeparator();
		setMenu.add(setMenu_Font);
		
		//Add the "help" menu and menu item to the menu bar
		menuBar.add(helpMenu);
		helpMenu.add(helpMenu_HelpTopics);
		helpMenu.addSeparator();
		helpMenu.add(helpMenu_AboutXFrame);
		
		/***************************************/		

		//Add menu bar to window 				
		this.setJMenuBar(menuBar);
		
		//set single pane to contain single page
		editArea=new JTextArea(20,50);
		singlePane=new JScrollPane(editArea);	
		singlePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(singlePane, BorderLayout.CENTER);
		editArea.setWrapStyleWord(true);//set linewrap
		editArea.setLineWrap(true);//true for wrap
		oldValue=editArea.getText();//get the contents of the original text editing area
	
		//set doublePanel to contain double Page
		editArea1=new JTextArea(20,25);
		JScrollPane scroller1=new JScrollPane(editArea1);	
		scroller1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editArea1.setWrapStyleWord(true);//set linewrap
		editArea1.setLineWrap(true);//true for wrap
		oldValue1=editArea1.getText();//get the contents of the original text editing area
	
		editArea2=new JTextArea(20,25);
		JScrollPane scroller2=new JScrollPane(editArea2);	
		scroller2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		doublePanel = new JPanel();
		doublePanel.setLayout(new GridLayout(1,2));
		doublePanel.add(scroller1);
		doublePanel.add(scroller2);
		editArea2.setWrapStyleWord(true);//set linewrap
		editArea2.setLineWrap(true);//true for wrap
		oldValue2=editArea2.getText();//get the contents of the original text editing area

		//default pageSetup
		PageSetup = new pageSetup();
		PageSetup.setVisible(false);

		//Edit area registered event listener (related to undo operation) 
		editArea.getDocument().addUndoableEditListener(undoHandler);
		editArea.getDocument().addDocumentListener(this);

		//Right click to create a pop-up menu 
		popupMenu=new JPopupMenu();
		popupMenu_Undo=new JMenuItem("UNDO");
		popupMenu_Cut=new JMenuItem("CUT");
		popupMenu_Copy=new JMenuItem("COPY");
		popupMenu_Paste=new JMenuItem("PASTE");
		popupMenu_Delete=new JMenuItem("DELETE");
		popupMenu_SelectAll=new JMenuItem("SELECTALL");

		popupMenu_Undo.setEnabled(false);

		//Add menu item and separator to the right click menu. 
		popupMenu.add(popupMenu_Undo);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_Cut);
		popupMenu.add(popupMenu_Copy);
		popupMenu.add(popupMenu_Paste);
		popupMenu.add(popupMenu_Delete);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_SelectAll);

		//Text edit area register event 
		popupMenu_Undo.addActionListener(this);
		popupMenu_Cut.addActionListener(this);
		popupMenu_Copy.addActionListener(this);
		popupMenu_Paste.addActionListener(this);
		popupMenu_Delete.addActionListener(this);
		popupMenu_SelectAll.addActionListener(this);

		//Text edit area register event 
		editArea.addMouseListener(new MouseAdapter()
		{	public void mousePressed(MouseEvent e)
			{	if(e.isPopupTrigger())//Returns whether the mouse event is a trigger event for the platform's pop-up menu
				{	popupMenu.show(e.getComponent(),e.getX(),e.getY());//post the menu at the calling location 
				}
				checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete and other functions 
				editArea.requestFocus();//Edit area get focus 
			}
			public void mouseReleased(MouseEvent e)
			{	if(e.isPopupTrigger())//Returns whether the mouse event is a trigger event for the platform's pop-up menu
				{	popupMenu.show(e.getComponent(),e.getX(),e.getY());//post the menu at the calling location 
				}
				checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete and other functions 
				editArea.requestFocus();//Edit area get focus 
			}
		});//Text editing area registered&& right click menu event /end 

		//Create and add status bar 
		statusLabel=new JLabel("　Get help information pressing F1!");
		this.add(statusLabel,BorderLayout.SOUTH);//Add status bar tab to window 
		
		//Create and add Toolbar
		JToolBar jToolBar1 = new JToolBar();
	    JButton jButton1 = new JButton();
	    JButton jButton2 = new JButton();
		JButton jButton3 = new JButton();
		JButton jButton4 = new JButton();
		JButton jButton5 = new JButton();
		JButton jButton6 = new JButton();
		
		ImageIcon imageIcon1;
		ImageIcon imageIcon2;
		ImageIcon imageIcon3;
		ImageIcon imageIcon4;
		ImageIcon imageIcon5;
		ImageIcon imageIcon6;
		  
		
		this.add(jToolBar1, BorderLayout.NORTH);
		
		jToolBar1.add(jButton1, null);
	    jToolBar1.add(jButton2, null);
	    jToolBar1.add(jButton3, null);
	    jToolBar1.add(jButton4, null);
	    jToolBar1.add(jButton5, null);
	    jToolBar1.add(jButton6, null);
		
		imageIcon1 = new ImageIcon(XFrame.class.getResource("new.png"));
	    imageIcon2 = new ImageIcon(XFrame.class.getResource("open.png"));
	    imageIcon3 = new ImageIcon(XFrame.class.getResource("save.png"));
	    imageIcon4 = new ImageIcon(XFrame.class.getResource("saveAs.png"));
	    imageIcon5 = new ImageIcon(XFrame.class.getResource("undo.png"));
	    imageIcon6 = new ImageIcon(XFrame.class.getResource("SinglePage.png"));
	    imageIcon6.setImage(imageIcon6.getImage().getScaledInstance(imageIcon5.getImage().getWidth(jButton5),imageIcon5.getImage().getHeight(jButton5),Image.SCALE_DEFAULT));
	    
	    jButton1.setToolTipText("new file");
	    jButton1.setIcon(null);
	    jButton1.setSelectedIcon(null);
	    jButton1.setText("");
	    jButton1.addActionListener(new Button1_actionAdapter(this));
	    jButton2.setToolTipText("open file");
	    jButton2.setVerifyInputWhenFocusTarget(true);
	    jButton2.setText("");
	    jButton2.addActionListener(new Button2_actionAdapter(this));
	    jButton3.setToolTipText("save");
	    jButton3.setText("");
	    jButton3.addActionListener(new Button3_actionAdapter(this));
	    jButton4.setToolTipText("save as");
	    jButton4.setText("");
	    jButton4.addActionListener(new Button4_actionAdapter(this));
	    jButton5.setToolTipText("undo");
	    jButton5.setText("");
	    jButton5.addActionListener(new Button5_actionAdapter(this));
	    jButton6.setToolTipText("page set");
	    jButton6.setText("");
	    jButton6.addActionListener(new Button6_actionAdapter(this));
	    
	    jButton1.setIcon(imageIcon1);
	    jButton2.setIcon(imageIcon2);
	    jButton3.setIcon(imageIcon3);
	    jButton4.setIcon(imageIcon4);
	    jButton5.setIcon(imageIcon5);
	    jButton6.setIcon(imageIcon6);

		
		//Set the location, size, and visibility of the window on the screen. 
		this.setLocation(100,50);//window size
		this.setSize(1000,600);
		this.setVisible(true);
		
		//Add the listener window 
		addWindowListener(new WindowAdapter()
		{	public void windowClosing(WindowEvent e)
			{
				exitWindowChoose();
			}
		});
		
		//Add the mouselistener to change pageset
		this.addMouseListener(new MouseListener() {
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				setpage();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		
		});
		
		checkMenuItemEnabled();
		editArea.requestFocus();
	
	}//end of constructor

class ChangeColorAction implements ActionListener{
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==C1){
			getContentPane().setBackground(Color.pink);
			editArea.setBackground(Color.getHSBColor(200,120,64));
			editArea1.setBackground(Color.getHSBColor(200,120,64));
			editArea2.setBackground(Color.getHSBColor(200,120,64));
			setVisible(true);
			repaint();
		}
		else if(e.getSource()==C2){
			getContentPane().setBackground(Color.getHSBColor(238,238,238));
			editArea.setBackground(Color.getHSBColor(270,270,270));
			editArea1.setBackground(Color.getHSBColor(270,270,270));
			editArea2.setBackground(Color.getHSBColor(270,270,270));
			setVisible(true);
			repaint();
		}
		else if(e.getSource()==C3){
			getContentPane().setBackground(Color.getHSBColor(355,355,355));
			editArea.setBackground(Color.getHSBColor(91,91,91));
			editArea1.setBackground(Color.getHSBColor(91,91,91));
			editArea2.setBackground(Color.getHSBColor(91,91,91));
			setVisible(true);
			repaint();
		}
		else if(e.getSource()==C4){
			getContentPane().setBackground(Color.white);
			editArea.setBackground(Color.white);
			editArea1.setBackground(Color.white);
			editArea2.setBackground(Color.white);
			setVisible(true);
			repaint();
		}
	}
}

//NOT FINISHED YET
class ChangeFontAction implements ActionListener{
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==I1){
			setFont(new FontUIResource("Dialog", Font.BOLD, 20));
			setVisible(true);
		}
		if(e.getSource()==I2){
			setFont(new FontUIResource("Dialog", Font.PLAIN, 20));
			setVisible(true);
		}
		if(e.getSource()==I3){
			setFont(new FontUIResource("Dialog", Font.ITALIC, 20));
			setVisible(true);
		}
		if(e.getSource()==I4){
			setFont(new FontUIResource("Dialog", Font.ROMAN_BASELINE, 20));
			setVisible(true);
		}
	}
}


class Button1_actionAdapter implements ActionListener {
	  XFrame adaptee;

	  Button1_actionAdapter(XFrame adaptee) {
	    this.adaptee = adaptee;
	  }

	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButton1_actionPerformed(e);
	  }
	}

class Button2_actionAdapter implements ActionListener {
	  XFrame adaptee;

	  Button2_actionAdapter(XFrame adaptee) {
	    this.adaptee = adaptee;
	  }

	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButton2_actionPerformed(e);
	  }
	}

class Button3_actionAdapter implements ActionListener {
	  XFrame adaptee;

	  Button3_actionAdapter(XFrame adaptee) {
	    this.adaptee = adaptee;
	  }

	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButton3_actionPerformed(e);
	  }
	}

class Button4_actionAdapter implements ActionListener {
	  XFrame adaptee;

	  Button4_actionAdapter(XFrame adaptee) {
	    this.adaptee = adaptee;
	  }

	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButton4_actionPerformed(e);
	  }
	}

class Button5_actionAdapter implements ActionListener {
	  XFrame adaptee;

	  Button5_actionAdapter(XFrame adaptee) {
	    this.adaptee = adaptee;
	  }

	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButton5_actionPerformed(e);
	  }
	}


class Button6_actionAdapter implements ActionListener {
	  XFrame adaptee;

	  Button6_actionAdapter(XFrame adaptee) {
	    this.adaptee = adaptee;
	  }

	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButton6_actionPerformed(e);
	  }
	}
	
	void jButton1_actionPerformed(ActionEvent e) {
	    newFile();
	  }

	void jButton2_actionPerformed(ActionEvent e) {
	    open();
	  }

	void jButton3_actionPerformed(ActionEvent e) {
	    save();
	  }

	void jButton4_actionPerformed(ActionEvent e) {
	    saveas();
	  }
	
	void jButton5_actionPerformed(ActionEvent e) {
	    undo();
	  }
		
	void jButton6_actionPerformed(ActionEvent e) {
	    pagesetup();
	  }
	
	//Set menu item availability: cut, copy, paste, delete function 
	public void checkMenuItemEnabled()
	{
		String selectText=editArea.getSelectedText();
		if(selectText==null)
		{	editMenu_Cut.setEnabled(false);
			popupMenu_Cut.setEnabled(false);
			editMenu_Copy.setEnabled(false);
			popupMenu_Copy.setEnabled(false);
			editMenu_Delete.setEnabled(false);
			popupMenu_Delete.setEnabled(false);
		}
		else
		{	editMenu_Cut.setEnabled(true);
			popupMenu_Cut.setEnabled(true); 
			editMenu_Copy.setEnabled(true);
			popupMenu_Copy.setEnabled(true);
			editMenu_Delete.setEnabled(true);
			popupMenu_Delete.setEnabled(true);
		}
		//paste function availability judgment 
		Transferable contents=clipBoard.getContents(this);
		if(contents==null)
		{	editMenu_Paste.setEnabled(false);
			popupMenu_Paste.setEnabled(false);
		}
		else
		{	editMenu_Paste.setEnabled(true);
			popupMenu_Paste.setEnabled(true);	
		}
	}

	//Call when closing the window
	public void exitWindowChoose()
	{
		editArea.requestFocus();
		String currentValue=editArea.getText();
		if(currentValue.equals(oldValue)==true)
		{	System.exit(0);
		}
		else
		{	int exitChoose=JOptionPane.showConfirmDialog(this,"Your file has not been saved. Do you want to save it? ","Exit Alert",JOptionPane.YES_NO_CANCEL_OPTION);
			if(exitChoose==JOptionPane.YES_OPTION)
			{	//boolean isSave=false;
				if(isNewFile)
				{	
					String str=null;
					JFileChooser fileChooser=new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("Confirm");
					fileChooser.setDialogTitle("Save As");
					
					int result=fileChooser.showSaveDialog(this);
					
					if(result==JFileChooser.CANCEL_OPTION)
					{	statusLabel.setText("You did not save the file!");
						return;
					}					
	
					File saveFileName=fileChooser.getSelectedFile();
				
					if(saveFileName==null||saveFileName.getName().equals(""))
					{	JOptionPane.showMessageDialog(this,"Illegal file name!","Illegal file name!",JOptionPane.ERROR_MESSAGE);
					}
					else 
					{	try
						{	FileWriter fw=new FileWriter(saveFileName);
							BufferedWriter bfw=new BufferedWriter(fw);
							bfw.write(editArea.getText(),0,editArea.getText().length());
							bfw.flush();
							fw.close();
							
							isNewFile=false;
							currentFile=saveFileName;
							oldValue=editArea.getText();
							
							this.setTitle(saveFileName.getName()+"  - File");
							statusLabel.setText("Open current file"+saveFileName.getAbsoluteFile());
							//isSave=true;
						}							
						catch(IOException ioException){					
						}				
					}
				}
				else
				{
					try
					{	FileWriter fw=new FileWriter(currentFile);
						BufferedWriter bfw=new BufferedWriter(fw);
						bfw.write(editArea.getText(),0,editArea.getText().length());
						bfw.flush();
						fw.close();
						//isSave=true;
					}							
					catch(IOException ioException){					
					}
				}
				System.exit(0);
				//if(isSave)System.exit(0);
				//else return;
			}
			else if(exitChoose==JOptionPane.NO_OPTION)
			{	System.exit(0);
			}
			else
			{	return;
			}
		}
	}

	//method of find word
	public void find(){
		final JDialog findDialog=new JDialog(this,"Find",false);//false, Allow other windows to be active at the same time
		Container con=findDialog.getContentPane();//return contentPane object
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel findContentLabel=new JLabel("Findcontent(N)：");
		final JTextField findText=new JTextField(15);
		JButton findNextButton=new JButton("Findnext(F)：");
		final JCheckBox matchCheckBox=new JCheckBox("Match case(C)");
		ButtonGroup bGroup=new ButtonGroup();
		final JRadioButton upButton=new JRadioButton("up(U)");
		final JRadioButton downButton=new JRadioButton("down(U)");
		downButton.setSelected(true);
		bGroup.add(upButton);
		bGroup.add(downButton);
		
		JButton cancel=new JButton("Cancel");
		//Cancel button event handler
		cancel.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	findDialog.dispose();
			}
		});
		//"findnext"ActionListener
		findNextButton.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	//"match case(C)"JCheckBox is select or not
				int k=0,m=0;
				final String str1,str2,str3,str4,strA,strB;
				str1=editArea.getText();
				str2=findText.getText();
				str3=str1.toUpperCase();
				str4=str2.toUpperCase();
				if(matchCheckBox.isSelected())//case sensitive
				{	strA=str1;
					strB=str2;
				}
				else//all the contents of the selected into uppercase (or lowercase)
				{	strA=str3;
					strB=str4;
				}
				if(upButton.isSelected())
				{	
					if(editArea.getSelectedText()==null)
						k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
					else
						k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);	
					if(k>-1)
					{	
						editArea.setCaretPosition(k);
						editArea.select(k,k+strB.length());
					}
					else
					{	JOptionPane.showMessageDialog(null,"sorry, find nothing","find",JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else if(downButton.isSelected())
				{	if(editArea.getSelectedText()==null)
						k=strA.indexOf(strB,editArea.getCaretPosition()+1);
					else
						k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);	
					if(k>-1)
					{	
						editArea.setCaretPosition(k);
						editArea.select(k,k+strB.length());
					}
					else
					{	JOptionPane.showMessageDialog(null,"sorry, find nothing","find",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});//"findnext" end
		//create"find" Dialog interface
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		JPanel directionPanel=new JPanel();
		directionPanel.setBorder(BorderFactory.createTitledBorder("direction"));
		//set directionPanel Component border;
		//BorderFactory.createTitledBorder(String title)
		directionPanel.add(upButton);
		directionPanel.add(downButton);
		panel1.setLayout(new GridLayout(2,1));
		panel1.add(findNextButton);
		panel1.add(cancel);
		panel2.add(findContentLabel);
		panel2.add(findText);
		panel2.add(panel1);
		panel3.add(matchCheckBox);
		panel3.add(directionPanel);
		con.add(panel2);
		con.add(panel3);
		findDialog.setSize(410,180);
		findDialog.setResizable(false);//Non adjustable size
		findDialog.setLocation(230,280);
		findDialog.setVisible(true);
	}
	
	//method of replace word
	public void replace() {
		final JDialog replaceDialog=new JDialog(this,"Replace",false);//false, Allow other windows to be active at the same time
		Container con=replaceDialog.getContentPane();//return contentPane object
		con.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel findContentLabel=new JLabel("Findcontent(N)：");
		final JTextField findText=new JTextField(15);
		JButton findNextButton=new JButton("Findnext(F):");
		JLabel replaceLabel=new JLabel("replace as(P)：");
		final JTextField replaceText=new JTextField(15);
		JButton replaceButton=new JButton("Replace(R)");
		JButton replaceAllButton=new JButton("Replace all(A)");
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	replaceDialog.dispose();
			}
		});
		final JCheckBox matchCheckBox=new JCheckBox("Macth case(C)");
		ButtonGroup bGroup=new ButtonGroup();
		final JRadioButton upButton=new JRadioButton("up(U)");
		final JRadioButton downButton=new JRadioButton("down(U)");
		downButton.setSelected(true);
		bGroup.add(upButton);
		bGroup.add(downButton);
		
		
		//"findnext" ActionListener
		findNextButton.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	//"matck case(C)", JCheckBox is selected or not
				int k=0,m=0;
				final String str1,str2,str3,str4,strA,strB;
				str1=editArea.getText();
				str2=findText.getText();
				str3=str1.toUpperCase();
				str4=str2.toUpperCase();
				if(matchCheckBox.isSelected())//case censitive
				{	strA=str1;
					strB=str2;
				}
				else//Case insensitive, this time all the contents of the selected into uppercase (or lowercase) 
				{	strA=str3;
					strB=str4;
				}
				if(upButton.isSelected())
				{	
					if(editArea.getSelectedText()==null)
						k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
					else
						k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);	
					if(k>-1)
					{	
						editArea.setCaretPosition(k);
						editArea.select(k,k+strB.length());
					}
					else
					{	JOptionPane.showMessageDialog(null,"sorry, find nothing","find",JOptionPane.INFORMATION_MESSAGE);
					}
				}
				else if(downButton.isSelected())
				{	if(editArea.getSelectedText()==null)
						k=strA.indexOf(strB,editArea.getCaretPosition()+1);
					else
						k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);	
					if(k>-1)
					{	
						editArea.setCaretPosition(k);
						editArea.select(k,k+strB.length());
					}
					else
					{	JOptionPane.showMessageDialog(null,"sorry, find nothing！","find",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});//"find next" end
		
		//"replace" ActionListener
		replaceButton.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	if(replaceText.getText().length()==0 && editArea.getSelectedText()!=null) 
					editArea.replaceSelection(""); 
				if(replaceText.getText().length()>0 && editArea.getSelectedText()!=null) 
					editArea.replaceSelection(replaceText.getText());
			}
		});//"replace" end
		
		//"replace" ActionListener
		replaceAllButton.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	editArea.setCaretPosition(0);	//Place the cursor at the beginning of the editing area
				int k=0,m=0,replaceCount=0;
				if(findText.getText().length()==0)
				{	JOptionPane.showMessageDialog(replaceDialog,"Please fill in the content!","Hint",JOptionPane.WARNING_MESSAGE);
					findText.requestFocus(true);
					return;
				}
				while(k>-1)
				{	
					final String str1,str2,str3,str4,strA,strB;
					str1=editArea.getText();
					str2=findText.getText();
					str3=str1.toUpperCase();
					str4=str2.toUpperCase();
					if(matchCheckBox.isSelected())//case match
					{	strA=str1;
						strB=str2;
					}
					else//Case insensitive, this time all the contents of the selected into uppercase (or lowercase)
					{	strA=str3;
						strB=str4;
					}
					if(upButton.isSelected())
					{	//k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
						if(editArea.getSelectedText()==null)
							k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
						else
							k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);	
						if(k>-1)
						{	//String strData=strA.subString(k,strB.getText().length()+1);
							editArea.setCaretPosition(k);
							editArea.select(k,k+strB.length());
						}
						else
						{	if(replaceCount==0)
							{	JOptionPane.showMessageDialog(replaceDialog, "sorry, find nothing", "note",JOptionPane.INFORMATION_MESSAGE); 
							}
							else
							{	JOptionPane.showMessageDialog(replaceDialog,"Replacement success"+replaceCount+"matching content!","Replacement success",JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
					else if(downButton.isSelected())
					{	if(editArea.getSelectedText()==null)
							k=strA.indexOf(strB,editArea.getCaretPosition()+1);
						else
							k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);	
						if(k>-1)
						{	
							editArea.setCaretPosition(k);
							editArea.select(k,k+strB.length());
						}
						else
						{	if(replaceCount==0)
							{	JOptionPane.showMessageDialog(replaceDialog, "Sorry, find nothing!", "note",JOptionPane.INFORMATION_MESSAGE); 
							}
							else
							{	JOptionPane.showMessageDialog(replaceDialog,"replacement success"+replaceCount+"matching content!!","replacement success",JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
					if(replaceText.getText().length()==0 && editArea.getSelectedText()!= null)
					{	editArea.replaceSelection("");
						replaceCount++;
					} 
					
					if(replaceText.getText().length()>0 && editArea.getSelectedText()!= null) 
					{	editArea.replaceSelection(replaceText.getText()); 
						replaceCount++;
					}
				}//while end
			}
		});//"replaceAll" end
		
		//create"replace" window
		JPanel directionPanel=new JPanel();
		directionPanel.setBorder(BorderFactory.createTitledBorder("direction"));
		//set directionPanel Component border;
		
		directionPanel.add(upButton);
		directionPanel.add(downButton);
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		JPanel panel4=new JPanel();
		panel4.setLayout(new GridLayout(2,1));
		panel1.add(findContentLabel);
		panel1.add(findText);
		panel1.add(findNextButton);
		panel4.add(replaceButton);
		panel4.add(replaceAllButton);
		panel2.add(replaceLabel);
		panel2.add(replaceText);
		panel2.add(panel4);
		panel3.add(matchCheckBox);
		panel3.add(directionPanel);
		panel3.add(cancel);
		con.add(panel1);
		con.add(panel2);
		con.add(panel3);
		replaceDialog.setSize(420,220);
		replaceDialog.setResizable(false);//Non adjustable size
		replaceDialog.setLocation(230,280);
		replaceDialog.setVisible(true);
	}

	//font method
	public void font(){
		final JDialog fontDialog=new JDialog(this,"set font",false);
		Container con=fontDialog.getContentPane();
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel fontLabel=new JLabel("font(F)：");
		fontLabel.setPreferredSize(new Dimension(100,20));
		JLabel styleLabel=new JLabel("style(Y)：");
		styleLabel.setPreferredSize(new Dimension(100,20));
		JLabel sizeLabel=new JLabel("size(S)：");
		sizeLabel.setPreferredSize(new Dimension(100,20));
		final JLabel sample=new JLabel("Try the sample here!");
		//sample.setHorizontalAlignment(SwingConstants.CENTER);
		final JTextField fontText=new JTextField(9);
		fontText.setPreferredSize(new Dimension(200,20));
		final JTextField styleText=new JTextField(8);
		styleText.setPreferredSize(new Dimension(200,20));
		final int style[]={Font.PLAIN,Font.BOLD,Font.ITALIC,Font.BOLD+Font.ITALIC};
		final JTextField sizeText=new JTextField(5);
		sizeText.setPreferredSize(new Dimension(200,20));
		JButton okButton=new JButton("ok");
		JButton cancel=new JButton("cancel");
		cancel.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	fontDialog.dispose();	
			}
		});
		Font currentFont=editArea.getFont();
		fontText.setText(currentFont.getFontName());
		fontText.selectAll();
		
		if(currentFont.getStyle()==Font.PLAIN)
			styleText.setText("Regular");
		else if(currentFont.getStyle()==Font.BOLD)
			styleText.setText("Bold");
		else if(currentFont.getStyle()==Font.ITALIC)
			styleText.setText("Italic ");
		else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
			styleText.setText("Bold Italic");
		styleText.selectAll();
		String str=String.valueOf(currentFont.getSize());
		sizeText.setText(str);
		sizeText.selectAll();
		final JList fontList,styleList,sizeList;
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String fontName[]=ge.getAvailableFontFamilyNames();
		fontList=new JList(fontName);
		fontList.setFixedCellWidth(86);
		fontList.setFixedCellHeight(20);
		fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final String fontStyle[]={"Regular","Bold","Italic","Bold Italic"};
		styleList=new JList(fontStyle);
		styleList.setFixedCellWidth(86);
		styleList.setFixedCellHeight(20);
		styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if(currentFont.getStyle()==Font.PLAIN)
			styleList.setSelectedIndex(0);
		else if(currentFont.getStyle()==Font.BOLD)
			styleList.setSelectedIndex(1);
		else if(currentFont.getStyle()==Font.ITALIC)
			styleList.setSelectedIndex(2);
		else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
			styleList.setSelectedIndex(3);
		final String fontSize[]={"8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72"};
		sizeList=new JList(fontSize);
		sizeList.setFixedCellWidth(43);
		sizeList.setFixedCellHeight(20);
		sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontList.addListSelectionListener(new ListSelectionListener()
		{	public void valueChanged(ListSelectionEvent event)
			{	fontText.setText(fontName[fontList.getSelectedIndex()]);
				fontText.selectAll();
				Font sampleFont1=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
				sample.setFont(sampleFont1);
			}
		});
		styleList.addListSelectionListener(new ListSelectionListener()
		{	public void valueChanged(ListSelectionEvent event)
			{	int s=style[styleList.getSelectedIndex()];
				styleText.setText(fontStyle[s]);
				styleText.selectAll();
				Font sampleFont2=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
				sample.setFont(sampleFont2);
			}
		});
		sizeList.addListSelectionListener(new ListSelectionListener()
		{	public void valueChanged(ListSelectionEvent event)
			{	sizeText.setText(fontSize[sizeList.getSelectedIndex()]);
				//sizeText.requestFocus();
				sizeText.selectAll();	
				Font sampleFont3=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
				sample.setFont(sampleFont3);
			}
		});
		okButton.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent e)
			{	Font okFont=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
				editArea.setFont(okFont);
				fontDialog.dispose();
			}
		});
		JPanel samplePanel=new JPanel();
		samplePanel.setBorder(BorderFactory.createTitledBorder("Sample"));
		//samplePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		samplePanel.add(sample);
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		
		panel2.add(fontText);
		panel2.add(styleText);
		panel2.add(sizeText);
		panel2.add(okButton);
		panel3.add(new JScrollPane(fontList));//JList不支持直接滚动，所以要让JList作为JScrollPane的视口视图
		panel3.add(new JScrollPane(styleList));
		panel3.add(new JScrollPane(sizeList));
		panel3.add(cancel);
		con.add(panel1);
		con.add(panel2);
		con.add(panel3);
		con.add(samplePanel);
		fontDialog.setSize(350,340);
		fontDialog.setLocation(200,200);
		fontDialog.setResizable(false);
		fontDialog.setVisible(true);
	}
	
	// The action of each button 
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==fileMenu_New)
		{	
			newFile();
		}
		
		else if(e.getSource()==fileMenu_Open)
		{	
			open();
		}
		
		else if(e.getSource()==fileMenu_Save)
		{	
			save();
		}
		
		else if(e.getSource()==fileMenu_SaveAs)
		{	
			saveas();
		}
		
		//page setup
		else if(e.getSource()==pageMenu_pageSetUp)
		{	editArea.requestFocus();
			pagesetup();
		}//finish page setup
		
		//print function begin
		else if(e.getSource()==fileMenu_Print)
		{	editArea.requestFocus();
			JOptionPane.showMessageDialog(this,"Sorry, this feature is not yet implemented! ","hint",JOptionPane.WARNING_MESSAGE);
		}//finish print function
		
		//exit function begin
		else if(e.getSource()==fileMenu_Exit)
		{	int exitChoose=JOptionPane.showConfirmDialog(this,"Are you sure? ( shuts down )","exit alert",JOptionPane.OK_CANCEL_OPTION);
			if(exitChoose==JOptionPane.OK_OPTION)
			{	System.exit(0);
			}
			else
			{	return;
			}
		}//finish exit function
		
		//undo function begin
		else if(e.getSource()==editMenu_Undo || e.getSource()==popupMenu_Undo)
		{	/*editArea.requestFocus();
			if(undo.canUndo())
			{	try
				{	undo.undo();
				}
				catch (CannotUndoException ex)
				{	System.out.println("Unable to undo:" + ex);
					//ex.printStackTrace();
				}
			}
			if(!undo.canUndo())
				{	editMenu_Undo.setEnabled(false);
				}*/undo();
		}//finish undo function
		
		//cut function
		else if(e.getSource()==editMenu_Cut || e.getSource()==popupMenu_Cut)
		{	editArea.requestFocus();
			String text=editArea.getSelectedText();
			StringSelection selection=new StringSelection(text);
			clipBoard.setContents(selection,null);
			editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
			checkMenuItemEnabled();//Set the availability of cut, copy, paste, and delete functions. 
		}//finish cut function				
				
		//copy function begin
				else if(e.getSource()==editMenu_Copy || e.getSource()==popupMenu_Copy)
				{	editArea.requestFocus();
					String text=editArea.getSelectedText();
					StringSelection selection=new StringSelection(text);
					clipBoard.setContents(selection,null);
					checkMenuItemEnabled();//Set the availability of cut, copy, paste, and delete functions. 
				}//finish copy function
		
				//paste function begin
				else if(e.getSource()==editMenu_Paste || e.getSource()==popupMenu_Paste)
				{	editArea.requestFocus();
					Transferable contents=clipBoard.getContents(this);
					if(contents==null)return;
					String text="";
					try
					{	text=(String)contents.getTransferData(DataFlavor.stringFlavor);
					}
					catch (Exception exception)
					{
					}
					editArea.replaceRange(text,editArea.getSelectionStart(),editArea.getSelectionEnd());
					checkMenuItemEnabled();
				}//finish paste function
		
				//delete function begin 
				else if(e.getSource()==editMenu_Delete || e.getSource()==popupMenu_Delete)
				{	editArea.requestFocus();
					editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
					checkMenuItemEnabled();	//Set the availability of cut, copy, paste, and delete functions. 
				}//finish delete function
		
				//find function
				else if(e.getSource()==editMenu_Find)
				{	editArea.requestFocus();
					find();
				}//finish find function
		
				//findnext function
				else if(e.getSource()==editMenu_FindNext)
				{	editArea.requestFocus();
					find();
				}//finish findnext function
		
				//replace function
				else if(e.getSource()==editMenu_Replace)
				{	editArea.requestFocus();
					replace();
				}//finish replace function
		
				//goto function
				else if(e.getSource()==editMenu_GoTo)
				{	editArea.requestFocus();
					JOptionPane.showMessageDialog(this,"Sorry, this feature is not yet implemented! ","hint",JOptionPane.WARNING_MESSAGE);
				}//not implemented
		
				//time and date
				else if(e.getSource()==editMenu_TimeDate)
				{	editArea.requestFocus();
					//SimpleDateFormat currentDateTime=new SimpleDateFormat("HH:mmyyyy-MM-dd");
					//editArea.insert(currentDateTime.format(new Date()),editArea.getCaretPosition());
					Calendar rightNow=Calendar.getInstance();
					Date date=rightNow.getTime();
					editArea.insert(date.toString(),editArea.getCaretPosition());
				}//finish timedate
		
				//select all function
				else if(e.getSource()==editMenu_SelectAll || e.getSource()==popupMenu_SelectAll)
				{	editArea.selectAll();
				}//finish select all function
		
				//word Wrap (have set)
				else if(e.getSource()==formatMenu_LineWrap)
				{	if(formatMenu_LineWrap.getState())
						editArea.setLineWrap(true);
					else 
						editArea.setLineWrap(false);

				}
				//font set
				else if(e.getSource()==formatMenu_Font)
				{	editArea.requestFocus();
					font();
				}//finish font set
		
				//Set status bar visibility 
				else if(e.getSource()==viewMenu_Status)
				{	if(viewMenu_Status.getState())
						statusLabel.setVisible(true);
					else 
						statusLabel.setVisible(false);
				}//Set status bar visibility 
				
				//help menu
				else if(e.getSource()==helpMenu_HelpTopics)
				{	editArea.requestFocus();
					JOptionPane.showMessageDialog(this,"If you like something, say something!","help menu",JOptionPane.INFORMATION_MESSAGE);
				}
				//about
				else if(e.getSource()==helpMenu_AboutXFrame)
				{	editArea.requestFocus();
					JOptionPane.showMessageDialog(this,
						"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n"+
						" Author: XFrame team \n"+
						" Lei Tang, Jingting Zhang, Sihan Wang, Chintan Patel, Jingyu Tan\n"+
						" Release time: 2016 fall                            \n"+
						" Description: basic functions and framework                \n"+
						" Development cycle : one month \n"+
						"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n",
						"XDF",JOptionPane.INFORMATION_MESSAGE);
				}
		

	}
	//newFile function begin
	public void newFile()
	{
		editArea.requestFocus();
		String currentValue=editArea.getText();
		boolean isTextChange=(currentValue.equals(oldValue))?false:true;
		if(isTextChange)
		{	int saveChoose=JOptionPane.showConfirmDialog(this,"Your file has not been saved. Do you want to save it? ","hint",JOptionPane.YES_NO_CANCEL_OPTION);
			if(saveChoose==JOptionPane.YES_OPTION)
			{	String str=null;
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
				fileChooser.setDialogTitle("save as");
				int result=fileChooser.showSaveDialog(this);
				if(result==JFileChooser.CANCEL_OPTION)
				{	statusLabel.setText("select no file");
					return;
				}
				File saveFileName=fileChooser.getSelectedFile();
				if(saveFileName==null || saveFileName.getName().equals(""))
				{	JOptionPane.showMessageDialog(this,"Illegal file name","Illegal file name",JOptionPane.ERROR_MESSAGE);
				}
				else 
				{	try
					{	FileWriter fw=new FileWriter(saveFileName);
						BufferedWriter bfw=new BufferedWriter(fw);
						bfw.write(editArea.getText(),0,editArea.getText().length());
						bfw.flush();//flushing the buffer
						bfw.close();
						isNewFile=false;
						currentFile=saveFileName;
						oldValue=editArea.getText();
						this.setTitle(saveFileName.getName()+" - XDF");
						statusLabel.setText("Current opened file："+saveFileName.getAbsoluteFile());
					}
					catch (IOException ioException)
					{
					}
				}
			}
			else if(saveChoose==JOptionPane.NO_OPTION)
			{	editArea.replaceRange("",0,editArea.getText().length());
				statusLabel.setText(" create new file");
				this.setTitle("no title - XDF");
				isNewFile=true;
				undo.discardAllEdits();	//discard all Undo operate
				editMenu_Undo.setEnabled(false);
				oldValue=editArea.getText();
			}
			else if(saveChoose==JOptionPane.CANCEL_OPTION)
			{	return;
			}
		}
		else
		{	editArea.replaceRange("",0,editArea.getText().length());
			statusLabel.setText(" create new file");
			this.setTitle("no title - XDF");
			isNewFile=true;
			undo.discardAllEdits();//discard all Undo operate
			editMenu_Undo.setEnabled(false);
			oldValue=editArea.getText();
		}
	}//finish new function
	

	//Open function
	public void open()
	{
		editArea.requestFocus();
		String currentValue=editArea.getText();
		boolean isTextChange=(currentValue.equals(oldValue))?false:true;
		if(isTextChange)
		{	int saveChoose=JOptionPane.showConfirmDialog(this,"Your file has not been saved. Do you want to save it? ","hint",JOptionPane.YES_NO_CANCEL_OPTION);
			if(saveChoose==JOptionPane.YES_OPTION)
			{	String str=null;
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				fileChooser.setDialogTitle("save as");
				int result=fileChooser.showSaveDialog(this);
				if(result==JFileChooser.CANCEL_OPTION)
				{	statusLabel.setText("select no file");
					return;
				}
				File saveFileName=fileChooser.getSelectedFile();
				if(saveFileName==null || saveFileName.getName().equals(""))
				{	JOptionPane.showMessageDialog(this,"Illegal file name","Illegal file name",JOptionPane.ERROR_MESSAGE);
				}
				else 
				{	try
					{	FileWriter fw=new FileWriter(saveFileName);
						BufferedWriter bfw=new BufferedWriter(fw);
						bfw.write(editArea.getText(),0,editArea.getText().length());
						bfw.flush();//flushing the buffer
						bfw.close();
						isNewFile=false;
						currentFile=saveFileName;
						oldValue=editArea.getText();
						this.setTitle(saveFileName.getName()+" - XDF");
						statusLabel.setText("Current opened file："+saveFileName.getAbsoluteFile());
					}
					catch (IOException ioException)
					{
					}
				}
			}
			else if(saveChoose==JOptionPane.NO_OPTION)
			{	String str=null;
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				fileChooser.setDialogTitle("Open file");
				int result=fileChooser.showOpenDialog(this);
				if(result==JFileChooser.CANCEL_OPTION)
				{	statusLabel.setText("select no file");
					return;
				}
				File fileName=fileChooser.getSelectedFile();
				if(fileName==null || fileName.getName().equals(""))
				{	JOptionPane.showMessageDialog(this,"Illegal file name","Illegal file name",JOptionPane.ERROR_MESSAGE);
				}
				else
				{	try
					{	FileReader fr=new FileReader(fileName);
						BufferedReader bfr=new BufferedReader(fr);
						editArea.setText("");
						while((str=bfr.readLine())!=null)
						{	editArea.append(str);
						}
						this.setTitle(fileName.getName()+" - XDF");
						statusLabel.setText(" Current opened file："+fileName.getAbsoluteFile());
						fr.close();
						isNewFile=false;
						currentFile=fileName;
						oldValue=editArea.getText();
					}
					catch (IOException ioException)
					{
					}
				}
			}
			else
			{	return;
			}
		}
		else
		{	String str=null;
			JFileChooser fileChooser=new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			fileChooser.setDialogTitle("Open file");
			int result=fileChooser.showOpenDialog(this);
			if(result==JFileChooser.CANCEL_OPTION)
			{	statusLabel.setText(" select no file ");
				return;
			}
			File fileName=fileChooser.getSelectedFile();
			if(fileName==null || fileName.getName().equals(""))
			{	JOptionPane.showMessageDialog(this,"Illegal file name","Illegal file name",JOptionPane.ERROR_MESSAGE);
			}
			else
			{	try
				{	FileReader fr=new FileReader(fileName);
					BufferedReader bfr=new BufferedReader(fr);
					editArea.setText("");
					while((str=bfr.readLine())!=null)
					{	editArea.append(str);
					}
					this.setTitle(fileName.getName()+" - XDF");
					statusLabel.setText(" Current opened file："+fileName.getAbsoluteFile());
					fr.close();
					isNewFile=false;
					currentFile=fileName;
					oldValue=editArea.getText();
				}
				catch (IOException ioException)
				{
				}
			}
		}
	}//finish open function
	//save function begin
	public void save()
	{
		editArea.requestFocus();
		if(isNewFile)
		{	String str=null;
			JFileChooser fileChooser=new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			fileChooser.setDialogTitle("save");
			int result=fileChooser.showSaveDialog(this);
			if(result==JFileChooser.CANCEL_OPTION)
			{	statusLabel.setText("select no file");
				return;
			}
			File saveFileName=fileChooser.getSelectedFile();
			if(saveFileName==null || saveFileName.getName().equals(""))
			{	JOptionPane.showMessageDialog(this,"Illegal file name","Illegal file name",JOptionPane.ERROR_MESSAGE);
			}
			else 
			{	try
				{	FileWriter fw=new FileWriter(saveFileName);
					BufferedWriter bfw=new BufferedWriter(fw);
					bfw.write(editArea.getText(),0,editArea.getText().length());
					bfw.flush();//flushing the buffer
					bfw.close();
					isNewFile=false;
					currentFile=saveFileName;
					oldValue=editArea.getText();
					this.setTitle(saveFileName.getName()+" - XDF");
					statusLabel.setText("Current opened file："+saveFileName.getAbsoluteFile());
				}
				catch (IOException ioException)
				{
				}
			}
		}
		else
		{	try
			{	FileWriter fw=new FileWriter(currentFile);
				BufferedWriter bfw=new BufferedWriter(fw);
				bfw.write(editArea.getText(),0,editArea.getText().length());
				bfw.flush();
				fw.close();
			}							
			catch(IOException ioException)
			{					
			}
		}
	}//finish the save function

	//save as function begin
	public void saveas()
	{
		editArea.requestFocus();
		String str=null;
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		fileChooser.setDialogTitle("save as");
		int result=fileChooser.showSaveDialog(this);
		if(result==JFileChooser.CANCEL_OPTION)
		{	statusLabel.setText("　select no file");
			return;
		}				
		File saveFileName=fileChooser.getSelectedFile();
		if(saveFileName==null||saveFileName.getName().equals(""))
		{	JOptionPane.showMessageDialog(this,"Illegal file name","Illegal file name",JOptionPane.ERROR_MESSAGE);
		}	
		else 
		{	try
			{	FileWriter fw=new FileWriter(saveFileName);
				BufferedWriter bfw=new BufferedWriter(fw);
				bfw.write(editArea.getText(),0,editArea.getText().length());
				bfw.flush();
				fw.close();
				oldValue=editArea.getText();
				this.setTitle(saveFileName.getName()+"  - XDF");
				statusLabel.setText("　Current opened file:"+saveFileName.getAbsoluteFile());
			}						
			catch(IOException ioException)
			{					
			}				
		}
	}//finish saveas function

	//when click the button call pagesetup frame
	public void pagesetup(){	
		oldValue = editArea.getText();
		oldValue1 = editArea1.getText();
		oldValue2 = editArea2.getText();
		PageSetup.setVisible(true);
		this.setpage();
	}
	
	//setup single page or double page of xpdf
	private boolean single = true;
	public void setpage(){
		if(PageSetup.SingleOrNot() == true && single==false){
			StringBuilder text = new StringBuilder();
			text.append(oldValue1);
			text.append("\r\n");
			text.append(oldValue2);
			if(oldValue1.equals("")&&oldValue2.equals("")){
				editArea.setText("");
			}else if(oldValue1.equals("")){
				editArea.setText(oldValue2);
			}else{
				editArea.setText(text.toString());
			}
			this.remove(doublePanel);
			this.add(singlePane, BorderLayout.CENTER);;
			this.validate();
			this.repaint();
			System.out.println("SinglePage");
			single=true;
		}else if(PageSetup.SingleOrNot() == false && single==true){
			if(oldValue.equals("")){
				editArea1.setText("");
				editArea2.setText("");
			}else{
				String []s = oldValue.split("\\r?\\n");
				if(s.length > 32){
					StringBuilder text1 = new StringBuilder();
					StringBuilder text2 = new StringBuilder();
					for(int i = 0; i < s.length/2;i++){
						text1.append(s[i]).append("\r\n");
						text2.append(s[i + s.length/2]).append("\r\n");
					}
					editArea1.setText(text1.toString());
					editArea2.setText(text2.toString());
				}else{
					StringBuilder text1 = new StringBuilder();
					for(int i = 0; i < s.length;i++){
						text1.append(s[i]).append("\r\n");
					}
					editArea1.setText(text1.toString());
					editArea2.setText("");
				}
			}
			this.remove(singlePane);
			this.add(doublePanel, BorderLayout.CENTER);
			this.validate();		
			this.repaint();
			System.out.println("DoublePage");
			single = false; 
		}
	}
	
	//undo function begin
	public void undo()
	{
		editArea.requestFocus();
		if(undo.canUndo())
		{	try
			{	undo.undo();
			}
			catch (CannotUndoException ex)
			{	System.out.println("Unable to undo:" + ex);
				//ex.printStackTrace();
			}
		}
		if(!undo.canUndo())
			{	editMenu_Undo.setEnabled(false);
			}
	}//undo function finished
	
	//Method to implement the "documentlistener" interface (related to undo operation) 
	public void removeUpdate(DocumentEvent e)
	{
		editMenu_Undo.setEnabled(true);
	}
	public void insertUpdate(DocumentEvent e)
	{
		editMenu_Undo.setEnabled(true);
	}
	public void changedUpdate(DocumentEvent e)
	{
		editMenu_Undo.setEnabled(true);
	}

	//Implementation of the interface undoableeditlistener class undohandler (related to undo operation)
	class UndoHandler implements UndoableEditListener
	{	public void undoableEditHappened(UndoableEditEvent uee)
	{
		undo.addEdit(uee.getEdit());
	}
	}

}

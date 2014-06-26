

import javax.swing.*; 
import javax.swing.filechooser.FileFilter;

import java.awt.*; 
import java.awt.event.*; 
import java.io.*; 

public class MapEditor extends JFrame implements ActionListener {



  private final int PREFERRED_WIDTH = 680;
  private final int PREFERRED_HEIGHT = 600;


  private ZoomPane _zoomPane;
  private MapScene _map;
  private Graph graph;
  private File mapFile;
  
  //Menu Bar
  private JMenuBar menuBar;
  private JMenu fileMenu;
  private JMenu modeMenu;
  //Menu Items
  private JMenuItem newMenu;
  private JMenuItem openMenu;
  private JMenuItem saveasMenu;
  private JMenuItem saveMenu;
  private JMenuItem insertLocMenu;
  private JMenuItem delLocMenu;
  private JMenuItem insertPathMenu;
  private JMenuItem delPathMenu;
  private JMenuItem locPropertyMenu;
  
  private final JFileChooser XMLFileChooser = new JFileChooser();
  
  private int modeSelect = 0;
  
  public static void main(String[] args) { 
    MapEditor mapEditor = new MapEditor(); 
    mapEditor.setVisible(true);
  } 

  public MapEditor() {
    setTitle("Map Editor");
    setSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    setBackground(Color.gray);
    
    //Menu settings
    menuBar = new JMenuBar();
    fileMenu = new JMenu("File");
    modeMenu = new JMenu("Mode");
    
    menuBar.add(fileMenu);
    menuBar.add(modeMenu);
    
    newMenu = new JMenuItem("New");
    openMenu = new JMenuItem("Open");
    saveMenu = new JMenuItem("Save");
    saveasMenu = new JMenuItem("Save As");
    
    insertLocMenu = new JMenuItem("Insert Location");
    delLocMenu = new JMenuItem("Delete Location");
    insertPathMenu = new JMenuItem("Insert Path");
    delPathMenu = new JMenuItem("Delete Path");
    locPropertyMenu = new JMenuItem("Location Property");
    
    insertLocMenu.addActionListener(this);
    delLocMenu.addActionListener(this);
    insertPathMenu.addActionListener(this);
    delPathMenu.addActionListener(this);
    locPropertyMenu.addActionListener(this);
    
    newMenu.addActionListener(this);
    openMenu.addActionListener(this);
    saveMenu.addActionListener(this);
    saveasMenu.addActionListener(this);
    
    fileMenu.add(newMenu);
    fileMenu.add(openMenu);
    fileMenu.add(saveMenu);
    fileMenu.add(saveasMenu);
    
    modeMenu.add(insertLocMenu);
    modeMenu.add(delLocMenu);
    modeMenu.add(insertPathMenu);
    modeMenu.add(delPathMenu);
    modeMenu.add(locPropertyMenu);
    
    this.setJMenuBar(menuBar);
    
    // Close when closed. For reals.
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //Add some features for XML map chooser
    XMLFileChooser.addChoosableFileFilter(new FileFilter(){
		
		@Override
		public boolean accept(File f) {
		    if (f.isDirectory()) {
		        return true;
		    }
		    
			String fileName = f.getName();
			String extension;
			int extLoc = fileName.lastIndexOf(".");
			
			if (extLoc > 0 &&  extLoc < fileName.length() - 1) {
	            extension = fileName.substring(extLoc + 1).toLowerCase();
	            if(extension.equals("xml"))
	            	return true;
	        }
			return false;
		}

		@Override
		public String getDescription() {
			return "XML Map File";
		}});
	XMLFileChooser.setAcceptAllFileFilterUsed(false);
	

  }


  public void createNewMap(){
	  JTextField bitmapName = new JTextField(9);
      JTextField pixelConstant = new JTextField(3);
      
      JPanel panel = new JPanel();
      panel.add(new JLabel("Bitmap Name: "));
      panel.add(bitmapName);
      panel.add(new JLabel("Scale Feet Per Pixel: "));
      panel.add(pixelConstant);
      
      int returnValue = JOptionPane.showConfirmDialog(null, panel, "Create a new XML map", JOptionPane.OK_CANCEL_OPTION);
      
      if(returnValue == JOptionPane.OK_OPTION){
    	    
    	    File file = new File(bitmapName.getText());
    	    double pixelValue = 0.0;
    	    
    	    if(file == null || !file.exists() || !file.isFile()){
    	    	JOptionPane.showMessageDialog(null, bitmapName.getText() + ": BitMap File does not exist!");
    	    	return;
    	    }
    	    
    	    try{
    	        pixelValue = Double.parseDouble(pixelConstant.getText());
    	    }catch(Exception e){
    	    	JOptionPane.showMessageDialog(null, "Please enter a valid pixel number!");
    	    	return;
    	    }
    	    
    	    getContentPane().removeAll();
    	    Image image = new ImageIcon(bitmapName.getText()).getImage();
            mapFile = null;
    	    
    	    graph = new Graph(bitmapName.getText(), pixelValue);
    	    
    	    _map = new MapScene(image, graph);
    	    _zoomPane = new ZoomPane(_map);

    	    getContentPane().add(_zoomPane);
    	    getContentPane().add(_zoomPane.getJSlider(), "Last");

    	    validate();
    	    
    	    MouseAdapter listener = new MouseAdapter() {
    	      public void mousePressed(MouseEvent e) {
    	        Point point = _zoomPane.toViewCoordinates(e.getPoint());
    	        _map.mousePressed(point);
    	      }
    	      
    	      public void mouseClicked(MouseEvent e){
    	    	  Point point = _zoomPane.toViewCoordinates(e.getPoint());
    	          _map.mouseClicked(point, e);
    	      }
    	      
    	      public void mouseReleased(MouseEvent e){
    	    	  Point point = _zoomPane.toViewCoordinates(e.getPoint());
    	          _map.mouseReleased(point);
    	      }
    	    };

    	    MouseMotionAdapter motionListener = new MouseMotionAdapter() {
    	      public void mouseDragged(MouseEvent e) {
    	        Point point = _zoomPane.toViewCoordinates(e.getPoint());
    	        _map.mouseDragged(point);
    	      }
    	      
    	    };

    	    _zoomPane.getZoomPanel().addMouseListener(listener);
    	    _zoomPane.getZoomPanel().addMouseMotionListener(motionListener);
    	    
      }
      
  }
  
  
private void openMapFile(File file){
	File xmlFile = file;
	
    getContentPane().removeAll();
    
    graph = XMLHandler.parseMap(xmlFile);
    Image image = new ImageIcon(graph.getBitName()).getImage();
    mapFile = xmlFile;
    
    _map = new MapScene(image, graph);
    _zoomPane = new ZoomPane(_map);

    getContentPane().add(_zoomPane);
    getContentPane().add(_zoomPane.getJSlider(), "Last");

    setTitle("Map Editor - " + xmlFile.getName());
    validate();
    
    MouseAdapter listener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        Point point = _zoomPane.toViewCoordinates(e.getPoint());
        _map.mousePressed(point);
      }
      
      public void mouseClicked(MouseEvent e){
    	  Point point = _zoomPane.toViewCoordinates(e.getPoint());
          _map.mouseClicked(point, e);
      }
      
      public void mouseReleased(MouseEvent e){
    	  Point point = _zoomPane.toViewCoordinates(e.getPoint());
          _map.mouseReleased(point);
      }
    };

    MouseMotionAdapter motionListener = new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        Point point = _zoomPane.toViewCoordinates(e.getPoint());
        _map.mouseDragged(point);
      }
      
    };

    _zoomPane.getZoomPanel().addMouseListener(listener);
    _zoomPane.getZoomPanel().addMouseMotionListener(motionListener);
    
}
  




public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	JMenuItem source = (JMenuItem)(e.getSource());
	
	if(source == newMenu){
		createNewMap();
		modeSelect = 0;	
	}
	
	if(source == openMenu){
		int returnVal = XMLFileChooser.showDialog(this,"Open XML Map");
		if(returnVal == JFileChooser.APPROVE_OPTION){
			if(XMLFileChooser.getSelectedFile().exists()){
				openMapFile(XMLFileChooser.getSelectedFile().getAbsoluteFile());
			}
		}
		
	}
	
	if(_map == null){
		return;
	}
	
	if(source == saveMenu){
		if(mapFile == null){
              saveMap();
	    }else{
		      XMLHandler.saveMap(graph, mapFile);
	    }
	}
	
	if(source == saveasMenu){
       saveMap();
	}
	
	if(source == insertLocMenu){
		modeSelect = 1;
		_map.changeMode(modeSelect);
		setTitle("Map Editor - Insert Location Mode");
	}
	
	if(source == delLocMenu){
		modeSelect = 2;
		_map.changeMode(modeSelect);
		setTitle("Map Editor - Delete Location Mode");
	}
	
	if(source == insertPathMenu){
		modeSelect = 3;
		_map.changeMode(modeSelect);
		setTitle("Map Editor - Insert Path Mode");
	}
	
	if(source == delPathMenu){
		modeSelect = 4;
		_map.changeMode(modeSelect);
		setTitle("Map Editor - Delete Path Mode");
	}
	
	if(source == locPropertyMenu){
	    _map.setLocProperty();
	}
	
}

private void saveMap(){
	  int returnVal = XMLFileChooser.showSaveDialog(this);
	  if(returnVal == JFileChooser.APPROVE_OPTION){
		  mapFile = XMLFileChooser.getSelectedFile().getAbsoluteFile();
		  setTitle("Map Editor - " + mapFile.getName());
	      XMLHandler.saveMap(graph, mapFile);  
	  }
}
}

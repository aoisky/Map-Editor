import java.util.Iterator;
import java.awt.Color;
import java.util.*;
import java.awt.Container;
import java.awt.Image;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import javax.swing.*;
import java.awt.GridLayout;
import javax.swing.filechooser.FileFilter;

public class MapViewer extends JFrame implements ActionListener
{
    private int nodes=0;
    private Location fromLoc;
    private Location toLoc;
    private int[] distTo;
    private Path[] edgeTo ;
    private PriorityQueue<Integer> pq;
	private final int PREFERRED_WIDTH = 680;
	private final int PREFERRED_HEIGHT = 600;
	private JScrollPane _scrollPane;
	private ZoomPane _zoomPane;
	private MapScene _map;
	private Graph graph;
    private JButton confirmButton;
    private JButton selectedButton;
	private File mapFile;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu OperationMenu;
	private JMenuItem openItem;
    private JMenuItem MSTItem;
	private JMenuItem directionItem;
    private JMenuItem cleanItem;
    private JFrame operationFrame = new JFrame();
    private JList fromList;
    private JList toList;
    private Point pointFrom, pointTo;
    private Location locFrom;
    private Location locTO;
	private final JFileChooser XMLFileChooser = new JFileChooser();
	private int operationMode = -1;
	
	public static void main(String[] args) {
            MapViewer mapViewer = new MapViewer();
    		mapViewer.setVisible(true);
  	}
	public MapViewer() {
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle("Map Viewer");
            setSize(680, 600);
    		setBackground(Color.gray);
    		this.menuBar = new JMenuBar();
    		this.fileMenu = new JMenu("File");
    		this.OperationMenu = new JMenu("Operation");
    		this.menuBar.add(this.fileMenu);
    		this.menuBar.add(this.OperationMenu);
    		this.openItem = new JMenuItem("Open");
    		this.MSTItem = new JMenuItem("MST");
            this.cleanItem= new JMenuItem("Recover");
    		this.directionItem = new JMenuItem("Direction");
            this.cleanItem.addActionListener(this);
    		this.MSTItem.addActionListener(this);
    		this.directionItem.addActionListener(this);
    		this.openItem.addActionListener(this);
    		this.fileMenu.add(this.openItem);
    		this.OperationMenu.add(this.MSTItem);
            this.OperationMenu.add(this.cleanItem);
    		this.OperationMenu.add(this.directionItem);
    		setJMenuBar(this.menuBar);
            setDefaultCloseOperation(3);
            this.XMLFileChooser.addChoosableFileFilter(new FileFilter(){
      			public boolean accept(File f){
        			if (f.isDirectory()) {
          				return true;
        			}
                    
                    String fileName = f.getName();

                    int extLoc = fileName.lastIndexOf(".");

        			if ((extLoc > 0) && (extLoc < fileName.length() - 1)) {
          				String extension = fileName.substring(extLoc + 1).toLowerCase();
          				if (extension.equals("xml"))
            				return true;
        			}
        			return false;
      			}

      			public String getDescription(){
       				return "XML Map File";
      			}
    		});
    		this.XMLFileChooser.setAcceptAllFileFilterUsed(false);
	}
    
    
    

    
    
    
    private void findDirection(Location locFrom,Location locTo,int size){
        //size=500;

        int[][] table=new int[size][size];
        int INFINITE=-1;
        int[] length = new int[size];
        boolean[] labeled= new boolean[size];
        int count=0;
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                table[i][j]=INFINITE;
            }
        }
        for (Location loc1 : graph.getLocList()){
            for (Location loc2 : graph.getLocList()){
                if(loc1.getId() == loc2.getId()) table[loc1.getId()][loc2.getId()]=0;
                else table[loc1.getId()][loc2.getId()]= INFINITE;
            }
        }
        for (Path tempPath:graph.getPathList()){
            Location[] locs = new Location[2];
            locs=tempPath.getLoc();
            table[locs[1].getId()][locs[0].getId()]=(int)((double)tempPath.length()*graph.getPixelConstant());
            table[locs[0].getId()][locs[1].getId()]=(int)((double)tempPath.length()*graph.getPixelConstant());
        }
        

        ArrayList<ArrayList<Integer>> path= new ArrayList<ArrayList<Integer>>();
        for (int i =0;i < size; i++){
            length[i]=table[locFrom.getId()][i];
            path.add(new ArrayList<Integer>());
            if (length[i]>INFINITE){
                path.get(i).add(locFrom.getId());
                path.get(i).add(i);
            }
            labeled[i]=false;
        }
        length[locFrom.getId()]=0;
        labeled[locFrom.getId()]=true;
        
        
        while (count < size){
            int minimum = INFINITE;
            int point = INFINITE;
            for(int i=0; i < size; i++){
                if(labeled[i] == true) continue;
                if(minimum == INFINITE && length[i] > INFINITE){
                    minimum = length[i];
                    point=i;
                }
                else if(minimum>INFINITE && length[i]>INFINITE && length[i]<minimum){
                    minimum=length[i];
                    point=i;
                }
            }
            if(minimum==INFINITE) break;
            labeled[point]=true;
            count++;
            for(int i=0;i<size;i++){
                if(labeled[i]==true)continue;
                if (length[point]!=INFINITE && table[point][i]!=INFINITE){
                    if(length[point]+table[point][i]<length[i] || length[i]==INFINITE){
                        length[i]=length[point]+table[point][i];
                         while (!path.get(i).isEmpty()) path.get(i).remove(0);
                         path.get(i).addAll(path.get(point));
                         path.get(i).add(i);
                    }
                }
            }
        }
        LinkedList<Integer> shortestPath = new LinkedList<Integer>();
        shortestPath.addAll(path.get(locTo.getId()));
        shortestPath.add(0,length[locTo.getId()]);
        int idpath[] = new int[size+1];
        int counter=0;
        for(Iterator i = shortestPath.iterator();i.hasNext();){
            //System.out.format("%d->",i.next());
            idpath[counter]=(Integer)i.next();
            counter++;
            //System.out.format("%d->",idpath[counter-1]);
        }
        //System.out.format("\n");
        if (idpath[0]>INFINITE){
            for(int i=1;idpath[i]!=locTo.getId();i++){
                for(Path tempPath : graph.getPathList()){
                    Location[] temploc =tempPath.getLoc();
                    if (temploc[0].getId()==idpath[i] && temploc[1].getId()==idpath[i+1]){
                            tempPath.selected=true;
                            break;
                    }
                    else if (temploc[0].getId()==idpath[i+1] && temploc[1].getId()==idpath[i]) {
                        tempPath.selected=true;
                        break;
                    }
                }
            }
        
            String st="The shortest Length is:" + idpath[0]+"\n the Green Line is the shortest path";
            JOptionPane.showMessageDialog(null,st);
        }
        else {
            JOptionPane.showMessageDialog(null,"The shorest Path does not exist");
        }
        
        //_zoomPane.repaint();
        
    }


    private void helerWindow(String title){
        operationFrame= new JFrame();
        operationFrame.setSize(400,300);
        operationFrame.setTitle(title);
        operationFrame.setLayout(new GridLayout(0,2));
        operationFrame.setLocation(680, 0);
        selectedButton = new JButton("Select Points");
        confirmButton  = new JButton("Find Direction");
        confirmButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for(Path tempPath:graph.getPathList()){
                    tempPath.selected=false;
                    tempPath.MSTselected=-1;
                }
                //System.out.format("Pressed!\n");
                int nodeFrom= Integer.parseInt(fromList.getSelectedValue().toString());
                int nodeTo= Integer.parseInt(toList.getSelectedValue().toString());
                //System.out.format("From: %d\n To:%d\n",nodeFrom,nodeTo);
                int twoPoint=0;
                for (Location loc : graph.getLocList()){
                    loc.setSelect(false);
                    if (nodeFrom==loc.getId()) {
                        pointFrom=loc.getPoint();
                        twoPoint++;
                    }
                    if (nodeTo==loc.getId()){
                        pointTo=loc.getPoint();
                        twoPoint++;
                    }
                }
                if (twoPoint==2){
                    fromLoc = graph.checkSelectLoc(pointFrom);
                    toLoc = graph.checkSelectLoc(pointTo);
                    fromLoc.setSelect(true);
                    toLoc.setSelect(true);
                    findDirection(fromLoc,toLoc,graph.getCurrentLocId()+1);
                }
                else {
                    JOptionPane.showMessageDialog(null,"Please Select Two Points");
                }
                _zoomPane.repaint();
            }
        });
        selectedButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for(Path tempPath:graph.getPathList()){
                    tempPath.selected=false;
                }
                int nodeTo=-9;
                int nodeFrom=-9;
                if(fromList.getSelectedValue()!=null)nodeFrom= Integer.parseInt(fromList.getSelectedValue().toString());
                if(toList.getSelectedValue()!=null) nodeTo= Integer.parseInt(toList.getSelectedValue().toString());
                int twoPoint=0;
                //System.out.format("From: %d\n To:%d\n",nodeFrom,nodeTo);
                for (Location loc : graph.getLocList()){
                    loc.setSelect(false);
                    if (nodeFrom==loc.getId()) {
                        pointFrom=loc.getPoint();
                        twoPoint++;
                    }
                    if (nodeTo==loc.getId()) {
                        pointTo=loc.getPoint();
                        twoPoint++;
                    }
                }
                if(twoPoint==2){
                    fromLoc = graph.checkSelectLoc(pointFrom);
                    toLoc = graph.checkSelectLoc(pointTo);
                    fromLoc.setSelect(true);
                    toLoc.setSelect(true);
                }
                else{
                    JOptionPane.showMessageDialog(null,"Please Select Two Points");
                }
                _zoomPane.repaint();

            }
        });
        JLabel fromLabel = new JLabel("From");
        JLabel toLabel = new JLabel("To");
        DefaultListModel listModel1 = new DefaultListModel();
        DefaultListModel listModel2 = new DefaultListModel();
        fromList= new JList(listModel1);
        toList= new JList(listModel2);
        nodes=0;
        //Iterator iter = g
        for (Location loc : this.graph.getLocList()){
            int newNode=loc.getId();
            listModel1.addElement(newNode);
            listModel2.addElement(newNode);
        }
        JScrollPane toScrollPane = new JScrollPane(toList);
        JScrollPane fromScrollPane = new JScrollPane(fromList);
        fromList.setAutoscrolls(true);
        toList.setAutoscrolls(true);
        operationFrame.add(fromLabel);
        operationFrame.add(toLabel);
        operationFrame.add(fromScrollPane);
        operationFrame.add(toScrollPane);
        
        operationFrame.add(selectedButton);
        operationFrame.add(confirmButton);
        operationFrame.setVisible(true);
    }
	private void openMapFile(File file)
  	{
        
        operationFrame.setVisible(false);
        operationFrame.getContentPane().removeAll();
    	File xmlFile = file;

    	getContentPane().removeAll();

    	this.graph = XMLHandler.parseMap(xmlFile);
    	Image image = new ImageIcon(this.graph.getBitName()).getImage();
    	this.mapFile = xmlFile;

    	this._map = new MapScene(image, this.graph);
    	this._zoomPane = new ZoomPane(this._map);

    	getContentPane().add(this._zoomPane);
    	getContentPane().add(this._zoomPane.getJSlider(), "Last");

    	setTitle("Map Viewer - " + xmlFile.getName());
    	validate();

    	MouseAdapter listener = new MouseAdapter() {
      		public void mousePressed(MouseEvent e) {
        		Point point = MapViewer.this._zoomPane.toViewCoordinates(e.getPoint());
        		MapViewer.this._map.mousePressed(point);
      		}

      		public void mouseClicked(MouseEvent e) {
        		Point point = MapViewer.this._zoomPane.toViewCoordinates(e.getPoint());
        		MapViewer.this._map.mouseClicked(point, e);
      		}

      		public void mouseReleased(MouseEvent e) {
        		Point point = MapViewer.this._zoomPane.toViewCoordinates(e.getPoint());
        		MapViewer.this._map.mouseReleased(point);
      		}
    	};
    	MouseMotionAdapter motionListener = new MouseMotionAdapter() {
      		public void mouseDragged(MouseEvent e) {
        		Point point = MapViewer.this._zoomPane.toViewCoordinates(e.getPoint());
        		MapViewer.this._map.mouseDragged(point);
      		}
    	};
    		this._zoomPane.getZoomPanel().addMouseListener(listener);
    		this._zoomPane.getZoomPanel().addMouseMotionListener(motionListener);
        
	}

	public void actionPerformed(ActionEvent e){
            JMenuItem source = (JMenuItem)e.getSource();
            //JButton buttonsource=(JButton)e.getSource();
    		if (source == this.openItem) {
                int returnVal = this.XMLFileChooser.showDialog(this, "Open XML Map");
                if ((returnVal == 0) && 
        		(this.XMLFileChooser.getSelectedFile().exists())) {
                    openMapFile(this.XMLFileChooser.getSelectedFile().getAbsoluteFile());
                }

    		}
            if (this._map == null) return;
        if (source == this.cleanItem){
            for(Path tempPath : graph.getPathList()){
                tempPath.selected = false;
                tempPath.MSTselected = -1;
            }
            _zoomPane.repaint();
        }
            if (source == this.MSTItem) {
                this.operationMode = 1;
                for(Path tempPath : graph.getPathList()){
                    tempPath.selected = false;
                    tempPath.MSTselected = -1;
                }
                this.operationMode = 2;
                operationFrame.setVisible(false);
                MST(graph.getCurrentLocId() + 1);
                _zoomPane.repaint();
            }
        
            if (source == this.directionItem) {
                for(Path tempPath:graph.getPathList()){
                    tempPath.selected = false;
                    tempPath.MSTselected = -1;
                }
                this.operationMode = 2;

                
                _zoomPane.repaint();
                helerWindow("Find Direction");
            }

    }
    private void MST(int size){
        int[][] table = new int[size][size];
        int INFINITE = 65535;
        boolean[] labeled = new boolean[size];
        if(fromLoc != null && toLoc != null){
        	fromLoc.setSelect(false);
        	toLoc.setSelect(false);
        }
        for(int i=0;i < size;i++){
            for(int j=0;j<size;j++){
                table[i][j] = INFINITE;
            }
            labeled[i]=false;
        }
        labeled[size-1] = true;
               for (Location loc1 : graph.getLocList()){
                   for (Location loc2 : graph.getLocList()){
                       if(loc1.getId() == loc2.getId()) table[loc1.getId()][loc2.getId()]=0;
                       else table[loc1.getId()][loc2.getId()]= INFINITE;
            }
        }
        for (Path tempPath : graph.getPathList()){
            Location[] locs = new Location[2];
            locs=tempPath.getLoc();
            tempPath.MSTselected = 0;
            table[locs[1].getId()][locs[0].getId()]=tempPath.length();
            table[locs[0].getId()][locs[1].getId()]=tempPath.length();
        }
        
        while (allLabeled(labeled,size) == false){
            int minimumPath = 65535;
            int start = -1;
            int end = -1;
            for(int i=0; i < size; i++){
                if(labeled[i] == true){
                    //System.out.format("~~~");
                    for(int j=0;j < size; j++){
                        if(table[i][j] < minimumPath && labeled[j] == false){
                            minimumPath = table[i][j];
                            start = i;
                            end = j;
                            //System.out.format("!");
                        }
                    }
                }
            }
            if (start != -1 && end != -1){
                for(Path tempPath : graph.getPathList()){
                    Location[] temploc = tempPath.getLoc();
                    if (temploc[0].getId() == start && temploc[1].getId() == end){
                        tempPath.MSTselected = 1;
                        break;
                    }
                    else if (temploc[0].getId() == end && temploc[1].getId() == start) {
                        tempPath.MSTselected = 1;
                        break;
                    }
                }
                labeled[end] = true;
                
            }
            else {
                for(int i = 0; i < size; i++){
                    if(labeled[i] == false){
                        labeled[i] = true;
                        break;
                    }
                }
                
            }
        }
                
    }
    private boolean allLabeled(boolean[] check,int size){
        for(int i = 0; i < size; i++){
            if(check[i] == false)
            	return false;
        }
        return true;
    }
    
    
    
}

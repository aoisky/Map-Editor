

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * This class is where you keep track of all your locations and edges
 * and you draw them in the draw() method.
 */
public class MapScene implements Scene {
  private final int INSERTLOC = 1;
  private final int DELETELOC = 2;
  private final int INSERTPATH = 3;
  private final int DELETEPATH = 4;

  private int locId = 0;
  private ChangeListener _listener;
  private Image _image;

  private Graph graph;
  private int mode = 0;
  
  private Point _lineStart;
  private Point _lineEnd;
  
  private Location movingLoc;


  public MapScene(Image image, Graph graph) {
    _image = image;
    this.graph = graph;
    int currentId = graph.getCurrentLocId();
    
    if(currentId > 0){
      locId = graph.getCurrentLocId() + 1;
    }
  }


  /**
   * Call this method whenever something in the map has changed that
   * requires the map to be redrawn.
   */
  private void changeNotify() {
    if (_listener != null) _listener.stateChanged(null);
  }


  public void changeMode(int mode){
	  this.mode = mode;
  }
  
  private int getLocId(){
	  return locId++;
  }
  /**
   * This method will draw the entire map.
   */
  public void draw(Graphics2D g) {
    // Draw the map image
    g.drawImage(_image, 0, 0, null);
    //Draw Locations
    drawPath(graph.getPathList(), g);
    drawLoc(graph.getLocList(),g);
    drawSelectedLoc(graph.getLocList(),g);
  
    if(mode == INSERTPATH){
    	g.setColor(Color.BLUE);
	    g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	    if (_lineStart != null && _lineEnd != null) {
	      g.drawLine(_lineStart.x, _lineStart.y, _lineEnd.x, _lineEnd.y);
	    }
    }
  }
  
  public void drawLoc(LinkedList<Location> locList, Graphics2D g){
	  for(Location loc : locList){
		  loc.draw(g);
	  }
  }
  
  public void drawSelectedLoc(LinkedList<Location> locList, Graphics2D g){
	  for(Location loc : locList){
		  if(loc.selected()){
		  loc.drawSelect(g);
		  }
	  }
  }
  
  public void drawPath(LinkedList<Path> pathList,Graphics2D g){
	// Draw the line
	  for(Path path : pathList){
		Point startPoint = path.getLoc()[0].getPoint();
		Point endPoint = path.getLoc()[1].getPoint();
	    g.setColor(Color.YELLOW);
	    
	    if(path.selected == true){
	    	g.setColor(Color.GREEN);
	    }
	    
	    if(path.MSTselected == 1){
	    	g.setColor(Color.GREEN);
	    }
	    
	    if(path.MSTselected == 0){
	    	g.setColor(new Color(0,0,0,0));
	    }
	    
	    g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	    if (startPoint != null && endPoint != null) {
	      g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
	    }
	  }
  }

  public void mouseClicked(Point p, MouseEvent e){
        if(mode != DELETELOC){
        	if(e.getClickCount() == 2){
        		locProperty(p);
        	}
        }
	  
	    if(mode == DELETELOC){
	    	deleteLocMode(p);
	    }
	    
  }
  
  public void mousePressed(Point p) {
    // Mark the beginning of the line
    _lineEnd = null;
    _lineStart = p;
    
    if(mode == INSERTLOC){
    	movingLoc = graph.checkSelectLoc(p);
        insertLocMode(p);
    }
    
    if(mode == DELETEPATH){
    	deletePathMode(p);
    }
    
  }

  public void mouseDragged(Point p) {
    // Mark the end of the line
    
	 if(mode == INSERTLOC){
        moveLoc(p);  //move Location in insert mode
	 }
	  
    if(mode == INSERTPATH){
    	_lineEnd = p;
    }
    changeNotify(); // redraw the map
  }
  
  public void mouseReleased(Point p){
	  
	  if(mode == INSERTPATH){
		  if(graph.checkSelectLoc(p) == null){
			  _lineEnd = null;
		  }
		  else{
		      insertPathMode(p);
		      _lineEnd = null;
		  }
	  }
	  changeNotify();
  }

  public int getWidth() { return _image.getWidth(null); }
  public int getHeight() { return _image.getHeight(null); }

  
  private void moveLoc(Point p){
	  Location loc = movingLoc;
	  if(loc != null){
		  loc.setPoint(p);
	  }
  }
  
  private void insertLocMode(Point p){
  	Location loc = graph.checkSelectLoc(p);
  	if(loc != null){
  		graph.clearSelect();
  		loc.setSelect(true);
  	}else{
  	    graph.addLoc(p, getLocId());
  	}
  	changeNotify();
  }
  
  private void deleteLocMode(Point p){
	  	Location loc = graph.checkSelectLoc(p);
	  	if(loc != null){
	  		graph.removeLoc(loc);
	  	}
	  	changeNotify();
  }
  
  private void insertPathMode(Point p){
	// Draw the line
	  Location locStart = graph.checkSelectLoc(_lineStart);
	  Location locEnd = graph.checkSelectLoc(p);
	  
	  if(locStart != null && locEnd != null && locStart != locEnd){
		  graph.addPath(locStart, locEnd);
	  }
  }
  
  private void deletePathMode(Point p){
	  Path path = graph.checkSelectPath(p);
	  	if(path != null){
	  		graph.removePath(path);
	  	}
	  	changeNotify();
  }

  
  public void setLocProperty(){
	  Location selectedLoc = null;
	  
	  for(Location loc : graph.getLocList()){
		  if(loc.selected()){
			  selectedLoc = loc;
			  break;
		  }
	  }
	  
	  if(selectedLoc != null){
		  String name = selectedLoc.getName();
		  int locId = selectedLoc.getId();
		  Point locPoint = selectedLoc.getPoint();
	      String locName = (String)JOptionPane.showInputDialog(
                  null, "Location Name: " + name + "\n"
                  + "Location Id: " + locId + "\n"
                  + "x: " + locPoint.x + "\n"
                  + "y: " + locPoint.y + "\n"
                  + "Please enter a Location Name",
                  "Location Property",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  null,
                  name);

          if ((locName != null) && (locName.length() > 0)) {
        	  selectedLoc.setName(locName);
            return;
          }

	  }
	  
  }
  
  private void locProperty(Point p){
	  Location loc = graph.checkSelectLoc(p);

	  if(loc != null){
		  String name = loc.getName();
		  int locId = loc.getId();
		  Point locPoint = loc.getPoint();
	      String locName = (String)JOptionPane.showInputDialog(
                  null, "Location Name: " + name + "\n"
                  + "Location Id: " + locId + "\n"
                  + "x: " + locPoint.x + "\n"
                  + "y: " + locPoint.y + "\n"
                  + "Please enter a Location Name",
                  "Location Property",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  null,
                  name);

          if ((locName != null) && (locName.length() > 0)) {
            loc.setName(locName);
            return;
          }

	  }
  }
  public void addChangeListener(ChangeListener listener) {
    _listener = listener;
  }
}

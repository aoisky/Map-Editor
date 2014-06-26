

import java.awt.*;

public class Location {
  private static final int SIZE = 10;
  private final static float dash1[] = {3.0f};
  private final static BasicStroke dashed = new BasicStroke(1.0f, 
                                            BasicStroke.CAP_BUTT, 
                                            BasicStroke.JOIN_MITER, 
                                            3.0f, dash1, 0.0f);

  private String name;
  private int id;
  private Point _point;
  private boolean _selected = false;

  public Location(Point point, int id) {
    _point = point;
    this.id = id;
  }

  public Location(String locName, Point point, int id) {
	    name = locName;
	    _point = point;
	    this.id = id;
  }
  
  public void draw(Graphics g) {
    g.setColor(Color.RED);
    g.fillOval((int) _point.getX() - SIZE/2, 
               (int) _point.getY() - SIZE/2, 
               SIZE, SIZE);
  }

  public void drawSelectRect(Graphics g){
	  Graphics2D g2 = (Graphics2D) g;
	  g2.setColor(Color.BLACK);
	  g2.setStroke(dashed);
	  g.drawRect((int) _point.getX() - SIZE/2,
	               (int) _point.getY() - SIZE/2,
	               SIZE, SIZE);
  }
  
  public void drawSelect(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
	  g2.setColor(Color.BLUE);
	  g.fillOval((int) _point.getX() - SIZE/2,
            (int) _point.getY() - SIZE/2,
            SIZE, SIZE);

  }

  
  public void setSelect(boolean select){
	  _selected = select;
  }
  
  public boolean selected(){
	  return _selected;
  }
  
  public void setName(String name){
	  this.name = name;
  }
  
  public String getName(){
	  return name;
  }
  
  public int getId(){
	  return id;
  }
  
  public void setPoint(Point p){
	  _point = (Point) p.clone();
  }
  
  public Point getPoint(){
	  return _point;
  }
  

  /**
   * Return true if this point is inside of this location.
   */
  public boolean isSelected(Point p) {
    int x = (int) _point.getX();
    int y = (int) _point.getY();
    int px = (int) p.getX();
    int py = (int) p.getY();
    int radius = SIZE / 2;
    return px > x - radius && px < x + radius && 
           py > y - radius && py < y + radius;
  }
}

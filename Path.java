import java.awt.Point;


public class Path {

	
	private Location startLoc;
	private Location endLoc;
    public boolean selected = false;
    public int MSTselected = -1;
	
	public Path(Location startLoc,Location endLoc){
		this.startLoc = startLoc;
		this.endLoc = endLoc;
	}
	
	public Location[] getLoc(){
		Location[] locs = new Location[2];
		locs[0] = startLoc;
		locs[1] = endLoc;
		return locs;
	}
	
    public int length(){
        Point startLocP = this.startLoc.getPoint();
        Point endLocP = this.endLoc.getPoint();
        int locDistance = (int)Math.sqrt(Math.pow(endLocP.getX() - startLocP.getX(), 2.0D) + Math.pow(endLocP.getY() - startLocP.getY(), 2.0D));
        return locDistance;
    }
	
	public boolean isSelected(Point point){
		Point startLocP = startLoc.getPoint();
		Point endLocP = endLoc.getPoint();
		
		int startX, startY, endX, endY;
		
		if(startLocP.x < endLocP.x){
			startX = startLocP.x;
			endX = endLocP.x;
		}else{
			startX = endLocP.x;
			endX = startLocP.x;
		}
		
		if(startLocP.y < endLocP.y){
			startY = startLocP.y;
			endY = endLocP.y;
		}else{
			startY = endLocP.y;
			endY = startLocP.y;
		}
		
		if(point.x >= startX && point.x <= endX && point.y >= startY && point.y <= endY){
			
			int pointDistance1 = (int) Math.sqrt(Math.pow(point.getX() - startLocP.getX(),2.0)+ Math.pow(point.getY() - startLocP.getY(),2.0));
			int pointDistance2 = (int) Math.sqrt(Math.pow(point.getX() - endLocP.getX(),2.0)+ Math.pow(point.getY() - endLocP.getY(),2.0));
			int locDistance = (int) Math.sqrt(Math.pow(endLocP.getX() - startLocP.getX(),2.0)+ Math.pow(endLocP.getY() - startLocP.getY(),2.0));
			
		   if(pointDistance1 + pointDistance2 == locDistance)
			   return true;
		}
		return false;
	}
	
}

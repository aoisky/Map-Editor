import java.awt.Point;
import java.util.LinkedList;


public class Graph {

	private LinkedList<Location> locList;
	private LinkedList<Path> pathList;
	private String bitmapName;
	private double pixelConstant;
	
	public Graph(String bitmapName, double pixelConstant){
		this.bitmapName = bitmapName;
		this.pixelConstant = pixelConstant;
		
		locList = new LinkedList<Location>();
		pathList = new LinkedList<Path>();
	}
	
	public String getBitName(){
		return bitmapName;
	}
	
	public double getPixelConstant(){
		return pixelConstant;
	}
	
	public LinkedList<Location> getLocList(){
		return locList;
	}
	
	public int getCurrentLocId(){
		int locId = 0;
		for(Location loc : locList){
			if(loc.getId() > locId){
				locId = loc.getId();
			}
		}
		return locId;
	}
	
	
	public LinkedList<Path> getPathList(){
		return pathList;
	}
	
	public void addLoc(Point p, int id){
		Location loc = new Location(p, id);
		locList.add(loc);
	}
	
	public void addLoc(String locName, Point p, int id){
		Location loc = new Location(locName, p, id);
		locList.add(loc);
	}
	
	public void addPath(Location startLoc, Location endLoc){
		if(!isExistPath(startLoc,endLoc)){
		    Path path = new Path(startLoc, endLoc);
		    pathList.add(path);
		}
	}
	
	public void addPath(int startLocId, int endLocId){
		Location startLoc = null, endLoc = null;
		
		for(Location loc : locList){
			if(loc.getId() == startLocId){
		        startLoc = loc;		
			}
			
			if(loc.getId() == endLocId){
				endLoc = loc;
			}

			if(startLoc != null && endLoc != null)
				break;
		}
		
		addPath(startLoc, endLoc);
		
	}
	
	public void clearSelect(){
		for(Location loc : locList){
			if(loc.selected()){
			  loc.setSelect(false);
			}
		}
	}
	
	public Location checkSelectLoc(Point point){
        for(Location loc : locList){
        	if(loc.isSelected(point)){
        		return loc;
        	}
        }
        return null;
	}
	
	public Path checkSelectPath(Point point){
		for(Path path : pathList){
			if(path.isSelected(point))
				return path;
		}
		return null;
	}
	
	public void removeLoc(Location loc){
		
		LinkedList<Path> removedPath  = new LinkedList<Path>();
		for(Path p: pathList){
			if(p.getLoc()[0] == loc || p.getLoc()[1] == loc){
				removedPath.add(p);
			}
		}
		
		for(Path removedP : removedPath){
			pathList.remove(removedP);
		}
		locList.remove(loc);
	}
	
	public void removePath(Path path){
		pathList.remove(path);
	}
	
	private boolean isExistPath(Location startLoc, Location endLoc){
		for(Path p : pathList){
			if((p.getLoc()[0] == startLoc && p.getLoc()[1] == endLoc) || (p.getLoc()[0] == endLoc && p.getLoc()[1] == startLoc) )
				return true;
		}
		return false;
	}
	
}

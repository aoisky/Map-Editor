import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLHandler {

	
	public static void saveMap(Graph graph, File file){

		FileWriter mapWriter;
		try {
			mapWriter = new FileWriter(file.getAbsolutePath());
			BufferedWriter out = new BufferedWriter(mapWriter);
		    out.write("<mapfile bitmap=\"" + graph.getBitName() + "\" scale-feet-per-pixel=\"" + graph.getPixelConstant() + "\">" +"\n");
		    for(Location loc : graph.getLocList()){
		    	int id = loc.getId();
		    	String name = loc.getName();
		    	if(name == null)
		    		name = "";
		    	int x = loc.getPoint().x;
		    	int y = loc.getPoint().y;
		    	
		    	out.write("<location id=\"" + id + "\" name=\"" + name + "\" x=\"" + x + "\" y=\"" + y + "\" />" +"\n");
		    }
		    
		    for(Path path : graph.getPathList()){
		    	int startId = path.getLoc()[0].getId();
		    	int endId = path.getLoc()[1].getId();
		    	
		    	out.write("<path idfrom=\"" + startId + "\" idto=\"" + endId + "\" " + "type=" + "\"undirected\" />" + "\n");
		    }
		    
		    out.write("</mapfile>\n");
		    
		    out.close();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Graph parseMap(File file){
		Graph graph = null;
		try{
			File XMLMapFile = file;
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder xmlBuilder = xmlFactory.newDocumentBuilder();
			Document xmlMap = xmlBuilder.parse(XMLMapFile);
			
			String bitmapName = xmlMap.getDocumentElement().getAttribute("bitmap");
			Double pixelConstant = Double.parseDouble(xmlMap.getDocumentElement().getAttribute("scale-feet-per-pixel"));
			graph = new Graph(bitmapName, pixelConstant);
			
            NodeList locNodes = xmlMap.getElementsByTagName("location");
            NodeList pathNodes = xmlMap.getElementsByTagName("path");
            
            for(int i = 0; i < locNodes.getLength(); i++){
            	Node locNode = locNodes.item(i);
            	
            	if (locNode.getNodeType() == Node.ELEMENT_NODE) {
            	  Element locElement = (Element) locNode;
            	  int locX = Integer.parseInt(locElement.getAttribute("x"));
            	  int locY = Integer.parseInt(locElement.getAttribute("y"));
            	  String locName = locElement.getAttribute("name");
            	  int id = Integer.parseInt(locElement.getAttribute("id"));
            	  
            	  if(locName == ""){
            		  locName = null;
            	  }
            	  
            	  Point point = new Point(locX, locY);
            	  graph.addLoc(locName, point, id);
            	}
            }
            
            for(int i = 0; i < pathNodes.getLength(); i++){
            	Node pathNode = pathNodes.item(i);
            	
            	if (pathNode.getNodeType() == Node.ELEMENT_NODE) {
            	  Element pathElement = (Element) pathNode;
            	  int idFrom = Integer.parseInt(pathElement.getAttribute("idfrom"));
            	  int idTo = Integer.parseInt(pathElement.getAttribute("idto"));
            	  
            	  graph.addPath(idFrom, idTo);
            	}
            }
		}catch(Exception e){
			
		}
		return graph;
		
	}
	
}

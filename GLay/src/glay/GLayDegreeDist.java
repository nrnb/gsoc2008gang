/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Testing purposes, degree distribution;
 * Obsolete ***
 */

package glay;

/**
 *
 * @author sugang
 */
import cytoscape.layout.AbstractLayout;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Date;
import cytoscape.view.CyNodeView;
import giny.view.NodeView;
import java.util.Random;
import java.util.Arrays;
import cytoscape.visual.Appearance;
import flanagan.math.PsRandom; //Advanced Random Number generator
import java.awt.Color;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.Cytoscape;
import cytoscape.visual.NodeShape;


public class GLayDegreeDist extends AbstractLayout{
    public GLayDegreeDist(){
    
    }
    
    /*Property Name*/
    public String toString(){
        return "G.Layout";
    }
    
    /*Display Name*/
    public String getName(){
        return "GLayout";
    }
    
    public void construct(){
        /*Construct the layout here*/
        /*The cryptic part is that it can access the hidden variables easily*/
        //System.out.println("Construct was called.");
        /*Specify the coordinates*/
        
        //Not exactly sure what this is for
        //this.initialize();
        //This should be the best way? Use iterator
        //Has to use the giny cast node view.
        
        //Very simple layout algorithm, plot via degree distribution
        
        Iterator<NodeView> nI = this.networkView.getNodeViewsIterator();
       
        Random gen = new Random();
        
        int maxDegree = 0;
        int minDegree = 1000000;
        
        //First iteration, find max and min degree
        
        //Let's try some visual style here too
        String styleName = "myVisualStyle";
        VisualStyleBuilder graphStyle = new VisualStyleBuilder(styleName, false);
        graphStyle.setNodeSizeLocked(false); //lock unlock node size
        
        
        
        while(nI.hasNext()){
            //No casting required
            NodeView nV = nI.next();
            
            maxDegree = nV.getDegree()>maxDegree?nV.getDegree():maxDegree;
            minDegree = nV.getDegree()<minDegree?nV.getDegree():minDegree;
            
            //System.out.println(nV.getDegree());
            //The efficient way should not be calling the update each time. Probably we should set update to false.
            //To set X and Y at the same time, use set OffSet
            //Vis mapping information lost? no.
            
            
            //place nodes according to degree
            //nV.setOffset(gen.nextInt(2000), gen.nextInt(2000));
            
            //The default size is 40, transparency is 0.1
            
            
            //Random set selected
            //boolean selected =  gen.nextBoolean();
            //if(selected){
            //    nV.setSelected(true);
            //}
            
            //Random Transparency
            //nV.setTransparency(new Float(0.5));
            
            
            //System.out.println(nV.getHeight() + " " + nV.getWidth() + " " + nV.getTransparency());
        }
        
        //System.out.println("Max Degree:" + maxDegree + " Min Degree:" + minDegree);
        //System.out.println("Total Nodes:" + this.networkView.getNodeViewCount());
        //Calendar cal = Calendar.getInstance();
        
        //Can either loop with iterator or with the for loop
        /*
        nI = this.networkView.getNodeViewsIterator();
        int spacing = 200;
        while(nI.hasNext()){
            NodeView nV = nI.next();
            nV.setOffset(spacing*nV.getDegree()+gen.nextInt(100), gen.nextInt(100));
        }
        */
        
        //initialize
        int [] yOffset = new int[maxDegree+1];
        for(int i=0; i<yOffset.length; i++){
            yOffset[i]=0;
                
            
        }
        
        int xSpacing = 100;
        int ySpacing = 50;
        nI = this.networkView.getNodeViewsIterator();
        while(nI.hasNext()){
            NodeView nV = nI.next();
            nV.setOffset(xSpacing*nV.getDegree(),ySpacing + yOffset[nV.getDegree()]*ySpacing);
            yOffset[nV.getDegree()]--;
            
            //nV.getNode().getRootGraphIndex;
            //neighbors array, neighbors list are all deprecated
           //System.out.println("Neighbours:" + 
           //            Arrays.toString(this.network.neighborsArray(nV.getNode().getRootGraphIndex())
           //        ));
            
            
            
           /*
            * This is where it's interesting.
            * The Opcacity attribute never worked
            * The node fill color is a string, how do i cover from java.awt.Color? by concatenating the int RGB values into #123456?
            * The rounded rectangle shape doesn't work, still the sharp edged rectangle(even i choose manually in the vismapper UI in cytoscape)
            * The nodesize intput doesn't work, why? althought the height and width params works
            */ 
            
            
            
            
            
            
            
           //graphStyle.addProperty( nV.getNode().getIdentifier(), VisualPropertyType.NODE_OPACITY, "0.5");
           graphStyle.addProperty( nV.getNode().getIdentifier(), VisualPropertyType.NODE_FILL_COLOR, "#E1E1E1");
           graphStyle.addProperty(nV.getNode().getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
           //graphStyle.addProperty(nV.getNode().getIdentifier(), VisualPropertyType.NODE_WIDTH, Integer.toString(gen.nextInt(50)+50));
           //graphStyle.addProperty(nV.getNode().getIdentifier(), VisualPropertyType.NODE_HEIGHT, Integer.toString(gen.nextInt(50)+10));
           graphStyle.addProperty(nV.getNode().getIdentifier(), VisualPropertyType.NODE_SIZE, Integer.toString(gen.nextInt(50)+10));
           
        
        }
        
        graphStyle.buildStyle();
        GlobalAppearanceCalculator gac = Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
        gac.setDefaultBackgroundColor(Color.white);
        
        //Need to refresh the view? weird. layout; 
        //
        Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
        //System.out.println(Integer.toString(gen.nextInt(50)+50));
    }
    
}

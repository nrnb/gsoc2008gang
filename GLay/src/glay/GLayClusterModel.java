/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Create a random graph, then spread the nodes into clusters
 * Testing purposes, Obsolete ****
 */

package glay;

/**
 *
 * @author sugang
 */

import cytoscape.layout.AbstractLayout;
import cytoscape.data.CyAttributes;
import cytoscape.Cytoscape;
import cytoscape.visual.VisualStyle;
import cytoscape.groups.*;
import cytoscape.visual.*;

import glay.util.Cluster;
import glay.util.GLayVisualStyle;

import java.util.*;

import giny.view.NodeView;
import giny.view.EdgeView;
import giny.model.Node;


//java random number generator is sufficient
import java.util.Random;


public class GLayClusterModel extends AbstractLayout{
    public GLayClusterModel(){
    
    }
    
    public String toString(){
        return "GLay.RandomClusterModel";   
    }
    
    public String getName(){
        return "GRCM";
    }
    
    public void construct(){
        //List<CyGroup> groupList = CyGroupManager.getGroupList();
        
        //it's kinda messy as all groups are collapsed into one group manager.
        //how do i get the groups belong to one network?
        //for(CyGroup group:groupList){
        //    System.out.println(group.getNodes().size());
        //}
        //int[] nodeIndexArray = this.network.getNodeIndicesArray();
        //for(int i:nodeIndexArray){
        //    
        //}
        CyAttributes attrN = Cytoscape.getNodeAttributes();
        CyAttributes attrE = Cytoscape.getEdgeAttributes();
        
        //first need to check out how many clusters are there
        Iterator<NodeView> nVI = this.networkView.getNodeViewsIterator();
        HashSet<Integer> hs = new HashSet<Integer>();
        while(nVI.hasNext()){
            NodeView nv = nVI.next();
            int membership = attrN.getIntegerAttribute(nv.getNode().getIdentifier(), "membership");
            hs.add(membership); 
        }
        System.out.println("Number of Clusters in the data:" + hs.size());
        
        //generate cluster center, temporary done like this
        int clusterCount = hs.size();
        int[][] clusterCoord = new int[2][clusterCount];
        Random gen = new Random();
        int spacing = 1000;
        for(int i=0; i<clusterCount; i++){
            clusterCoord[0][i] = (int)Math.round((spacing+gen.nextInt(500))*Math.sin(gen.nextDouble()*2*Math.PI));
            clusterCoord[1][i] = (int)Math.round((spacing+gen.nextInt(500))*Math.cos(gen.nextDouble()*2*Math.PI));
        }
        
        //set node coordinates
        //iterate again, set location
        int spacingCluster = 200;
        nVI = this.networkView.getNodeViewsIterator();
        while(nVI.hasNext()){
            NodeView nV = nVI.next();
            
            int membership = attrN.getIntegerAttribute(nV.getNode().getIdentifier(), "membership");
            
            
            int xCoord = clusterCoord[0][membership] + (int)Math.round(spacingCluster*Math.sin(gen.nextDouble()*2*Math.PI));
            int yCoord = clusterCoord[1][membership] + (int)Math.round(spacingCluster*Math.sin(gen.nextDouble()*2*Math.PI));
            
            //nV.setOffset(xCoord, yCoord);
            nV.setXPosition(xCoord, false);
            nV.setYPosition(yCoord, false);
            
            
        }
        
        //VisualStyle vs = GLayVisualStyle.createClusterVisualStyle(this.network);
        
        
        //still need to solve this visual style problem
        //Cytoscape.getVisualMappingManager().setVisualStyle("GLay Visual"+this.network.getIdentifier());
        
        //only redraw graph once
        this.networkView.updateView();
    }

}

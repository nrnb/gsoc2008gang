/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

import cytoscape.visual.*;

import glay.util.Cluster;
import glay.util.GLayVisualStyle;

import java.util.*;
import giny.view.NodeView;
import giny.view.EdgeView;

import java.util.Random;

public class GLayCluster extends AbstractLayout{
    private Cluster clusterEngine;
    private CyAttributes cyNodeAttr;
    private CyAttributes cyEdgeAttr;
    private String edgeMembershipAttr;
    
    public GLayCluster(){
        clusterEngine = new Cluster();
        cyNodeAttr = Cytoscape.getNodeAttributes();
        cyEdgeAttr = Cytoscape.getEdgeAttributes();
        edgeMembershipAttr = "edgeMembership";
    }
    
    public String toString(){
        return "G.Cluster";   
    }
    
    public String getName(){
        return "GCluster";
    }
    
    public void construct(){
        //generate 10 clusters with random membership
        //can also assign other memberships
        clusterEngine.randomMembership(this.networkView, 10);
        
        
        //has to detect how many clusters are there
        Iterator<NodeView> nI = this.networkView.getNodeViewsIterator();
        NodeView nV;
        
        //Iterate all nodes to see how many clusters are there?
        //create nodes which resemble the cluster center?
        
        HashSet<Integer> clusterMembership = new HashSet();
        
        while(nI.hasNext()){
            nV = nI.next();
            //System.out.println(this.cyNodeAttr.getAttribute(nV.getNode().getIdentifier(), clusterEngine.getAttrName()));
            //String num = "10";
            //int membership2 = (int)num; 
            //The conversion of datatype is a pain.
            int membership = this.cyNodeAttr.getIntegerAttribute(nV.getNode().getIdentifier(), clusterEngine.getAttrName());
            clusterMembership.add(membership);
            this.cyNodeAttr.setAttribute(nV.getNode().getIdentifier(), "Degree", nV.getDegree());
            this.cyNodeAttr.setAttribute(nV.getNode().getIdentifier(), "Opacity", Math.random());
            
        }
        
        //System.out.println(clusterMembership.size());
        int clusterCount = clusterMembership.size();
        int[][] clusterCoord = new int[2][clusterCount];
        Random gen = new Random();
        
        
        int spacing = 1000;
        for(int i=0; i<clusterCount; i++){
            clusterCoord[0][i] = (int)Math.round(spacing*Math.sin(gen.nextDouble()*2*Math.PI));
            clusterCoord[1][i] = (int)Math.round(spacing*Math.cos(gen.nextDouble()*2*Math.PI));
        }
        
        //iterate again, set location
        int spacingCluster = 200;
        nI = this.networkView.getNodeViewsIterator();
        while(nI.hasNext()){
            nV = nI.next();
            
            int membership = this.cyNodeAttr.getIntegerAttribute(nV.getNode().getIdentifier(), clusterEngine.getAttrName());
            
            
            int xCoord = clusterCoord[0][membership] + (int)Math.round(spacingCluster*Math.sin(gen.nextDouble()*2*Math.PI));
            int yCoord = clusterCoord[1][membership] + (int)Math.round(spacingCluster*Math.sin(gen.nextDouble()*2*Math.PI));
            
            nV.setOffset(xCoord, yCoord);
            
            
        }
        
        
        
        
        
        /*Assign edge membership, either intra cluster or inter cluster*/
        Iterator<EdgeView> eI = this.networkView.getEdgeViewsIterator();
        EdgeView eV;
        
        int intraEdgeCount = 0;
        int interEdgeCount = 0;
        while(eI.hasNext()){
            eV = eI.next();
            //System.out.println(eV.getEdge().getSource().getIdentifier() + " " + eV.getEdge().getTarget().getIdentifier() + " " + eV.getEdge().isDirected());
            int membership1 = cyNodeAttr.getIntegerAttribute(eV.getEdge().getSource().getIdentifier(), clusterEngine.getAttrName());
            int membership2 = cyNodeAttr.getIntegerAttribute(eV.getEdge().getTarget().getIdentifier(), clusterEngine.getAttrName());
            if(membership1 == membership2){
                //intra-cluster edge
                cyEdgeAttr.setAttribute(eV.getEdge().getIdentifier(), this.edgeMembershipAttr, "intra");
                intraEdgeCount ++;
            }
            else{
                //inter-cluster edge
                cyEdgeAttr.setAttribute(eV.getEdge().getIdentifier(), this.edgeMembershipAttr, "inter");
                interEdgeCount ++;
            }
        }
        
        CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
        String[] names = cyNodeAttrs.getAttributeNames();
        for(int i=0; i<names.length; i++){
            System.out.println(names[i]);
        }
        
        
        //This shows the intra and inter counts.
        //The attributes can be mapped with color.
        System.out.println(intraEdgeCount + " " + interEdgeCount + " " + this.networkView.edgeCount());
        VisualStyle vs = GLayVisualStyle.createClusterVisualStyle(this.network);
        Cytoscape.getVisualMappingManager().setVisualStyle("TESTY");
       
    }
    
    //need to create a specific visual style
    
}

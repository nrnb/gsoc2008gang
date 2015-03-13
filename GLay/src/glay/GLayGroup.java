/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Generate random membership, obsolute ***
 */

package glay;

/**
 *
 * @author sugang
 */
import java.util.Random;
import java.util.Iterator;

import giny.view.NodeView;

import cytoscape.layout.AbstractLayout;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class GLayGroup extends AbstractLayout  {

    private int clusterCount;
    private int clusterSpacing;
    private Random gen;
    private CyAttributes cyNodeAttrs;
    
    
    public GLayGroup(){
        clusterCount = 10;
        clusterSpacing = 2000;
        gen = new Random();
        cyNodeAttrs = Cytoscape.getNodeAttributes();
    }
    
    public String toString(){
        return "G.Group";   
    }
    
    public String getName(){
        return "GGroup";
    }
    
    public void construct(){
        int totalNodes = this.networkView.nodeCount();
        
        //Are there any differences in implementing these methods?
        //They seem to be equivalent
        //System.out.println(this.networkView.nodeCount() +" "+ this.network.getNodeCount() +" "+ this.networkView.getNodeViewCount());
        
        //This is not the best model. the cluster membership should be an attribute of node initially, but 
        //extracted into an array to speed up.
        //A loop to extract membership from nodes into an array. As nodes could be sorted, so the membership array should be updated whenever
        //there's a sort-like operation
        
        
        int[] membership = this.clusterMembershipGenerator(totalNodes);
        int[][] coord = this.clusterCoordGenerator();
        
        //Arrange node coordinates wrt the cluster coordinates
        Iterator<NodeView> nI = this.networkView.getNodeViewsIterator();
        NodeView nV;
        
        //Better iteration methods? right now can only get the iterator
        
        
        
        int index = 0;
        while(nI.hasNext()){
            nV = nI.next();
            
            System.out.println(this.cyNodeAttrs.getAttribute(nV.getNode().getIdentifier(), "membership"));
            
            //Complete collapsing
            nV.setOffset(coord[0][membership[index]],coord[1][membership[index]]);
            
            //Added some randomness
            nV.setOffset(coord[0][membership[index]]+200+gen.nextInt(200),coord[1][membership[index]]+gen.nextInt(200)+200);
            
            
            index ++ ;
        }
        
        
    }
    
    private int[] clusterMembershipGenerator(int nodeCount){
        this.clusterCount = 10;
        //gen = new Random();
        int[] membershipArray = new int[nodeCount];
        for(int i=0; i<nodeCount; i++){
            membershipArray[i] = this.gen.nextInt(this.clusterCount);
        }
        //return membershipArray;
        
        //Set the clusterMembership as the attribute of the node
        Iterator<NodeView> nI = this.networkView.getNodeViewsIterator();
        while(nI.hasNext()){
            NodeView nV = nI.next();
            this.cyNodeAttrs.setAttribute(nV.getNode().getIdentifier(), "membership", this.gen.nextInt(this.clusterCount));
        }
        
        
        
        
        return membershipArray;
    }
    
    private int[][] clusterCoordGenerator(){
        int[][] coord = new int[2][clusterCount];
        for(int i=0; i<this.clusterCount; i++){
            coord[0][i] = gen.nextInt(clusterSpacing)+100;
            coord[1][i] = gen.nextInt(clusterSpacing)+100;
        }
        return coord;
    }
}

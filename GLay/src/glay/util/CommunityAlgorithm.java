/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * ** Obsolete ***
 */

package glay.util;


import cytoscape.*;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

import java.util.*;

import giny.model.*;


/**
 *
 * @author sugang
 */
public abstract class CommunityAlgorithm {
    protected int nodeCount;
    protected int edgeCount;
    protected CyNetwork network;
    protected CyNetworkView networkView;
    protected int[] graphIndex;
    protected int[] membership;
    protected String[] graphNodeName;
    
    //Shows the current progress
    protected volatile int progress;
    
    public CommunityAlgorithm(){
        this.network = Cytoscape.getCurrentNetwork();
        this.networkView = Cytoscape.getCurrentNetworkView();
        this.nodeCount = this.network.getNodeCount();
        this.edgeCount = this.network.getEdgeCount();
        if(nodeCount > 0){
            this.graphIndex = new int[nodeCount];
            this.membership = new int[nodeCount];
            this.graphNodeName = new String[nodeCount];
            
            //initialize, not here, as differnt methods have differnet initalizations.
            
            
        }
        else{
            //error handling
        }
    }
    
    //This method must be over-rideen to perform the clustering
    public abstract void execute();
    
    public int getCurrentProgress(){
        return this.progress;
    }
}

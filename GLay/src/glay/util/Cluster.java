/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Simple method to gen random memberships
 */

package glay.util;

/**
 *
 * @author sugang
 */

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

import java.util.Random;
import java.util.Iterator;
import giny.view.NodeView;


public class Cluster {
    private Random gen;
    private CyAttributes cyNodeAttr;
    private String clustAttr;
    
    public Cluster(){
        gen = new Random();
        cyNodeAttr = Cytoscape.getNodeAttributes();
        clustAttr = "cluster";
        
    }
    
    public void randomMembership(CyNetworkView nView, int clusterCount){
        Iterator<NodeView> nI = nView.getNodeViewsIterator();
        NodeView nV;
        
        while(nI.hasNext()){
            nV = nI.next();
            
            cyNodeAttr.setAttribute(nV.getNode().getIdentifier(), clustAttr, gen.nextInt(clusterCount));
        }
    
    }
    
    public String getAttrName(){
        return this.clustAttr;
    }
}

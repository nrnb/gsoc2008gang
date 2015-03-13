/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * *A backup copy of the fastGreedy algorithm
 */

package glay.util;

/**
 *
 * @author sugang
 */
import java.util.*;
import cytoscape.*;
import cytoscape.data.CyAttributes;


//use colt sparse matrix
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.function.*;

import giny.model.Node;
import giny.model.Edge;

import metaNodePlugin2.*;
import metaNodePlugin2.model.MetaNode;
import cytoscape.visual.VisualStyle;
import glay.util.GLayVisualStyle;

import cytoscape.groups.*;
import csplugins.layout.algorithms.force.ForceDirectedLayout;

import cytoscape.layout.algorithms.GridNodeLayout;


import glay.GLayClusterModel;

public class FastGreedy2 extends CommunityAlgorithm implements Runnable{
    private double modularity;
    private int mode;
    
    
    public void setMode(int mode){
        this.mode = mode;
    }
    
    public FastGreedy2(){
        //Call the parent constructor.
        //need to expand all the metanodes..this need to fixed when refactoring
        //MetaNode.expandAll();
        super();
        this.modularity = 0;
    }
    
    public double getModularity(){
        return this.modularity;
    }
    
    /*Still use the sparse matrix implementation, but with improved structure*/
    public void execute(){
        //expand all metanodes first
        
        
        
        System.out.println(nodeCount);
        System.out.println(edgeCount);
        System.out.println(network.getNodeCount());
        System.out.println(network.getEdgeCount());
        
        this.progress = 0;
        double[] ai = new double[nodeCount];
        double qInitial = 0;
        
        int counter = 0;
        //Iterator<Node> it = network.nodesIterator();
        //while(it.hasNext()){
        
        
        List<Node> nodeList = network.nodesList();
        int[] nodeIndexes = network.getNodeIndicesArray();
        Arrays.sort(nodeIndexes);
        
        //Collections.sort(nodeList); //can't sort, doesn't implement comparable
        for(int index:nodeIndexes){
        //Node node = it.next();
             
             graphIndex[counter] = index;//node.getRootGraphIndex();
             graphNodeName[counter] = network.getNode(index).getIdentifier();//node.getIdentifier();
             membership[counter] = counter;
             ai[counter] = network.getDegree(graphIndex[counter])/(2.0*edgeCount);
             qInitial -= ai[counter]*ai[counter];
             //System.out.println(network.getDegree(index));
             counter++;
        //}
        }
        
        SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        
        //if(this.mode == 0){
            MaxFunKW func = new MaxFunKW(ai, this.mode);
        //}
        double maxDeltaQ = 0;
        double q = 0;
        int maxI=0;
        int maxJ=0;
        double deltaQ = 0;
        
        for(int i=0; i<nodeCount; i++){
            for(int j=0; j<nodeCount; j++){
                if(i==j){
                    continue;
                }
                else if(network.getEdgeCount(network.getNode(graphIndex[i]), network.getNode(graphIndex[j]), false)>0 || network.getEdgeCount(network.getNode(graphIndex[j]), network.getNode(graphIndex[i]), false)>0){
                    deltaQ = (1.0d/(2*edgeCount)-(network.getDegree(graphIndex[i])*network.getDegree(graphIndex[j]))/(4.0*Math.pow(edgeCount, 2.0))) * 2; //initialize by 2 fold??
                    
                    //q += deltaQ;
                    
                    deltaQMx.setQuick(i, j, deltaQ);
                    if(maxDeltaQ <= deltaQ){
                        maxDeltaQ = deltaQ;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        
//Take a look at the matrix
//        for(int i=0; i<nodeCount; i++){
//            for(int j=0; j<nodeCount; j++){
//                System.out.printf("%2.3f ", deltaQMx.getQuick(i, j));
//                        
//                        //print(new PrintfFormat("%1.3d ").sprintf(deltaQMx.getQuick(i, j)));
//            }
//            System.out.println("");
//        }
        
        
        
        
        //make sure i,j are ordered
        if(maxI > maxJ){
            //swap
            int temp = maxI;
            maxI = maxJ;
            maxJ = temp;
        }
        
        q = qInitial;
        counter = 0;
        
        System.out.println("qInitial:" + qInitial);
        
        while(maxDeltaQ > 0){
            counter++;
            this.progress = (int)(100.0*counter/nodeCount);
            q += maxDeltaQ;
        
        
        for(int k=0; k<nodeCount; k++){
                if(k==maxJ || k==maxI){
                    continue;
                }
                else{
                    //that k is connected to both I and J
                    //we have an issue here, how to detect whether k is connected to i or j?
                    
                    if(deltaQMx.getQuick(maxI, k)!=0 && deltaQMx.getQuick(maxJ, k)!=0){
                        deltaQMx.setQuick(maxJ, k, deltaQMx.getQuick(maxJ, k)+deltaQMx.getQuick(maxI, k));
                        
                        
                        
                        //System.out.println("added");
                    }
                    else if(deltaQMx.getQuick(maxI, k)==0 && deltaQMx.getQuick(maxJ, k)!=0){
                        deltaQMx.setQuick(maxJ, k, deltaQMx.getQuick(maxJ, k)-2*ai[maxI]*ai[k]);
                    }
                    else if(deltaQMx.getQuick(maxJ, k)==0 && deltaQMx.getQuick(maxI, k)!=0 ){
                        deltaQMx.setQuick(maxJ, k, deltaQMx.getQuick(maxI, k)-2*ai[maxJ]*ai[k]);
                    }
                    else{
                        //both are zero
                        //nothing is done
                        
                        //it seems that nothing is wrong.
                        //but the result is not quite correct
                        //both are zero, no need to update
                        //System.out.println("This is wrong!");
                        //System.out.println("Do nothing.");
                    }
                }
            }//end update jth row
        
            int membershipI = membership[maxI];
            int membershipJ = membership[maxJ];
            for(int k=0;k<nodeCount; k++){
                deltaQMx.setQuick(k, maxJ, deltaQMx.getQuick(maxJ, k));
                deltaQMx.setQuick(maxI, k, 0.0);
                deltaQMx.setQuick(k, maxI, 0.0);
                if(membership[k] == membershipI){
                    membership[k] = membershipJ;
                }
            }
            
            ai[maxJ] = ai[maxI] + ai[maxJ];
            ai[maxI] = 0;
            
            deltaQMx.trimToSize();
            
            maxDeltaQ = 0;
            maxI = 0;
            maxJ = 0;
            func.reset(ai);
            
            deltaQMx.forEachNonZero(func);
            maxDeltaQ = func.max;
            maxI = func.row;
            maxJ = func.column;
            
            if(maxI > maxJ){
                //swap
                int temp = maxI;
                maxI = maxJ;
                maxJ = temp;
            }
            
            //System.out.println("maxDeltaQ" + maxDeltaQ);
        }//end of while loop
        
        
        //these all can be done in linear time    
        HashMap<Integer, Integer> membershipMapping = new HashMap<Integer, Integer>();
        int index=0;
        for(int i=0; i<membership.length; i++){
            if(membershipMapping.containsKey(new Integer(membership[i]))){
            
            }
            else{
                membershipMapping.put(new Integer(membership[i]), new Integer(index));
                index++;
            }
        }
        
        for(int i=0; i<membership.length; i++){
            membership[i] = membershipMapping.get(new Integer(membership[i])).intValue();
        }
        
        assignMembership(network, graphIndex, membership, membershipMapping);
        
        //System.out.println(q);
        this.modularity = q;
        //This notifies that the current thread is over
        this.progress = 100;
    }
    
    //this method may vary, right now it's fastgreedy 2 only
    private static void assignMembership(CyNetwork network, int[] graphIndex, int[] membership, HashMap<Integer, Integer> membershipMapping){
        //this.graphIndex
        //this.membership
        
        
        CyAttributes attrN = Cytoscape.getNodeAttributes();
        CyAttributes attrE = Cytoscape.getEdgeAttributes();
        
        ArrayList<List> nodeList = new ArrayList<List>();
        for(int i=0; i<membershipMapping.size(); i++){
            nodeList.add(new ArrayList<Node>());
        }
        
        //Create lists of nodes that will be created
        for(int i=0; i<network.getNodeCount(); i++){
            attrN.setAttribute(network.getNode(graphIndex[i]).getIdentifier(), "membership", membership[i]);
            attrN.setAttribute(network.getNode(graphIndex[i]).getIdentifier(), "type", 0); //the default type parameter is 0
            attrN.setAttribute(network.getNode(graphIndex[i]).getIdentifier(), "listActive", 1); //default
            
            //each list contains the nodes to be added
            nodeList.get(membership[i]).add(network.getNode(graphIndex[i]));
        }
        
        //This is related to metanode, this should be performed before the calculation?
//        MetaNode.expandAll();
//        List<CyGroup> grpList = CyGroupManager.getGroupList();
//        for(int i=0; i<grpList.size(); i++){
//            CyGroup grp = grpList.get(i);
//            if(grp.getGroupName().startsWith(network.getIdentifier())){
//                //MetaNode.getMetaNode(grp);
//                //remove all associated metanodes
//                
//                //MetaNode.removeMetaNode(grp.getGroupNode());
//                //System.out.println("removed metagrp!");
//                CyGroupManager.removeGroup(grp);
//            }
//        }
        
        //tweaks,create the new group
        for(int i=0; i<nodeList.size(); i++){
            CyGroup group = CyGroupManager.createGroup(network.getIdentifier()+"_"+i+"[CLUSTER]", nodeList.get(i), "metaNode");
            
            
            //add attribute to metaNode
            
            //make metaNodeDifferent
            attrN.setAttribute(group.getGroupNode().getIdentifier(), "type", 1);
            
            
            //it's always on the current network view
            //if(i < nodeList.size()-1){
            //    CyGroupManager.setGroupViewer(group, "metaNode", Cytoscape.getCurrentNetworkView(), false); //whether to notify for update
            //}
            //else{
                
                //could because the metanode plugin is not loaded yet, need to call when everything is ready
                CyGroupManager.setGroupViewer(group, "metaNode", Cytoscape.getCurrentNetworkView(), true);
            //}
            //The layout cast will not be done here    
        }
        
        //assign membership
                //iterates through all edges, assign membership
        int intraEdge = 0;
        int interEdge = 0;
        int totalEdge = 0;
        Iterator<Edge> it = network.edgesIterator();
        while(it.hasNext()){
            Edge e = it.next();
            if(
                    attrN.getIntegerAttribute(e.getSource().getIdentifier(),"membership") == attrN.getIntegerAttribute(e.getTarget().getIdentifier(),"membership")
               ){
                    //System.out.println();
                    //intra cluster edge
                    attrE.setAttribute(e.getIdentifier(), "class", 0);
                    intraEdge ++ ;
            }
            else{
                    //inter cluster edge
                    attrE.setAttribute(e.getIdentifier(), "class", 1);
                    interEdge ++;
            }
        }
    
    }
    
    public void run(){
        this.reset();
        this.execute();
    }
    
    public void reset(){
//This is related to metanode, this should be performed before the calculation?
        MetaNode.expandAll();
        List<CyGroup> grpList = CyGroupManager.getGroupList();
        for(int i=0; i<grpList.size(); i++){
            CyGroup grp = grpList.get(i);
            if(grp.getGroupName().startsWith(network.getIdentifier())){
                //MetaNode.getMetaNode(grp);
                //remove all associated metanodes
                
                //MetaNode.removeMetaNode(grp.getGroupNode());
                //System.out.println("removed metagrp!");
                CyGroupManager.removeGroup(grp);
            }
            else{
                System.out.println("WTF");
            }
            //CyGroupManager.
        }
    }
}

//better to implement as inner class
class MaxFun implements IntIntDoubleFunction{
        public double max;
        public int row;
        public int column;
        
        
        
        public MaxFun(){
            
            max = -Double.MAX_VALUE;
            row = 0;
            column = 0;
        }
        
        public void reset(){
            max = -Double.MAX_VALUE;
            row = 0;
            column = 0;
        }
        
        public double apply(int row, int col, double value){
            if(max < value){
                max = value;
                this.row = row;
                this.column = col;
            }
            return value;
            
        }
}

//better to implement as inner class
class MaxFunKW implements IntIntDoubleFunction{
        public double max;
        private double maxInternal; //for consolidation ratio
        public int row;
        public int column;
        private int mode;
        
        //first let's just use the number of degrees
        private double[] ai;
        
        
        public MaxFunKW(double[] ai, int mode){
            this.ai = ai;
            this.mode = mode;
            max = -Double.MAX_VALUE;
            maxInternal = -Double.MAX_VALUE;
            row = 0;
            column = 0;
        }
        
        public void reset(double[] ai){
            this.ai = ai;
            max = -Double.MAX_VALUE;
            maxInternal = -Double.MAX_VALUE;
            row = 0;
            column = 0;
        }
        
        public double apply(int row, int col, double value){
            //value won't be zero cuz it's foreach non-zero
            //just try to implement the consolidation ratio here
            
            //System.out.println(ratio);
            
            //for consolidation ratio, comparison is based on Q * ratio
            //the returned value is still value
            if(mode == 0){
                if(max < value){
                max = value;
                this.row = row;
                this.column = col;
                }
            }
            else if(mode == 1){
                double ratio = Math.min(this.ai[row]/this.ai[col], this.ai[col]/this.ai[row]);
                if(maxInternal < value*ratio){
                    maxInternal = value*ratio;

                    max = value;
                    this.row = row;
                    this.column = col;
                }
            }
            return value;
            
        }
}
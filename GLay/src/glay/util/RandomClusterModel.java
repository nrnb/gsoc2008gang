/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Creates various random cluster models as needed
 */

package glay.util;

/**
 *
 * @author sugang
 * Create a random clustered graph, with defined number of clusters, and a given cluster size distribution
 */


//Cystoscape imports
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.Cytoscape;
import cytoscape.view.*;
import cytoscape.data.*;
import cytoscape.groups.*;
import cytoscape.data.CyAttributes;

//Giny imports
import giny.view.*;

//Java imports
import java.util.*;



//Statistics imports
//import flanagan.math.PsRandom;
import cern.jet.random.*;


public class RandomClusterModel {
    //the indexes here must match the indexes in the combo box
    public static final int EXPONENTIAL_CSIZE_DIST = 2;
    public static final int POISSON_CSIZE_DIST = 1;
    public static final int UNIFORM_CSIZE_DIST = 0;
    public static final int NORMAL_CSIZE_DIST = 3; //this require an additional variance
    private double pIntra;
    private double pInter;
    //private double ratio;
    private int csizeDist;
    private int csizeMean;
    private int clusterNum;
    private int[] clusterSize;
    private int nodeNum;
    private int intraEdgeNumTotal;
    private int interEdgeNumTotal;
    private int intraEdgeNum;
    private int interEdgeNum;
    private int[] nodeIdInterval;
    private CyNetwork network;
    //private PsRandom gen;
    
    public RandomClusterModel(double pIntra, double ratio, int csizeDist){
        //validation required
        //ratio gives the ratio of intra/inter p, this ratio ranges from 0 to 1
        
        this.pIntra = pIntra;
        this.pInter = pIntra*ratio;
        this.csizeDist = csizeDist;
        //this.csizeMean = csizeMean;    
    }
    
    
    //Process clustersize generation
    private void genClusterSize(){
        clusterSize = new int[clusterNum];
        this.nodeNum = 0;
        this.intraEdgeNumTotal = 0;
        this.interEdgeNumTotal = 0;
        this.nodeIdInterval = new int[clusterNum];
        
        //gen = new PsRandom();
        //depend on which
        if(this.csizeDist == RandomClusterModel.EXPONENTIAL_CSIZE_DIST){
            for(int i=0; i<clusterNum; i++){
                
                clusterSize[i] = new Double(Exponential.staticNextDouble(1.0/this.csizeMean)).intValue();
                nodeNum += clusterSize[i];
                nodeIdInterval[i] = nodeNum;
                intraEdgeNumTotal += clusterSize[i]*(clusterSize[i]-1)/2;
            }
        }
        else if(this.csizeDist == RandomClusterModel.POISSON_CSIZE_DIST){
            for(int i=0; i<clusterNum; i++){
                clusterSize[i] = new Double(Poisson.staticNextInt(this.csizeMean)).intValue();
                nodeNum += clusterSize[i];
                nodeIdInterval[i] = nodeNum;
                intraEdgeNumTotal += clusterSize[i]*(clusterSize[i]-1)/2;
            }
        }
        //default is uniform
        else if(this.csizeDist == RandomClusterModel.UNIFORM_CSIZE_DIST){
            for(int i=0; i<clusterNum; i++){
                clusterSize[i] = this.csizeMean;
                nodeNum += clusterSize[i];
                nodeIdInterval[i] = nodeNum;
                intraEdgeNumTotal += clusterSize[i]*(clusterSize[i]-1)/2;
            }
        }
        
        //has to calculate inter-cluster size
        for(int i=0; i<clusterNum; i++){
            for(int j=i+1; j<clusterNum; j++){
                interEdgeNumTotal += (clusterSize[i]*clusterSize[j]);
            }
        }
        
        
        
        //return clusterSize;
        for(int i=0; i<clusterNum; i++){
            System.out.println(clusterSize[i] + " _ " + nodeIdInterval[i]);
        }
        
        this.interEdgeNum = new Double(this.interEdgeNumTotal * this.pInter).intValue();
        this.intraEdgeNum = new Double(this.intraEdgeNumTotal * this.pIntra).intValue();
        
        System.out.println("total Num of nodes:" + nodeNum);
        System.out.println("total Num of intra-cluster Edges:" + intraEdgeNumTotal);
        System.out.println("total Num of inter-cluster Edges:" + interEdgeNumTotal);
        System.out.println("total Num of edges:" + nodeNum*(nodeNum-1)/2);
        System.out.println("Edges need to be Gen intra" + intraEdgeNum);
        System.out.println("Edges need to be Gen inter" + interEdgeNum);
        
        //by now we know the nodeNum, we need to know the intervals
        //Then we should sampe for edges
        //intra-cluster. self-loops, duplicate edges are not permitted
        //This method is different as the result number of edges will be exact p
        //The O^2 method is simply loop through all the combinations, then create edges accordingly, there guaranteed no duplicates
        //G n p model is easier to code; G n model is more difficult[easier to use the cytoscape trick]
        
        
        
    }
    
    private void genNetwork(){
    //can only be called when clusterSize dist has been generated
    this.network = Cytoscape.createNetwork("Clustered Erdos-Renyi", false);
    ArrayList<CyNode> nodes = new ArrayList<CyNode>(this.nodeNum);
    CyAttributes attrN = Cytoscape.getNodeAttributes();
    CyAttributes attrE = Cytoscape.getEdgeAttributes();
    //CyNode[] nodes = new CyNode[this.nodeNum];
    //CyNetworkView nwv = Cytoscape.createNetworkView(nw);
    /*
    
    for(int i=0; i<this.nodeNum; i++){
        CyNode node = Cytoscape.getCyNode(new Integer(i).toString(), true);
        this.network.addNode(node);
        nodes[i]=node;
        
        //is create a view required? weird.
        //This can be done later too?
        //nwv.addNodeView(node.getRootGraphIndex());
        //only return a cyNetwork
    }
    */
    
    //this block creates the nodes, and node groups(clusters)
    
    //add cluster attributes to nodes as well, seems easier for some operations
    for(int i=0; i<this.nodeIdInterval.length; i++){
        int startId;
        int endId;
        ArrayList<CyNode> ids = new ArrayList<CyNode>();
        //note the start, end id
        if(i==0){
            startId=0;
            endId=this.nodeIdInterval[i];
        }
        else{
            startId=this.nodeIdInterval[i-1];
            endId = this.nodeIdInterval[i];
        }
        
        for(int id=startId; id<endId; id++){
            CyNode node = Cytoscape.getCyNode(new Integer(id).toString(), true);
            ids.add(node);
            this.network.addNode(node);
            nodes.add(node);
            attrN.setAttribute(node.getIdentifier(), "membership", i);
        }
        
        //if the group already exists, can't over write
        //all groups are collapsed into one single manager
        //use the unique identifier for the network
        this.network.getIdentifier();
        //CyGroupManager.createGroup(this.network.getIdentifier()+"_"+i, ids, i + "_viewer");
    }
    
    System.out.println("Created Nodes:" + nodes.size());
    
    //Let's just use the simple n p model, runs on n^2 time
    //can use multi threads to speed up tho.
    //intra-cluster and inter-cluster can use two threads to generate
    //Generate Edges:
    int intraCounter = 0;
    for(int i=0; i<this.nodeIdInterval.length; i++){
        int startId;
        int endId;
        if(i==0){
            startId=0;
            endId=this.nodeIdInterval[i];
        }
        else{
            startId=this.nodeIdInterval[i-1];
            endId = this.nodeIdInterval[i];
        }
        
        
        for(int source=startId; source<endId; source++){
            for(int target=source+1; target<endId; target++){
                if(Math.random() > this.pIntra){
                    continue;
                }
                else{
                    //create edge, there's a problem here. if it has been created, it must be created again.
                    //ignore here
                    //System.out.println(source + " " + target);
                    CyEdge edge = Cytoscape.getCyEdge(nodes.get(source), nodes.get(target), Semantics.INTERACTION, "default", false, false);
                    if(edge==null){
                        //create edge
                        edge = Cytoscape.getCyEdge(nodes.get(source), nodes.get(target), Semantics.INTERACTION, "default", true, false);
                        this.network.addEdge(edge);
                        intraCounter++;
                        attrE.setAttribute(edge.getIdentifier(), "class", 0);
                    }
                    else{
                        //do nothing
                    }
                    
                }
            }
        }
        
        
        
        
        
    }
    System.out.println("Created " + intraCounter + " intra-cluster edges.");
    
    
    //Create inter-cluster edges
    int interCounter = 0;
    for(int i=0; i<this.nodeIdInterval.length-1; i++){
        int startId1;
        int endId1;
        if(i==0){
            startId1=0;
            endId1=this.nodeIdInterval[i];
        }
        else{
            startId1=this.nodeIdInterval[i-1];
            endId1 = this.nodeIdInterval[i];
        }
        for(int j=i+1; j<this.nodeIdInterval.length; j++){
            
            //System.out.println("mark");
            int startId2 = this.nodeIdInterval[j-1];
            int endId2 = this.nodeIdInterval[j];
            //here we need to loop through and create edges
            for(int source=startId1; source<endId1; source++){
                for(int target=startId2; target<endId2; target++){
                    if(Math.random()>this.pInter){
                        continue;
                    }
                    else{
                        CyEdge edge = Cytoscape.getCyEdge(nodes.get(source), nodes.get(target), Semantics.INTERACTION, "default", false, false);
                        if(edge==null){
                        //create edge
                        edge = Cytoscape.getCyEdge(nodes.get(source), nodes.get(target), Semantics.INTERACTION, "default", true, false);
                        attrE.setAttribute(edge.getIdentifier(), "class", 1);
                        this.network.addEdge(edge);
                        interCounter++;
                    }
                    }
                }
            }
            
            
            
        
        }
        
    
    }
    System.out.println("Created " + interCounter + " inter-cluster edges.");
    
    
    
    
    
    //return nw;
    }
    
    
    
    public CyNetwork generateModel(int clusterNum, int clusterSizeMean){
        //validation required
        this.csizeMean = clusterSizeMean;
        this.clusterNum = clusterNum;
        this.genClusterSize();
        this.genNetwork();
        //create two threads to sample inter and intra cluster edges
        
        
        
        
        
        //Return a cynetwork, with nodes and edges
        //edges are marked with inter/intra attributes.
        //or just create cygroup from this clustering? may be way cleaner
        return this.network;
    }
    
    
    
    public static void main(String[] args){
        //ratio goes to 1, gen a network with no cluster
        RandomClusterModel rcm = new RandomClusterModel(0.2, 0.1, RandomClusterModel.POISSON_CSIZE_DIST);
        rcm.generateModel(4,32);
        /*
         * int[] cSize = new int[20];
        cSize = rcm.genClusterSize(20);
        for(int i=0; i<20; i++){
            System.out.println(cSize[i]);
        }
         */
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * The CORE of this plugin, the very first draft of the fast greedy algorithm
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
import cytoscape.layout.*;


public class FastGreedy implements Runnable {
    private volatile int progress;
    /*implements the row max heap, faster, but not done yet*/
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void run(){
        this.run_sm(Cytoscape.getCurrentNetwork());
        
        //so actually the layout cast is done here, try whether it works or not
        //Cytoscape.getCurrentNetworkView().applyLayout(new GLayClusterModel());
        
        
        //let's just apply on a group of nodes
        //Cytoscape.getCurrentNetworkView().applyLayout(new ForceDirectedLayout());
        
        //fuck, another null pointer, because i didn't import the entire plugin directory
        //fix this later
        CyLayoutAlgorithm alg = new ForceDirectedLayout(); //CyLayouts.getLayout("force-directed");
        //alg.setSelectedOnly(true);
        
        //get just one group, but need to notify metanode to expand/collapse
        //MetaNode.collapseAll();
        
        for(int j=0;j<CyGroupManager.getGroupList().size();j++){
        CyGroup grp = CyGroupManager.getGroupList().get(j); //get the first one
        List<CyNode> nodeList = grp.getNodes();
        List<CyEdge> edgeList = grp.getInnerEdges();
        
        int[] nodes = new int[nodeList.size()];
        int[] edges = new int[edgeList.size()];
        
        //nodes = nodeList.toArray(new CyNode[nodes.length]);
        //edges = nodeList.toArray(new CyEdge[edges.length]);
        for(int i=0; i<nodeList.size(); i++){
            nodes[i] = nodeList.get(i).getRootGraphIndex();
        }
        
        for(int i=0; i<edgeList.size(); i++){
            edges[i] = edgeList.get(i).getRootGraphIndex();
        }
        
        System.out.println(nodes.length);
        System.out.println(edges.length);
        
        
        //only apply layout on cluster 1, with the inner edges
        alg.unlockAllNodes();
        Cytoscape.getCurrentNetworkView().applyLayout(alg, nodes, edges);
        alg.unlockAllNodes();
        
        }
        
        
        
        
        
        VisualStyle vs = GLayVisualStyle.createClusterVisualStyle(Cytoscape.getCurrentNetwork());
        
        
        //still need to solve this visual style problem
        Cytoscape.getVisualMappingManager().setVisualStyle("GLay Visual"+Cytoscape.getCurrentNetwork().getIdentifier());
        
        
        //List<CyNode> nodeList;
        //List<CyEdge> edgeList;
        //CyNode[] nodes;
        //CyEdge[] edges;
        
        //let's try to apply layout on the subgraph
        /*
        List<CyGroup> grpList = CyGroupManager.getGroupList();
        for(int i=0; i<grpList.size(); i++){
            //apply layouts for each group
            //nodes = grpList.get(i).getNodes().toArray(new CyNode[grpList.get(i).getNodes().size()]);
            //edges = grpList.get(i).getInnerEdges().toArray(new CyEdge[grpList.get(i).getInnerEdges().size()]);
            //System.out.println(nodes.length + " " + edges.length);
            
            nodeList = grpList.get(i).getNodes();
            edgeList = grpList.get(i).getInnerEdges();
            
            
            int[] nodes = new int[nodeList.size()];
            int[] edges = new int[edgeList.size()];
            for(int j=0; j<nodeList.size(); j++){
                nodes[j] = nodeList.get(j).getRootGraphIndex();
            }
            for(int j=0; j<edgeList.size(); j++){
                edges[j] = edgeList.get(j).getRootGraphIndex();
                //edges[j] = 1;
            }

            //it didn't give me any errors..the layout thread just halted somewhere in the algorithm
            //Cytoscape.getCurrentNetworkView().applyLayout(new ForceDirectedLayout(), nodes, edges);
            
            //This line is never performed
            System.out.println("Done.");
            break;
        }
        
        //System.out.println(grpList.size());
        */
        
        
        
        
    }
    
    public int getCurrentProgress(){
        return this.progress;
    }
    
    //the consolidation ratio simply uses the size of cluster as a balancing factor
    
    
    
    
    
    //the heap implementaion, haven't been successful yet...
    public static void run_hp(CyNetwork network){
        //revised fast greedy method to boost performance
        int nodeCount = network.getNodeCount();
        int edgeCount = network.getEdgeCount();
        
        int[] graphIndex = new int[nodeCount];
        int[] membership = new int[nodeCount];
    
        String[] graphNodeName = new String[nodeCount];
        double[] ai = new double[nodeCount];
        double qInitial = 0;
        
        ArrayList<Integer> activeRows = new ArrayList<Integer>();
        
        int counter = 0;
        Iterator<Node> it = network.nodesIterator();
        while(it.hasNext()){
            Node node = it.next();
            graphIndex[counter] = node.getRootGraphIndex();
            graphNodeName[counter] = node.getIdentifier();
            membership[counter] = counter;
            
            ai[counter] = network.getDegree(graphIndex[counter])/(2.0*edgeCount);
            qInitial -= ai[counter]*ai[counter];
            activeRows.add(new Integer(counter));
            
            
            counter++;
            
        }
        
        //initialize ai and qInitial

        //for(int i=0; i<nodeCount; i++){

        //}
        
        //calculate the initial q, when every node is in the single cluster
        
        //for(int i=0; i<nodeCount; i++){
           
        //}
        //SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        
        //sparse matrix
        SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        SparseDoubleMatrix1D rowMaxHeap = new SparseDoubleMatrix1D(nodeCount);
        SparseDoubleMatrix1D rowMaxColHeap = new SparseDoubleMatrix1D(nodeCount);
        
        
        double maxDeltaQ = 0;
        double q = 0;
        int maxI=0;
        int maxJ=0;
        double deltaQ = 0;
        double rowMax = 0;
        //initializaion
        for(int i=0; i<nodeCount; i++){
            rowMax = 0;
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
                    if(rowMax < deltaQ){
                        rowMax = deltaQ;
                        
                        rowMaxColHeap.setQuick(i, j); //this stores which columns in the row holds the max
                    }
                }
            }//end of j loop
            rowMaxHeap.setQuick(i, rowMax);
        }
        //end of initialization
        if(maxI > maxJ){
            //swap
            int temp = maxI;
            maxI = maxJ;
            maxJ = temp;
        }
        //starting looping through
        
        q = qInitial;
        
        
        while(maxDeltaQ > 0){
            System.out.println("> " + q + " " + maxDeltaQ);
            q += maxDeltaQ;
            //print
            //System.out.println("q: " + q);
            
            
            
            
            //to be processed: jth row, jth col, ith row, ith col
            
            //update the jth row/col
            //update the max of jth row
            //and it's possible the entire jth row's row max is a negative value
            rowMax = -Double.MAX_VALUE;
            double update = 0;
            for(int k=0; k<nodeCount; k++){
                if(k==maxJ || k==maxI){
                    continue;
                }
                else{
                    //that k is connected to both I and J
                    //we have an issue here, how to detect whether k is connected to i or j?
                    
                    if(deltaQMx.getQuick(maxI, k)!=0 && deltaQMx.getQuick(maxJ, k)!=0){
                        update = deltaQMx.getQuick(maxJ, k)+deltaQMx.getQuick(maxI, k);
                        deltaQMx.setQuick(maxJ, k, update);
                        if(rowMax < update){
                            rowMax = update;
                            rowMaxColHeap.setQuick(maxJ, k);
                        }
                        
                        
                        //System.out.println("added");
                    }
                    else if(deltaQMx.getQuick(maxI, k)==0 && deltaQMx.getQuick(maxJ, k)!=0){
                        update = deltaQMx.getQuick(maxJ, k)-2*ai[maxI]*ai[k];
                        deltaQMx.setQuick(maxJ, k, update);
                        if(rowMax < update){
                            rowMax = update;
                            rowMaxColHeap.setQuick(maxJ, k);
                        }
                    }
                    else if(deltaQMx.getQuick(maxJ, k)==0 && deltaQMx.getQuick(maxI, k)!=0 ){
                        update = deltaQMx.getQuick(maxI, k)-2*ai[maxJ]*ai[k];
                        deltaQMx.setQuick(maxJ, k, update);
                        if(rowMax < update){
                            rowMax = update;
                            rowMaxColHeap.setQuick(maxJ, k);
                        }
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
            //update the rowMax of row J
            rowMaxHeap.setQuick(maxJ, rowMax);
            
            
            //from now, in the iterations later, the ith row and col should not be touched anymore
            //this has an issue too
            //activeRows.remove(new Integer(maxI));
            
            
            //remove the ith row
            rowMaxHeap.setQuick(maxI, 0);
            
            
        
            //explicitly update jth col, can be merged to above scripts
            //The issue is ... the jth row is updated as well, so the row max has changed...
            //need to double check, update the row max heap
            //update = -Double.MAX_VALUE;
            
            //must do a through 
            for(int k=0;k<nodeCount; k++){
                
                update = deltaQMx.getQuick(maxJ, k);
                deltaQMx.setQuick(k, maxJ, update);
                
                //if update is bigger than the row max, just maintain the row max
                if(update >= rowMaxHeap.getQuick(k)){
                    rowMaxHeap.setQuick(k, update);
                    rowMaxColHeap.setQuick(k, maxJ);
                }
                
                //if the updated value is smaller than row max, and it was the row max, then need to find the new row max
                else if(update < rowMaxHeap.getQuick(k) && (int)rowMaxColHeap.getQuick(k) == maxJ){
                    //for this row, find the new biggest value
                    update = -Double.MAX_VALUE;
                    for(int i=0; i<nodeCount; i++){
                        if(update < rowMaxHeap.getQuick(i)){
                            update = rowMaxHeap.getQuick(i);
                            rowMaxColHeap.setQuick(k, i);
                        }
                    }
                    rowMaxHeap.setQuick(k, update);
                }
                
                
                
                deltaQMx.setQuick(maxI, k, 0.0);
                deltaQMx.setQuick(k, maxI, 0.0);
            }
            
            //won't be touched again
            //reset the ith row and column; to 0, won't affect results
            //for(int k=0;k<nodeCount;k++){
                
            //}
            
            //update ai for the next round
            ai[maxJ] = ai[maxI] + ai[maxJ];
            ai[maxI] = 0;
            
            //update membership
            //merge i to j
            //all membership i merge to j
            //this is order n, not too bad
            int membershipI = membership[maxI];
            int membershipJ = membership[maxJ];
            for(int i=0; i<nodeCount; i++){
                //membership[maxI] = membership[maxJ];
                if(membership[i] == membershipI){
                    membership[i] = membershipJ;
                }
            }
            //trim 
            deltaQMx.trimToSize();
            
            //start another round of maxdeltaq
            maxDeltaQ = 0;
            maxI = 0;
            maxJ = 0;
            
            
            for(int i=0; i<activeRows.size(); i++){
                if(maxDeltaQ < rowMaxHeap.get(activeRows.get(i).intValue())){
                    maxDeltaQ = rowMaxHeap.get(activeRows.get(i).intValue());
                    maxI = activeRows.get(i).intValue();
                    maxJ = (int)rowMaxColHeap.getQuick(i);
                }
            }
            
            
            
//            update = 0;
//            for(int i=0; i<activeRows.size(); i++){
//                for(int j=0; j<activeRows.size(); j++){
//                    update = deltaQMx.getQuick(activeRows.get(i), activeRows.get(j));
//                    if(maxDeltaQ <= update){
//                        maxDeltaQ = update;
//                        maxI = i;
//                        maxJ = j;
//                    }
//                }
//            }
            
            
            //just to ensure swapping, always maxi is smaller than maxj
            if(maxI > maxJ){
                //swap
                int temp = maxI;
                maxI = maxJ;
                maxJ = temp;
            }
            
            
        }
        
        System.out.println(q);

    }
    
    
    /*the sparse matrix implementation, not most efficient, but works*/
    public void run_sm(CyNetwork network){
        //take a cynetwork;
        //assign 'membership' attributes to nodes
        //assign 'class' attributes to edges
        this.progress = 0;
        
        
        
        
        int nodeCount = network.getNodeCount(); //which is n
        int edgeCount = network.getEdgeCount(); //
        int[] graphIndex = new int[nodeCount];
        int[] membership = new int[nodeCount];
        double[] ai = new double[nodeCount];
        String[] graphNodeName = new String[nodeCount]; 
        int counter = 0;
        double qInitial = 0;
        Iterator<Node> it = network.nodesIterator();
        while(it.hasNext()){
            Node node = it.next();
            graphIndex[counter] = node.getRootGraphIndex();
            graphNodeName[counter] = node.getIdentifier();
            
            //initialize the membership
            membership[counter] = counter;
            ai[counter] = network.getDegree(graphIndex[counter])/(2.0*edgeCount);
            qInitial -= ai[counter]*ai[counter];
             
            counter++;
        }

          //take a look at how nodes are initialized.
//        for(int i=0; i<graphIndex.length; i++){
//            System.out.println(i + " " + graphIndex[i] + " " + graphNodeName[i]);
//        }
//        System.out.println("Node count: " + nodeCount + " Edge count:" + edgeCount);
        
        //initialize ai
        
        //for(int i=0; i<nodeCount; i++){
            
        //}
        
        //calculate the initial q, when every node is in the single cluster
        
        //for(int i=0; i<nodeCount; i++){
           
        //}
        
        //initialize the sparse matrix
        SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        Fun func = new Fun(ai);
        
        
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
        
        //System.out.println("The total initial Q:" + q);
        if(maxI > maxJ){
            //swap
            int temp = maxI;
            maxI = maxJ;
            maxJ = temp;
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
//        System.out.println("Max i:" + maxI);
//        System.out.println("Max j:" + maxJ);
//        System.out.println("Max deltaQ:" + maxDeltaQ);
//        System.out.println("Q-initial:" + qInitial);
        
        //add q to maxDeltaQ
        q = qInitial; // + maxDeltaQ;
        
        //this works because there's only one single peak.
        //the initialization should be modified..it always merge node with low degree first
        counter = 0;
        while(maxDeltaQ > 0){
            
            counter++;
            this.progress = (int)(100.0*counter/nodeCount);
            //System.out.println("In Fast_greedy thread:" + this.progress);
            
            //try{
            //    Thread.sleep(10);
            //}
            //catch(Exception e){
            //}
            //try not to sleep
            
            q += maxDeltaQ;
            //print
            //System.out.println("q: " + q);
            
            
            
            
            //to be processed: jth row, jth col, ith row, ith col
            
            //update the jth row/col
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
        
            //explicitly update jth col, can be merged to above scripts
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
            
            //reset the ith row and column; to 0, won't affect results
            //for(int k=0;k<nodeCount;k++){
                
                
            //}
            
            //update ai for the next round
            ai[maxJ] = ai[maxI] + ai[maxJ];
            ai[maxI] = 0;
            
            //update membership
            //merge i to j
            //all membership i merge to j

            //for(int i=0; i<nodeCount; i++){
                //membership[maxI] = membership[maxJ];
                
            //}
            //trim 
            deltaQMx.trimToSize();
            
            //start another round of maxdeltaq
            maxDeltaQ = 0;
            maxI = 0;
            maxJ = 0;
            
            //when it resets, it reloads the ai?
            func.reset(ai);
            
            deltaQMx.forEachNonZero(func);
            maxDeltaQ = func.max;
            maxI = func.row;
            maxJ = func.column;
            
            /*
            for(int i=0; i<nodeCount; i++){
                for(int j=0; j<nodeCount; j++){
                    if(maxDeltaQ <= deltaQMx.getQuick(i, j)){
                        maxDeltaQ = deltaQMx.getQuick(i, j);
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
             */
        
            //just to ensure swapping, always maxi is smaller than maxj
            if(maxI > maxJ){
                //swap
                int temp = maxI;
                maxI = maxJ;
                maxJ = temp;
            }
            
            //System.out.println(q + " " + maxDeltaQ);
            //it's not most efficient, as it has to iterate through all the members, but way faster.
        }//end of while loop
        
        //the membership array shows the membership
        //for(int i=0;i<membership.length; i++){
        //    System.out.print(membership[i]+" "+graphNodeName[i]);
        //    System.out.println(" ");
        //}
        
        //just need to assign the attributes to the graph
        //recode the membership
        //ArrayList<Integer> recodedMembership = new ArrayList<Integer>();
        //for(int i=0; i<membership.length; i++){
        //    if(recodedMembership.contains(new Integer(membership[i]))){
       //         //do nothing
        //    }
        //    else{
        //        recodedMembership.add(membership[i]);
        //    }
       // }
       // System.out.println("clusterNum:" + recodedMembership.size());
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
//        
//        //the membership array shows the membership
//        for(int i=0;i<membership.length; i++){
//            System.out.print(membership[i]);
//            System.out.println(" ");
//        }
        
        
        
        assignMembership(network, graphIndex, membership, membershipMapping);
        
        
        System.out.println(q);
        
        //try select a group
        this.progress = 100;
        
        
    }
    
    
    
    public static void run_1(CyNetwork network){
        //take a cynetwork;
        //assign 'membership' attributes to nodes
        //assign 'class' attributes to edges
        
        
        
        
        int nodeCount = network.getNodeCount(); //which is n
        int edgeCount = network.getEdgeCount(); //
        
        
        int[] graphIndex = new int[nodeCount];
        int[] membership = new int[nodeCount];
        String[] graphNodeName = new String[nodeCount]; 
        int counter = 0;
        Iterator<Node> it = network.nodesIterator();
        while(it.hasNext()){
            Node node = it.next();
            graphIndex[counter] = node.getRootGraphIndex();
            graphNodeName[counter] = node.getIdentifier();
            
            //initialize the membership
            membership[counter] = counter;
            counter++;
        }

          //take a look at how nodes are initialized.
//        for(int i=0; i<graphIndex.length; i++){
//            System.out.println(i + " " + graphIndex[i] + " " + graphNodeName[i]);
//        }
//        System.out.println("Node count: " + nodeCount + " Edge count:" + edgeCount);
        
        //initialize ai
        double[] ai = new double[nodeCount];
        for(int i=0; i<nodeCount; i++){
            ai[i] = network.getDegree(graphIndex[i])/(2.0*edgeCount);
        }
        
        //calculate the initial q, when every node is in the single cluster
        double qInitial = 0;
        for(int i=0; i<nodeCount; i++){
            qInitial -= ai[i]*ai[i];
        }
        
        //initialize the sparse matrix
        SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        
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
        
        //System.out.println("The total initial Q:" + q);
        if(maxI > maxJ){
            //swap
            int temp = maxI;
            maxI = maxJ;
            maxJ = temp;
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
//        System.out.println("Max i:" + maxI);
//        System.out.println("Max j:" + maxJ);
//        System.out.println("Max deltaQ:" + maxDeltaQ);
//        System.out.println("Q-initial:" + qInitial);
        
        //add q to maxDeltaQ
        q = qInitial; // + maxDeltaQ;
        
        //this works because there's only one single peak.
        //the initialization should be modified..it always merge node with low degree first
        while(maxDeltaQ > 0){
            q += maxDeltaQ;
            //print
            //System.out.println("q: " + q);
            
            
            
            
            //to be processed: jth row, jth col, ith row, ith col
            
            //update the jth row/col
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
        
            //explicitly update jth col, can be merged to above scripts
            for(int k=0;k<nodeCount; k++){
                deltaQMx.setQuick(k, maxJ, deltaQMx.getQuick(maxJ, k));
            }
            
            //reset the ith row and column; to 0, won't affect results
            for(int k=0;k<nodeCount;k++){
                deltaQMx.setQuick(maxI, k, 0.0);
                deltaQMx.setQuick(k, maxI, 0.0);
            }
            
            //update ai for the next round
            ai[maxJ] = ai[maxI] + ai[maxJ];
            ai[maxI] = 0;
            
            //update membership
            //merge i to j
            //all membership i merge to j
            int membershipI = membership[maxI];
            int membershipJ = membership[maxJ];
            for(int i=0; i<nodeCount; i++){
                //membership[maxI] = membership[maxJ];
                if(membership[i] == membershipI){
                    membership[i] = membershipJ;
                }
            }
            //trim 
            deltaQMx.trimToSize();
            
            //start another round of maxdeltaq
            maxDeltaQ = 0;
            maxI = 0;
            maxJ = 0;
            for(int i=0; i<nodeCount; i++){
                for(int j=0; j<nodeCount; j++){
                    if(maxDeltaQ <= deltaQMx.getQuick(i, j)){
                        maxDeltaQ = deltaQMx.getQuick(i, j);
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        
            //just to ensure swapping, always maxi is smaller than maxj
            if(maxI > maxJ){
                //swap
                int temp = maxI;
                maxI = maxJ;
                maxJ = temp;
            }
            
            System.out.println(q + " " + maxDeltaQ);
        }//end of while loop
        
        //the membership array shows the membership
        //for(int i=0;i<membership.length; i++){
        //    System.out.print(membership[i]+" "+graphNodeName[i]);
        //    System.out.println(" ");
        //}
        
        //just need to assign the attributes to the graph
        //recode the membership
        //ArrayList<Integer> recodedMembership = new ArrayList<Integer>();
        //for(int i=0; i<membership.length; i++){
        //    if(recodedMembership.contains(new Integer(membership[i]))){
       //         //do nothing
        //    }
        //    else{
        //        recodedMembership.add(membership[i]);
        //    }
       // }
       // System.out.println("clusterNum:" + recodedMembership.size());
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
//        
//        //the membership array shows the membership
//        for(int i=0;i<membership.length; i++){
//            System.out.print(membership[i]);
//            System.out.println(" ");
//        }
        
        
        
        assignMembership(network, graphIndex, membership, membershipMapping);
        
        
        System.out.println(q);
        
        //try select a group
        
        
        
    }
    
    
    private static void assignMembership(CyNetwork network, int[] graphIndex, int[] membership, HashMap<Integer, Integer> membershipMapping){
        //will assign the mmebership
        //create groups as well
        
        //System.out.println("Start assigning");
        //assign node attributes
        CyAttributes attrN = Cytoscape.getNodeAttributes();
        CyAttributes attrE = Cytoscape.getEdgeAttributes();
        
        
        //This iterates through all the nodes, assign the cluster membership
        ArrayList<List> nodeList = new ArrayList<List>();
        for(int i=0; i<membershipMapping.size(); i++){
            nodeList.add(new ArrayList<Node>());
        }
        
        
        //i can do
        for(int i=0; i<network.getNodeCount(); i++){
            attrN.setAttribute(network.getNode(graphIndex[i]).getIdentifier(), "membership", membership[i]);
            
            //each list contains the nodes to be added
            nodeList.get(membership[i]).add(network.getNode(graphIndex[i]));
        }
//       
        
        //to create metanode
        //need to create a node group for metanodes as well
        //ArrayList<MetaNode>
        
        //this line creates the metanodes; however, this operation will clear all the metanodes already created;
        //if the user created metanode which has dependencies on the current metanode, it should be expanded
        MetaNode.expandAll();
        List<CyGroup> grpList = CyGroupManager.getGroupList();
        for(int i=0; i<grpList.size(); i++){
            CyGroup grp = grpList.get(i);
            if(grp.getGroupName().startsWith(network.getIdentifier())){
                //MetaNode.getMetaNode(grp);
                //remove all associated metanodes
                
                //MetaNode.removeMetaNode(grp.getGroupNode());
                CyGroupManager.removeGroup(grp);
                //System.out.println("removed metagrp!");
            }
            
        }
        
        
        
        
        for(int i=0; i<nodeList.size(); i++){
            //don't specify, specify later on?
            CyGroup group = CyGroupManager.createGroup(network.getIdentifier()+"_"+i, nodeList.get(i), null); //if i put null here, it won't work
            //MetaNode newNode = new MetaNode(group);
            //newNode.collapse(recursive, multipleEdges, true, null);
            //newNode.collapse(true, false, true, null);
            
            //if there's no network view, create one
            //if (i < nodeList.size()-1)
            //  CyGroupManager.setGroupViewer(group, "metaNode", Cytoscape.getCurrentNetworkView(), false);
            //else
              CyGroupManager.setGroupViewer(group, "metaNode", Cytoscape.getCurrentNetworkView(), true);
            // MetaNode newNode = new MetaNode(group);
            //newNode.collapse(recursive, multipleEdges, true, null);
            //newNode.collapse(true, false, true, null);
            
        }
//        
        
        //System.out.println("Created these groups:" + CyGroupManager.getGroupList().size());
        
        
        
        
        
        
        
        
        
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
        
        //System.out.println(intraEdge + " " + interEdge);
        
        //try select a group
        //this will be very slow for large networks
        //need to figure out a way to select nodes wrt ...
        
        
        //Iterator<Node> ite = network.nodesIterator();
        //while(ite.hasNext()){
        //    Node nd = ite.next();
        //    if(attrN.getIntegerAttribute(nd.getIdentifier(), "membership") == 0){
        //        network.setSelectedNodeState(nd, true);
        //    }
        // }
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //expired version
    private static void community(CyNetwork network){
        //initialize
        int nodeCount = network.getNodeCount(); //which is n
        int edgeCount = network.getEdgeCount(); //which is m
        //int nRow = nodeCount;
        
        //create a array storing the rootgraphindex of the nodes
        int[] graphIndex = new int[nodeCount];
        int counter = 0;
        Iterator<Node> it = network.nodesIterator();
        while(it.hasNext()){
            Node node = it.next();
            graphIndex[counter] = node.getRootGraphIndex();
            counter++;
        }
        
        //Create a new network based on the clustering results.
        //The sparseDouble matrix should have been initialized with all 0
        //The unintialized values are all automatically set to 0
        SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        double[] ai = new double[nodeCount];
        
        //this should be implemented through a max-heap
        double maxDeltaQ = 0;
        int maxI=0;
        int maxJ=0;
        double deltaQ = 0;
        //System.out.println(deltaQ.getQuick(0, 0) + deltaQ.getQuick(1, 1));
        //deltaQ.setQuick(0, 0, 15.0);
        //System.out.println(deltaQ.getQuick(0, 0) + deltaQ.getQuick(1, 1));
        
        //initalize the sparse matrix
        //
        double q = 0;
        for(int i=0; i<nodeCount; i++){
            for(int j=0; j<nodeCount; j++){
                if(i==j){
                    continue;
                }
                else if(network.getEdgeCount(network.getNode(graphIndex[i]), network.getNode(graphIndex[j]), false)>0 
                        || network.getEdgeCount(network.getNode(graphIndex[j]), network.getNode(graphIndex[i]), false)>0
                        ){
                    deltaQ = 1.0d/(2*edgeCount)-(network.getDegree(graphIndex[i])*network.getDegree(graphIndex[j]))/(4.0*Math.pow(edgeCount, 2.0));
                    
                    q += deltaQ;
                    
                    deltaQMx.setQuick(i, j, deltaQ);
                    if(maxDeltaQ <= deltaQ){
                        maxDeltaQ = deltaQ;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        
        System.out.println("The total initial Q:" + q);
        //System.out.println("The total initial Q");
        
        //ensure maxI is always smaller than maxJ
        if(maxI > maxJ){
            //swap
            int temp = maxI;
            maxI = maxJ;
            maxJ = temp;
        }
        
        //initialize with k/(2m)
        for(int i=0; i<nodeCount; i++){
            ai[i] = network.getDegree(graphIndex[i])/(2.0*edgeCount);
        }
        
        double qInitial = 0;
        for(int i=0; i<nodeCount; i++){
            qInitial -= ai[i]*ai[i];
        }
        
        //qInitial is calculated correctly
        System.out.println("The initial q is:" + qInitial);
        
        //the max delta q is not correct
        System.out.println(maxDeltaQ + " " + maxI + " " + maxJ);
        
        //Take a look at the matrix
        for(int i=0; i<nodeCount; i++){
            for(int j=0; j<nodeCount; j++){
                System.out.printf("%2.3f ", deltaQMx.getQuick(i, j));
                        
                        //print(new PrintfFormat("%1.3d ").sprintf(deltaQMx.getQuick(i, j)));
            }
            System.out.println("");
        }
        
        
        
        
        //System.out.println(ai[0] +" " + ai[1] + " "+ ai[2]);
        
        //print the loaded cell numbers
        //successfully loaded
        //System.out.println(edgeCount + " " + deltaQMx.cardinality());
        //foreachnonzero will be way faster but i don't know how to use it..
        
        //the looping begins here
        //create an iteration arraylist
        //ArrayList<Integer> iterRow = new ArrayList<Integer>();
        //for(int i=0; i<nodeCount; i++){
        //    iterRow.add(i);
        //}
        
        //must remove the object by new Integer
        //System.out.println(iterRow.size());
        //iterRow.remove(5);
        //iterRow.remove(5);
        //System.out.println(iterRow.size());
        
        //store the Q
        //or Q can be recalculated based on the clustering results
        

        
        
        double totalQ = 0;
        int mergeCount = 0;
        
        while(maxDeltaQ > 0){
            //need to merge the ith and jth row
            //update the jth row, removing the ith col
            //set all the ith col to be 0 later
            
            //update jth row
            for(int k=0; k<nodeCount; k++){
                if(k==maxJ || k==maxI){
                    continue;
                }
                else{
                    //that k is connected to both I and J
                    //we have an issue here, how to detect whether k is connected to i or j?
                    
                    if(deltaQMx.getQuick(maxI, k)!=0 && deltaQMx.getQuick(maxJ, k)!=0){
                        deltaQMx.setQuick(maxJ, k, deltaQMx.getQuick(maxJ, k)+deltaQMx.getQuick(maxI, k));
                        
                        
                        
                        System.out.println("added");
                    }
                    else if(deltaQMx.getQuick(maxI, k)==0 && deltaQMx.getQuick(maxJ, k)!=0){
                        deltaQMx.setQuick(maxJ, k, deltaQMx.getQuick(maxJ, k)-2*ai[maxI]*ai[k]);
                    }
                    else if(deltaQMx.getQuick(maxJ, k)==0 && deltaQMx.getQuick(maxI, k)!=0 ){
                        deltaQMx.setQuick(maxJ, k, deltaQMx.getQuick(maxI, k)-2*ai[maxJ]*ai[k]);
                    }
                    else{
                        //both are zero
                        //should never be performed
                        //it seems that nothing is wrong.
                        //but the result is not quite correct
                        //both are zero, no need to update
                        //System.out.println("This is wrong!");
                        //System.out.println("Do nothing.");
                    }
                }
            }
            //where jth column is not updated, should be updated?
            //it won't affect the result probably.
            for(int k=0;k<nodeCount; k++){
                deltaQMx.setQuick(k, maxJ, deltaQMx.getQuick(maxJ, k));
            }
            
            
            
            //reset the ith row
            //for(int k=0; k<nodeCount; k++){
            //    deltaQMx.setQuick(maxI, k, 0.0);
            //}
            
            //update the ai
            ai[maxJ] = ai[maxI] + ai[maxJ];
            ai[maxI] = 0;
        
        
            
            
            
            //remove the ith row and column by set to 0;
            for(int k=0;k<nodeCount;k++){
                deltaQMx.setQuick(maxI, k, 0.0);
                deltaQMx.setQuick(k, maxI, 0.0);
            }
            
            deltaQMx.trimToSize();
            
            
            
            //also loop to get the new maxQ [actually only the jth row is changed, that's why it's better to keep a max heap for each row]
            //what is max heap anyway?
            //can shrink this by iterating only the rows and cols in the arraylist
            
            
            //foreach non zero will be way faster
            maxDeltaQ = 0;
            maxI = 0;
            maxJ = 0;
            for(int i=0; i<nodeCount; i++){
                for(int j=0; j<nodeCount; j++){
                    if(maxDeltaQ <= deltaQMx.getQuick(i, j)){
                        maxDeltaQ = deltaQMx.getQuick(i, j);
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        
            if(maxI > maxJ){
                //swap
                int temp = maxI;
                maxI = maxJ;
                maxJ = temp;
            }
        
            System.out.println(maxDeltaQ + " " + maxI + " " + maxJ);
            
            //why there's a difference of factor of 2?
            totalQ += maxDeltaQ;
            System.out.println("Total Q at this round:" + (totalQ + qInitial));
            mergeCount++;
        }//the loop
        //end of while loop
        
        
        
        
        System.out.println("Total increased Q:" + totalQ);
        System.out.println("Total Q at the end:" + (totalQ + qInitial));
        System.out.println("Merge Count:" + mergeCount);
        
        
        
        
        
        
        
        
        
        
    }
    
}    
    
    
    
    
//    
//    
//    public static void communityOld(){
//        //get network
//        CyNetwork network = Cytoscape.getCurrentNetwork();
//    
//        //get edge and node counts
//        int nodeCount = network.getNodeCount();
//        int edgeCount = network.getEdgeCount();
//        int nRow = nodeCount;
//        //int nCol = nodeCount;
//        
//        //store the rootgraph index mapping with the current index mapping
//        //actually, it will be more convenient to just store the nodes?
//        int[] graphIndex = new int[nodeCount];
//        int counter = 0;
//        Iterator<Node> it = network.nodesIterator();
//        while(it.hasNext()){
//            Node node = it.next();
//            graphIndex[counter] = node.getRootGraphIndex();
//            counter++;
//        }
//        //the getNodeIndicesArray is deprecated
//        //graphIndex = network.getNodeIndicesArray();
//        
//        
//        
//        
//        
//        
//        //create a sparse matrix, to store the community
//        SparseDoubleMatrix2D adj = new SparseDoubleMatrix2D(nRow, nRow);
//        SparseDoubleMatrix2D deltaQMx = new SparseDoubleMatrix2D(nRow, nRow);
//       
//        //initialize the adjacent matrix and delta Q
//        double deltaQ=0;
//        double totalQ=0; //which is actually a negative value in the beginning
//        double maxDeltaQ=0;
//        int maxI=0;
//        int maxJ=0;
//        for(int i=0;i< nodeCount; i++){
//            for(int j=0;j < nodeCount; j++){
//                if(i==j){
//                    continue;
//                }
//                else if(network.getEdgeCount(network.getNode(graphIndex[i]), network.getNode(graphIndex[j]), true)>0){
//                    //there's an edge
//                    adj.setQuick(i, j, 1.0);
//                    deltaQ = 1.0d/(2*edgeCount)-(network.getDegree(graphIndex[i])*network.getDegree(graphIndex[j]))/(4.0*Math.pow(edgeCount, 2.0));
//                    deltaQMx.setQuick(i, j, deltaQ);
//                    totalQ += deltaQ - 1.0d/(2*edgeCount);
//                    if(deltaQ > maxDeltaQ){
//                        maxDeltaQ = deltaQ;
//                        maxI = i;
//                        maxJ = j;
//                    }
//                }
//            }
//        }
//        
//        if(maxI > maxJ){
//            int temp = maxJ;
//            maxJ = maxI;
//            maxI =temp;
//        }
//        //a faster way is to loop through edges, instead of nodes
//        //just make the code cleaner, do one more iteration
//        //double maxDeltaQ=0;
//        //record the coordinates
//        //double maxQi=0;
//        //double maxQj=0;
//        //do{
//        //    
//        //}
//        //while(deltaQ>0); //assume there's only one single peak, deltaQ grows monotonously
//        
//        while(maxDeltaQ > 0){
//            //need to create a new sparse matrix, the key is how to merge columns
//        
//        }
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        //initialize the sparse matrix with initial values
//        //need to record the max for each row
//        //i dunno whether i need the maxHeap
////        
////        
////        double maxQ = 0;
////        int maxI=0;
////        int maxJ=0;
////        double q=0;
////        for(int i=0; i<nodeCount; i++){
////            //double maxQ = 0;
////            for(int j=0; j<nodeCount; j++){
////                    //if i don't set the value, the default is zero?
////                    //CyEdge edgeCheck = Cytoscape.getCyEdge(arg0, arg1, arg2, j, arg4)
////                    if(i==j){
////                        continue;
////                    }
////                    else if(network.getEdgeCount(network.getNode(graphIndex[i]), network.getNode(graphIndex[j]), true)>0){
////                        //node i and node j are connected
////                        double deltaQ = 1.0d/(2*edgeCount)-(network.getDegree(graphIndex[i])*network.getDegree(graphIndex[j]))/(4.0*Math.pow(edgeCount, 2.0));
////                        
////                        //this can be even more optimized
////                        q += deltaQ - 1.0d/(2*edgeCount);
////                        
////                        //to make it symmetrical
////                        if(deltaQ > maxQ){
////                            maxQ = deltaQ;
////                            //store the indexes as well
////                            maxI = i;
////                            maxJ = j;
////                        }
////                        
////                        csm.setQuick(i, j, deltaQ);
////                        csm.setQuick(j, i, deltaQ);
////                    }
////                    //csm.setQuick(j, j, j);
////                }
////        }
////        
////        if(maxI > maxJ){
////            int temp = maxJ;
////            maxJ = maxI;
////            maxI =temp;
////        }
////        
////        //validity check
////        System.out.println(maxI+" "+maxJ+" "+q);
////        
////        //now we have maxI, maxJ, q, maxQ ready
////        //need to: remove the jth column and row, then merge the matrix, recalculate
////        //1. update q
////        
////        //This won't iterate to the end, will stop at where the Q start to drop
////        //which make sense if Q is monotonous increasing and decreasing
////        while(maxQ > 0){
////            q = q + maxQ;
////            nRow--;
////            nCol--; 
////            SparseDoubleMatrix2D csm2 = new SparseDoubleMatrix2D(nRow, nCol);
////            
////            double deltaQ = 0;
////            for(int i=0; i<nRow; i++){
////                    //for each row
////                    
////                    
////                    //if it's the max i row
////                    if(i == maxI){
////                        //add the maxI row and maxJ row;
////                        for(int j=0; j<nCol+1; j++){
////                            //deltaQ = csm.getQuick(maxI, j)+csm.getQuick(maxJ, j);
////                            //csm2.setQuick(i, j, csm.getQuick(maxI, j)+csm.getQuick(maxJ, j));
////                            //This merge require recalculation
////                            //need a sparse ajacency matrix too
////                            
////                            
////                            
////                            
////                            
////                            
////                            
////                            
////                        }                    
////                    }//end of if i
////                    else if(i == maxJ){
////                        //skip the max j row, which is merged to max i
////                        continue;
////                    }
////                    
////                    //mods end
////                    int offset = 0;
////                    for(int j=0; j<nCol; j++){
////                        if(j == maxJ){
////                                //skip
////                             offset = 1;
////                             continue;
////                        }
////                        else{
////                             csm2.setQuick(i, j, csm.getQuick(i, j+offset));
////                        }
////                    }
////            }//end of for        
////            
////            //the csm2, new sparse matrix is completed
////            csm = csm2;
////            
////            //then do the same thing again, look for the maximum q
////            
////        
////        
////        
////        }//end while, for all the merging
////        
////        
////        
////        
////        
////        
////        
////        
////        
////        
////        
////        
////        //max heap structure required; largest element of each row; 
////        
////        //ArrayList containling ai (edge ends connecting community i)
////        ArrayList<Integer> edgeAi = new ArrayList<Integer>();
////        
//    }
//}
class Fun implements IntIntDoubleFunction{
        public double max;
        private double maxInternal; //for consolidation ratio
        public int row;
        public int column;
        
        //first let's just use the number of degrees
        private double[] ai;
        
        
        public Fun(double[] ai){
            this.ai = ai;
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
            double ratio = Math.min(this.ai[row]/this.ai[col], this.ai[col]/this.ai[row]);
            //System.out.println(ratio);
            
            //for consolidation ratio, comparison is based on Q * ratio
            //the returned value is still value
            
            if(maxInternal < value*ratio){
                maxInternal = value*ratio;
                
                max = value;
                this.row = row;
                this.column = col;
            }
            return value;
            
        }
}


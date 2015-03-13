/*
 * GLayMainPanel.java
 *
 * Created on June 24, 2008, 10:58 AM
 * 
 * Create GLay main panel in the tab.
 * harness all the functions
 */

package glay;

/**
 *
 * @author  sugang
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.*;
import java.util.*;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.layout.CyLayouts;
import cytoscape.Cytoscape;
import cytoscape.CyMain;
import cytoscape.util.CytoscapeAction;
import cytoscape.CyNetwork;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.CyNetworkView;
import cytoscape.*;

import glay.GLayDegreeDist;
import glay.GLayGroup;
import glay.GLayCluster;
import glay.util.RandomClusterModel;
import glay.GLayRandomGraphDialog;
import glay.GLayRandomGraphPanel;
import glay.util.FastGreedy;
import glay.GLayMainPanel;
import glay.GLayResultPanel;
import glay.util.GLayProgressMonitor;
import glay.util.FastGreedy;
import glay.util.FastGreedy2;
import glay.util.GLayVisualStyle;

import cytoscape.visual.VisualStyle;

import metaNodePlugin2.*;
import metaNodePlugin2.model.MetaNode;

import cytoscape.groups.*;
import cytoscape.data.CyAttributes;

import giny.model.*;
import giny.view.*;
import java.text.DecimalFormat;

//Where are the other layout algorithms?
import csplugins.layout.algorithms.force.ForceDirectedLayout;
import cytoscape.layout.algorithms.GridNodeLayout;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import cytoscape.actions.GinyUtils;
import cytoscape.actions.*;

import cytoscape.ding.DingNetworkView;

public class GLayMainPanel extends javax.swing.JPanel implements PropertyChangeListener {

        //define the function on list selection
        
        private void listDeSelected(int index){
            //System.out.println("Deselect:" + index);
        }
    
    
        private void listSelected(int[] indices){
            System.out.println("Select:" + indices.length);
            //what i need to do, is not select the node
            //but rather, set the node membership to be the negative value
            //so that it can be mapped differently
            //maybe not a good idea
            CyAttributes attrN = Cytoscape.getNodeAttributes();
            
            List<CyGroup> grpList = CyGroupManager.getGroupList();
            CyNetwork network = Cytoscape.getCurrentNetwork();
            String id = network.getIdentifier();
            if(indices.length == 0){
                
                //deselected all
                //CyNet
                for(CyGroup grp:grpList){
                    List<CyNode> nodeList = grp.getNodes();
                    for(CyNode node:nodeList){
                        //reset
                        attrN.setAttribute(node.getIdentifier(), "listActive", 1);
                        attrN.setAttribute(node.getIdentifier(), "type", 0);
                    }
                }
                
                //restore the viewport
                //Cytoscape.getCurrentNetworkView().fitContent();
                
            }
            else{
            
            
            
            for(CyGroup grp:grpList){
                //System.out.println("Iterate over grp");
                
                boolean selected = false;
                for(int index:indices){
                    if(grp.getGroupName().startsWith(id+"_"+index+"[")){
                        //System.out.println("Found the selected grp");
                        List<CyNode> nodeList = grp.getNodes();
                        for(CyNode node:nodeList){
                            //This cluster is currently active
                            attrN.setAttribute(node.getIdentifier(), "listActive", 1);
                            attrN.setAttribute(node.getIdentifier(), "type", 2);
                        }
                        selected = true;
                        break;
                    }

                }
                if(!selected){
                    List<CyNode> nodeList = grp.getNodes();
                        for(CyNode node:nodeList){
                            //this cluster is currently not active
                            attrN.setAttribute(node.getIdentifier(), "listActive", 0);
                            attrN.setAttribute(node.getIdentifier(), "type", 0);
                        }
                }
            
            
            }//end of iterate over cygroup
            
            
//                    else{
//                        //System.out.println("Foundt the unselected grp");
//                        List<CyNode> nodeList = grp.getNodes();
//                        for(CyNode node:nodeList){
//                            //this cluster is currently not active
//                            attrN.setAttribute(node.getIdentifier(), "listActive", 0);
//                            attrN.setAttribute(node.getIdentifier(), "type", 0);
//                        }
//                    }
            
            }
            //Cytoscape.getCurrentNetworkView().applyVizmapper(Cytoscape.getVisualMappingManager().getVisualStyle());
            //Cytoscape.getCurrentNetworkView().updateView();
            //Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
            Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
            //Cytoscape.getVisualMappingManager().setVisualStyle("GLay"); // see how this works
            
        }
    
    
    
    
        private DefaultListModel clusterListModel;
    
        
        private Task task;
        
        class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        private double blowUpCoefficient; //Temporarily stored here
            
            
        @Override
        public Void doInBackground() {
            
            UIstatus(false);
            
            //the correct way to access
            //GLayMainPanel.this.
            
            
            
            //this line below is the task
            //Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(progress); //set progress is a method of swing worker
            
            
            //this looks like a global variable
            progressBar.setVisible(true);
            
            
            //expand all metanodes to review all
            MetaNode.expandAll();
            
            
            FastGreedy2 fg = new FastGreedy2();
            
            
            if(GLayMainPanel.this.comboAlgorithm.getSelectedIndex()==0){
                fg.setMode(0);
                
                
            }
            else if(GLayMainPanel.this.comboAlgorithm.getSelectedIndex()==1){
                fg.setMode(1);
                //Thread th = new Thread(fg);
                //th.start();
            }
            else{
            
            }
            //expand all metanodes
            
            
            
            Thread th = new Thread(fg);
            th.start();
            
            
            //th.start();
            //
            
            
            while (progress < 100) {
                GLayMainPanel.this.statusMsg.setText("Processing...");
                //System.out.println("Starting in the main thread");
                
                //Sleep for up to one second.
                //try {
                    //Thread.sleep(random.nextInt(1000));
                //    Thread.sleep(5);
                //} catch (InterruptedException ignore) {}
                //Make random progress.
                //progress += random.nextInt(10);
                //setProgress(Math.min(progress, 100));
                //FastGreedy fg = new FastGreedy();
                //fg.run_sm(Cytoscape.getCurrentNetwork());
                //progress = 100;
                //setProgress(100);
                
                //I should let fast greedy fired in an independent thread
                //and check the status in this thread
                //try{
                    //th.wait();
                    //when th is waiting, released the object lock?
                    //obviously this is not safe?
                    try{
                        //th.sleep(10);
                        Thread.sleep(100);
                        //System.out.println("In main thread.");
                        //th.sleep(50);
                        progress = fg.getCurrentProgress();
                        //System.out.println("Progress in calling thread:" + progress);
                        //set the progress.
                        setProgress(progress);
                    }
                    catch(InterruptedException e){
                        
                    }
                    
                    
                    //th.notify();
               // }
               // catch(InterruptedException e){}
                
            }//This means it's complete
            
            //cast the layout here
            // this refer to the outer class GLayMainPanel.this. ...
            //This is the part to cast visual styles
            
            //let's try the hierarchical layout
            //Cytoscape.getCurrentNetworkView().applyLayout(new ForceDirectedLayout());
            GLayMainPanel.this.statusMsg.setText("Casting Layouts...");
            
            //alg.setSelectedOnly(true);
            
            //get all the metaNodes
            /*
            MetaNode.expandAll();
            List<CyGroup> grpList = CyGroupManager.getGroupList();
            for(CyGroup grp:grpList){
                if(grp.getGroupName().startsWith(Cytoscape.getCurrentNetwork().getIdentifier())){
                    //apply visual style independently
                    List<CyNode> nodeList = grp.getNodes();
                    List<CyEdge> edgeList = grp.getInnerEdges();
                    CyNode[] nodes = new CyNode[nodeList.size()];
                    CyEdge[] edges = new CyEdge[edgeList.size()];
                    nodes = nodeList.toArray(new CyNode[nodeList.size()]);
                    edges = edgeList.toArray(new CyEdge[edgeList.size()]);
                    System.out.println(nodes.length);
                    System.out.println(edges.length);
                    
                    Cytoscape.getCurrentNetworkView().applyLayout(alg, nodes, edges);
                    break;
                    
                }
                else{
                    continue;
                }
            }
            */
            
            //To avoid over force directed, apply grid first to normalize? it should not be that problem
            //The funny thing is, force directed can't be performed repeatedly
            //The initiallocations were not normalized
            //Cytoscape.getCurrentNetworkView().applyLayout(new GridNodeLayout());
            
            //normalize the initial position first to avoid percularities: not always necessary
            
            ///////////////////////////////////////////////////
            
            //Try to create the list panel
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            //GLayMainPanel.this.clusterListModel.add(0, "Hey!");
            //all metanodes should have been expanded, create groups.
            //add
            //GLayMainPanel.this.clusterList.addListSelectionListener(new ListSelectionListener());
            
            
            GLayMainPanel.this.clusterListModel.clear();
            List<CyGroup> grpList = CyGroupManager.getGroupList();
            int metaNodeCount = 0;
            for(CyGroup grp:grpList){
               if(grp.getGroupName().endsWith("[CLUSTER]")){
                   GLayMainPanel.this.clusterListModel.add(metaNodeCount, "Cluster " + metaNodeCount); 
                   metaNodeCount++;
               }
            }
            
            
            
            ForceDirectedLayout alg = new ForceDirectedLayout();
            
            //apply on the full graph first
            //if reset is required
            if(GLayMainPanel.this.resetCheck.isSelected())Cytoscape.getCurrentNetworkView().applyLayout(new GridNodeLayout());
            Cytoscape.getCurrentNetworkView().applyLayout(alg);
            
            
            if(GLayMainPanel.this.comboLayout.getSelectedIndex() == 0){
                //cast the normal ones
                //do nothing
            }
            //Can be refactored into a funcion
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Cytoscape.getCurrentNetworkView().applyLayout(alg); 
            
            //This must be calculated on the entire network!
            else if(GLayMainPanel.this.comboLayout.getSelectedIndex() == 1){
            this.blowUpCoefficient = Cytoscape.getCurrentNetwork().getNodeCount()/45.0;
            System.out.println(this.blowUpCoefficient);
            
            
            MetaNode.collapseAll(); //i don't know whether this is performe in a different thread or not
            
           // alg.unlockAllNodes();
            
            //the layout must be casted first on the entire set, then spread out
            Cytoscape.getCurrentNetworkView().applyLayout(alg);
            
            //only when the reset is required
            
             
            //alg.unlockAllNodes();
            
            //so this function ..should be in the current thread, but not in the gui thread
            //need to recenter the graph
            double x = 0.0;
            double y = 0.0;
            int total = 0;
            
            //optimize later
            grpList = CyGroupManager.getGroupList();
            for(CyGroup grp:grpList){
                if(grp.getGroupName().startsWith(Cytoscape.getCurrentNetwork().getIdentifier())){
                    //apply visual style independently
                    /*
                    List<CyNode> nodeList = grp.getNodes();
                    List<CyEdge> edgeList = grp.getInnerEdges();
                    CyNode[] nodes = new CyNode[nodeList.size()];
                    CyEdge[] edges = new CyEdge[edgeList.size()];
                    nodes = nodeList.toArray(new CyNode[nodeList.size()]);
                    edges = edgeList.toArray(new CyEdge[edgeList.size()]);
                    System.out.println(nodes.length);
                    System.out.println(edges.length);
                    
                    Cytoscape.getCurrentNetworkView().applyLayout(alg, nodes, edges);
                    break;
                     */
                    NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(grp.getGroupNode());
                    //nv.setXPosition(nv.getXPosition()*10, false);
                    //nv.setYPosition(nv.getYPosition()*10, false);
                    x+=nv.getXPosition();
                    y+=nv.getYPosition();
                    total++;
                    
                    

                    
                }
                else{
                    continue;
                }
            }
            
            //Cytoscape.getCurrentNetworkView().updateView();
            //the new center
            x = x/total;
            y = y/total;
            
            //however, this may be reseted, need to ensure only performed once.
            //the key is that the metanodes have been created; may accidentally hit the button twice
            
            //System.out.println(Cytoscape.getCurrentNetwork().getNodeCount()); //shit, this is the metanode count...
            
            for(CyGroup grp:grpList){
                if(grp.getGroupName().startsWith(Cytoscape.getCurrentNetwork().getIdentifier())){
                    //apply visual style independently
                    /*
                    List<CyNode> nodeList = grp.getNodes();
                    List<CyEdge> edgeList = grp.getInnerEdges();
                    CyNode[] nodes = new CyNode[nodeList.size()];
                    CyEdge[] edges = new CyEdge[edgeList.size()];
                    nodes = nodeList.toArray(new CyNode[nodeList.size()]);
                    edges = edgeList.toArray(new CyEdge[edgeList.size()]);
                    System.out.println(nodes.length);
                    System.out.println(edges.length);
                    
                    Cytoscape.getCurrentNetworkView().applyLayout(alg, nodes, edges);
                    break;
                     */
                    //a constant is here to blow up the backbone
                    
                    
                    
                    
                    NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(grp.getGroupNode());
                    nv.setXPosition(nv.getXPosition()+(nv.getXPosition()-x)*this.blowUpCoefficient, false);
                    nv.setYPosition(nv.getYPosition()+(nv.getYPosition()-y)*this.blowUpCoefficient, false);
                    //x+=nv.getXPosition();
                    //y+=nv.getYPosition();
                    //total++;
                    
                    

                    
                }
                else{
                    System.out.println("There's another group");
                    continue;
                }
            }
            
            MetaNode.expandAll();
            
            //Cytoscape.getCurrentNetworkView().setZoom(1.0);
            Cytoscape.getCurrentNetworkView().fitContent();
            //Cytoscape.getCurrentNetworkView().updateView();
            //Cytoscape.getCurrentNetworkView().redrawGraph(arg0, arg1);
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
            
            
            
            
            
            
            
            
            
            //create a new visual, or just implement the current one
            VisualStyle vs = GLayVisualStyle.createClusterVisualStyle(Cytoscape.getCurrentNetwork());
            Cytoscape.getVisualMappingManager().setVisualStyle("GLay");
            
            //this line above is the task
            //System.out.println("ended");
            
            //the call layout methods should be placed here as well.
            
            //the cast visual style lines should be used here.
            
            DecimalFormat nf = new DecimalFormat();
            nf.setMaximumFractionDigits(3);
            
            GLayMainPanel.this.statusMsg.setText("Modularity: "+nf.format(fg.getModularity()));
            //System.out.println("Finished");
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            //Toolkit.getDefaultToolkit().beep();
           // startButton.setEnabled(true);
            //setCursor(null); //turn off the wait cursor
            //taskOutput.append("Done!\n");
            
            //can even access the parent..wow!
            
            /*i don't quite understand this, but the innner class can access outter class*/
            //looks like the outter class
            progressBar.setVisible(false);
            //progressBar.setVisible(false);
            progressBar.setValue(0);
            UIstatus(true);
            
        }
    }

    public void UIstatus(boolean status){
        this.executeButton.setEnabled(status);
        this.jButton1.setEnabled(status);
        //this.jButton2.setEnabled(status);
        this.jButton3.setEnabled(status);
        this.comboAlgorithm.setEnabled(status);
        this.comboLayout.setEnabled(status);
        this.resetCheck.setEnabled(status);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }

    
    
    
    /** Creates new form GLayMainPanel */
    public GLayMainPanel() {
        //initialize the model upon creation
        //create the model first before init
        this.clusterListModel = new DefaultListModel();
        //this.clusterListModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        
        initComponents();
    }
    
    public JTabbedPane getTabbedPane(){
        return this.bodyPane;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        bodyPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comboAlgorithm = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        comboLayout = new javax.swing.JComboBox();
        executeButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        progressBar = new javax.swing.JProgressBar();
        resetCheck = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        statusMsg = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clusterList = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        randGraphPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        clusterNumField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        clusterSizeField = new javax.swing.JTextField();
        pInField = new javax.swing.JTextField();
        pRatioField = new javax.swing.JTextField();
        clusterSizeDistCombo = new javax.swing.JComboBox();
        generateButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel8 = new javax.swing.JPanel();
        messageLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        nodeNumLabel = new javax.swing.JLabel();
        edgeNumLabel = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));

        jLabel1.setText("Algorithm");

        comboAlgorithm.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Fast Greedy", "Fast Greedy(KW)" }));

        jLabel2.setText("Layout");

        comboLayout.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Hierarchical" }));

        executeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/control_fastforward_blue.png"))); // NOI18N
        executeButton.setToolTipText("Start Clustering");
        executeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        executeButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        executeButton.setLabel("Execute");
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });

        progressBar.setVisible(false);

        resetCheck.setText("Reset");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboLayout, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboAlgorithm, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(resetCheck))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboLayout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(resetCheck)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(executeButton))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Global"));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/cog.png"))); // NOI18N
        jButton1.setText("");
        jButton1.setToolTipText("Reset Default");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/control_equalizer.png"))); // NOI18N
        jButton3.setText("");
        jButton3.setToolTipText("Remove GLay Panel");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addContainerGap(2, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        statusMsg.setText("Ready");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMsg)
                .addContainerGap(196, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(statusMsg)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Clusters"));

        clusterList.setModel(this.clusterListModel);
        jScrollPane1.setViewportView(clusterList);
        this.clusterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.clusterList.addListSelectionListener(new ListSelectionListener(){ public void valueChanged(ListSelectionEvent evt){
            //System.out.println("Selected Index:"+ evt.getFirstIndex());
            //System.out.println("Selected Index:"+ evt.getLastIndex());
            //only get the end value

            if(evt.getValueIsAdjusting()){
                //process the previously selected grp
                //System.out.println(evt.getFirstIndex());
                //listDeSelected(evt.getFirstIndex());
                //return(void);
                return;
            }
            else{
                //process the selected grp
                //System.out.println(evt.getLastIndex());
                //listSelected(evt.getLastIndex());
                //System.out.println(evt.getSource());

                //why so dumb..
                //ListSelectionModel lsm = (ListSelectionModel) evt.getSource();
                //int index = lsm.getMinSelectionIndex();
                //System.out.println("Selected:" + index);
                //System.out.println(clusterList.getSelectedIndex());
                //clusterList.getSelectedIndices();
                listSelected(clusterList.getSelectedIndices());
            }

        }});

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/delete.png"))); // NOI18N
        jButton4.setToolTipText("De-pop up all clusters");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/add.png"))); // NOI18N
        jButton5.setToolTipText("Pop up clusters from selected nodes");
        jButton5.setBorder(null);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/zoom_in.png"))); // NOI18N
        jButton6.setToolTipText("fit selected clusters");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/zoom_out.png"))); // NOI18N
        jButton7.setToolTipText("Fit entire graph");
        jButton7.setBorder(null);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/shape_handles.png"))); // NOI18N
        jButton8.setToolTipText("Select nodes in the selected clusters");
        jButton8.setBorder(null);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5)
                    .addComponent(jButton4)
                    .addComponent(jButton8)
                    .addComponent(jButton6)
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bodyPane.addTab("Community Str", jPanel1);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));

        clusterNumField.setText("4");

        jLabel3.setText("Number");

        jLabel4.setText("Size");

        jLabel5.setText("p-In");

        jLabel6.setText("p-In/Out ratio");

        jLabel7.setText("Size Distribution");

        clusterSizeField.setText("32");

        pInField.setText("0.2");

        pRatioField.setText("0.01");

        clusterSizeDistCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Uniform", "Poisson", "Exponential" }));

        generateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glay/extras/arrow_rotate_anticlockwise.png"))); // NOI18N
        generateButton.setText("Generate");
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(generateButton, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(clusterSizeDistCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pRatioField)
                            .addComponent(pInField)
                            .addComponent(clusterSizeField)
                            .addComponent(clusterNumField, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(clusterNumField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(clusterSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(pInField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(pRatioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(clusterSizeDistCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generateButton)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        messageLabel.setText("...");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel)
                .addContainerGap(215, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(messageLabel)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));

        jLabel8.setText("Node Num:");

        jLabel9.setText("Edge Num:");

        nodeNumLabel.setText("...");

        edgeNumLabel.setText("...");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nodeNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeNumLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(nodeNumLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(edgeNumLabel))
                .addContainerGap(403, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout randGraphPanelLayout = new javax.swing.GroupLayout(randGraphPanel);
        randGraphPanel.setLayout(randGraphPanelLayout);
        randGraphPanelLayout.setHorizontalGroup(
            randGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, randGraphPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(randGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        randGraphPanelLayout.setVerticalGroup(
            randGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(randGraphPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bodyPane.addTab("Rand Gen", randGraphPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bodyPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bodyPane, javax.swing.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
// TODO add your handling code here:
    //remove the panels, however, the actions have been added, how to remove the actions?
    //Cytoscape.getDesktop()
     CytoPanelImp ctrlPanel = (CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
     int indexInCytoPanel = ctrlPanel.indexOfComponent("GLay");
     ctrlPanel.remove(indexInCytoPanel);
     
     //GLayMainPanel gmp = new GLayMainPanel();
            
            //can add an icon here
            //ctrlPanel.add("GLay", gmp);
            
     //int indexInCytoPanel = ctrlPanel.indexOfComponent(gmp);
     //       ctrlPanel.setSelectedIndex(indexInCytoPanel);
}//GEN-LAST:event_jButton3ActionPerformed

private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed
// TODO add your handling code here:
    //run the algorithm, then apply layouts.
    
    
    
    
    
    //create a panel, could be duplicated
    //each must be assigned with the corresponding network
    //GLayResultPanel grp = new GLayResultPanel();
    //CytoPanelImp ctrlPanel = (CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
    //int indexInCytoPanel = ctrlPanel.indexOfComponent("GLay");
    //GLayMainPanel mainPanel = (GLayMainPanel)ctrlPanel.getComponentAt(indexInCytoPanel);
    //mainPanel.getTabbedPane().addTab("result", grp);
    
    //need to run the progess in a seperate thread
    //GLayProgressMonitor progressbar = new GLayProgressMonitor();
    //this is the mother
    //ProgressMonitor monitor = new ProgressMonitor(Cytoscape.getDesktop(), "Work in progress", "Seriously, working in progress", 0, 100);
    
    //System.out.println("Button clicked");
    //monitor.setProgress(20);
    //GLayProgressMonitor gpm = new GLayProgressMonitor();
    //this.progressBar.setVisible(true);
    task = new Task();
    task.addPropertyChangeListener(this);
    task.execute();
    
}//GEN-LAST:event_executeButtonActionPerformed

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
// TODO add your handling code here:
    this.clusterList.clearSelection();
    //List<CyNode> nodeList = Cytoscape.getCurrentNetwork().nodesList();
}//GEN-LAST:event_jButton4ActionPerformed

private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// TODO add your handling code here:
    //need to retrive the groups
    List<NodeView> nodeList = Cytoscape.getCurrentNetworkView().getSelectedNodes();
    CyNetwork nw = Cytoscape.getCurrentNetwork();
    
    HashSet<CyGroup> groupSet = new HashSet<CyGroup>();
    for(NodeView node:nodeList){
        groupSet.addAll(CyGroupManager.getGroup((CyNode)node.getNode()));
    }
    
    //System.out.println(groupSet.size());
    int nwIdLength = nw.getIdentifier().length()+1;
    ArrayList<Integer> index = new ArrayList<Integer>();
    for(CyGroup grp:groupSet){
        String id = grp.getGroupName();
        if(id.startsWith(nw.getIdentifier()+"_")){
            int end = id.indexOf("[CLUSTER]");
            //System.out.println(id.substring(nwIdLength, end));
            //index = Integer.parseInt(id.substring(nwIdLength, end));
            index.add(Integer.parseInt(id.substring(nwIdLength, end)));
            //this.clusterList.setS
        }   
    }
    
    int[] indices = new int[index.size()];
    for(int i=0; i<indices.length; i++){
        indices[i] = index.get(i).intValue();
    }
    
    this.clusterList.setSelectedIndices(indices);
    
}//GEN-LAST:event_jButton5ActionPerformed

private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
// TODO add your handling code here:
    //the painful work around:
    //get selected now
    CyNetworkView nwv = Cytoscape.getCurrentNetworkView();
    CyNetwork nw = Cytoscape.getCurrentNetwork();
    int[] indices = this.clusterList.getSelectedIndices();
    
    if(indices.length == 0){
        nwv.fitContent();
    }
    else{
        //save the current selected
        int[] selected = nwv.getSelectedNodeIndices();
        ArrayList<Node> nodeL = new ArrayList<Node>();
        for(int index:selected){
            nodeL.add(nw.getNode(index));
        }

        //
        //fit selected
        //FitContentAction action = new FitContentAction();
        //action.actionPerformed(null); //can i do this way? should be a fireaction, and it works...__-, i am so creative
        GinyUtils.deselectAllNodes(nwv);
        
        //then need to select the selected clusters
        for(int index:indices){
            //
            CyGroup grp = CyGroupManager.findGroup(nw.getIdentifier()+"_"+index+"[CLUSTER]");
            List<CyNode> nodeList = grp.getNodes();
            //for(CyNode node:nodeList){
             //   nw.setSe
            //}
            nw.setSelectedNodeState(nodeList, true);
        }
        
        ((DingNetworkView) Cytoscape.getCurrentNetworkView()).fitSelected();
        GinyUtils.deselectAllNodes(nwv);
        
        
        nw.setSelectedNodeState(nodeL, true);
        nwv.updateView();
        
        
    }
}//GEN-LAST:event_jButton6ActionPerformed

private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
// TODO add your handling code here:
    Cytoscape.getCurrentNetworkView().fitContent();
}//GEN-LAST:event_jButton7ActionPerformed

private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
// TODO add your handling code here:
    //select nodes
    CyNetwork nw = Cytoscape.getCurrentNetwork();
    CyNetworkView nwv = Cytoscape.getCurrentNetworkView();
    
    
    int[] indices = this.clusterList.getSelectedIndices();
    List<CyNode> nodeList = new ArrayList<CyNode>();
    for(int index:indices){
        CyGroup grp = CyGroupManager.findGroup(nw.getIdentifier() + "_" + index + "[CLUSTER]");
        nodeList.addAll(grp.getNodes());
    }
    
    nw.setSelectedNodeState(nodeList, true);
    nwv.updateView();
}//GEN-LAST:event_jButton8ActionPerformed

private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
// TODO add your handling code here:
    //It can be tricky here.
    System.out.println(this.pInField.getText());
    System.out.println(this.pRatioField.getText());
    System.out.println(this.clusterNumField.getText());
    System.out.println(this.clusterSizeField.getText());
    System.out.println(this.clusterSizeDistCombo.getSelectedIndex());
    
    double pIn = Double.parseDouble(this.pInField.getText());
    double pRatio = Double.parseDouble(this.pRatioField.getText());
    int model = this.clusterSizeDistCombo.getSelectedIndex();
    int clusterNum = Integer.parseInt(this.clusterNumField.getText());
    int clusterSize = Integer.parseInt(this.clusterSizeField.getText());
    
    //create the random graph
    //validation required for input fields
    
    
    
    
    RandomClusterModel rcm = new RandomClusterModel(pIn, pRatio, model);
    CyNetwork network = rcm.generateModel(clusterNum, clusterSize);
    CyNetworkView nwv = Cytoscape.createNetworkView(network);
    
    //Apply a certain layout
    //nwv.applyLayout(new GridNodeLayout());
    //nwv.applyLayout(new GLayClusterModel());
    
    //These msgs are displayed after creation
    this.messageLabel.setText("Network Generation Successful");
    this.nodeNumLabel.setText(network.getNodeCount()+"");
    this.edgeNumLabel.setText(network.getEdgeCount()+"");
}//GEN-LAST:event_generateButtonActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
    this.clusterListModel.clear();
    this.comboAlgorithm.setSelectedIndex(0);
    this.comboLayout.setSelectedIndex(0);
    this.resetCheck.setSelected(false);
    this.statusMsg.setText("Ready");
    
    //can refactor this function 
    List<CyGroup> grpList = CyGroupManager.getGroupList();
    for(CyGroup grp:grpList){
        if(grp.getGroupName().startsWith(Cytoscape.getCurrentNetwork().getIdentifier())){
            CyGroupManager.removeGroup(grp);
        }
    }
}//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane bodyPane;
    private javax.swing.JList clusterList;
    private javax.swing.JTextField clusterNumField;
    private javax.swing.JComboBox clusterSizeDistCombo;
    private javax.swing.JTextField clusterSizeField;
    private javax.swing.JComboBox comboAlgorithm;
    private javax.swing.JComboBox comboLayout;
    private javax.swing.JLabel edgeNumLabel;
    private javax.swing.JButton executeButton;
    private javax.swing.JButton generateButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JLabel nodeNumLabel;
    private javax.swing.JTextField pInField;
    private javax.swing.JTextField pRatioField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel randGraphPanel;
    private javax.swing.JCheckBox resetCheck;
    private javax.swing.JLabel statusMsg;
    // End of variables declaration//GEN-END:variables

}

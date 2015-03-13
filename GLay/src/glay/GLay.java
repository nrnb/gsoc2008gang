/*
 * This is the test Template for GLay Panels
 */

package glay;

/**
 *
 * @author Gang Su, sugang@umich.edu
 */
import javax.swing.*;
import java.awt.event.ActionEvent;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.layout.CyLayouts;
import cytoscape.Cytoscape;
import cytoscape.CyMain;
import cytoscape.util.CytoscapeAction;
import cytoscape.CyNetwork;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.CyNetworkView;

import cytoscape.groups.*;

import glay.GLayDegreeDist;
import glay.GLayGroup;
import glay.GLayCluster;
import glay.util.RandomClusterModel;
import glay.GLayRandomGraphDialog;
import glay.GLayRandomGraphPanel;
import glay.util.FastGreedy;
import glay.GLayMainPanel;
import glay.util.SampleGroupViewer;

import java.util.*;
import giny.model.*;
import glay.util.FastGreedy2;






public class GLay extends CytoscapePlugin{
    /*
     * Constructor
     */
    //private boolean active;
    
    public GLay(){
        //System.out.println("This is a plugin");
        //JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Plugin Loaded");
        //A pluin.props file is required in the jar, as to be part of the properties.
        //GLayDegreeDist glayAlgorithm = new GLayDegreeDist();
        //GLayGroup glayGroup = new GLayGroup();
        //GLayCluster glayCluster = new GLayCluster();
        //CyLayouts.addLayout(glayCluster, "g.Layouts");
        //CyLayouts.addLayout(glayAlgorithm, "g.Layouts");
        //CyLayouts.addLayout(glayGroup, "g.Layouts");
        
        //Set the entry point. I am not very clear about this model
        //of adding an action, then adding the menu group
        RandomGraphAction action = new RandomGraphAction(this);
        Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
        //this.active = false;
        
        
        
    }
    
    public class RandomGraphAction extends CytoscapeAction{
        private JFrame jframe;
        //private boolean isActive;
        
        public RandomGraphAction(GLay glay){
            //This is passing the name to create the action
            super("GLay");
            setPreferredMenu("Plugins");
            //to avoid duplicate activation of the side panel.
            //is this the best solution?
            //this.isActive = false;
        }
        
        public void actionPerformed(ActionEvent e){
              //RandomClusterModel rcm = new RandomClusterModel(0.2, 0.01, RandomClusterModel.POISSON_CSIZE_DIST);
              //rcm.generateModel(4,32);
              
              //Create network, create network view, apply layout
//            This is equivalent to creating a new thread, not good, use panels instead
//            java.awt.EventQueue.invokeLater(new Runnable(){
//                public void run() {
//                    new GLayRandomGraphDialog().setVisible(true);
//                }
//            });
            /*
              if(!this.isActive){
              CytoPanelImp ctrlPanel = (CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
              GLayRandomGraphPanel grgp = new GLayRandomGraphPanel();
              ctrlPanel.add("R.G.G",grgp);
              
              int indexInCytoPanel = ctrlPanel.indexOfComponent(grgp);
              ctrlPanel.setSelectedIndex(indexInCytoPanel);
              this.isActive = true;
              }
            */
            //FastGreedy.community(Cytoscape.getCurrentNetwork());
           //FastGreedy.run_sm(Cytoscape.getCurrentNetwork());
           //CyNetworkView nv = Cytoscape.getCurrentNetworkView();
           //nv.applyLayout(new GLayClusterModel());
            
            
            
            ////////////////////////////////////////////////////////////////////////////////////////////////
            //To create a panel for Glay
            //* 
            //* 
            CytoPanelImp ctrlPanel = (CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
            int indexInCytoPanel = ctrlPanel.indexOfComponent("GLay");
            if(indexInCytoPanel < 0 ){
            
            
            GLayMainPanel gmp = new GLayMainPanel();
            
            //can add an icon here
            ctrlPanel.add("GLay", gmp);
            
            indexInCytoPanel = ctrlPanel.indexOfComponent(gmp);
            ctrlPanel.setSelectedIndex(indexInCytoPanel);
//            //////////////////////////////////////////////////////////////////////////////////////////////////
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            //this.isActive = true; //make sure only initialize one instance, but how to remove it then; each time it creates a new action
            /*
            SampleGroupViewer sgv = new SampleGroupViewer();
            System.out.println(sgv.getViewerName());
            
            //failed, don't know why
            ArrayList<Node> nodeList = new ArrayList<Node>();
            CyNetwork network = Cytoscape.getCurrentNetwork();
            List nodeL = network.nodesList(); //list is abstract...wtf, it's an interface!
            List sublist = nodeL.subList(0, 20);
            CyGroupManager.createGroup("Random Group", sublist, sgv.getViewerName());
            */
            
            
            
            
            //test the new structure
            //FastGreedy2 fg2 = new FastGreedy2();
            //fg2.execute();
            //System.out.println("ended");
            
         }
    }
        
}
}
    


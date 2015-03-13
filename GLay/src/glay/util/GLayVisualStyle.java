/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * This creates the visual style dynamically to Cytoscape
 */

package glay.util;

/**
 *
 * @author sugang
 */
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import java.util.Random;

import java.awt.Color;
//import java.awt.color.ICC_ColorSpace;
import java.awt.color.*;

public class GLayVisualStyle {
    
    public static VisualStyle createClusterVisualStyle(CyNetwork network){
        
        
        
        
        
        VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
        NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
        
        CalculatorCatalog calculatorCatalog = vmManager.getCalculatorCatalog();
        
        if(calculatorCatalog.getVisualStyle("GLay") != null){
            return calculatorCatalog.getVisualStyle("GLay");
        }
        else{
        
        
        //Discrete mapping for edge attr
        //why we use color.blue here? it's confusing man
        //Object defaultObj = VisualPropertyType.EDGE_COLOR.getDefault(Color.BLACK);    
        DiscreteMapping edgeColorMapping = new DiscreteMapping(Color.BLACK, ObjectMapping.EDGE_MAPPING);
        //the network is used here to load the attributes
        edgeColorMapping.setControllingAttributeName("class", network, false);
        //Maybe i need string values?
        edgeColorMapping.putMapValue(0, Color.BLACK);
        edgeColorMapping.putMapValue(1, Color.DARK_GRAY);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //defaultObj = VisualPropertyType.EDGE_OPACITY.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());    
        DiscreteMapping edgeOpacityMapping = new  DiscreteMapping(255, ObjectMapping.EDGE_MAPPING);
        edgeOpacityMapping.setControllingAttributeName("class", network, false); //if there's a weight
        edgeOpacityMapping.putMapValue(0, 220);
        edgeOpacityMapping.putMapValue(1, 120);
        
        //defaultObj = VisualPropertyType.EDGE_LINE_STYLE.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());    
        DiscreteMapping edgeTypeMapping = new  DiscreteMapping(LineStyle.SOLID, ObjectMapping.EDGE_MAPPING);
        edgeTypeMapping.setControllingAttributeName("class", network, false); //if there's a weight
        edgeTypeMapping.putMapValue(0, LineStyle.SOLID);
        edgeTypeMapping.putMapValue(1, LineStyle.LONG_DASH);   
        
        
        
        
        Calculator edgeColorCal = new BasicCalculator("Edge Color Calculator", edgeColorMapping, VisualPropertyType.EDGE_COLOR);
        Calculator edgeOpacityCal = new BasicCalculator("Edge Opacity Calculator", edgeOpacityMapping, VisualPropertyType.EDGE_OPACITY);
        Calculator edgeTypeCal = new BasicCalculator("Edge Opacity Calculator", edgeTypeMapping, VisualPropertyType.EDGE_LINE_STYLE);
        edgeAppCalc.setCalculator(edgeColorCal);
        edgeAppCalc.setCalculator(edgeOpacityCal);
        edgeAppCalc.setCalculator(edgeTypeCal);
        //DiscreteMapping 
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        
        
        //VisualPropertyType.NODE_LABEL_OPACITY
        
        //A lil bit confusing here
        DiscreteMapping nodeLabelOpacityMapping = new  DiscreteMapping(255, ObjectMapping.NODE_MAPPING);
        nodeLabelOpacityMapping.setControllingAttributeName("listActive", network, false); //if there's a weight
        nodeLabelOpacityMapping.putMapValue(0, 70);
        nodeLabelOpacityMapping.putMapValue(1, 255);
        Calculator nodeLabelOpacityCal = new BasicCalculator("Node Label Opacity Cal", nodeLabelOpacityMapping, VisualPropertyType.NODE_LABEL_OPACITY);
        nodeAppCalc.setCalculator(nodeLabelOpacityCal);
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //DiscreteMapping disMapping = new DiscreteMapping();
        //defaultObj = VisualPropertyType.NODE_FILL_COLOR.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
        //need to change the attributes, append "glay_" as prefix
        DiscreteMapping nodeOpacityMapping = new  DiscreteMapping(255, ObjectMapping.NODE_MAPPING);
        nodeOpacityMapping.setControllingAttributeName("listActive", network, false); //if there's a weight
        nodeOpacityMapping.putMapValue(0, 70);
        nodeOpacityMapping.putMapValue(1, 240);
        Calculator nodeOpacityCal = new BasicCalculator("Node Opacity cal", nodeOpacityMapping, VisualPropertyType.NODE_OPACITY);
        nodeAppCalc.setCalculator(nodeOpacityCal);
        
                
        
        DiscreteMapping nodeColorMapper = new DiscreteMapping(Color.RED, "membership", ObjectMapping.NODE_MAPPING);
        //nodeColorMapper.setControllingAttributeName("membership", network, false);
        Random rand = new Random();
        
        
        
        //essentially don't want all the color values too be too low
        //the hsv is not working..
        //ICC_ColorSpace cs = new ICC_ColorSpace(ICC_Profile.getInstance(ColorSpace.TYPE_HSV));
        float[] components = new float[3];
        int col;
        //System.out.println(col);
        //hsv color space, to control the brightness so that the nodes won't be too dark
        for(int i=0; i<50; i++){
            components[0] = rand.nextFloat();//rand.nextInt(255); H
            components[1] = (float)(rand.nextFloat());//20+rand.nextInt(225); S
            components[2] = (float)(rand.nextFloat()/3+0.66);//20+rand.nextInt(225); B
            //components[2] = -3;
            
            col = Color.HSBtoRGB(components[0], components[1], components[2]);
            nodeColorMapper.putMapValue(i, new Color(col));
        }
        
        Calculator nodeCal = new BasicCalculator("Membership Cal", nodeColorMapper, VisualPropertyType.NODE_FILL_COLOR);
        nodeAppCalc.setCalculator(nodeCal);
        
        //defaultObj = VisualPropertyType.NODE_SIZE.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
        DiscreteMapping nodeSizeMapper = new DiscreteMapping(50, ObjectMapping.NODE_MAPPING);
        nodeSizeMapper.setControllingAttributeName("type", network, false);
        nodeSizeMapper.putMapValue(0, 50);
        nodeSizeMapper.putMapValue(1, 100);
        nodeSizeMapper.putMapValue(2, 80); //this is for active ones
        Calculator nodeSizeCal = new BasicCalculator("Node Size Calculator", nodeSizeMapper, VisualPropertyType.NODE_SIZE);
        nodeAppCalc.setCalculator(nodeSizeCal);
        
        //Set the default node appearance
        //NodeAppearance defaultNodeApp = new NodeAppearance();
        //defaultNodeApp.set
        
        //defaultObj = VisualPropertyType.NODE_SHAPE.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
        DiscreteMapping nodeShapeMapper = new DiscreteMapping(NodeShape.ELLIPSE, ObjectMapping.NODE_MAPPING);
        nodeShapeMapper.setControllingAttributeName("type", network, false);
        nodeShapeMapper.putMapValue(1, NodeShape.ELLIPSE);
        nodeShapeMapper.putMapValue(0, NodeShape.ELLIPSE);
        nodeShapeMapper.putMapValue(2, NodeShape.ELLIPSE); //the extended type
        
        
        //The errors and exceptions here are not thrown
        Calculator nodeShapeCal = new BasicCalculator("Node Shape Calculator", nodeShapeMapper, VisualPropertyType.NODE_SHAPE);
        nodeAppCalc.setCalculator(nodeShapeCal);
        
        
        //defaultObj = VisualPropertyType.NODE_LABEL.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle()); 
        PassThroughMapping nodeLabelMapper = new PassThroughMapping(new String(), "ID");
        Calculator nodeLabelCalculator = new BasicCalculator("ID", nodeLabelMapper, VisualPropertyType.NODE_LABEL);
        nodeAppCalc.setCalculator(nodeLabelCalculator);
        //nodeLabelMapper.setControllingAttributeName("ID", network, false);
        //nodeLabelMapper.putMapValue("ID");
        
        //defaultObj = VisualPropertyType.NODE_LINE_WIDTH.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
        DiscreteMapping nodeLineWidthMapper = new DiscreteMapping(4, ObjectMapping.NODE_MAPPING);
        nodeLineWidthMapper.setControllingAttributeName("type", network, false);
        nodeLineWidthMapper.putMapValue(1, 10);
        nodeLineWidthMapper.putMapValue(0, 4);
        nodeLineWidthMapper.putMapValue(2, 8);
        Calculator nodeLineWidthCal = new BasicCalculator("Node Line Width Calculator", nodeLineWidthMapper, VisualPropertyType.NODE_LINE_WIDTH);
        nodeAppCalc.setCalculator(nodeLineWidthCal);
        
        //defaultObj = VisualPropertyType.NODE_BORDER_OPACITY.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
        DiscreteMapping nodeBorderOpacityMapper = new DiscreteMapping(200, ObjectMapping.NODE_MAPPING);
        nodeBorderOpacityMapper.setControllingAttributeName("type", network, false);
        nodeBorderOpacityMapper.putMapValue(1, 150);
        Calculator nodeBorderOpacityCal = new BasicCalculator("Node Border Opacity Calculator", nodeBorderOpacityMapper, VisualPropertyType.NODE_BORDER_OPACITY);
        nodeAppCalc.setCalculator(nodeBorderOpacityCal);
        
        
        
        
        //need to find a way to set defaults
        //Appearance app = new Appearance();
        //app.set(VisualPropertyType.NODE_SHAPE, NodeShape.HEXAGON);
        
        
        
        
        
        //ndm.putMapValue(0, Color.BLUE);
        //ndm.putMapValue(1, Color.ORANGE);
        //ndm.putMapValue(2, Color.GREEN);
        //ndm.putMapValue(3, Color.magenta);
        //ndm.putMapValue(4, Color.)
        
        
        
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Continuous Mapping 
        //ContinuousMapping cm = new ContinuousMapping();
        
        //ContinuousMapping cm = new ContinuousMapping(defaultObj, ObjectMapping.NODE_MAPPING);
       // cm.setControllingAttributeName("membership", network, false);
        
      //  Interpolator numToColor = new LinearNumberToColorInterpolator();
     //   cm.setInterpolator(numToColor);
        
       // Color underColor = new Color(255,0,0);
      //  Color minColor = new Color(200,100,0);
      //  Color midColor = new Color(150,200,100);
     //   Color maxColor = new Color(50,255,150);
     //   Color overColor = new Color(0,100,255);
        
     //   BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
     //   BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
     //   BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);
        
     //   cm.addPoint(0, bv0);
    //    cm.addPoint(10, bv1);
     //   cm.addPoint(30, bv2);
        
        
        //let's try another one with opacity with continous mapping
        
        //defaultObj = VisualPropertyType.NODE_SIZE.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
       // ContinuousMapping cm2 = new ContinuousMapping(defaultObj, ObjectMapping.NODE_MAPPING);
        //cm2.setControllingAttributeName("Opacity", network, false);
        
       // Interpolator numToOpacity = new LinearNumberToNumberInterpolator();
       // cm2.setInterpolator(numToOpacity);
        
      //  BoundaryRangeValues bv01 = new BoundaryRangeValues(50,50,50);
       // BoundaryRangeValues bv02 = new BoundaryRangeValues(200,200,200);
        
      //  cm2.addPoint(0.0, bv01);
      //  cm2.addPoint(1.0, bv02);
        
       // Calculator nodeCal2 = new BasicCalculator("OpacityCal", cm2, VisualPropertyType.NODE_SIZE);
       //nodeAppCalc.setCalculator(nodeCal2);
        
        //System.out.println(nodeAppCalc.getCalculators().size()+" calculators");
        
        
        
        //use calculators..
        //Here it means to use the current calculator
        GlobalAppearanceCalculator gac = vmManager.getVisualStyle().getGlobalAppearanceCalculator();
        
        //use default for node appearance
        //NodeAppearanceCalculator nac = vmManager.getVisualStyle().getNodeAppearanceCalculator();
        NodeAppearanceCalculator nac = nodeAppCalc;
        EdgeAppearanceCalculator eac = edgeAppCalc;
        
        gac.setDefaultBackgroundColor(Color.white);
        
        
        
        
        VisualStyle visualStyle = new VisualStyle("GLay", nac, eac, gac);
        
        //otherways to double check
        //if(calculatorCatalog.getVisualStyle("GLay Visual")==null){
        
        //can test to see, if the visualstyle already exists, get it. otherwise, replace it
        
        
        calculatorCatalog.addVisualStyle(visualStyle);
        VisualPropertyType.NODE_FILL_COLOR.setDefault(visualStyle, new Color(200,200,200));
        VisualPropertyType.NODE_SHAPE.setDefault(visualStyle, NodeShape.ELLIPSE);
        VisualPropertyType.EDGE_COLOR.setDefault(visualStyle, new Color(50,150,200));
        VisualPropertyType.EDGE_LINE_WIDTH.setDefault(visualStyle, 4);
        VisualPropertyType.NODE_SIZE.setDefault(visualStyle, 50);
        VisualPropertyType.NODE_BORDER_COLOR.setDefault(visualStyle, new Color(100,100,100));
        //}
        
        return visualStyle;
        }
    }
}

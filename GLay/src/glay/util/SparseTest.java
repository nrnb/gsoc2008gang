/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Test the efficiency of the sparse matrix. Obsolete
 */

package glay.util;

/**
 *
 * @author sugang
 */
import cern.colt.matrix.impl.*;
import cern.colt.function.*;

public class SparseTest {

    public static void main(String[] args){
        SparseDoubleMatrix2D mx = new SparseDoubleMatrix2D(10,10);
        for(int i=0; i<2000; i++){
            for(int j=0; j<2000; j++){
                //System.out.printf("%2.2f ", mx.getQuick(i, j));
                if(i==j){
                    mx.setQuick(i, j, Math.random());
                }
            }
            //System.out.println();
        }
        
        
        
        
       /* 
       for(int i=0; i<2000; i++){
            for(int j=0; j<2000; j++){
                System.out.printf("%2.2f ", mx.getQuick(i, j));
            }
            System.out.println();
        }
       */
       Fun func = new Fun(); 
       
       //mx.forEachNonZero(function(x){});
       for(int i=0; i<2000; i++){
            mx.forEachNonZero(func);
       }
       
       System.out.println("max:" + func.max + " " + func.row + " " + func.column);
       
       /*
       for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                System.out.printf("%2.2f ", mx.getQuick(i, j));
            }
            System.out.println();
        }
        */
    }
    
    
}

class Fun implements IntIntDoubleFunction{
        public double max;
        public int row;
        public int column;
        
        public Fun(){
            max = Double.MIN_VALUE;
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

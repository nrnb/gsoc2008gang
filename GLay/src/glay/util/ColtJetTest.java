/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Testing random number gen in cold package
 */

package glay.util;

/**
 *
 * @author sugang
 */
import cern.jet.random.*;

public class ColtJetTest {

    public static void main(String[] args){
        System.out.println(Exponential.staticNextDouble(0.01));
        System.out.println(Poisson.staticNextInt(10));
        //System.out.println(Uniform.stat)
    }
}

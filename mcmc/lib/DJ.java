import BayesianNetworks.*;
import InferenceGraphs.*;
import InterchangeFormat.*;

import java.io.*;
import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Date;

public class DJ{
	public static void main(String[] args){
		try{
			InferenceGraph G=new InferenceGraph("hw5.bif");
			Vector nodes=G.get_nodes();
			InferenceGraphNode n=((InferenceGraphNode)nodes.elementAt(0));
			System.out.println(n.get_name());	
			n.get_Prob().print();		

			//Create string of variable-value pairs for probability table at node 0*/
			String[][] s=new String[1][2];	
			s[0][0]="B";
			s[0][1]="False";
			//Compute probability with given variable-value pairs;
			System.out.println(n.get_function_value(s));	
			
			Vector children=n.get_children();//get_parents() works too;	
			for (Enumeration e = children.elements(); e.hasMoreElements(); ) {
			        InferenceGraphNode no = (InferenceGraphNode)(e.nextElement());
				System.out.println("\t"+no.get_name());			
				no.get_Prob().print();	//Get the probability table object for the node -> Look at ProbabilityFunction.java for info
			}				
			
		}
		catch(IFException e){
			System.out.println("Formatting Incorrect "+e.toString());
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found "+e.toString());
		}
		catch(IOException e){
			System.out.println("File not found "+e.toString());
		}
	}
}

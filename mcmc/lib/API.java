/**
 * BayesNet.java
 * @author Fabio G. Cozman 
 * Copyright 1996 - 1999, Fabio G. Cozman,
 *          Carnergie Mellon University, Universidade de Sao Paulo
 * fgcozman@usp.br, http://www.cs.cmu.edu/~fgcozman/home.html
 *
 * The JavaBayes distribution is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License or, at your option, any later version), 
 * provided that this notice and the name of the author appear in all 
 * copies. Upon request to the author, some of the packages in the 
 * JavaBayes distribution can be licensed under the GNU Lesser General
 * Public License as published by the Free Software Foundation (either
 * version 2 of the License, or (at your option) any later version).
 * If you're using the software, please notify fgcozman@usp.br so
 * that you can receive updates and patches. JavaBayes is distributed
 * "as is", in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with the JavaBayes distribution. If not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*Modified by DJ for CSEP573*/

/*Stripped down version of the file BayesNet.java in the BayesNet.jar */

/* ************************************************************************ */

public class BayesNet {

	/*Constructors*/

  /**
   * Default constructor for a BayesNet.
   */
  public BayesNet() {
  }

  /**
   * Simple constructor for a BayesNet.
   * @param n_n Name of the network.
   * @param n_v Number of variables in the network.
   * @param n_f Number of probability distributions in the network.
   */
  public BayesNet(String n_n, int n_v, int n_f) {
  }

  /**
   * Simple constructor for a BayesNet.
   * @param n_n Name of network.
   * @param p Properties of the network.
   */
  public BayesNet(String n_n, Vector p) {
  }

  /**
   * Simple constructor for a BayesNet; creates a copy of a given
   * network.
   * @param bn Network to be copied.
   */
  public BayesNet(BayesNet bn) {
  }

  /**
   * Construct a BayesNet from a textual description in a stream. Used by DJ.java to read alarm.bif and create a BayesNet object*/
*/
  public BayesNet(InputStream istream) throws IFException {   
  }   


	
 /**
   * Find the ProbabilityFunction that corresponds to a
   * given ProbabilityVariable.
   * Note: the index of a variable is used by the function, as it
   * is the only reference to the variable that is guaranteed to identify
   * the variable uniquely.
   */
  public ProbabilityFunction get_function(ProbabilityVariable p_v) {
  }

  /**
   * Save a BayesNet object in a stream, in the BIF InterchangeFormat.
   */
  public void save_bif(PrintStream out) {
  }

  /**
   * Get all the evidence contained in the network variables.
   */
  public String[][] get_all_evidence() {
  }

  /**
   * Determine the position of a variable given its name.
   */

  public int index_of_variable(String n_vb) {
  }

  /**
   * Print a BayesNet in the standard output.
   */
  public void print() {
  }

  /**
   * Print a BayesNet in a given stream.
   */
  public void print(PrintStream out) {
  }

  /* *************************************************************** *
   * Methods that allow basic manipulation of non-public variables   *
   * *************************************************************** */

  /**
   * Get the name of the network.
   */
  public String get_name() {
  }

  /**
   * Set the name of the network.
   */
  public void set_name(String n) {
  }

  /**
   * Get the properties.
   */
  public Vector get_properties() {
  }

  /** 
   * Set the properties.
   */
  public void set_properties(Vector prop) {
  }
  
  /**
   * Add a property.
   */
  public void add_property(String prop) {
  }

  /**
   * Remove a property.
   */
  public void remove_property(String prop) {
  }

  /**
   * Remove a property.
   */
  public void remove_property(int i) {
  }

  /**
   * Get the number of variables in the network.
   */
  public int number_variables() {
  }

  /**
   * Get the number of distributions in the network.
   */
  public int number_probability_functions() {
  }

  /**
   * Get the probability variable at a given index.
   */
  public ProbabilityVariable get_probability_variable(int index) {
  }

  /**
   * Get the probability function at a given index.
   */
  public ProbabilityFunction get_probability_function(int index) {
  }

  /**
   * Get the probability variables.
   */
  public ProbabilityVariable[] get_probability_variables() {
  }

  /**
   * Get the probability functions.
   */
  public ProbabilityFunction[] get_probability_functions() {
  }
  
  /**
   * Get the utility function.
   */
  public DiscreteFunction get_utility_function() {
  }

  /**
   * Set a probability variable given its constituents.
   */
  public void set_probability_variable(int index, String name,
				       String v[], Vector vec) {    
  }

  /**
   * Set a probability function given its constituents.
   */
  public void set_probability_function(int index,
                        ProbabilityVariable[] variables,
				        double values[], Vector vec) {  
  }

  /**
   * Set a probability variable given its index.
   */
  public void set_probability_variable(int index, ProbabilityVariable p_v) {
  }

  /**
   * Set a probability variable given its index.
   */
  public void set_probability_function(int index, ProbabilityFunction p_f) {
  }

  /**
   * Set the vector of probability variables.
   */
  public void set_probability_variables(ProbabilityVariable pvs[]) {
  }

  /**
   * Set the vector of probability functions.
   */
  public void set_probability_functions(ProbabilityFunction pfs[]) {
  }
}




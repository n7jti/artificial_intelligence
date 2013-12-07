import BayesianNetworks.*;
import InferenceGraphs.*;
import InterchangeFormat.*;

import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class A4{

    Random _rnd;
    InferenceGraph _graph;
    InferenceGraphNode _curNode;
    Map<String,String> _evidence;
    //Map<String,String> _pairs;
    String[][] _pairs;
    Map<String, Integer> _indexes;

    Vector<InferenceGraphNode> _nodes;
    Diagnosis _diagnosis;

    // Main function
    // Argument 0 is the input file
    public static void main(String[] args){
        try{
            A4 a4 = new A4();
            a4.solve(args[0]);                    
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

    // Constructor
    A4(){
        _rnd = new Random(); // initialze the random number generator
        _diagnosis = new Diagnosis();
    }

    // Computes the probabilities 
    void solve(String inputFile) throws IFException, IOException, FileNotFoundException {

        // parse the input file into a map
        _evidence = readInput(inputFile);

        // parse the output file into an InferenceGraph
        _graph = new InferenceGraph("alarm.bif");
        _nodes = _graph.get_nodes();

        //generate a random start
        Map<String,String> pairs = randomStart(_graph);

        // put the evidence into our random start
        pairs.putAll(_evidence);

        // populate the _pairs array and the index into the _pairs array
        _pairs = mapToArray(pairs);
        _indexes = pairsToMap(_pairs);

        //prime the mcmc
        for (int count = 0; count < 1e6; count++) {
            mcmc();
        }

        // Now, get our counts!
        for (int count = 0; count < 5e7; count++) {
            mcmc();
            if (count % 50 == 0) {
                _diagnosis.count(_pairs,_indexes);
            }
        }

        // output the diagnosis
        _diagnosis.displayOutput();
    }

    // Reads the input file
    HashMap<String, String> readInput(String inputFile) throws FileNotFoundException, IOException {
        BufferedReader inBuf = new BufferedReader(new FileReader(inputFile));
        HashMap<String,String> map = new HashMap<String,String>();
        Pattern pattern = Pattern.compile("\"(.+)\"\\s*\\=\\s*\"(.+)\"");
        String line;
        line = inBuf.readLine();
        while (line != null){	
            if (line.charAt(0) != '.') {
                Matcher match = pattern.matcher(line);
                if (match.find()) {
                    String key = match.group(1);
                    String value = match.group(2);
                    map.put(key, value);
                }
            }
            else{
                break;
            }
            line = inBuf.readLine();
        }
        return map;
    }
    Map<String, Integer> pairsToMap(String pairs[][]){
        HashMap<String, Integer> map = new HashMap<String,Integer>();
        for(int index = 0; index < pairs.length; index++) {
            map.put(pairs[index][0], index);
        }
        return map; 
    }

    String[][] mapToArray(Map<String,String> map){
        String array[][] = new String[map.size()][2];
        Iterator it = map.entrySet().iterator();
        int index = 0;
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            //System.out.println((String)entry.getKey() + ": " + (String)entry.getValue());
            array[index][0] = (String)entry.getKey();
            array[index][1] = (String)entry.getValue();
            index++;
        }

        return array;
    } 

    // Chooses a value for the node at random.  Used by randomStart
    String randomValue(InferenceGraphNode node){
        // get the list of values
        String vals[] = node.get_values();

        // chose a value randomly
        int index = _rnd.nextInt(vals.length);

        // return the value
        return vals[index];
    } 

    // Choose a radom start for the graph by choosing a random value for each node. 
    HashMap<String,String> randomStart(InferenceGraph graph)
    {
        Vector<InferenceGraphNode> nodes = graph.get_nodes();
        Iterator<InferenceGraphNode> it = nodes.iterator();
        HashMap<String,String> map = new HashMap<String,String>();
        int index = 0;
        while (it.hasNext()) {
            // get name
            InferenceGraphNode no = it.next();
            map.put(no.get_name(), randomValue(no));    
            index++;
        }
        return map;
    }

    InferenceGraphNode randomNode(){
        // choose a node at random, making sure you dont' choose
        // 1. the current node.
        // 2. An evidence node.
        boolean found = false;

        int cantidateNode;
        InferenceGraphNode node;
        while(true) {
            cantidateNode = _rnd.nextInt(_nodes.size());
            node = _nodes.elementAt(cantidateNode);
            String nodeName = node.get_name();
            if(!_evidence.containsKey(nodeName)) {
                if (_curNode == null) {
                    break;
                }
                else{
                    if(!nodeName.equals(_curNode.get_name())) {
                        break;
                    }
                }
            }
        }
        return node;
    }

    /*
    int indexOfCurNode(){
        int index = 0;
        String name = _curNode.get_name();
        while (index < _pairs.length-1) {
            if(name.equals(_pairs[index][0])) {
                break;
            }
            index++;
        }
        return index;
    } 
    */
    
    int indexOfCurNode(){
        return _indexes.get(_curNode.get_name());
    }

    //foo
    void mcmc(){
        _curNode = randomNode();
        String values[] = _curNode.get_values();
        double probs[] = new double[values.length];
        //String pairs[][] = mapToArray(_pairs, _curNode);
        int curIndex = indexOfCurNode();

        for (int index = 0; index < values.length; index++) {
            _pairs[curIndex][1] = values[index];
            probs[index] = _curNode.get_function_value(_pairs);
            Vector<InferenceGraphNode> childNodes = _curNode.get_children();
            double pChild = 1;
            Iterator<InferenceGraphNode> it = childNodes.iterator();
            while(it.hasNext()) {
                InferenceGraphNode childNode = it.next();
                pChild *= childNode.get_function_value(_pairs);
            }
            probs[index] *= pChild;
        }

        //Now, normalize the probability
        double sum = 0.0;
        for(int index = 0; index < probs.length; index++) {
            sum += probs[index];
        }
        for(int index = 0; index < probs.length; index++) {
            probs[index] = probs[index] / sum;
        }

        //Now, generate random value
        double choice = _rnd.nextDouble();
        sum = 0.0;
        for(int index = 0; index < probs.length; index++) {
            sum += probs[index];
            if (choice < sum || index == probs.length-1) {
                //_pairs.put(_curNode.get_name(),values[index]);
                _pairs[curIndex][1] = values[index];
                break;
            }
        }
    }

    /* 
     
    String[][] mapToArray(Map<String,String> map, InferenceGraphNode node){
        String array[][] = new String[map.size()][2];
        String variable = node.get_name();
        Iterator it = map.entrySet().iterator();
        // Put the value of node in the first position;
        int index = 0;
        array[index][0] = node.get_name();
        array[index][1] = map.get(array[index][0]);
        index++;
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            if(!variable.equals((String)entry.getKey())) {
                //System.out.println((String)entry.getKey() + ": " + (String)entry.getValue());
                array[index][0] = (String)entry.getKey();
                array[index][1] = (String)entry.getValue();
                index++;
            }
        }
        return array;
    }

     
     
    void displayVariables(InferenceGraph graph){
        Vector<InferenceGraphNode> nodes = graph.get_nodes();
        Iterator<InferenceGraphNode> it = nodes.iterator();
        while (it.hasNext()) {
            // get name
            InferenceGraphNode no = it.next();
            System.out.print(no.get_name() + ": ");
            // get values
            String vals[] = no.get_values();
            for (String val : vals) {
                System.out.print(" " + val);
            }
            System.out.println("");
        }
    }

    void displayVariables(String values[][]){
        for(String[] foo : values) {
            System.out.println(foo[0] + ": " + foo[1]);
        }
    }

    void displayVariables(Map<String, String> map){
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            System.out.println((String)entry.getKey() + ": " + (String)entry.getValue());
        }
    } 
     
    void displayNodeProbs(InferenceGraphNode node, Map<String,String> pairs){
        node.get_Prob().print();

        //show the values of the parents
        Vector<InferenceGraphNode> parents = node.get_parents();
        for (int index = 0; index < parents.size(); index++) {
            InferenceGraphNode parent = parents.elementAt(index);
            System.out.println(parent.get_name() + " :" + pairs.get(parent.get_name()));
        }

        double probs[] = getNodeProbabilities(node,pairs);
        String values[] = node.get_values();
        for(int index = 0; index < probs.length; index++ ) {
            if(index == 0) {
                System.out.print(node.get_name() + " " + values[index] + ": " + probs[index]);
            }
            else{
                System.out.print(", " + values[index] + ": " + probs[index]);
            }
        }
        System.out.println("");
    } 
     
    double[] getNodeProbabilities(InferenceGraphNode node, Map<String,String> values){
        String vals[] = node.get_values();
        double probs[] = new double[vals.length];
        String pairs[][] = mapToArray(values, node);

        for (int index = 0; index < vals.length; index++) {
            pairs[0][1] = vals[index];
            probs[index] = node.get_function_value(pairs);
        }

        return probs;
    } 
     
    Map<String,InferenceGraphNode> getNodeMap(InferenceGraph graph){
        HashMap<String, InferenceGraphNode> map = new HashMap<String, InferenceGraphNode>();
        Vector<InferenceGraphNode> nodes = graph.get_nodes();
        Iterator<InferenceGraphNode> it = nodes.iterator();
        while (it.hasNext()) {
            InferenceGraphNode node = it.next();
            map.put(node.get_name(), node);
        }
        return map;
    } 
     
    */


}
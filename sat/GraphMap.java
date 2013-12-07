import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.ProcessBuilder;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Math;

public class GraphMap{

    String _graphFile;
    String _satInfile;
    String _satOutFile;
    String _mapFile;

    int _gPhoneSize; // size of square array
    int _gPhone[][]; // array for graph
    int _phoneNodeLookup[]; // maps from index to node number
    HashMap<Integer, Integer> _phoneMap; // maps from node number to index

    int _gMailSize;  // size of square array (
    int _gMail[][]; // array for graph
    int _mailNodeLookup[]; // maps from index to node number
    HashMap<Integer, Integer> _mailMap; // maps from node number to index

    int _gMap[][];

    public GraphMap(String graphfile, String satinfile, String satoutfile, String mapfile)
    {
        _graphFile = graphfile;
        _satInfile = satinfile;
        _satOutFile = satoutfile;
        _mapFile = mapfile;
    }

    void solve() throws IOException, InterruptedException{
        readGraphFile();
        //dumpEdgeArray(_gMail,_gMailSize,_mailNodeLookup);
        //System.out.println("");
        //dumpEdgeArray(_gPhone, _gPhoneSize, _phoneNodeLookup);

        BufferedWriter outBuf = new BufferedWriter(new FileWriter(_satInfile));

        outputHeader(outBuf, "Hurrah!");
        //outputArray(outBuf,_gPhone, _gPhoneSize, 1, "Phone");
        //outputArray(outBuf,_gMail, _gMailSize, _gPhoneSize * _gPhoneSize + 1, "Mail");

        outputConstraints(outBuf);
        outBuf.close();

        executeCommand();

        readSatFile();
    }

    void satoutput() throws IOException
    {
        readGraphFile();
        //dumpEdgeArray(_gMail,_gMailSize,_mailNodeLookup);
        //System.out.println("");
        //dumpEdgeArray(_gPhone, _gPhoneSize, _phoneNodeLookup);

        BufferedWriter outBuf = new BufferedWriter(new FileWriter(_satInfile));

        outputHeader(outBuf, "Hurrah!");
        //outputArray(outBuf,_gPhone, _gPhoneSize, 1, "Phone");
        //outputArray(outBuf,_gMail, _gMailSize, _gPhoneSize * _gPhoneSize + 1, "Mail");

        outputConstraints(outBuf);
        outBuf.close();

    }
    
    void satinput() throws IOException
    {
        readGraphFile();
        //dumpEdgeArray(_gMail,_gMailSize,_mailNodeLookup);
        //System.out.println("");
        //dumpEdgeArray(_gPhone, _gPhoneSize, _phoneNodeLookup);
        readSatFile();

    }

    void readGraphFile() throws IOException{
        FileReader input = new FileReader(_graphFile);
        BufferedReader bufRead = new BufferedReader(input);
        String line;    // String that holds current file line

        LinkedList<Integer> gPhone = new LinkedList<Integer>();
        LinkedList<Integer> gMail = new LinkedList<Integer>();
        HashSet<Integer> gPhoneSet = new HashSet<Integer>();
        HashSet<Integer> gMailSet = new HashSet<Integer>();

        line = bufRead.readLine();
        while (line != null){	
            String[] edge=line.split(" ");
            Integer nextValue1;
            Integer nextValue2;

           nextValue1 = Integer.parseInt(edge[0]); 
           nextValue2 = Integer.parseInt(edge[1]); 
           if (0 == nextValue1) {
               break;
           }
           gPhone.addLast(nextValue1);
           gPhone.addLast(nextValue2);
           gPhoneSet.add(nextValue1);
           gPhoneSet.add(nextValue2);
           line = bufRead.readLine();
        }

        line = bufRead.readLine();
        while (line != null){	
            String[] edge=line.split(" ");
            Integer nextValue1 = Integer.parseInt(edge[0]);
            Integer nextValue2 = Integer.parseInt(edge[1]);
            gMail.addLast(nextValue1);
            gMail.addLast(nextValue2);
            gMailSet.add(nextValue1);
            gMailSet.add(nextValue2);
            line = bufRead.readLine();
        }

        // now we've read in the values we need setup our data structures
        _gPhoneSize = gPhoneSet.size();
        _phoneNodeLookup = toInt(gPhoneSet);
        _gPhone = new int[_gPhoneSize][_gPhoneSize];
        _phoneMap = buildMap(_phoneNodeLookup, _gPhoneSize);
        populateArray(_gPhone, _phoneMap, gPhone);



        _gMailSize = gMailSet.size();
        _mailNodeLookup = toInt(gMailSet);
        _gMail = new int[_gMailSize][_gMailSize];
        _mailMap = buildMap(_mailNodeLookup,_gMailSize);
        populateArray(_gMail, _mailMap, gMail);

        _gMap = new int[_gMailSize][_gPhoneSize];
    }

    /*
    void outputArray(BufferedWriter buf, int a[][], int max, int offset, String comment) throws IOException {
        //buf.write("c ");
        //buf.write(comment);
        //buf.newLine();
        for(int rows = 0; rows < max; rows++) {
            for(int cols = 0; cols < max; cols++) {
                int variable = rows * max + cols + offset;

                if(a[rows][cols] == 0) {
                    buf.write("-");
                }
                buf.write(variable + " 0");
                buf.newLine();
            }
        }
    }
    */

    int[] toInt(Set<Integer> set) {
      int[] a = new int[set.size()];
      int i = 0;
      for (Integer val : set) 
          a[i++] = val;
      return a;
    }

    HashMap<Integer,Integer> buildMap(int[] a, int size){
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int count = 0; count < size; count++) {
            map.put(a[count], count);
        }

        return map;
    }

    void populateArray(int a[][], HashMap<Integer, Integer> map, List<Integer> data){
        Iterator<Integer> itData = data.iterator();
        while(itData.hasNext()) {
            Integer first = itData.next();
            Integer second = itData.next();
            a[map.get(first)][map.get(second)] = 1;
        }
    }

    void dumpEdgeArray(int a[][], int size, int map[])
    {
        for(int row = 0; row < size; row++) {
            for(int col = 0; col < size; col++) {
                if(a[row][col] == 1) {
                    System.out.println(map[row] + " " + map[col]);
                }
            }
        }
    }

    void outputHeader(BufferedWriter buf, String comment) throws IOException {
        // calcuate the variable count
        //int variableCount = _gPhoneSize * _gPhoneSize + _gMailSize * _gMailSize + _gMailSize * _gPhoneSize;
        int variableCount = _gMailSize * _gPhoneSize;

        // calculate clause count

        // one clause fore each cell in the phone graph
        //int clauseCount = _gPhoneSize * _gPhoneSize;
        
        // One clause for each cell in the mail graph
        //clauseCount += _gMailSize * _gMailSize;

        // One clause for each pair of cells in each row of the mapping
        int clauseCount = _gMailSize * ((_gPhoneSize * (_gPhoneSize - 1)) / 2);

        // one clause for each row of the mapping
        clauseCount += _gMailSize;

        // one clause fore each pair of cells in each column of the mapping
        clauseCount += _gPhoneSize * ((_gMailSize * (_gMailSize-1))/2);

        // and toos in the map constraint
        clauseCount += countMapConstraint();


        //buf.write("c ");
        //buf.write(comment);
        //buf.newLine();
        buf.write("p cnf");
        buf.write(" " + variableCount);
        buf.write(" " + clauseCount);
        buf.newLine();
    }

    int mapVariable(int row, int col) {
        //int variable = _gMailSize * _gMailSize + _gPhoneSize * _gPhoneSize;
        // variable += row * _gPhoneSize + col + 1;
        return row * _gPhoneSize + col + 1;
    }

    int mapRow(int variable) {
        int row = (variable-1) / _gPhoneSize;
        return row;
    }

    int mapCol(int variable){
        int col = (variable-1) % _gPhoneSize;
        return col;  
    }

    void outputRowConstraint(int row, BufferedWriter buf, String comment) throws IOException {
        //buf.write("c " + comment);
        //buf.newLine();
        for (int col1 = 0; col1 < _gPhoneSize-1; col1++) {
            for(int col2 = col1 + 1; col2 < _gPhoneSize; col2++) {
                buf.write("-" + mapVariable(row, col1) + " -" + mapVariable(row, col2) + " 0");
                buf.newLine();
            }
        }
        for(int col = 0; col < _gPhoneSize; col ++) {
            buf.write (mapVariable(row,col) + " ");
        }
        buf.write("0");
        buf.newLine();
    }

    void outputConstraints(BufferedWriter buf) throws IOException {
        for(int row = 0; row < _gMailSize; row++) {
            outputRowConstraint(row, buf, "Row: " + row);
        }

        for(int col = 0; col < _gPhoneSize; col++) {
            outputColumnConstraint(col, buf, "Col: "+ col);
        }

        outputMapConstraint(buf);
    }

    void outputColumnConstraint(int col, BufferedWriter buf, String comment) throws IOException {
        //buf.write("c " + comment);
        //buf.newLine();
        for (int row1 = 0; row1 < _gMailSize-1; row1++) {
            for(int row2 = row1 + 1; row2 < _gMailSize; row2++) {
                buf.write("-" + mapVariable(row1, col) + " -" + mapVariable(row2, col) + " 0");
                buf.newLine();
            }
        }
    }

    /*
    int phoneVariable(int row, int col){
        return row * _gPhoneSize + col + 1;
    }

    int mailVariable(int row, int col){
        return row * _gMailSize + col + _gPhoneSize * _gPhoneSize + 1;
    }
    */

    int countMapConstraint()
    {
        int count = 0;
        // calcuate the number of constraints in the Map Constraint.  
        // I'm sure there is a forumula for this, but I don't care enough to figure it out!
        for (int mail1=0; mail1 < _gMailSize; mail1++) {
            for(int phone1=0; phone1 < _gPhoneSize; phone1++) {
                for(int mail2 = mail1+1; mail2 < _gMailSize; mail2++) {
                    for (int phone2=0; phone2 < _gPhoneSize; phone2++) {
                        if(phone2 != phone1) {
                            if(_gMail[mail1][mail2] == 1 && _gPhone[phone1][phone2] == 0) {
                                count++;
                            }

                            if(_gMail[mail1][mail2] == 0 && _gPhone[phone1][phone2] == 1) {
                                count++;
                            }

                            if(_gMail[mail2][mail1] == 1 && _gPhone[phone2][phone1] == 0) {
                                count++;
                            }

                            if(_gMail[mail2][mail1] == 0 && _gPhone[phone2][phone1] == 1) {
                                count++;
                            }

                        }
                    }
                }
            }
        }
        return count;
    }

    void outputMapConstraint(BufferedWriter buf) throws IOException {
        for (int mail1=0; mail1 < _gMailSize; mail1++) {
            for(int phone1=0; phone1 < _gPhoneSize; phone1++) {
                for(int mail2 = mail1+1; mail2 < _gMailSize; mail2++) {
                    for (int phone2=0; phone2 < _gPhoneSize; phone2++) {
                        if(phone2 != phone1) {

                            // one direction

                            if(_gMail[mail1][mail2] == 1 && _gPhone[phone1][phone2] == 0) {
                                buf.write(
                                    "-"  + mapVariable(mail1, phone1) + 
                                    " -" + mapVariable(mail2, phone2) + 
                                    " 0");
                                buf.newLine();
                            }
                            
                            if(_gMail[mail1][mail2] == 0 && _gPhone[phone1][phone2] == 1) {
                                buf.write(
                                    "-"  + mapVariable(mail1, phone1) + 
                                    " -" + mapVariable(mail2, phone2) + 
                                    " 0");
                                buf.newLine();
                            }

                            // the other direction
                            if(_gMail[mail2][mail1] == 1 && _gPhone[phone2][phone1] == 0) {
                                buf.write(
                                    "-"  + mapVariable(mail1, phone1) + 
                                    " -" + mapVariable(mail2, phone2) + 
                                    " 0");
                                buf.newLine();
                            }
                            
                            if(_gMail[mail2][mail1] == 0 && _gPhone[phone2][phone1] == 1) {
                                buf.write(
                                    "-"  + mapVariable(mail1, phone1) + 
                                    " -" + mapVariable(mail2, phone2) + 
                                    " 0");
                                buf.newLine();
                            }
                            
                        }
                    }
                }
            }
        }
    }

    void executeCommand() throws IOException, InterruptedException{
        String[] cmd = {"./minisat", _satInfile, _satOutFile};
        Process p = new ProcessBuilder(cmd).redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT).start();
        p.waitFor();

    }

    void readSatFile() throws IOException{
        FileReader input = new FileReader(_satOutFile);
        BufferedReader buf = new BufferedReader(input);
        String line;

        // Read SAT
        line = buf.readLine();

        BufferedWriter outBuf = new BufferedWriter(new FileWriter(_mapFile));
        if(line.equals("UNSAT")) {

            //System.out.println("0");
            outBuf.write("0");
        }
        else{
            

            line = buf.readLine();
            String[] variables = line.split(" ");
            for(int index = 0; index < variables.length; index++) {
                int value = Integer.parseInt(variables[index]);
                if (value > 0) {
                    // We've got a true!  This is a map!
                    int row = mapRow(value);
                    int col = mapCol(value);
                    
                    //outBuf.write(_phoneNodeLookup[col] + " " + _mailNodeLookup[row]);
                    outBuf.write(_mailNodeLookup[row] + " " + _phoneNodeLookup[col]);
                    outBuf.newLine();
                }
            }
        }

        outBuf.close();
        

    }

}
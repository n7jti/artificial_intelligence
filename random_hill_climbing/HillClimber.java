import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
import java.util.Comparator;


class RegionAvgCmp  implements Comparator {
    public RegionAvgCmp(HashMap<Integer, BidData> bids)
    {
        _bids = bids;
    }

    HashMap<Integer, BidData> _bids;

    public int compare(Object o1, Object o2){
        Integer bid1 = (Integer)o1;
        Integer bid2 = (Integer)o2;
        if (_bids.get(bid1).avgPerRegion < _bids.get(bid2).avgPerRegion) {
            return -1;
        }
        else if (_bids.get(bid1).avgPerRegion > _bids.get(bid2).avgPerRegion) {
            return 1;
        }
        
        return 0;
    }
}

class ValueCmp  implements Comparator {
    public ValueCmp(HashMap<Integer, BidData> bids)
    {
        _bids = bids;
    }

    HashMap<Integer, BidData> _bids;

    public int compare(Object o1, Object o2){
        Integer bid1 = (Integer)o1;
        Integer bid2 = (Integer)o2;
        if (_bids.get(bid1).value < _bids.get(bid2).value) {
            return -1;
        }
        else if (_bids.get(bid1).value > _bids.get(bid2).value) {
            return 1;
        }
        
        return 0;
    }
}


public class HillClimber
{
    public HillClimber(String inputFile, String outputFile)
    {
        _inputFile = inputFile;
        _outputFile = outputFile;
        _startTime = System.currentTimeMillis();
    }

    String _inputFile;
    String _outputFile;
    HashMap<Integer, BidData> _bids;
    CantidateSolution _bestSolution;
    double _minutes;
    long _regionCount;
    long _bidCount;
    long _companyCount;
    long _startTime;
    long _stopTime;


    public void Solve(){
        try{	
            ReadBids();
            long iterations = 0;

            CantidateSolution solution;
            _bestSolution = Iteration(iterations);
            //System.out.println("("+ iterations + ")" + "Value: " + _bestSolution.value.toString());

            Double firstValue = _bestSolution.value;

            // OutputResults(_bestSolution);

            long minimum = 0;
            long maximum = 0;
            long total = 0;


            while(System.currentTimeMillis() < (_stopTime - maximum)){
                iterations++;
                long start = System.currentTimeMillis();
                solution = Iteration(iterations);
                long stop = System.currentTimeMillis();
                long current = stop - start;
                total += current;
                if (minimum == 0) {
                    minimum = current;
                }
                if (current < minimum) {
                    minimum = current;
                }

                if (current > maximum) {
                    maximum = current;
                }

                //System.out.println((current / 1000.0) + " Seconds");

                if (solution.value > _bestSolution.value) {
                    _bestSolution = solution;
                    //System.out.println("("+ iterations + ")" + "Value: " +solution.value.toString());
                }
            }
            
            OutputResults(_bestSolution);
            /*
            OutputResults(_bestSolution);
            System.out.println("Iterations: " + iterations);
            System.out.println("Delta-V: " + (_bestSolution.value - firstValue));
            System.out.println("Total Seconds: " + (total/1000.0));
            System.out.println("Minimum Iteration: " + minimum / 1000.0);
            System.out.println("Maximum Iteration: " + maximum / 1000.0);
            System.out.println("Average Iteration: " + (total/1000.0/iterations));\ 
            */
            
            System.out.print(_inputFile + "," + _bestSolution.value  + "," + iterations + "," + total + "," + minimum + "," + maximum + "\n");

        }catch (ArrayIndexOutOfBoundsException e){		             
            System.out.println("Usage: java ReadFile filename\n");          
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public CantidateSolution Iteration(long iteration){
        CantidateSolution solution;
        //solution = GenerateGoodStart(iteration);
        solution = GenerateRandomStart();

        Double value;
        do{
            value = solution.value;
            solution = neighborhood(solution);

            // We didn't get a better solution above, try "AddOne"
            if(value == solution.value) {
                solution = AddOne(solution,0.0);
            }

            if(value == solution.value) {
                solution = OneForTwo(solution);
            }
        }while (value < solution.value);

        return solution;
    }

    void ReadBids() throws IOException {
        /*  File Reading Stuff */
        FileReader input = new FileReader(_inputFile);
        //FileWriter output = new FileWriter(args[1]);
        /* Filter FileReader through a Buffered read to read a line at a time */
        BufferedReader bufRead = new BufferedReader(input);
        //BufferedWriter outp = new BufferedWriter(output);

        String line;    // String that holds current file line

        /*Data Structures to Store Bids*/
        HashMap<Integer,BidData> Bids=new HashMap<Integer,BidData>();

        // Read first line
        line = bufRead.readLine();
        _minutes = Double.parseDouble(line);
        _stopTime = _startTime + (long)(_minutes * 60 * 1000);

        line = bufRead.readLine();
        line = bufRead.readLine();
        _regionCount = Integer.parseInt(line);
       
        line = bufRead.readLine();
        line = bufRead.readLine();
        _bidCount = Integer.parseInt(line);

        line = bufRead.readLine();
        line = bufRead.readLine();
        _companyCount = Integer.parseInt(line);
        
        line = bufRead.readLine();
        // Reading lines ending with a # (bids), and store in Bids
        Integer count = 0;
        while (line != null){			  
              if(line.endsWith("#")){
                      String[] res=line.split(" ");
                      BidData curbid= new BidData();
                      curbid.company=Integer.parseInt(res[0]);
                      curbid.value=Double.parseDouble(res[1]);
                      curbid.region=new HashSet<Integer>();
                      for(int k=2;k<res.length-1;k++){
                                      curbid.region.add(Integer.parseInt(res[k]));					  
                      }		

                      //store the bid
                      curbid.regionCount = curbid.region.size();
                      curbid.avgPerRegion = curbid.value / curbid.regionCount;
                      Bids.put(count,curbid);
                      count++;
              }
          line = bufRead.readLine();		      
        }
        bufRead.close();

        _bids = Bids;
    }

    void OutputResults(CantidateSolution sol){
        try{
            sol.sort();
            BufferedWriter outFile = new BufferedWriter(new FileWriter(_outputFile));
            //outFile.write("Value : "+sol.value.toString()+"\n");
            Iterator<Integer> it=sol.bids.iterator();
            //outFile.write("Solution : ");
            while(it.hasNext()){
                  Integer i=it.next();
                  outFile.write(i.toString()+" ");			  
            }
            outFile.write("#\n");
            outFile.close();
        }catch (IOException e){
            System.out.println("Value : "+sol.value.toString());
            Iterator<Integer> it=sol.bids.iterator();
            System.out.print("Solution : ");
            while(it.hasNext()){
                  Integer i=it.next();
                  System.out.print(i.toString()+" ");			  
            }
            System.out.print("#\n");
        }
    }

    CantidateSolution GenerateRandomStart(){
        // Hash set containing an entry for each bid
        HashSet<Integer> Avail=new HashSet<Integer>();	      
        Integer count = _bids.size();
        for(Integer index = 0; index < count; index++) {
            Avail.add(index);
        }


        //Generate Random Solution
        Random gen=new Random();

        // list of selected bids
        LinkedList<Integer> bidlist=new LinkedList<Integer>();

        // The total value of selected bids
        Double value=0.0;

        // Here's the algorithym to select a random solution
        // 1. Choose a bid at random from the avaialble set.
        // 2. Put the chosen bid in the Bids list. 
        // 3. Go through all the bids remaining in the available set and remove any colliding bids.
        // 4. Repeat from step 1 until no bids remain.
        // The bid list now contains a valid solution chosen at random. 

        // Iterate as long as anything is left in the "available set"
        while(Avail.size()>0){

              // generate a random integer between 0 and the size of the Available set.
              Integer newbid=gen.nextInt(Avail.size());

              // Randomly choose an integer out of the Avail Set
              newbid=(Integer)Avail.toArray()[newbid];

              // Add the value of the chosen bid
              value+=_bids.get(newbid).value;	

              // Add the chosen bid to the bidList (a list of integers)				  
              bidlist.add(newbid);

              RemoveColidingBids(_bids.get(newbid),Avail);
        }

        CantidateSolution sol = new CantidateSolution();
        //Collections.sort(bidlist);
        sol.value = value;
        sol.bids = bidlist;
        return sol;
    }


    CantidateSolution GenerateGoodStart(long offset){
        // Hash set containing an entry for each bid
        HashSet<Integer> Avail=new HashSet<Integer>();	      
        Integer count = _bids.size();

        LinkedList<Integer> SortedBids = new LinkedList<Integer>();
        for(Integer index = 0; index < count; index++) {
            Avail.add(index);
            SortedBids.add(index);
        }

        if (offset < SortedBids.size()) {
            for(int remove = 0; remove < offset; remove++) {
                SortedBids.removeFirst();
            }
        }

        //Sort the bids
        Collections.sort(SortedBids, new RegionAvgCmp(_bids));

        // list of selected bids
        LinkedList<Integer> bidlist=new LinkedList<Integer>();

        // The total value of selected bids
        Double value=0.0;

        Random gen=new Random();
        while(Avail.size()>0){
            Integer newbid;
            if(SortedBids.size() > 0) {
                newbid = SortedBids.removeFirst();  
            }
            else
            {
                // generate a random integer between 0 and the size of the Available set.
                newbid=gen.nextInt(Avail.size());

                // Randomly choose an integer out of the Avail Set
                newbid=(Integer)Avail.toArray()[newbid];
            }

            // Add the value of the chosen bid
            value+=_bids.get(newbid).value;	

            // Add the chosen bid to the bidList (a list of integers)				  
            bidlist.add(newbid);

            RemoveColidingBids(_bids.get(newbid),Avail);

            // clean out our SortedBidsList
            Iterator<Integer> li = SortedBids.iterator();
            while (li.hasNext()) {
                Integer bidItem = li.next();
                if(!Avail.contains(bidItem)) {
                  li.remove();
                }
            }
        }

        CantidateSolution sol = new CantidateSolution();
        //Collections.sort(bidlist);
        sol.value = value;
        sol.bids = bidlist;
        return sol;
    }

    void RemoveColidingBids(BidData Bid, HashSet<Integer> AvailSet){
        HashSet<Integer> RemoveSet=new HashSet<Integer>(); // HashSet to hold the items to remove from the AvailSet

        // Get an iterator to iterate over all the integers in the availble HashSet
        Iterator<Integer> it=AvailSet.iterator();

        // While there are any left
        while(it.hasNext()){

              // Get the next integer
              Integer k=it.next();

              // If there is something in the intersection, or the company matches
              if(Bid.company.equals(_bids.get(k).company) || (!Collections.disjoint(Bid.region, _bids.get(k).region))){

                      // add K to the "Remove" set
                      RemoveSet.add(k);
              }
        }
        //Remove every bid from the remove set from the Avail list. 
        AvailSet.removeAll(RemoveSet);
    }

    void RemoveColidingBids(HashSet<Integer> regions, HashSet<Integer> companies, Double minValue, HashSet<Integer> AvailSet){
        HashSet<Integer> RemoveSet=new HashSet<Integer>(); // HashSet to hold the items to remove from the AvailSet

        // Get an iterator to iterate over all the integers in the availble HashSet
        Iterator<Integer> it=AvailSet.iterator();

        // While there are any left
        while(it.hasNext()){

              // Get the next integer
              Integer k=it.next();

              if ((_bids.get(k).value <= minValue) ||                    // if the value is less than the target
                  (companies.contains(_bids.get(k).company)) ||         // if the company is already represented
                  !Collections.disjoint(_bids.get(k).region,regions)){  // if it contains a region already bid on
                  // add K to the "Remove" set
                  RemoveSet.add(k);
              }
        }
        //Remove every bid from the remove set from the Avail list. 
        AvailSet.removeAll(RemoveSet);
    }

    CantidateSolution neighborhood(CantidateSolution sol){
        CantidateSolution nextSol = (CantidateSolution)sol.clone();

        // Now, walk through each bid in the cantidate solution
        // remove the bid from the solution
        // find the set of available bids to replace the current bid
        // If the resulting value is higher, then choose that as the next cantidate solution and 
        // return

        Double value = nextSol.value;
        LinkedList<Integer> bidList = nextSol.bids;
        Integer stopBid = bidList.getFirst();

        do{
            // Hash set containing an entry for each bid
            HashSet<Integer> Avail=new HashSet<Integer>();
            Integer count = _bids.size();
            for(Integer index = 0; index < count; index++) {
                Avail.add(index);
            }	 

            // pull the first bid off the front
            Integer bid = bidList.removeFirst();
            
            // Remove that bid from the Available List
            Avail.remove(bid);
            value -= _bids.get(bid).value;
            bidList.remove(bid);
            Integer bestBid = bid;
            Double bestValue = _bids.get(bestBid).value;
            
            
            // Now walk the list and remove any colisions from the Avail list
            HashSet<Integer> regions = new HashSet<Integer>();
            HashSet<Integer> companies = new HashSet<Integer>();
            Iterator<Integer> it = bidList.iterator();
            while(it.hasNext()) {
                Integer i = it.next();
                regions.addAll(_bids.get(i).region);
                companies.add(_bids.get(i).company);
            }
            RemoveColidingBids(regions, companies, bestValue, Avail);

            if (Avail.size() > 0) {
                // We've got other bids that can replace the one removed
                // Look for one with a highestvalue
                Iterator<Integer> it2 = Avail.iterator();
                while(it2.hasNext()) {
                    Integer newBid = it2.next();
                    Double curValue = _bids.get(newBid).value;
                    if(curValue > bestValue) {
                        bestValue = curValue;
                        bestBid = newBid;
                    }
                }

                //If the highest-value bid is bigger than where we started then that is uphill
                if (bestValue > _bids.get(bid).value) {
                    bidList.addLast(bestBid);
                    value += bestValue;
                    nextSol.value = value;
                    // We're done!  We found some uphill
                    return nextSol;
                }                
            }

            bidList.addLast(bid);
            value += _bids.get(bid).value;

        } while(bidList.getFirst() != stopBid);

        // We didn't find any up-hill. Return the old best 
        return sol;
    }

    CantidateSolution AddOne(CantidateSolution sol, Double min){
        CantidateSolution nextSol = (CantidateSolution)sol.clone();
        LinkedList<Integer> bidList = nextSol.bids;

        // Get the set of all available bids
        HashSet<Integer> Avail=new HashSet<Integer>();
        Integer count = _bids.size();
        for(Integer index = 0; index < count; index++) {
            Avail.add(index);
        }	

        // Create the list of companies and regions
        HashSet<Integer> regions = new HashSet<Integer>();
        HashSet<Integer> companies = new HashSet<Integer>();
        Iterator<Integer> it = bidList.iterator();
        while(it.hasNext()) {
            Integer i = it.next();
            regions.addAll(_bids.get(i).region);
            companies.add(_bids.get(i).company);
        }

        RemoveColidingBids(regions, companies, min, Avail);

        Integer bestBid = -1;
        Double bestValue = 0.0;
        if (Avail.size() > 0) {
            Iterator<Integer> it2 = Avail.iterator();
            while(it2.hasNext()) {
                Integer newBid = it2.next();
                if (_bids.get(newBid).value > bestValue) {
                    bestValue = _bids.get(newBid).value;
                    bestBid = newBid;
                }
            }
            //Now we've got the largest extra bid
            bidList.addLast(bestBid);
            nextSol.value += bestValue;
            return nextSol;
        }
        // There were no additional coliding bids
        return sol;
    }


    //Remove one bid and add two more with a larger total
    CantidateSolution OneForTwo(CantidateSolution sol){
        CantidateSolution nextSol = (CantidateSolution)sol.clone();
        LinkedList<Integer> bidList = nextSol.bids;
        Double value = nextSol.value;

        Integer stopBid = bidList.getFirst();
        do{
            // Hash set containing an entry for each bid
            HashSet<Integer> Avail=new HashSet<Integer>();
            Integer count = _bids.size();
            for(Integer index = 0; index < count; index++) {
                Avail.add(index);
            }	 

            // pull the first bid off the front
            Integer bid = bidList.removeFirst();

            // Remove that bid from the Available List
            Avail.remove(bid);
            bidList.remove(bid);
            Double removedBidValue = _bids.get(bid).value;
            value -= removedBidValue;

            // Now walk the list and remove any colisions from the Avail list
            HashSet<Integer> regions = new HashSet<Integer>();
            HashSet<Integer> companies = new HashSet<Integer>();
            Iterator<Integer> it = bidList.iterator();
            while(it.hasNext()) {
                Integer i = it.next();
                regions.addAll(_bids.get(i).region);
                companies.add(_bids.get(i).company);
            }

            // Remove coliding bids, but leave all bids irrespecive of value
            RemoveColidingBids(regions, companies, 0.0, Avail);

            if (Avail.size() > 0) {
                //This is the list of possible 1st bids
                //For each of these recalculate the colision list and see if the combination
                //of the two is bigger than the one we removed. 
                Iterator<Integer> it2 = Avail.iterator();
                while(it2.hasNext()) {
                    Integer oneBid = it2.next();
                    Double oneValue = _bids.get(oneBid).value;
                    HashSet<Integer> innerAvail = (HashSet<Integer>)Avail.clone(); 
                    RemoveColidingBids(_bids.get(oneBid),innerAvail);
                    if(innerAvail.size() > 0) {
                        // Now we look for combinations that total more than the bid we removed
                        Iterator<Integer>it3 = innerAvail.iterator();
                        while(it3.hasNext()){
                            Integer twoBid = it3.next();
                            Double twoValue = _bids.get(twoBid).value;
                            if( (oneValue + twoValue) > removedBidValue  ) {
                                //Found it
                                bidList.addLast(oneBid);
                                bidList.addLast(twoBid);
                                value += oneValue;
                                value += twoValue;
                                return nextSol;
                            }
                        }

                    }
                    
                }
            }

            bidList.addLast(bid);
            value += removedBidValue;
            
        }while (bidList.getFirst() != stopBid);
        // There were no additional coliding bids
        return sol;
    }

}

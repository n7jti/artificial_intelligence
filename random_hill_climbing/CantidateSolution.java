import java.util.LinkedList;
import java.util.Collections;

public class CantidateSolution{
    public LinkedList<Integer> bids;
    public Double value;

    public Object clone(){
        CantidateSolution cs = new CantidateSolution();
        cs.bids = (LinkedList<Integer>)bids.clone();
        cs.value = value;
        return cs;
    }

    public void sort(){
        Collections.sort(bids);
    }

}

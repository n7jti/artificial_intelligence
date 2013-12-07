import java.util.HashSet;

public class BidData{
    public HashSet<Integer> region;
    public Integer company;
    public Double value;
    public Double avgPerRegion;
    public Integer regionCount;
    	
    public String toString(){
            String s=new String();
            s="Company:"+company.toString();
            s=s+" Bid Amount:"+value.toString();
            s=s+" Regions:"+region.toString();
            return s;
    }
}

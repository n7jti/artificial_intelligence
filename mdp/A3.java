import java.io.IOException;

public class A3 {
    public static void main(String[] args) {
        double p;
        if (args.length > 0) {
            p = Double.parseDouble(args[0]);
        }
        else
        {
            p = 4.0/13.0;
        }
        

        Blackjack bj = new Blackjack( p );

        try{
            bj.solve();
        }
        catch (IOException e){
            e.printStackTrace();  
        }
        
    }
}


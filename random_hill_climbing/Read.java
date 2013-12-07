import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Iterator;
import java.util.Random;

public class Read {
       /**
       * @param args
       */
        public static void main(String[] args) {
            try{
                HillClimber hc = new HillClimber(args[0],args[1]);
                hc.Solve();   
            }catch (ArrayIndexOutOfBoundsException e){		             
                // print usage
                usage();
            }
	}

        public static void usage()
        {
            System.out.println("Usage: java ReadFile inputfile outputfile\n");	
        }
}





import java.io.*;

public class A2 {
       /**
       * @param args
       */
        public static void main(String[] args) {
            try{
                GraphMap gm = new GraphMap(args[0], args[1], args[2], args[3]);
                if (Integer.parseInt(args[4]) == 1) {
                    
                    //gm.solve();
                    gm.satoutput();
                }
                else
                {
                    gm.satinput();
                }
            }catch (ArrayIndexOutOfBoundsException e){		             
                // print usage
                usage();
            }catch (IOException e){
                e.printStackTrace();  
            }
	}

        public static void usage()
        {
            System.out.println("Usage: java A2 inputfile outputfile\n");	
        }
}





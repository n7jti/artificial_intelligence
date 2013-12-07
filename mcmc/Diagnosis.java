import java.text.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;


public class Diagnosis{
    //Diagnoses

    //Hypovolemia: True False
    int _hyp[];

    //LVFailure: True False
    int _lvf[];

    //Anaphylaxis: True False
    int _ana[];

    //InsuffAnesth: True False 
    int _ins[];

    //PulmEmbolus: True False
    int _pul[];

    //Intubation: Normal Esophageal OneSided
    int _int[];

    //KinkedTube: True False
    int _kin[];

    //Disconnect: True False
    int _dis[];

    double _total;

    public Diagnosis(){
        _hyp = new int[2];
        _lvf = new int[2];
        _ana = new int[2];
        _ins = new int[2];
        _pul = new int[2];
        _int = new int[3];
        _kin = new int[2];
        _dis = new int[2];
    }

    public void clear()
    {
        _total = 0.0;
        java.util.Arrays.fill(_hyp,0);
        java.util.Arrays.fill(_lvf,0);
        java.util.Arrays.fill(_ana,0);
        java.util.Arrays.fill(_ins,0);
        java.util.Arrays.fill(_pul,0);
        java.util.Arrays.fill(_int,0);
        java.util.Arrays.fill(_kin,0);
        java.util.Arrays.fill(_dis,0);
    }

    public void count(String pairs[][], Map<String, Integer> indexes){
        String key;
        String value;
        _total += 1.0;


        value = pairs[ indexes.get("Hypovolemia") ][1];
        if (value.equals("True")) {
            _hyp[0]++;
        }
        else{
            _hyp[1]++;
        }

        value = pairs[ indexes.get("LVFailure") ][1];
        if (value.equals("True")) {
            _lvf[0]++;
        }
        else{
            _lvf[1]++;
        }

        value = pairs[ indexes.get("Anaphylaxis") ][1];
        if (value.equals("True")) {
            _ana[0]++;
        }
        else{
            _ana[1]++;
        }

        value = pairs[ indexes.get("InsuffAnesth") ][1];
        if (value.equals("True")) {
            _ins[0]++;
        }
        else{
            _ins[1]++;
        }

        value = pairs[ indexes.get("PulmEmbolus") ][1];
        if (value.equals("True")) {
            _pul[0]++;
        }
        else{
            _pul[1]++;
        }

        value = pairs[ indexes.get("Intubation") ][1];
        if(value.equals("Normal")) {
            _int[0]++;
        }
        else if (value.equals("Esophageal")) {
            _int[1]++;
        }
        else{
            _int[2]++;
        }

        value = pairs[ indexes.get("KinkedTube") ][1];
        if (value.equals("True")) {
            _kin[0]++;
        }
        else{
            _kin[1]++;
        }

        value = pairs[ indexes.get("Disconnect") ][1];
        if (value.equals("True")) {
            _dis[0]++;
        }
        else{
            _dis[1]++;
        }
  
    }

    public void displayOutput()
    {
        DecimalFormat formatter = new DecimalFormat("#.#####");
        //"Hypovolemia" = { 0.44311 0.55689 }

        //Hypovolemia: True False
        System.out.println("\"Hypovolemia\" = {" + 
                           formatter.format( _hyp[0] / _total ) + " " + 
                           formatter.format( _hyp[1] / _total ) + "}");

        //LVFailure: True False
        System.out.println("\"LVFailure\" = {" + 
                           formatter.format( _lvf[0] / _total ) + " " + 
                           formatter.format( _lvf[1] / _total ) + "}");

        //Anaphylaxis: True False
        System.out.println("\"Anaphylaxis\" = {" + 
                           formatter.format( _ana[0] / _total ) + " " + 
                           formatter.format( _ana[1] / _total ) + "}");

        //InsuffAnesth: True False
        System.out.println("\"InsuffAnesth\" = {" + 
                           formatter.format( _ins[0] / _total ) + " " + 
                           formatter.format( _ins[1] / _total ) + "}");


        //PulmEmbolus: True False
        System.out.println("\"PulmEmbolus\" = {" + 
                           formatter.format( _pul[0] / _total ) + " " + 
                           formatter.format( _pul[1] / _total ) + "}");


        //Intubation: Normal Esophageal OneSided
        System.out.println("\"Intubation\" = {" + 
                           formatter.format( _int[0] / _total ) + " " + 
                           formatter.format( _int[1] / _total ) + " " + 
                           formatter.format( _int[2] / _total ) +"}");


        //KinkedTube: True False
        System.out.println("\"KinkedTube\" = {" + 
                           formatter.format( _kin[0] / _total ) + " " + 
                           formatter.format( _kin[1] / _total ) + "}");


        //Disconnect: True False
        System.out.println("\"Disconnect\" = {" + 
                           formatter.format( _dis[0] / _total ) + " " + 
                           formatter.format( _dis[1] / _total ) + "}");


    }
}

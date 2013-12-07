import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Blackjack{
    TreeMap<State, Value> _stateMap;
    double _p;

    public Blackjack(double p){
        _p = p;
    }

    public void solve() throws IOException{
        initialize();
        recurse();
        displayPolicy();

    }
    
    void validateState(State state){
        if (state._turn) {
            if(state._dealerMinSum > 10) {
                throw new IllegalArgumentException("Dealer minimum sum too large for players turn!");
            }

            if(state._minSum < 2 || state._minSum > 22) {
                throw new IllegalArgumentException("minSum out of range");
            }

            if (state._isBlackjack == true) {
                if (state._isTwoCards == false || state._hasAces == false) {
                    throw new IllegalArgumentException("Blackjack state is bad");
                }
            }
        }
        else{
            if (state._minSum > 21) {
                throw new IllegalArgumentException("Turn should be true here!");
            }

            if(state._dealerMinSum < 2 || state._dealerMinSum > 22) {
                throw new IllegalArgumentException("minimum sum for dealers is 3");
            }

            if (state._isBlackjack == true) {
                if (state._isTwoCards == false || state._dealerHasAces == false) {
                    throw new IllegalArgumentException("Blackjack state is bad");
                }
            }
        }
    }

    void enqueue(State state, LinkedList<State> queue)
    {
        validateState(state);
        //if (!queue.contains(state)) {
            queue.add(state);
        //}
    }

    void initialize(){
        // Goal here is to initialze our map of states to values
        TreeMap<State, Value> stateMap = new TreeMap<State,Value>();
        LinkedList<State> stateQueue = new LinkedList<State>();

        //Put in the first set of states
        for(int firstCard = 1; firstCard < 11; firstCard++) {
            for(int secondCard = 1; secondCard < 11; secondCard++) {
                int minSum = firstCard + secondCard;
                boolean hasAces = (firstCard == 1  || secondCard == 1);
                boolean isTwoCards = true;
                boolean isBlackjack = (minSum == 11 && (firstCard == 1 || firstCard == 10));
                int pair = (firstCard == secondCard ? firstCard : 0);
                boolean turn = true;
                for (int dFirstCard = 1; dFirstCard < 11; dFirstCard ++) {
                    int dealerMinSum = dFirstCard;
                    boolean dealerHasAces = (dFirstCard == 1);
                    State state = new State(minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                    enqueue(state, stateQueue);
                    
                    if (!isBlackjack) {
                        //Generate the initial states where we stand on the current hand
                        for (int dcard = 1; dcard < 11; dcard++) {
                            int dsum = dealerMinSum + dcard; // these are just the first two cards, so we can't bust (yet)
                            dealerHasAces = (dcard == 1 ? true : state._dealerHasAces);
                            isBlackjack = (dealerMinSum == 11 && dealerHasAces); // only way to blackjack on the first two cards
                            State standState = new State(minSum, hasAces, dsum, dealerHasAces, true, isBlackjack, pair, false);
                            enqueue(standState, stateQueue);
                            //if(state._minSum == 20 && state._pair == 10 && standState._dealerMinSum == 12 && standState._dealerHasAces == false) {
                            //    throw new IllegalArgumentException();
                            //}
                        }
                    }
                }
            }
        }

        // Ok, the queue has been primed.  Now what we need to do is walk the queue and add the resulting states
        // into our state map.  For each state we don't find in the map already, we add it to the queue
        // and we iterate the queue until it is empty. This will generate all our states
        while (stateQueue.peek() != null) {
            State state = stateQueue.remove();
            if(!stateMap.containsKey(state)) { // Don't need to recalculate states we've already covered
                Value value = initializeValue(state);
                stateMap.put(state,value);
                value = null;
                if(!state.isTerminal()) {
                    // every non-terminal state generates another set of states;
                    if(state._turn) {
                        // generate the next set of player states (hit, and "hit followed by stand");
                        for(int nextCard = 1; nextCard < 11; nextCard++) {
                            int minSum = state._minSum + nextCard;
                            minSum = (minSum > 22 ? 22 : minSum); // Cap at 22 (this is BUST)
                            boolean hasAces = (nextCard == 1 ? true : state._hasAces);
                            boolean isTwoCards = false;
                            boolean isBlackjack = false;
                            int pair = state._pair;
                            boolean turn = true;
                            State newState = new State(minSum, hasAces, state._dealerMinSum, state._dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                            enqueue(newState, stateQueue);

                            if (!newState.isTerminal()) {
                                //Generate the initial states where we stand on the current hand
                                for (int dcard = 1; dcard < 11; dcard++) {
                                    int dsum = newState._dealerMinSum + dcard; // these are just the first two cards, so we can't bust (yet)
                                    boolean dealerHasAces = (dcard == 1 ? true : newState._dealerHasAces);
                                    isBlackjack = (dsum == 11 && dealerHasAces); // only way to blackjack on the first two cards
                                    State newStandState = new State(newState._minSum, newState._hasAces, dsum, dealerHasAces, true, isBlackjack, newState._pair, false);
                                    enqueue(newStandState, stateQueue);
                                    //if (dcard == 1 && state._dealerMinSum == 10 && newState._pair == 9 && newState._minSum == 20 && newState._hasAces == true){
                                    //    throw new IllegalArgumentException();
                                    //}
                                }
                            }
                        }
                    }
                    else
                    {
                        // generate the next set of dealer states
                        for (int dCard = 1; dCard < 11; dCard++) {
                            int dSum = state._dealerMinSum + dCard;
                            dSum = (dSum > 22 ? 22 : dSum);
                            boolean dealerHasAces = (dCard == 1 ? true : state._dealerHasAces);
                            State newState = new State(state._minSum, state._hasAces, dSum, dealerHasAces, false, false, state._pair, false);
                            enqueue(newState, stateQueue);
                        }
                    }
                }
            }
        }
        _stateMap = stateMap;
    }

    Value initializeValue(State state)
    {
        Value value = new Value();
        if (state.isTerminal()) {
            if(state._turn){
                if (state._isBlackjack) {
                    // Player Blackjack!
                    value._vstar = 2.5;
                }
                else if(state._minSum == 22) {
                    // Player Busted!
                    value._vstar = 0.0;
                }
            } 
            else {
                if (!state._isBlackjack) {
                    int dealerSum = state._dealerMinSum;
                    if(state._dealerHasAces) {
                        if (dealerSum < 12) {
                            dealerSum += 10; // soft hand
                        }
                    }
                    int playerSum = state._minSum;
                    if(state._hasAces) {
                        if(state._minSum < 12) {
                            playerSum += 10; // soft hand
                        }
                    }

                    if (dealerSum == playerSum) {
                        value._vstar = 1.0; // push
                    }
                    else if(playerSum > dealerSum || dealerSum > 21) {
                        value._vstar = 2.0; // player Wins!
                    }
                }
            }
            value._action = Action.TERMINAL;
        }
        return value;
    }

    State standState(State state, int card){
        // hit the dealer
        int sum = state._dealerMinSum + card;
        sum = (sum > 22 ? 22 : sum);
        boolean hasAces = (card == 1 ? true : state._dealerHasAces);
        boolean isDealerFirstCard = state._turn;
        boolean isTwoCards = isDealerFirstCard;
        boolean isBlackjack = (sum == 11 && hasAces && isTwoCards);
        State stand = new State(state._minSum,
                                state._hasAces,
                                sum,
                                hasAces,
                                isTwoCards,
                                isBlackjack,
                                state._pair,
                                false);
         validateState(stand);
         return stand;
    }

    State hitState(State state, int card){
        State hit = null;
        if(state._turn) {
            // hit the player
            int sum = state._minSum + card;
            sum = (sum > 22 ? 22 : sum);
            hit = new State(  sum,
                            card == 1 ? true : state._hasAces,
                            state._dealerMinSum,
                            state._dealerHasAces,
                            false,
                            false,
                            state._pair,
                            true);
            validateState(hit);
        }
        
        return hit;
    }

    State splitState(State state, int card){
        int sum = state._pair + card;
        boolean hasAces = (card == 1 ? true : state._hasAces);
        boolean isBlackJack = (sum == 11 && hasAces);
        State split = new State(
                            sum,
                            hasAces,
                            state._dealerMinSum,
                            state._dealerHasAces,
                            true,
                            isBlackJack,
                            state._pair == card ? state._pair : 0,
                            true);
        return split;
    }



    State hitDealer(State state, int card){
        // hit the dealer
        int sum = state._dealerMinSum + card;
        sum = (sum > 22 ? 22 : sum);
        boolean hasAces = card == 1 ? true : state._dealerHasAces;
        State stand = new State(state._minSum,
                                state._hasAces,
                                sum,
                                hasAces,
                                false,
                                false,
                                state._pair,
                                false);
        validateState(stand);
        return stand;
    }

    void dumpStateValue(State state, Value value){
        System.out.println( state.toString() );
        System.out.println( value.toString() );
    }

    void displayPolicy() throws IOException 
    {
        BufferedWriter outBuf = new BufferedWriter(new FileWriter("Policy.txt"));

        // 5 to 19
        for(int sum = 5; sum < 20; sum++) {
            //System.out.print(sum + "\t");
            outBuf.write(sum + "\t");
            for (int dsum = 1; dsum < 11; dsum++ ) {
                int minSum = sum;
                boolean hasAces = false;
                boolean isTwoCards = true;
                boolean isBlackjack = false;
                int pair = 0;
                boolean turn = true;
                int dealerMinSum = (dsum == 10 ? 1 : dsum + 1);
                boolean dealerHasAces = (dealerMinSum == 1);
                State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                Value value = _stateMap.get(state);
                displayAction(value, outBuf);
            }
            //System.out.println("");
            outBuf.newLine();
        }
        
        // A2 to A9
        for(int sum = 3; sum < 11; sum++) {
            //System.out.print("A" + (sum - 1) + "\t");
            outBuf.write("A" + (sum - 1) + "\t");
            for (int dsum = 1; dsum < 11; dsum++ ) {
                int minSum = sum;
                boolean hasAces = true;
                boolean isTwoCards = true;
                boolean isBlackjack = false;
                int pair = 0;
                boolean turn = true;
                int dealerMinSum = (dsum == 10 ? 1 : dsum + 1);
                boolean dealerHasAces = (dealerMinSum == 1);
                State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                Value value = _stateMap.get(state);
                displayAction(value, outBuf);
            }
            //System.out.println("");
            outBuf.newLine();
        }

        /*

        // Blackjack
        System.out.print("A10\t");
        for (int dsum = 1; dsum < 11; dsum++ ) {
            System.out.print("S ");
        }
        System.out.println("");
        */

        // 22 to 1010
        for(int card = 2; card < 11; card++) {
            //System.out.print("" + card + "" + card + "\t");
            outBuf.write("" + card + "" + card + "\t");
            for (int dsum = 1; dsum < 11; dsum++ ) {
                int minSum = 2 * card;
                boolean hasAces = false;
                boolean isTwoCards = true;
                boolean isBlackjack = false;
                int pair = card;
                boolean turn = true;
                int dealerMinSum = (dsum == 10 ? 1 : dsum + 1);
                boolean dealerHasAces = (dealerMinSum == 1);
                State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                Value value = _stateMap.get(state);
                displayAction(value, outBuf);
            }
            //System.out.println("");
            outBuf.newLine();
        }

        // AA
    
        //System.out.print("AA\t");
        outBuf.write("AA\t");
        for (int dsum = 1; dsum < 11; dsum++ ) {
            int minSum = 2;
            boolean hasAces = true;
            boolean isTwoCards = true;
            boolean isBlackjack = false;
            int pair = 1;
            boolean turn = true;
            int dealerMinSum = (dsum == 10 ? 1 : dsum + 1);
            boolean dealerHasAces = (dealerMinSum == 1);
            State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
            Value value = _stateMap.get(state);
            displayAction(value, outBuf);
        }
        //System.out.println("");
        outBuf.newLine();

        outBuf.flush();
        outBuf.close();
    }

    void recurseValue(State state, Value value){
        if (value._action != Action.UNINITIALIZED) {
            //dumpStateValue(state,value);
            return;
        }

        // Stand
        double standValue = 0.0;
        for (int card = 1; card < 11; card++) {
            State standS;
            if (state._turn) {
                standS = standState(state,card);
            }
            else{
                standS = hitDealer(state, card);
            }
            
            Value standV = _stateMap.get(standS);
            recurseValue(standS, standV);
            if(card == 10) {
                standValue += _p * standV._vstar;
            }
            else
            {
                standValue += ((1- _p)/9) * standV._vstar;
            }
        }
        value._stand = standValue;
        
        // Hit
        double hitValue = 0;
        if(state._turn) { // HIT is only for players.  Once it is the dealers turn, all you can do is stand
            for (int card = 1; card < 11; card++) {
                State hitS = hitState(state, card);
                validateState(hitS);
                Value hitV = _stateMap.get(hitS);
                recurseValue(hitS, hitV);
                if (card == 10) {
                    hitValue += _p * hitV._vstar;
                }
                else{
                    hitValue += ((1-_p)/ 9) * hitV._vstar;
                }
            }
        }
        value._hit = hitValue;


        //Double Down
        if (state._turn && state._isTwoCards) {
            //doubling down is an option!
            double ddValue = 0;
            
            for (int card = 1; card < 11; card++) {
                State hitS = hitState(state, card);
                validateState(hitS);
                Value hitV = _stateMap.get(hitS);
                if (card == 10) {
                    ddValue += _p * hitV._stand;
                }
                else{
                    ddValue += ((1-_p)/ 9) * hitV._stand;
                }
            }
            ddValue = 2 * ddValue - 1;
            value._doubleDown = ddValue;
        }

        //Split
        if(state._turn && state._isTwoCards && state._pair > 0 && state._pair != 1) {
            // Splitting is 
            //iterate to get the value of split!
            double delta = 0;
            int iterations = 0;
            do{
                iterations++;
                double splitValue = 0;
                for(int card = 1; card < 11; card++) {
                    State splitS = splitState(state, card);
                    Value splitV = _stateMap.get(splitS);
                    if (card == 10) {
                        if (card == state._pair) {
                            splitValue += _p * splitV._split;
                        }
                        else {
                            splitValue += _p * splitV._vstar;
                        }
                    }
                    else {
                        if(card == state._pair) {
                            splitValue += ((1-_p)/9) * splitV._split;
                        }
                        else {
                            splitValue += ((1-_p)/9) * splitV._vstar;
                        }
                    }
                }
                splitValue = 2 * splitValue - 1;
                delta = Math.abs(splitValue - value._split);
                value._split = splitValue;
            }while (delta > 1.0e-6);
           
        }

        if(state._turn && state._isTwoCards && state._pair > 0 && state._pair == 1) {
            // Splitting Aces. This is an exception to the rule. If the player gets a pair of aces that is a very strong
            // hand.  She can split this but she will onl get one additional card per split hand, and she will not be
            // allowed to resplit. Morever, if the card is a face card, it will not be counted a blackjack, and 
            // will be treated as a regular 21
            double splitValue = 0;
            for(int card = 1; card < 11; card++) {
                State splitS = splitState(state, card);
                Value splitV = _stateMap.get(splitS);
                if (card == 10) {
                        //splitValue += _p * splitV._stand;
                        //this would be blackjack, but it isn't. It is a normal 21
                        State normalS = new State(21,true,splitS._dealerMinSum,splitS._dealerHasAces,false,false, 0, true);
                        Value normalV = _stateMap.get(normalS);
                        splitValue += _p * normalV._stand;
                }
                else {
                        splitValue += ((1-_p)/9) * splitV._stand;
                }
            }
            splitValue = 2 * splitValue-1;
            value._split = splitValue;
        }

        // vstar -- optimal strategy
        value._vstar = value._stand;
        value._action = Action.STAND;

        //check hit
        if(value._hit > value._vstar) {
            value._vstar = value._hit;
            value._action = Action.HIT;
        }

        //check double
        if(value._doubleDown > value._vstar) {
            value._vstar = value._doubleDown;
            value._action = Action.DOUBLEDOWN;
        }

        // check split
        if(value._split > value._vstar) {
            value._vstar = value._split;
            value._action = Action.SPLIT;
        }

        
        //Debug Dump
        //dumpStateValue(state,value);
    }

    void recurse()
    {
        // 5 to 19
        for(int sum = 5; sum < 20; sum++) {
            //int sum = 13; // just this card for debug purposes
            for (int dsum = 1; dsum < 11; dsum++ ) {
                int minSum = sum;
                boolean hasAces = false;
                boolean isTwoCards = true;
                boolean isBlackjack = false;
                int pair = 0;
                boolean turn = true;
                int dealerMinSum = dsum;
                boolean dealerHasAces = (dsum == 1);
                State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                Value value = _stateMap.get(state);
                recurseValue(state,value);
                
            }
        }

        // A3 - A9
        for(int sum = 3; sum < 11; sum++) {
            //int sum = 13; // just this card for debug purposes
            for (int dsum = 1; dsum < 11; dsum++ ) {
                int minSum = sum;
                boolean hasAces = true;
                boolean isTwoCards = true;
                boolean isBlackjack = false;
                int pair = 0;
                boolean turn = true;
                int dealerMinSum = dsum;
                boolean dealerHasAces = (dsum == 1);
                State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                Value value = _stateMap.get(state);
                recurseValue(state,value);
                
            }
        }

        //AA - 1010
        for (int card = 1; card < 11; card++) {
            for (int dsum = 1; dsum < 11; dsum++ ) {
                int minSum = 2 * card;
                boolean hasAces = (card == 1 ? true : false);
                boolean isTwoCards = true;
                boolean isBlackjack = false;
                int pair = card;
                boolean turn = true;
                int dealerMinSum = dsum;
                boolean dealerHasAces = (dsum == 1);
                State state = new State( minSum, hasAces, dealerMinSum, dealerHasAces, isTwoCards, isBlackjack, pair, turn);
                Value value = _stateMap.get(state);
                recurseValue(state,value);
            }
        }
    }

    void displayAction(Value value, BufferedWriter outBuf) throws IOException
    {
        switch(value._action) {
        case HIT:
            //System.out.print("H ");
            outBuf.write("H ");
            break;
        case STAND:
            //System.out.print("S ");
            outBuf.write("S ");
            break;
        case DOUBLEDOWN:
            //System.out.print("D ");
            outBuf.write("D ");
            break;
        case SPLIT:
            //System.out.print("P ");
            outBuf.write("P ");
            break;
        default:
            //System.out.print("? ");
            outBuf.write("? ");
        }
    }

}


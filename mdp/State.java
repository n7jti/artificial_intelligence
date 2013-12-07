
public class State implements Comparable<State>{
    public final int _minSum;             // The nimimum sum of the player's hand so far
    public final boolean _hasAces;             // The number of Aces in teh player's hands so far
    public final int _dealerMinSum;        // The minimum sum of the Dealer's hand so far
    public final boolean _dealerHasAces;       // The number of aces in the dealer's hand so far
    public final boolean _isTwoCards;      // true if the players hand is just two cards
    public final boolean _isBlackjack;     // true if the player has blackjack
    public final int _pair;                // 0 indicates no pair, a number between 1 and 10 indicates a pair of that card
    public final boolean _turn;            // true when it is the player's turn, false when it is the dealer's turn. 

    State(int minSum, boolean hasAces, int dealerMinSum, boolean dealerHasAces, boolean isTwoCards, boolean isBlackjack, int pair, boolean turn){
        _minSum = minSum;
        _hasAces = hasAces;
        _dealerMinSum = dealerMinSum;
        _dealerHasAces = dealerHasAces;
        _isTwoCards = isTwoCards;
        _isBlackjack = isBlackjack;
        _pair = pair;
        _turn = turn; 
    }

    public int compareTo( State that){
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == that) return EQUAL;

        if (_minSum < that._minSum) return AFTER;
        if (_minSum > that._minSum) return BEFORE;

        if (!_isTwoCards && that._isTwoCards) return  BEFORE;
        if (_isTwoCards && !that._isTwoCards) return  AFTER;

        if (!_isBlackjack && _isBlackjack) return  AFTER;
        if (_isBlackjack && !_isBlackjack) return  BEFORE;

        if (_pair < that._pair) return  AFTER;
        if (_pair > that._pair) return  BEFORE;

        if (!_hasAces && that._hasAces) return  AFTER;
        if (_hasAces && !that._hasAces) return  BEFORE;

        if (!_turn && that._turn) return  AFTER;
        if (_turn && !that._turn) return  BEFORE;

        if (_dealerMinSum < that._dealerMinSum) return  AFTER;
        if (_dealerMinSum > that._dealerMinSum) return  BEFORE;

        if (!_dealerHasAces && that._dealerHasAces) return  AFTER;
        if (_dealerHasAces && !that._dealerHasAces) return  BEFORE;

        //all comparisons have yielded equality
        //verify that compareTo is consistent with equals (optional)
        assert this.equals(that) : "compareTo inconsistent with equals.";
            
        return EQUAL;
    }

    @Override public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof State) {
            State that = (State) other;
            result = (
                _minSum == that._minSum &&
                _hasAces == that._hasAces &&
                _dealerMinSum == that._dealerMinSum &&
                _dealerHasAces == that._dealerHasAces &&
                _isTwoCards == that._isTwoCards &&
                _isBlackjack == that._isBlackjack &&
                _pair == that._pair &&
                _turn == that._turn
            );
        }
        return result;
    }

    @Override public int hashCode() {
        int result = 13;
        result = 37 * result + _minSum;
        result = 37 * result + (_hasAces ? 0 : 1);
        result = 37 * result + _dealerMinSum;
        result = 37 * result + (_dealerHasAces ? 0 : 1);
        result = 37 * result + (_isBlackjack ? 0 : 1);
        result = 37 * result + _pair;
        result = 37 * result + (_turn ? 0 : 1);

        return result;
    }

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append("minsum: " + _minSum + NEW_LINE);
        result.append("hasAces: " + _hasAces + NEW_LINE);
        result.append("dealerMinSum: " + _dealerMinSum + NEW_LINE);
        result.append("dealerHasAces:" + _dealerHasAces + NEW_LINE);
        result.append("isBlackjack: " + _isBlackjack + NEW_LINE);
        result.append("pair: " + _pair + NEW_LINE);
        result.append("turn: " + _turn + NEW_LINE);
        result.append("}");
        return result.toString();
    }

    public boolean isTerminal()
    {
        if (_isBlackjack) {
            return true;
        }

        if (_minSum > 21) {
            return true;
        }

        if (_dealerMinSum > 16) {
            return true;
        }

        if (_dealerMinSum > 6 && _dealerMinSum < 12 && _dealerHasAces) {
            return true;
        }

        return false;
    }


}

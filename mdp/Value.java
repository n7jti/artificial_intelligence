
public class Value{
    public double _vstar;
    public double _stand;
    public double _hit;
    public double _doubleDown;
    public double _split;
    public Action _action;

    public Value()
    {
        _action = Action.UNINITIALIZED;
    }

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append("vstar: " + _vstar + NEW_LINE);
        result.append("stand: " + _stand + NEW_LINE);
        result.append("hit: " + _hit + NEW_LINE);
        result.append("doubleDown:" + _doubleDown + NEW_LINE);
        result.append("split: " + _split + NEW_LINE);
        result.append("action: " + _action + NEW_LINE);
        result.append("}");
        return result.toString();
    }
}

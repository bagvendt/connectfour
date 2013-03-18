/**
 * A Decision, for use when implementing move ordering
 */
class Decision implements Comparable<Decision> {
    int column;
    int utility;
    /**
     * Creates a new decision
     * @param column The column where the decision applies.
     * @param utility the corresponding utility value.
     * @return A decision with a utility value and column
     */
    public Decision(int column, int utility) {
        this.column = column;
        this.utility = utility;
    }

    /**
     * A implementation of compareTo from the Comparable interface.
     * Makes sure that decisions can be sorted in a non decreasing manner by utility value.
     */
    @Override
    public int compareTo(Decision o) {
    	if (utility < o.utility) return -1;
    	if (utility > o.utility) return  1;
    	return 0;
    }
}
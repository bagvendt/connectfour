public enum Player {
	BLUE(1),
	RED(2);
	
	
	private int value;    

	  private Player(int value) {
	    this.value = value;
	  }

	  public int getValue() {
	    return value;
	  }
}

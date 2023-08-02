
public abstract class Clue {
	char let;
	boolean present;
	
	public Clue(char let, boolean present) {
		this.let = let;
		this.present = present;
	}
	
	public abstract boolean isValid(String word);
}

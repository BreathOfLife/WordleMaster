
public class GrayClue extends Clue {

	public GrayClue(char let) {
		super(let, false);
	}

	@Override
	public boolean isValid(String word) {
		return !word.contains(Character.toString(let));
	}

}

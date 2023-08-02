
public class GreenClue extends Clue {

	int correctPos;
	
	public GreenClue(char let, int pos) {
		super(let, true);
		this.correctPos = pos;
	}

	@Override
	public boolean isValid(String word) {
		if (word.indexOf(let) == correctPos) {
			return true;
		}
		return false;
	}

}

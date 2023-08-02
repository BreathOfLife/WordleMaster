
public class YellowClue extends Clue {

	int incorrectPos;
	
	public YellowClue(char let, int pos) {
		super(let, true);
		this.incorrectPos = pos;
	}

	@Override
	public boolean isValid(String word) {
		if (word.contains(Character.toString(let)) && word.indexOf(let) != incorrectPos) {
			return true;
		}
		return false;
	}

}

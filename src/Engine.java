import java.util.ArrayList;

public class Engine {
	
	static public final int SUGGESTIONS_SHOWN = 5;
	
	
	public ArrayList<Clue> clues;
	public Display disp;
	public String[] possibleWords;
	
	public static void main(String[] args) {
		Engine eng = new Engine();
	}
	
	public Engine() {
		FileIO.init();
		clues = new ArrayList<>();
		possibleWords = FileIO.getAllWords();
		disp = new Display(this);
	}

	public void submitWord(String fullWord, int[] colors) {
		for (int i = 0; i < 5; i++) {
			char let = fullWord.charAt(i);
			boolean doubleLetterUsed = false;
			for (int j = 0; j < 5; j++) {
				if (j != i) {
					if (fullWord.charAt(j) == fullWord.charAt(i)) {
						doubleLetterUsed = true;
					}
				}
			}
			switch (colors[i]) {
			case 0:
				if (!doubleLetterUsed) {
					clues.add(new GrayClue(let));
				} else {
					clues.add(new YellowClue(let,i)); //Don't ask me why a yellow clue has to be made if its a double letter, im so confused
				}
				break;
			case 1:
				clues.add(new YellowClue(let, i));
				break;
			case 2:
				
				clues.add(new GreenClue(let, i));
				boolean confirmedDoubleLetter = false;
				if (doubleLetterUsed) {
					for (int j = 0; j < 5; j++) {
						if (j != i) {
							if (fullWord.charAt(j) == fullWord.charAt(i)) {
								if (colors[j] == 1) {
									confirmedDoubleLetter = true;
								}
							}
						}
					}
					if (!confirmedDoubleLetter) {
						for (int j = 0; j < 5; j++) {
							if (!((fullWord.charAt(j) == fullWord.charAt(i)) && colors[j] == 2)) {
								//Set all other positions to yellow so algorithm understands it is only at green
								clues.add(new YellowClue(let,j));
							}
						}
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Not a valid color?");
			}
		}
		limitPossibleWords();
		if (possibleWords.length == 0) {
			disp.errorMsg("One of us did something wrong because this doesn't make any sense... And i'm 90% sure it wasn't me");
		}
		generateSuggestions();
	}

	private void limitPossibleWords() {
		int currLen = possibleWords.length;
		for (int i = 0; i < possibleWords.length; i++) {
			String word = possibleWords[i];
			for (Clue clue : clues) {
				if (!clue.isValid(word)) {
					currLen--;
					possibleWords[i] = null;
					break;
				}
			}
		}
		String[] tempArr = new String[currLen];
		int newIndex = 0;
		for (String word : possibleWords) {
			if (word != null) {
				tempArr[newIndex++] = word;
			}
		}
		possibleWords = tempArr;
	}

	private void generateSuggestions() {
		String[] possibleSuggestions = FileIO.getAllWords();
		
		//If theres less than the shown number of suggestions worth of possible words, just show the possible words
		if (possibleWords.length <= SUGGESTIONS_SHOWN) {
			possibleSuggestions = possibleWords;
		}
		
		String[] topSuggestions = new String[SUGGESTIONS_SHOWN];
		for (int i = 0; i < topSuggestions.length; i++) {
			topSuggestions[i] = "-----";
		}
		int[] topScores = new int[SUGGESTIONS_SHOWN];
		for (int i = 0; i < topScores.length; i++) {
			topScores[i] = -1;
		}
		
		for (String suggestedWord : possibleSuggestions) {
			int score = 0;
			for (String possibleWord : possibleWords) {
				for (int sI = 0; sI < 5; sI++) {
					for (int pI = 0; pI < 5; pI++) {
						if (suggestedWord.charAt(sI) == possibleWord.charAt(pI)) {
							//Yellow Point
							if (!suggestedWord.substring(0,sI).contains(suggestedWord.substring(sI,sI+1))) { //Dont give them yellow points just for double letters
								score++;
							}
							if (sI == pI) {
								//Addition Green Points
								score += 5;
							}
						}
					}
				}
				if (possibleWord.equals(suggestedWord)) {
					score += 25; //Nearly doubles points for that word if they are the same word. This helps boost words that will actually solve it rather than just words that get close
				}
			}
			
			for (int i = 0; i < topSuggestions.length; i++) {
				if (topScores[i] < score) {
					for (int j = topSuggestions.length - 1; j > i; j--) {
						topSuggestions[j] = topSuggestions[j - 1];
						topScores[j] = topScores[j - 1];
					}
					topSuggestions[i] = suggestedWord;
					topScores[i] = score; 
					break;
				}
			}
		}
		
		
		disp.setSuggestions(topSuggestions, topScores);
	}

}

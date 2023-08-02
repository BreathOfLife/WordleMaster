import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileIO {
	
	public static String[] words;
	
	public static void init() {
		words = new String[12947];
		int index = 0;
		try (BufferedReader br = new BufferedReader(new FileReader("FiveLetterWords"))) {
			String token = br.readLine();
			while (token != null) {
				words[index++] = token;
				token = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isWordPresent(String string) {
		for (String word : words) {
			if (word.equals(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static String[] getAllWords() {
		return words.clone();
	}
}

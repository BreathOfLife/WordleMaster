import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

public class Display extends JFrame {
	
	static final int WIDTH = 1000;
	static final int HEIGHT = 750;
	
	Engine eng;

	public Display(Engine eng) {
		this.eng = eng;
		
		Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
		((JPanel) getContentPane()).setBorder(padding);
		setBackground(Color.BLACK);
		getContentPane().setLayout(new GridLayout(2,2,10,10));
		
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new GridLayout(2,5,10,10));
		add(editPanel);
		
		ArrayList<JTextField> textFieldList = new ArrayList<>();
		Font fieldFont = new Font("SansSerif", Font.BOLD, 20);
		for (int i = 0; i < 5; i++) {
			textFieldList.add(new JTextField(1));
			textFieldList.get(i).setFont(fieldFont);
			textFieldList.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			textFieldList.get(i).addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(java.awt.event.KeyEvent evt) {
					JTextField field = (JTextField) evt.getSource();
					JPanel parent = (JPanel) field.getParent();
					int index = -1;
					for (int i = 0; i < 5; i++) {
						if (parent.getComponent(i) == field) {
							index = i;
							break;
						}
					}
					
					if (field.getText().length() >= 1) {
						if (index != 4) {
							parent.getComponent(index + 1).requestFocus();
						}
						evt.consume();
					} else if (!(Character.isLetter(evt.getKeyChar()))) {
						evt.consume();
					} else {
						evt.setKeyChar(Character.toUpperCase(evt.getKeyChar()));
						if (index != 4) {
							parent.getComponent(index + 1).requestFocus();
						}
					}

				}
				
				public void keyPressed(java.awt.event.KeyEvent evt) {
					JTextField field = (JTextField) evt.getSource();
					JPanel parent = (JPanel) field.getParent();
					int index = -1;
					for (int i = 0; i < 5; i++) {
						if (parent.getComponent(i) == field) {
							index = i;
							break;
						}
					}
					
					if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			    		submit();
			    	}
			    	if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			    		if (index != 0) {
			    			field.setText("");
							parent.getComponent(index - 1).requestFocus();
						}
			    	}
				}
			});
			editPanel.add(textFieldList.get(i));
		}


		ArrayList<JComboBox<String>> dropdownList = new ArrayList<>();
		String[] options = {"Gray", "Yellow", "Green" };
		Font smallFont = new Font("SansSerif", Font.PLAIN, 20);
		for (int i = 0; i < 5; i++) {
			dropdownList.add(new JComboBox<>(options));
			dropdownList.get(i).setFont(smallFont);
			((JLabel)dropdownList.get(i).getRenderer()).setHorizontalAlignment(JLabel.CENTER);
			editPanel.add(dropdownList.get(i));
		}

		JPanel history = new JPanel();
		history.setLayout(new GridLayout(0,5));
		add(history);
		
		Font subFont = new Font("SansSerif", Font.BOLD, 40);
		JButton submit = new JButton("Submit");
		submit.setFont(subFont);
		submit.addActionListener(evt -> {
			submit();
		});
		add(submit);

		JPanel suggestionPanel = new JPanel();
		suggestionPanel.setLayout(new GridLayout(Engine.SUGGESTIONS_SHOWN,1));
		Font sugFont = new Font("SansSerif", Font.BOLD, 35);
		Font sugConfFont = new Font("SansSerif", Font.ITALIC, 15);
		for (int i = 0; i < Engine.SUGGESTIONS_SHOWN; i++) {
			JPanel suggestionCombo = new JPanel();
			suggestionCombo.setBackground(Color.LIGHT_GRAY);
			JTextField suggestion = new JTextField();
			suggestion.setHorizontalAlignment(SwingConstants.CENTER);
			suggestion.setEditable(false);
			suggestion.setFont(sugFont);
			suggestion.setBackground(Color.LIGHT_GRAY);
			suggestion.setBorder(null);
			JTextField suggestionConf = new JTextField();
			suggestionConf.setHorizontalAlignment(SwingConstants.CENTER);
			suggestionConf.setEditable(false);
			suggestionConf.setFont(sugConfFont);
			suggestionConf.setForeground(Color.BLUE);
			suggestionConf.setBackground(Color.LIGHT_GRAY);
			suggestionConf.setBorder(null);
			suggestionCombo.add(suggestion);
			suggestionCombo.add(suggestionConf);
			suggestionPanel.add(suggestionCombo);
		}
		add(suggestionPanel);
		
		setTitle("Wordle Helper");
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void submit() {
		JPanel editPanel = (JPanel) (getContentPane().getComponent(0));
		String fullWord = "";
		for (int i = 0; i < 5; i++) {
			JTextField currentField = (JTextField) editPanel.getComponent(i);
			String text = currentField.getText();
			if (text == null || text.equals("")) {
				errorMsg("Incomplete Word");
				clear();
				update();
				return;
			}
			fullWord = fullWord + text;
		}
		fullWord = fullWord.toLowerCase();
		if (FileIO.isWordPresent(fullWord)) {
			int[] colors = new int[5];
			int numGreens = 0;
			for (int i = 0; i < 5; i++) {
				JComboBox<String> currentDrop = (JComboBox<String>) editPanel.getComponent(i + 5);
				switch ((String) currentDrop.getSelectedItem()) {
				case "Gray":
					colors[i] = 0;
					break;
				case "Yellow":
					colors[i] = 1;
					break;
				case "Green":
					colors[i] = 2;
					numGreens++;
					break;
				default:
					throw new IllegalArgumentException("Not a valid color?");
				}
			}
			if (numGreens == 5) {
				errorMsg("I mean congratulations I guess, I was the reason you got that but whatever");
			}
			clear();
			eng.submitWord(fullWord, colors);
			addToHistory(fullWord, colors);
			update();
			return;
		} else {
			errorMsg("Not A Recognized Word");
			clear();
			update();
			return;
		}
		
	}

	private void addToHistory(String fullWord, int[] colors) {
		String upWord = fullWord.toUpperCase();
		JPanel history = (JPanel) (getContentPane().getComponent(1));
		GridLayout historyLO = (GridLayout) history.getLayout();
		historyLO.setRows(historyLO.getRows() + 1);
		Font font = new Font("SansSerif", Font.PLAIN, 30);
		for (int i = 0; i < 5; i++) {
			JTextField letter = new JTextField(1);
			letter.setHorizontalAlignment(SwingConstants.CENTER);
			letter.setText(upWord.substring(i,i+1));
			letter.setEditable(false);
			Color color = null;
			switch (colors[i]) {
			case 0:
				color = Color.GRAY;
				break;
			case 1:
				color = Color.YELLOW;
				break;
			case 2:
				color = Color.GREEN;
				break;
			default:
				throw new IllegalArgumentException("Not a valid color?");
			}
			letter.setBackground(color);
			letter.setFont(font);
			history.add(letter);
			
		}
		
	}

	private void clear() {
		JPanel editPanel = (JPanel) (getContentPane().getComponent(0));
		for (int i = 0; i < 5; i++) {
			JTextField currentField = (JTextField) editPanel.getComponent(i);
			currentField.setText("");
		}
		for (int i = 5; i < 10; i++) {
			JComboBox<String> currentBox = (JComboBox<String>) editPanel.getComponent(i);
			currentBox.setSelectedIndex(0);
		}
	}

	public void errorMsg(String string) {
		JOptionPane.showMessageDialog(null, string, "Error Message", JOptionPane.ERROR_MESSAGE);
	}

	private void update() {
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	public void setSuggestions(String[] suggestionsLow, int[] scores) {
		String[] suggestions = new String[suggestionsLow.length];
		for (int i = 0; i < suggestions.length; i++) {
			suggestions[i] = suggestionsLow[i].toUpperCase();
		}
		
		JPanel suggestionPanel = (JPanel) (getContentPane().getComponent(3));
		for (int i = 0; i < Engine.SUGGESTIONS_SHOWN; i++) {
			JPanel suggestionCombo = (JPanel) suggestionPanel.getComponent(i);
			JTextField suggestion = (JTextField) suggestionCombo.getComponent(0);
			JTextField suggestionConf = (JTextField) suggestionCombo.getComponent(1);
			if (suggestions[i] != null) {
				suggestion.setText(suggestions[i]);
			} else {
				suggestion.setText("-----");
			}
			suggestion.setBorder(null);
			if (scores[i] != -1) {
				suggestionConf.setText(Integer.toString(scores[i]));
			} else {
				suggestionConf.setText("-");
			}
			suggestionConf.setBorder(null);
		}
		JPanel editPanel = (JPanel) (getContentPane().getComponent(0));
		for (int i = 0; i < 5; i++) {
			JTextField currentField = (JTextField) editPanel.getComponent(i);
			currentField.setText(suggestions[0].substring(i,i+1));
		}
	}
}

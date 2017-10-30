
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.util.LinkedList;

public class SudokuBoard extends JFrame {

	//instance variables
	private final int SIZE = 9;
	private final int EASY_DIFF = 36;
	private final int MED_DIFF = 33;
	private final int HARD_DIFF = 30;
	private JTextField [][] fields = new JTextField[SIZE][SIZE];
	private JPanel boardPanel, appPanel, btnPanel;
	private static final Font BOLD = new Font("Monospaced", Font.BOLD, 20);
	JButton newGame, reset, exit, help;
	Numbers numbers = new Numbers();//generate numbers
	private LinkedList<Integer> filled = new LinkedList<Integer>(); //linkedList to hold coordinates of user/help filled cells
	String [] options = new String[]{"Easy","Medium","Hard"};
	private int userFilled = 0;	//counts total number of cells filled either by user himself or by help button pressed by user
	private int difference;	//cells that are left to be filled (difference between total num of cells and cells already filled by program
	private int helpCount = 0;	//counts number of times help button has been pressed
	private int wrongCount = 0;	//counts how many times user entered incorrect number
	
	public SudokuBoard(){
		
		setTitle("Sudoku");
		
		//panel for Sudoku board
		boardPanel = new JPanel(new GridLayout(SIZE,SIZE));
		boardPanel.setBorder(new LineBorder(Color.BLACK, 4));
		
		appPanel = new JPanel(new BorderLayout(10, 10));//panel that holds board and buttons
		btnPanel = new JPanel(new FlowLayout());//panel that holds buttons
	
		//new game button, generates new Sudoku number board
		newGame = new JButton();
		newGame.setText("New Game");
		
		newGame.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){		
				clearBoard();
				numbers.generateNumbers();//generate new numbers
				int diff = getDifficulty();
				difference = (SIZE * SIZE) - diff; //cells that are not filled yet
				userFilled = 0;
				helpCount = 0;
				wrongCount = 0;
				displayNumbers(diff);
			}
		});
			
		exit = new JButton();
		exit.setText("Exit");
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		//reset button, deletes all numbers that have been entered so far by user (or by help button)
		reset = new JButton();
		reset.setText("Reset");
		reset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//resets board
				int row, col;
				int listSize = filled.size() / 2; //divide by 2, because in each iteraton two elements are removed from list
				for(int x = 0; x < listSize; x++){
					//take last two numbers from list 'filled' and delete the content of this index on the board
					col = filled.removeLast();
					row = filled.removeLast();
					fields[row][col].setText("");
					fields[row][col].setEditable(true);
					fields[row][col].setBackground(Color.WHITE);
				}
				userFilled = 0;
				helpCount = 0;
				wrongCount = 0;
			}
		});
		
		//help button displays a new number in board
		help = new JButton();
		help.setText("Help");
		help.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				boolean set = false;
				for(int x = 0; x < fields.length; x++){
					for(int y = 0; y < fields.length; y++){
						if(fields[x][y].getText().isEmpty()){
							fields[x][y].setText(Integer.toString(numbers.getNum(x, y)));//display number in grid
							fields[x][y].setEditable(false);
							userFilled++;
							helpCount++;
							filled.add(x);//add coordinates of cell to list
							filled.add(y);
							set = true;
							break;
						}
					}
					if(set)
						break; //if empty cell was found and filled exit
				}
				if(userFilled == difference){
					//if board is filled, check if user won or not
					checkForWin();
				}
			}
		});
		
		//create cells (JTextfields) for Sudoku board, set borders, set font, etc
		for(int row = 0; row < fields.length; row++){		
			for(int col = 0; col < fields.length; col++){
				
				fields[row][col] = new JTextField();
				final int r = row;
				final int c = col;
				//this ActionListener is invoked when user enters a number in a textfield and presses enter
				//compare number entered by user to pre-generated number in grid, if the same, add number to board 
				//if not the same keep textfield empty and increment wrongCount
				fields[row][col].addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event){
						
						if(fields[r][c].getText().equals(Integer.toString(numbers.getNum(r, c)))){
							fields[r][c].setBackground(new Color(153,235,255));
							fields[r][c].setEditable(false);
							filled.add(r);
							filled.add(c);
							userFilled++;
							if(userFilled == difference){
								checkForWin();
							}
						}else{
							fields[r][c].setText("");
							wrongCount++;
						}
					}
				});//end inner class
				
				//add all jtextfields to the boardPanel, setBorder, etc.
				boardPanel.add(fields[row][col]);
				fields[row][col].setText("");
				fields[row][col].setEditable(true);
				fields[row][col].setHorizontalAlignment(JTextField.CENTER);
				fields[row][col].setFont(BOLD);
				fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.BLACK));
				
				//set borders on subgrids
				if(col == 2 || col == 5){
					fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,1,3, Color.BLACK));
				}
				
				if(row == 2 || row == 5){
					fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,3,1, Color.BLACK));
				}
				
				if(row == 2 && col == 2 || row == 2 && col == 5 || row == 5 && col == 2 || row == 5 && col == 5){
					fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,3,3, Color.BLACK));
				}
			}
		}
		
		//get difficulty from user
		int difficulty = getDifficulty();
		difference = (SIZE * SIZE) - difficulty;
			
		//display certain number of numbers in grid
		displayNumbers(difficulty);
		
		//add all the GUI components to frame
		appPanel.add(boardPanel, BorderLayout.CENTER);
		appPanel.add(btnPanel, BorderLayout.SOUTH);
		btnPanel.add(newGame, BorderLayout.WEST);
		btnPanel.add(reset, BorderLayout.CENTER);
		btnPanel.add(help, BorderLayout.CENTER);
		btnPanel.add(exit, BorderLayout.EAST);
		
		add(appPanel, BorderLayout.CENTER);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);	
	}//end constructor
	
	public void checkForWin(){	
		if(helpCount <= 5 && wrongCount <= 10){
			JOptionPane.showMessageDialog(null, "Congratulations! You won!");
		}else if(helpCount > 5 && wrongCount <= 10){
			JOptionPane.showMessageDialog(null, "Sorry, you lost. You pressed the help button too many times!");
		}else if(helpCount <= 5 && wrongCount > 10){
			JOptionPane.showMessageDialog(null, "Sorry, you lost. You entered a wrong number too many times!");
		}else{
			JOptionPane.showMessageDialog(null, "Sorry, you lost. You pressed the help button and entered a wrong number too many times!");
		}
	}
	
	//clears the board of all numbers
	private void clearBoard(){
		
		for(int row = 0; row < fields.length; row++){		
			for(int col = 0; col < fields.length; col++){
				
				fields[row][col].setText("");
				fields[row][col].setEditable(true);
				fields[row][col].setHorizontalAlignment(JTextField.CENTER);
				fields[row][col].setFont(BOLD);
				fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.BLACK));
				fields[row][col].setBackground(Color.WHITE);
				
				if(col == 2 || col == 5){
					fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,1,3, Color.BLACK));
				}			
				if(row == 2 || row == 5){
					fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,3,1, Color.BLACK));
				}		
				if(row == 2 && col == 2 || row == 2 && col == 5 || row == 5 && col == 2 || row == 5 && col == 5){
					fields[row][col].setBorder(BorderFactory.createMatteBorder(1,1,3,3, Color.BLACK));
				}
			}
		}
	}
	
	//displays a certain amount of numbers in board for user to see (amount of numbers depends on difficulty)
	public void displayNumbers(int difficulty){
		
		Random random = new Random();
		int randCol, num;
		int row = 0;
		int numInRow = 0;
		int counter = 1;
		
		if(difficulty == EASY_DIFF){
			num = 4;//number of numbers displayed in each row
		}else if(difficulty == MED_DIFF || difficulty == HARD_DIFF){
			num = 3;
		}else{
			num = 4;
		}
	
		//display 36 numbers in grid for easy difficulty, display less for medium and hard
		for(int x = 0; x < difficulty; x++){
			
			//ensure visible numbers are evenly spread on board
			if(numInRow == num){
				row++;
				numInRow = 0;
								
				if(row >= fields.length){
					row = 0;
				}
			}
			
			//generate a random column to display number in
			randCol = random.nextInt(9);
			
			if(fields[row][randCol].getText().isEmpty()){	
				fields[row][randCol].setText(Integer.toString(numbers.getNum(row, randCol)));
				fields[row][randCol].setEditable(false);
				numInRow++;
				
				//medium difficulty = 33 numbers, every time counter % 5 = 0, allow for an additional number to be added
				//this ensures that numbers are evenly spread across the board for this difficulty
				if(difficulty == MED_DIFF && counter % 5 == 0){
					numInRow--;
				}
				
				//hard difficulty = 30 numbers, every time counter % 9 = 0, allow for an additional number to be added
				//this ensures that numbers are evenly spread across the board for this difficulty
				if(difficulty == HARD_DIFF && counter % 9 == 0){
					numInRow--;
				}
			}else{
				//if cell is already filled, start over
				x--;
			}
			
			//this counter helps with spreading the numbers evenly across the board
			if(counter < difficulty){
				counter++;
			}
			
		}			
	}
	
	public int getDifficulty(){
		//get difficulty from user through Dialog box
		int difficulty;
		String s = (String)JOptionPane.showInputDialog(null, "Please choose the difficulty:\n", "Sudoku Difficulty", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(s == options[0]){
			difficulty = EASY_DIFF;
		}else if(s == options[1]){
			difficulty = MED_DIFF;
		}else if(s == options[2]){
			difficulty = HARD_DIFF;
		}else{
			difficulty = EASY_DIFF;
		}
		return difficulty;
	}
}//end class

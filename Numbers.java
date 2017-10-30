import java.util.Random;

public class Numbers {

	private final int SIZE = 9;
	public Integer [][] numbers = new Integer[SIZE][SIZE];
	Random random = new Random();
	private int count = 0;
	private boolean success = true;
	private boolean filledCell;
	private int tryRowCount = 0;
	
	public Numbers(){
		initArray();
		generateNumbers();
	}
	
	public void generateNumbers(){
		
		//generate 81 numbers that serve as underlying solution
		for(int row = 0; row < SIZE; row++){	
			for(int col = 0; col < SIZE; col++){
				
				success = true;
				count = 0;
				int randNum = random.nextInt(9)+1;
				boolean cellFilled = fillCell(row, col, randNum);
				
				if(cellFilled){
					//if cell could be filled successfully continue with next cell in same row
					continue;
				}else{
					initRow(row);//initialize row and start over filling current row
					row--;
					tryRowCount++;
					
					if(tryRowCount == 200){
						//if after 200 times no solution has been found, initialize array and start from beginning
						//(avoids that program enters infinite loop if no possible solution exists)
						row = -1;
						initArray();
						tryRowCount = 0;
					}
					break;
				}			
			}
		}
	}
	
	private void initRow(int row){
		for(int col = 0; col < SIZE; col++){
			numbers[row][col] = 0;
		}
	}
	
	private boolean fillCell(int row, int column, int randNum){
		
		//if no duplicate is found in row, col or subgrid place random number and return true
			if(noDuplicate(row, column, randNum)){		
				numbers[row][column] = randNum;	
				return true;				
		//if a duplicate is found, generate new random number and call method fillCell again, keep doing it until number is found
		//if no fitting number is found after having tried 50 times, it is to assume that no number will fit, so return false and stop recursion
			}else{
				randNum = random.nextInt(9)+1;
				count++;
				if(count == 50){
					success = false;
				}else{
					//keep calling fillCell method until fitting number is found
					fillCell(row, column, randNum);
				}
			}			
			//if false is returned, initialize row and start over
			return success;	
	}
	
	public boolean noDuplicate(int row, int column, int num){		
		//check if there is a duplicate in row, column or subgrid
		return !foundInRow(row, num) && !foundInCol(column, num) && !foundInSubGrid(row - row%3, column - column%3, num);	
	}
	
	private boolean foundInRow(int row, int num){
		//check for duplicate in row, return true if duplicate is found, otherwise return false
		for (int col = 0; col < SIZE; col++)
	        if (numbers[row][col] == num){
	            return true;
	        }
	    return false;
	}
	
	private boolean foundInCol(int col, int num){
		//check for duplicate in column, if duplicate is found return true, otherwise return false
		for (int row = 0; row < SIZE; row++)
	        if (numbers[row][col] == num){
	            return true;
	        }
	    return false;
	}
	
	private boolean foundInSubGrid(int startRow, int startCol, int num){
		//check for duplicate in subgrid, return true if duplicate found, otherwise return false
		for (int row = 0; row < 3; row++)
	        for (int col = 0; col < 3; col++)
	            if (numbers[row + startRow][col + startCol] == num){
	                return true;
	            }
	    return false;
	}
	
	private void initArray(){
			//initialize array, set every cell to 0
			for(int x=0; x < numbers.length; x++){
				for(int y = 0; y < numbers.length; y++){
					numbers[x][y]=0;
				}
			}
		}
	
	public void printArray(){
		//print numbers
		for(int row = 0; row < numbers.length; row++){		
			for(int col = 0; col < numbers.length; col++){
				
				System.out.printf("%d ", numbers[row][col]);
			}
			System.out.println();
		}
	}
	
	public int getNum(int row, int col){	
		int number = numbers[row][col];
		return number;
	}
}//end class

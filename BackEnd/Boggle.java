
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Boggle {
    TrieSet    lex;	   // The dictionary, stored in a TrieSet
    TrieSet    allWords;   // The dictionary words on the current board
    TrieSet    foundWords; // The valid words found so far by our one player
    Square[][] board;	   // The 4x4 board
    String[]   dice;       // An array of dice -- explained later!
	
	Boggle(String string) throws FileNotFoundException{
		TrieSet trie = new TrieSet();
		Scanner scanner = new Scanner(new File(string));
		if(scanner!=null){
		    while(scanner.hasNext()){
			trie.add(scanner.next());
		    }
		}
		fillDice();// IS THIS CORRECT?

	}

	public Square[][] getBoard() {
		return board;
	}
	
	public TrieSet getAllWords() {
		return allWords;
	}

	public TrieSet getLexicon() {
		return lex;
	}
	
	public int numFoundWords() {
		return foundWords.size();
	}
	
	public String toString() {
		String string = "";
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				string = string + board[i][j] + " ";//Aesthetic spaces
			}
			System.out.println();
		}
		return string;	
	}
	
	public boolean contains(String w) {
		if(allWords.contains(w))
			return true;
		return false;
	}

	public boolean addGuess(String lowerCase) {
		boolean flag = false;
		if(allWords.contains(lowerCase)) {
			if(!foundWords.contains(lowerCase)) {
				flag = true;
				foundWords.add(lowerCase);
			}
		}
		return flag;
	}
	
	public void newGame() throws FileNotFoundException {// IS THIS CORRECT?
		fillDice();
		lex = new TrieSet();
		//Get the dictionary in lex
		Scanner scanner = new Scanner(new File("lexicon.txt"));
		while(scanner.hasNext()) {
			lex.add(scanner.next());
		}
		board = new Square [4][4];
		allWords = new TrieSet();
		foundWords = new TrieSet();
		fillBoardFromDice();
		findAllWords();
		
	}
	
	public ArrayList<Square> findPath(String w){
		//handle when w is empty
		if(w.equals(""))
			return new ArrayList();
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				ArrayList<Square> path = new ArrayList<Square>();// IS THERE AN ERROR HERE?
				if((board[i][j].toString()).equals(w.charAt(0))) {
					board[i][j].mark();// Mark the square because it is the first letter of the word
					path.add(board[i][j]);
					ArrayList<Square> list = findPath(board[i][j], w.substring(1), path);
					if(list.size() == 0) {
						board[i][j].unmark();
						continue;
					}
					return list;
				}
			}
		}
		return new ArrayList<Square>();
	}
	
	private void fillDice() throws FileNotFoundException {// Is that's it?
		dice = new String [16];// is this step necessary?
		Scanner scanner = new Scanner(new File("dice.txt"));
		int i = 0;
		while(scanner.hasNext()) {
			dice[i] = scanner.next();
			i++;
		}
		scanner.close();
	}
	
	private void fillBoardFromDice() {// Is that's it?
		//board = new Square[4][4];
		ArrayList<Integer> list = new ArrayList<Integer>();
		int rand = (int)(Math.random()*(16));
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				while(list.contains(rand)) {
					rand = (int)(Math.random()*(16));
				}
				//Get a random letter from dice[rand] and pass it to the blank
				// If it is Q make it qu
				int rand2 = (int)(Math.random()*(6));
				//The random letter in the dice
				String c = dice[rand].charAt(rand2) + "";
				if(c.equals("Q") || c.equals("q"))// Is there an error here?
					c = "qu";
				board[i][j] = new Square(i, j, c);
				list.add(rand);
			}
		}
	}
	
	
	private void findAllWords() {
		for (Square[] row : board) {
		    for (Square sq : row) {
				findAllWords(sq, "");
		    }
		}
	}
	
	
	private void findAllWords(Square sq, String prefix) {
		// if prefix is in lexicon then add it to allwords
		String l = sq.toString();
		if (lex.contains(prefix)) {
		    allWords.add(prefix);
		}
		if (lex.containsPrefix(prefix) == true) {
		    int x = sq.getX();
		    int y = sq.getY();
		    sq.mark();
		    for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
				    if (i >= 0 && j >= 0 && i < 4 && j < 4 && board[i][j].isMarked() == false
					    && ((i != x) || (j != y))) {
						board[i][j].mark();
						this.findAllWords(board[i][j], prefix + l);
						board[i][j].unmark();
				    }
				}
		    }
		    //TODO TEST
		    sq.unmark();
		}
	}

	private ArrayList<Square> findPath(Square sq, String w, ArrayList<Square> path) {
		//ArrayList<Square> list = new ArrayList<Square>();
		//Return an empty list if the word w is not in the board
		/*if(!allWords.contains(w))
			return new ArrayList<Square>();*/
		if(w.equals(""))
			return path;
		
	    int x = sq.getX();
	    int y = sq.getY();
	    //sq.mark();
	    for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
			    if (i >= 0 && j >= 0 && i < 4 && j < 4 && board[i][j].isMarked() == false
				    && ((i != x) || (j != y)) && board[i][j].toString().equals(w.charAt(0))) {// IS THERE AN INDEX OUT OF BOND ERROR HERE? FIX IT IF SO!
					board[i][j].mark();
					path.add(board[i][j]);
					findPath(board[i][j], w.substring(1), path);//
					board[i][j].unmark();
			    }
			}
		}
		return new ArrayList<Square>();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Boggle boggle = new Boggle(args[0]);
		BoggleFrame bFrame = new BoggleFrame(boggle);
		bFrame.pack();
		bFrame.setLocationRelativeTo(null);
		bFrame.setVisible(true);
	    }

}

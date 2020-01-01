/**
 * Square class
 * Represents a single die on the boggle board.
 * 
 * @author Alexa Sharp 2009
 * 
 * No need to change any of this class.
 */

public class Square {
    private int x;      // the x-coordinate of this Square on the boggle board
    private int y;      // the y-coordinate of this Square on the boggle board
    private String letter;  // the letter contained on this square. Could be "qu"
    private boolean marked; 

    public Square( int x, int y, String letter ) {
	this.x = x;
	this.y = y;
	this.letter = letter.toLowerCase();
    }

    public int      getX()      { return x; }

    public int      getY()      { return y; }

    public boolean  isMarked()  { return marked; }

    public void     mark()      { marked = true; }

    public void     unmark()    { marked = false; }

    public String   toString()  { return letter; }

}

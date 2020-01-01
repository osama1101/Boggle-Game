import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**  
 * This is the GUI for your boggle game.
 * 
 * It requires a working Boggle class, with the 
 * methods defined exactly as on the lab description
 *
 * You do not have to modify this class at all.
 * 
 * @author Alexa Sharp 2009
 * @author Benjamin Kuperman (Spring 2011)
 * @author John Donaldson (Fall 2019)
 * 
 * based on code by Owen Astrachan.
 */


public class BoggleFrame extends JFrame {

    private static final long serialVersionUID = -2462602286620587815L;

    private Boggle boggle;

    // Visual components of the GUI
    private BoggleBoardPanel  myBoardPanel;
    private WordEntryField    wordEntryField;
    private PlayerView 	      humanArea;
    private JProgressBar      myProgress;
    private javax.swing.Timer myTimer;

    // Vars to keep track of the timer info
    private int mySeconds;
    private int myGameLength;

    public BoggleFrame(Boggle boggle) {
        try{
            this.boggle = boggle;
            myGameLength = 120;
            initPanels();
            setUpMenuBar();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            pack();
            setVisible(true);
            newGame();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    // Initialize all the GUI panels
    private void initPanels() throws Exception {
        Container contentPane = getContentPane();  
        humanArea = new PlayerView(" Score");
        myBoardPanel = new BoggleBoardPanel();
        wordEntryField = new WordEntryField();
        contentPane.add(wordEntryField,BorderLayout.SOUTH);
        contentPane.add(humanArea, BorderLayout.WEST);
        contentPane.add(myBoardPanel, BorderLayout.CENTER);
        contentPane.add(makeProgressBar(), BorderLayout.NORTH);
    }
    // Initialize the menus
    private void setUpMenuBar()  {
        //Set Up Menu Bar
        JMenuBar menu = new JMenuBar();

        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic('G');
        menu.add(gameMenu);

        JMenuItem newRandom = new JMenuItem("New Game");
        gameMenu.add(newRandom);
        newRandom.setMnemonic('N');
        newRandom.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                try {
					newGame();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }});

        gameMenu.addSeparator();

        JMenuItem gameTime = new JMenuItem("Time (secs)");
        gameMenu.add(gameTime);
        gameTime.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String secs = JOptionPane.showInputDialog(BoggleFrame.this, "time in seconds");
                try {
                    int len = Integer.parseInt(secs);
                    myGameLength = len;
                    myProgress.setMaximum(myGameLength);
                } catch (NumberFormatException e1) {
                    if (secs != null) {
                        showError(secs+" not valid integer value");
                    }
                }

            }
        });
        gameMenu.addSeparator();
        JMenuItem quitGame = new JMenuItem("Quit");
        gameMenu.add(quitGame);
        quitGame.setMnemonic('Q');
        quitGame.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                System.exit(0);
            }});


        // Help menu
        JMenu helpMenu = new JMenu("Help");
        menu.add(helpMenu);
        helpMenu.setMnemonic('H');

        JMenuItem aboutGame = new JMenuItem("About...");
        helpMenu.add(aboutGame);
        aboutGame.setMnemonic('A');
        aboutGame.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent e)  {
                JOptionPane.showMessageDialog(BoggleFrame.this,
                    "Boggle gobble gobble boggle\n", 
                    "About Game",
                    JOptionPane.PLAIN_MESSAGE);
            }});
        setJMenuBar(menu);
    }

    // Start a new game by reseting all variables, etc.
    private void newGame() throws FileNotFoundException {
        boggle.newGame();	// roll the dice anew  
        mySeconds = 0;	    // restart the timer
        // Update the GUI stuff
        myProgress.setValue(0);
        myBoardPanel.newGame();
        humanArea.setReady();
        myBoardPanel.unHighlightAllDice();
        wordEntryField.setReady();
        ((JPanel) getContentPane()).revalidate();
        repaint();
        myTimer.start();
    }

    private JPanel makeProgressBar() {
        JPanel panel = new JPanel();
        myProgress = new BoggleProgress(0,myGameLength);
        myProgress.setStringPainted(true);
        myTimer = new javax.swing.Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                mySeconds++;
                myProgress.setValue(mySeconds);
                if (mySeconds > myGameLength) {
                    gameOver();
                }
            }     
        });
        panel.add(myProgress);
        return panel;
    }

    private void gameOver() {
        myTimer.stop();
        mySeconds = myGameLength; // ensure over
        myProgress.setValue(myGameLength);
	humanArea.gameOver();
        repaint();
        wordEntryField.setUnready();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Boggle Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * A class defining the visual appearance of a Boggle board, and
     * defining some related methods.
     */
    private class BoggleBoardPanel extends JPanel {
      
	private static final long serialVersionUID = 102026256021871501L;

	public  final Color BACKGROUNDCOLOR = new Color(100, 100, 100);

        private DiePanel theDice[][];   // draw the Dice here
        private int rows, cols;   

        BoggleBoardPanel() {
            this.rows = 4; this.cols = 4;

            // create a JPanel with rowsXcols GridLayout to hold the DiePanels
            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new GridLayout(rows,cols,1,1));
            innerPanel.setBackground(BACKGROUNDCOLOR);

            // Create Individual DiePanels, and add them
            theDice = new DiePanel[rows][cols];
            for (int row = 0; row < rows; row++)      {
                for (int col = 0; col < cols; col++)      {
                    theDice[row][col] = new DiePanel();

                    innerPanel.add(theDice[row][col]);
                }
            }
            innerPanel.setBorder(BorderFactory.createMatteBorder(10,10,10,10,      
                        BACKGROUNDCOLOR));
            this.add(innerPanel);
        }

        /**
         * There's a new game, update display to reflect this new game.
         */
        public void newGame()  {
            Square[][] board = boggle.getBoard();
            //Set the DiePanels with given letters
            for (int row = 0; row < rows; row++)  {
                for (int col = 0; col < cols; col++)  {
                    theDice[row][col].setFace(board[row][col].toString());
                }
            }
        }

        public void highlightDice(List<Square> locations) {
            if(locations == null) return;

            Integer loc;
            int row, col;

            unHighlightAllDice();
            for(int i = 0; i < locations.size(); i++) {
                Square cell = (Square) locations.get(i);
                highlightDie(cell.getX(), cell.getY());
            }
            this.repaint(this.getVisibleRect());
        }

        /**
         * Highlight the specified die, given row and column.
         */
        public void highlightDie(int row, int column) {
            theDice[row][column].highlight();
        }

        /**
         * Unhighlight all dice.
         */
        public void unHighlightAllDice()  {
            for (int row = 0; row < theDice.length; row++)   {
                for (int col = 0; col < theDice[row].length; col++)  {
                    theDice[row][col].unHighlight();
                }
            }
            this.repaint(this.getVisibleRect());
        }

        // For displaying one Die on the board
        private class DiePanel extends JPanel {

            private static final long serialVersionUID = 7240204605994075384L;
	    private String face;
            private boolean isHighlighted;
            private JLabel faceLabel;

            private final Color DIECOLOR = new Color(230, 230, 230);
            private final Color FACECOLOR = new Color(3, 51, 217);

            private final Font FACEFONT = new Font("SansSerif", Font.PLAIN, 32);
            private final int DIESIZE = 80;

            public DiePanel()  {
                face = new String(""); 
                faceLabel = new JLabel("", SwingConstants.CENTER);
                setLayout(new BorderLayout());
                add(faceLabel, BorderLayout.CENTER);
                setBackground(BACKGROUNDCOLOR);
                setSize(DIESIZE, DIESIZE);
            }

            public Dimension getPreferredSize()  {
                return (new Dimension (DIESIZE+1, DIESIZE+1));
            }

            public Dimension getMinimumSize()  {
                return (getPreferredSize());
            }

            public void setFace( String chars )  {
                if (chars.length() > 1) {
                    face = chars.substring(0,1) + chars.substring(1).toLowerCase();
                }
                else {
                    face = chars;
                }
            }

            public String getFace() {
                return face;
            }

            /**
             * Draw one die including the letter centered in the middle of the die.
             * If highlight is true, we 
             * reverse the background and letter colors to highlight the die.
             */
            public void paintComponent(Graphics g)  {
                super.paintComponent(g);

                int centeredXOffset, centeredYOffset;
                // Draw the blank die
                g.setColor( (isHighlighted) ? FACECOLOR : DIECOLOR);
                g.fillRoundRect( 0, 0, DIESIZE, DIESIZE, DIESIZE/2, DIESIZE/2);

                // Outline the die with black
                g.setColor(Color.black);
                g.drawRoundRect( 0, 0, DIESIZE, DIESIZE, 
                        DIESIZE/2, DIESIZE/2);
                Graphics faceGraphics = faceLabel.getGraphics();
                faceGraphics.setColor( isHighlighted ? DIECOLOR : FACECOLOR);   
                Color myColor =  isHighlighted ? DIECOLOR : FACECOLOR;  
                faceLabel.setForeground(myColor);
                faceLabel.setFont(FACEFONT);
                faceLabel.setText(face);
            }

            public void unHighlight()  {
                isHighlighted = false;
            }

            public void highlight()  {
                isHighlighted = true;
            }
        }

    } // class BoggeBoardPanel


    class WordEntryField extends JPanel {

	private static final long serialVersionUID = -5401637544650199651L;
	private JTextField textField;
        private JButton myDoneButton;

        public WordEntryField() {
            //Set up for human player's Text Entry Field
            textField = new JTextField(30);
            //Add listener to text entry field
            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e) {
                    String w = textField.getText().toLowerCase();
                    if(w.length() == 0) {
                        humanArea.showError( "","empty word, ignored" );
                    } 
                    else if(!boggle.contains( w ) ) {
			if(boggle.getLexicon().contains(w)){
			    humanArea.showError(w, "not on board");
			}
			else {
			    humanArea.showError(w, "not in lexicon");
			}
		    }
                    else if (!boggle.addGuess(textField.getText().toLowerCase())) {
                        humanArea.showError(w,"already guessed");
                        clear();
		    }
		    else {
			List<Square> path = boggle.findPath(w);
			humanArea.showWord(w,path);
		    }
                }});
            myDoneButton = new JButton("DONE");
            myDoneButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gameOver();
                }
            });
            this.add(new JLabel("Enter word: "));
            this.add(textField);
            this.add(myDoneButton);
            setUnready();
        }

        public void clear() {
            textField.setText("");
        }

        public void setUnready() {
            clear();
            textField.setEditable(false);
            repaint(getVisibleRect());
            // attempt to give up focus to top-level frame
            BoggleFrame.this.requestFocus();
        }

        public void setReady() {
            textField.setEditable(true);
            textField.requestFocus();
        }
    } // class WordEntryField


    class BoggleProgress extends JProgressBar{

	private static final long serialVersionUID = 7274990827640558124L;
	private String myString;

        public BoggleProgress(int min, int max) {
            super(min,max);
        }
        public String getString() {
            if (mySeconds < myGameLength) {
                myString = ""+(myGameLength - mySeconds)+" seconds left";
            }
            else {
                myString = "game over";
            }
            setString(myString);
            return myString;
        }
    } // class BoggleProgress





    // Maintains name, score, and word list information for one player
    class PlayerView extends JPanel { // implements IPlayerView{

	private static final long serialVersionUID = -4905877814778673122L;

	private String playerName;

        private final Font ScoreFont = new Font("SansSerif", Font.PLAIN, 18);
        private final Font WordFont = new Font("Helvitica", Font.PLAIN, 14);
        private final Font LabelFont = new Font("Helvitica", Font.PLAIN, 14);

        private JPanel topPanel, wordPanel, namePanel, scorePanel, allWordsPanel;
        private ExpandableList myWordList;
	private ExpandableList allWordsList;
        private JLabel nameText, scoreText;

        public PlayerView(String player) {
            playerName = new String(player);

            // Set-Up Top of Score Area
            namePanel = new JPanel();
            nameText = new JLabel(player);
            nameText.setFont(ScoreFont);
            namePanel.setLayout( new BorderLayout() );
            namePanel.add(nameText, BorderLayout.CENTER);

            scorePanel = new JPanel();
            scoreText = new JLabel("  0");
            scoreText.setFont(ScoreFont);
            scorePanel.setLayout( new BorderLayout() );
            scorePanel.add(scoreText, BorderLayout.CENTER);

            topPanel = new JPanel();
            BoxLayout layout = new BoxLayout(topPanel,BoxLayout.LINE_AXIS);

            topPanel.setLayout(layout);
            topPanel.add(namePanel);
            topPanel.add(Box.createRigidArea(new Dimension(10,0)));
            topPanel.add(scorePanel);
            topPanel.add(Box.createRigidArea(new Dimension(10,0)));

            // Create bordering for top panel
            Border raisedBevel, loweredBevel, compound;

            raisedBevel = BorderFactory.createRaisedBevelBorder();
            loweredBevel = BorderFactory.createLoweredBevelBorder(); 
            compound = BorderFactory.createCompoundBorder(raisedBevel,
                    loweredBevel);
            topPanel.setBorder(compound);

            // Set up area to display word list
            wordPanel = new JPanel();
            Border etched = BorderFactory.createEtchedBorder();
            TitledBorder etchedTitle =
                BorderFactory.createTitledBorder(etched, "Word List");
            etchedTitle.setTitleJustification(TitledBorder.RIGHT);
            wordPanel.setBorder(etchedTitle);
            myWordList = new ExpandableList();
	    myWordList.setFont(WordFont);
            myWordList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String word = e.getActionCommand();
                    List<Square> list = boggle.findPath(word);
                    myBoardPanel.highlightDice(list);
                }          
            });
            wordPanel.add(new JScrollPane(myWordList,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

	    // Set up area to display all words
            allWordsPanel = new JPanel();
            Border etched2 = BorderFactory.createEtchedBorder();
            TitledBorder etchedTitle2 =
                BorderFactory.createTitledBorder(etched2, "All Words");
            etchedTitle2.setTitleJustification(TitledBorder.RIGHT);
            allWordsPanel.setBorder(etchedTitle2);
	    allWordsList = new ExpandableList();
	    allWordsList.setFont(WordFont);
            allWordsList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String word = e.getActionCommand();
                    List<Square> list = boggle.findPath(word);
                    myBoardPanel.highlightDice(list);
                }          
            });
            allWordsPanel.add(new JScrollPane(allWordsList,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

            setLayout(new BorderLayout(4,10));
            add(topPanel, BorderLayout.NORTH);
            add(wordPanel, BorderLayout.WEST);
	    add(allWordsPanel, BorderLayout.EAST);
        }

        public void showError(String word, String error) {
            JOptionPane.showMessageDialog(this,
                    word + ": " + error + "!!!",
                    error,
                    JOptionPane.ERROR_MESSAGE);
            wordEntryField.clear();
        }

        public void setReady()  {
            resetScore();      // zero out score  
            myWordList.clear();  // remove words from Panel/list
	    allWordsList.clear();
            repaint(getVisibleRect());
        }

	public void gameOver(){
	    for(String s : boggle.getAllWords())
		allWordsList.add(s);
            repaint(getVisibleRect());
	}

        public void setName(String newName)  {
            playerName = newName;
            nameText.setText(playerName);
            repaint();
        }

        public void showWord(String word, List<Square> letterLocations ) {
            myWordList.add(word);
            scoreText.setText("" + boggle.numFoundWords());
            scoreText.repaint(scoreText.getVisibleRect());
            myWordList.repaint(myWordList.getVisibleRect());
            myBoardPanel.highlightDice(letterLocations);
            wordEntryField.clear();      //clear the wordEntryField text
        }

        public void resetScore() {
            scoreText.setText(0+"");
            scoreText.repaint(scoreText.getVisibleRect());
        }

    } // class PlayerView

}

public class minSweeper2 {

	private static JFrame frame = new JFrame("Mine-Sweeper Game");

	private static final int EASY_GRID = 64, EASY_ROWS = 8, EASY_COLS = 8, EASY_MINES = 10, EASY_FLAG = 10;
	private static final int MEDIUM_GRID = 256, MEDIUM_ROWS = 16, MEDIUM_COLS = 16, MEDIUM_MINES = 40, MEDIUM_FLAG = 40;
	private static final int HARD_GRID = 512, HARD_ROWS = 16, HARD_COLS = 32, HARD_MINES = 99, HARD_FLAG = 99;
	private boolean[] flagged = new boolean[GRID_SIZE];
	private static int GRID_SIZE = 64;
	private static int ROWS = 8;
	private static int COLS = 8;
	private static int TOTAL_MINES = 10;
	private static int flagCount = 10;
	private static final JRadioButtonMenuItem easy = new JRadioButtonMenuItem("Easy");
	private static final JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
	private static final JRadioButtonMenuItem hard = new JRadioButtonMenuItem("Hard");

	public ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == easy) {
				GRID_SIZE = EASY_GRID;
				ROWS = EASY_ROWS;
				COLS = EASY_COLS;
				TOTAL_MINES = EASY_MINES;
				flagCount = EASY_FLAG;
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(300, 390);
			} else if (e.getSource() == medium) {
				GRID_SIZE = MEDIUM_GRID;
				ROWS = MEDIUM_ROWS;
				COLS = MEDIUM_COLS;
				TOTAL_MINES = MEDIUM_MINES;
				flagCount = MEDIUM_FLAG;
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(600, 600);
			} else {
				GRID_SIZE = HARD_GRID;
				ROWS = HARD_ROWS;
				COLS = HARD_COLS;
				TOTAL_MINES = HARD_MINES;
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(1150, 700);
				flagCount = HARD_FLAG;
			}
			resetAndRefreshGame();
		}
	};

	private JButton[] buttons = new JButton[GRID_SIZE];
	private boolean[] isMine = new boolean[GRID_SIZE];
	private boolean[] opened = new boolean[GRID_SIZE];
	private boolean gameStop = false;
	private boolean firstClick = true;
	private int minesPlaced = 0;
	private JButton smile = new JButton();
	private Random rnd = new Random();
	public static int value = 0;
	private ImageIcon grid = createImageIcon("/pictures/cover.png");
	private ImageIcon[] NUMBER_IMAGES = { createImageIcon("/pictures/0.png"), createImageIcon("/pictures/1.png"),
			createImageIcon("/pictures/2.png"), createImageIcon("/pictures/3.png"), createImageIcon("/pictures/4.png"),
			createImageIcon("/pictures/5.png"), createImageIcon("/pictures/6.png"), createImageIcon("/pictures/7.png"),
			createImageIcon("/pictures/8.png"), };

	private ImageIcon[] SMILE_IMAGES = { createImageIcon("/pictures/face-dead.png"),
			createImageIcon("/pictures/face-smile.png"), createImageIcon("/pictures/face-win.png"), };

	private ImageIcon[] BOMB_IMAGES = { createImageIcon("/pictures/mine-grey.png"),
			createImageIcon("/pictures/mine-misflagged.png"), createImageIcon("/pictures/mr.png"),
			createImageIcon("/pictures/flag.png"), };
	private JMenuBar menu = new JMenuBar();

	private JLabel h, t, o;
	private JLabel hundredsLBL, tensLBL, onesLBL;
	private static ImageIcon[] digits = new ImageIcon[10];
	private Timer tmr;
	private int count = 0;
	private JPanel game;

	public void start() {
		frame = new JFrame("Mine-Sweeper Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);

		JMenu myMenu = new JMenu("Difficulty");
		JMenu myOtherMenu = new JMenu("High Score");
		frame.setJMenuBar(menu);
		menu.add(myMenu);
		menu.add(myOtherMenu);

		ButtonGroup group = new ButtonGroup();
		group.add(easy);
		group.add(medium);
		group.add(hard);

		myMenu.add(easy);
		myMenu.add(medium);
		myMenu.add(hard);

		easy.addActionListener(listener);
		medium.addActionListener(listener);
		hard.addActionListener(listener);

		JPanel header = new JPanel();
		header.setLayout(new FlowLayout());

		h = dog(h);
		t = dog(t);
		o = dog(o);

		header.add(h);
		header.add(t);
		header.add(o);

		game = new JPanel();
		game.setLayout(new GridLayout(ROWS, COLS));
		flagCount = EASY_FLAG;
		for (int i = 0; i < GRID_SIZE; ++i) {
			final int spot = i;
			buttons[i] = new JButton(grid);
			buttons[i].setPreferredSize(new Dimension(35, 35));

			buttons[i].addActionListener(e -> {

				if (opened[spot]) {
					handleChordReveal(spot);
				} else {
					handleChoice(spot);
				}
			});
			flagMaker(spot);
			game.add(buttons[i]);
			nom(flagCount);
			ImageIcon imgIcon = (ImageIcon) buttons[i].getIcon();
			Image img = imgIcon.getImage();
			Image scaledImg = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
			ImageIcon largeIcon = new ImageIcon(scaledImg);
			buttons[i].setIcon(largeIcon);

		}

		tmr = new Timer(1000, e -> {
			setValue(count++);
			count %= 100;
		});

		smile.setIcon(SMILE_IMAGES[1]);
		smile.setPreferredSize(new Dimension(55, 55));
		smile.addActionListener(e -> {

			resetAndRefreshGame();
			count = 0;
			tmr.start();
		});

		header.add(smile);
		hundredsLBL = time(hundredsLBL);
		tensLBL = time(tensLBL);
		onesLBL = time(onesLBL);

		header.add(hundredsLBL);
		header.add(tensLBL);
		header.add(onesLBL);

		frame.getContentPane().add(game, BorderLayout.CENTER);
		frame.getContentPane().add(header, BorderLayout.NORTH);
		frame.pack();
		frame.setVisible(true);
		setValue(0);

	}

	public void resetAndRefreshGame() {
		gameStop = false;
		firstClick = true;
		count = 0;
		flagCount = TOTAL_MINES;

		game.removeAll();
		game.setLayout(new GridLayout(ROWS, COLS));

		buttons = new JButton[GRID_SIZE];
		isMine = new boolean[GRID_SIZE];
		opened = new boolean[GRID_SIZE];
		flagged = new boolean[GRID_SIZE];
		for (int i = 0; i < GRID_SIZE; i++) {
			final int spot = i;
			buttons[i] = new JButton(grid);
			buttons[i].setPreferredSize(new Dimension(55, 55));
			buttons[i].addActionListener(e -> {
				if (opened[spot]) {
					handleChordReveal(spot);
				} else {
					handleChoice(spot);
				}
			});
			flagMaker(spot);
			game.add(buttons[i]);
		}

		game.revalidate();
		game.repaint();
		tmr.start();
		smile.setIcon(SMILE_IMAGES[1]);
		setValue(0);

		nom(flagCount);
	}

	private void handleChoice(int spot) {
		if (gameStop || opened[spot] || flagged[spot])
			return;
		if (firstClick) {
			firstClick = false;
			addMines(spot);
			tmr.start();
		}

		if (isMine[spot]) {
			buttons[spot].setIcon(BOMB_IMAGES[2]);
			revealAllMines(spot);
			hasLost();
		} else {
			int countMines = countAdjacentMines(spot);
			buttons[spot].setIcon(NUMBER_IMAGES[countMines]);

			if (countMines > 0 && checkFlaggedAroundNumber(spot, countMines)) {
				revealSurroundingTiles(spot);
			} else if (countMines == 0) {
				revealEmptyTiles(spot);
			}
			opened[spot] = true;
		}

		hasWon();
	}

	private void handleChordReveal(int spot) {
		if (!opened[spot])
			return;

		int countMines = countAdjacentMines(spot);
		if (checkFlaggedAroundNumber(spot, countMines)) {
			revealSurroundingTiles(spot);
		}
	}

	private boolean checkFlaggedAroundNumber(int spot, int countMines) {
		if (!opened[spot])
			return false;
		int rowInd = spot / COLS;
		int colInd = spot % COLS;
		int flaggedCount = 0;

		for (int rc = -1; rc <= 1; rc++) {
			for (int cc = -1; cc <= 1; cc++) {
				if (rc == 0 && cc == 0)
					continue;

				int rowVal = rowInd + rc;
				int colVal = colInd + cc;
				if (rowVal >= 0 && rowVal < ROWS && colVal >= 0 && colVal < COLS) {
					int adjacentSpot = rowVal * COLS + colVal;
					if (isMine[adjacentSpot]) {
						hasLost();
						revealAllMines(adjacentSpot);
					}
					if (flagged[adjacentSpot]) {
						flaggedCount++;
					}
				}
			}
		}

		return flaggedCount == countMines;
	}

	private void revealEmptyTiles(int spot) {

		// Base cases
		if ((spot < 0 || spot >= ROWS * COLS || opened[spot] || isMine[spot])) {
			return;
		}

		if (flagged[spot]) {
			return;
		}
		int adjacentMines = countAdjacentMines(spot);
		opened[spot] = true;
		buttons[spot].setIcon(NUMBER_IMAGES[adjacentMines]);
		if (adjacentMines > 0)
			return;

		int row = spot / COLS;
		int col = spot % COLS;

		// Recursive way
		for (int r = -1; r <= 1; r++) {
			for (int c = -1; c <= 1; c++) {
				if (r == 0 && c == 0)
					continue;

				int newRow = row + r;
				int newCol = col + c;
				int newSpot = newRow * COLS + newCol;

				if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
					revealEmptyTiles(newSpot);
				}
			}
		}
	}

	private void revealSurroundingTiles(int spot) {
		int rowInd = spot / COLS;
		int colInd = spot % COLS;

		for (int rc = -1; rc <= 1; rc++) {
			for (int cc = -1; cc <= 1; cc++) {
				if (rc == 0 && cc == 0)
					continue;

				int rowVal = rowInd + rc;
				int colVal = colInd + cc;

				if (rowVal >= 0 && rowVal < ROWS && colVal >= 0 && colVal < COLS) {
					int adjacentSpot = rowVal * COLS + colVal;

					if (!opened[adjacentSpot] && !isMine[adjacentSpot] && !flagged[adjacentSpot]) {
						int countMines = countAdjacentMines(adjacentSpot);
						buttons[adjacentSpot].setIcon(NUMBER_IMAGES[countMines]);

						if (countMines == 0) {
							revealEmptyTiles(adjacentSpot);
						}
						opened[adjacentSpot] = true;
					}
				}
			}
		}
	}

	private void addMines(int firstClickSpot) {

		Arrays.fill(isMine, false);
		minesPlaced = 0;

		while (minesPlaced < TOTAL_MINES) {
			int spot = rnd.nextInt(GRID_SIZE);
			if (spot != firstClickSpot && !isMine[spot]) {
				isMine[spot] = true;
				minesPlaced++;
			}
		}
	}

	private int countAdjacentMines(int spot) {
		int rowInd = spot / COLS;
		int colInd = spot % COLS;
		int count = 0;

		for (int rc = -1; rc <= 1; rc++) {
			for (int cc = -1; cc <= 1; cc++) {
				if (rc == 0 && cc == 0)
					continue;

				int rowVal = rowInd + rc;
				int colVal = colInd + cc;

				if (rowVal >= 0 && rowVal < ROWS && colVal >= 0 && colVal < COLS) {
					int adjacentSpot = rowVal * COLS + colVal;
					if (isMine[adjacentSpot])
						count++;
				}
			}
		}

		return count;
	}

	private void revealAllMines(int spot) {
		for (int i = 0; i < GRID_SIZE; i++) {
			if (isMine[i]) {
				buttons[spot].setIcon(BOMB_IMAGES[2]);
				if (isMine[i] && buttons[i].getIcon() == BOMB_IMAGES[3]) {
					buttons[i].setIcon(BOMB_IMAGES[3]);
					continue;
				}
				buttons[i].setIcon(BOMB_IMAGES[0]);
			} else if (buttons[i].getIcon() == BOMB_IMAGES[3]) {
				buttons[i].setIcon(BOMB_IMAGES[1]);
			}
		}
	}

	private void hasLost() {
		smile.setIcon(SMILE_IMAGES[0]);
		gameStop = true;
		tmr.stop();
		setValue(0);

	}

	private void hasWon() {
		for (int i = 0; i < GRID_SIZE; i++) {
			if (!isMine[i] && !opened[i]) {
				return;
			}
		}
		smile.setIcon(SMILE_IMAGES[2]);
		gameStop = true;
		tmr.stop();
		setValue(0);

	}

	private void setValue(int n) {
		int value = n % 1000;
		if (value < 0)
			value *= -1;

		int hundreds = value / 100;
		int tens = (value % 100) / 10;
		int ones = value % 10;

		hundredsLBL.setIcon(digits[hundreds]);
		tensLBL.setIcon(digits[tens]);
		onesLBL.setIcon(digits[ones]);
	}

	private JLabel time(JLabel digitLBL) {
		for (int i = 0; i < 10; i++) {
			digits[i] = createImageIcon("/pictures2/" + i + ".png");
			Image img = digits[i].getImage();
			Image large = img.getScaledInstance(26, 46, Image.SCALE_SMOOTH);
			digits[i] = new ImageIcon(large);
		}
		return new JLabel(digits[0]);
	}

	private static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = minSweeper2.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void flagMaker(int spot) {
		if (gameStop || opened[spot])
			return;
		buttons[spot].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (!opened[spot]) {
						if (!flagged[spot] && flagCount > 0) {
							buttons[spot].setIcon(BOMB_IMAGES[3]);
							flagged[spot] = true;
							flagCount--;
						} else if (flagged[spot]) {
							buttons[spot].setIcon(grid);
							flagged[spot] = false;
							flagCount++;
						}
						nom(flagCount);
					}
				}
			}
		});
	}

	private static JLabel dog(JLabel digitLBL) {
		for (int i = 0; i < 10; i++) {
			digits[i] = createImageIcon("/pictures2/" + i + ".png");
			Image img = digits[i].getImage();
			Image large = img.getScaledInstance(26, 46, Image.SCALE_SMOOTH);
			digits[i] = new ImageIcon(large);
		}
		return new JLabel(digits[0]);
	}

	public void nom(int n) {
		if (flagCount < 0) {
			return;
		}
		flagCount = n;
		value = n % 1000;
		if (n < 0)
			value *= -1;

		int hundreds = value / 100;
		int tens = (value % 100) / 10;
		int ones = value % 10;

		h.setIcon(digits[hundreds]);
		t.setIcon(digits[tens]);
		o.setIcon(digits[ones]);
	}
}

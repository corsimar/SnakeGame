import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Main {
	
	///////////////////////////////// GAME VARIABLES /////////////////////////////////
	int snakeW = 32, snakeH = 32, colliderW = 32, colliderH = 32;
	int snakeIncrease = 15;
	public int direction = -1; // 0 - up, 1 - right, 2 - down, 3 - left
	int score = 0;
	int snakeStep = 2;
	int snakeSize = 0;
	int gameSpeed = 15;
	int wallThickness = 5;
	
	int nextX, nextY;
	int lastX, lastY;
	int lastDir;
	
	boolean appleSpawned = false;
	
	JLabel collider;
	///////////////////////////////// GAME VARIABLES /////////////////////////////////
	
	///////////////////////////////// USER INTERFACE /////////////////////////////////
	private JFrame frame;
	int panelW, panelH;
	
	BufferedImage snakeUpBuffered = null, snakeRightBuffered = null, snakeDownBuffered = null, snakeLeftBuffered = null;
	BufferedImage snakeBodyBuffered = null;
	BufferedImage appleBuffered = null;
	Image snakeUpImage, snakeRightImage, snakeDownImage, snakeLeftImage;
	Image snakeBodyImage;
	Image appleImage;
	
	JLabel snake;
	JLabel snakeTail;
	ArrayList<JLabel> snakeBody = new ArrayList<JLabel>();
	JLabel apple;
	
	JLabel wallUp, wallRight, wallDown, wallLeft;
	
	JLabel scoreLabel;
	JButton startButton;
	
	JLabel loseGameLabel;
	///////////////////////////////// USER INTERFACE /////////////////////////////////
	
	///////////////////////////////// OTHERS /////////////////////////////////
	Timer timer;
	Timer gameTimer;
	///////////////////////////////// OTHERS /////////////////////////////////
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	///////////////////////////////// GAME FUNCTIONS /////////////////////////////////
	
	void StartGame() {
		snake.setBounds(panelW / 2 - snake.getBounds().width / 2, panelH / 2 - snake.getBounds().height, snake.getBounds().width, snake.getBounds().height);
		snake.setVisible(true);
		
		collider.setVisible(true);
		SetSnakeColliders();
		
		direction = 0;
		gameTimer = new Timer();
		gameTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(snakeStep > 0) {
					lastX = snake.getBounds().x;
					lastY = snake.getBounds().y;
				
					nextX = lastX; nextY = lastY;
			
					lastDir = direction;
				
					if(direction == 0)
						nextY -= snakeStep;
					else if(direction == 1)
						nextX += snakeStep;
					else if(direction == 2)	
						nextY += snakeStep;
					else if(direction == 3)
						nextX -= snakeStep;
				
					snake.setBounds(nextX, nextY, snake.getBounds().width, snake.getBounds().height);
					SetSnakeColliders();
				
					if(collider.getBounds().y < 6) 
						LoseGame();
					if(collider.getBounds().x > panelW - 52) 
						LoseGame();
					if(collider.getBounds().y > panelH - 76) 
						LoseGame();
					if(collider.getBounds().x < 6) 
						LoseGame();
				
					int auxX, auxY;
					for(JLabel part: snakeBody) {
						auxX = lastX; auxY = lastY;
						lastX = part.getBounds().x;
						lastY = part.getBounds().y;
					
						part.setBounds(auxX, auxY, part.getBounds().width, part.getBounds().height);
						if(Distance(collider, part) <= 30 && IsComponentInFront(collider, part, direction))
							LoseGame();
					}
		
					frame.repaint();
				
					if(!appleSpawned) {
						SpawnApple();
					}
					if(Distance(snake, apple) <= 30) {
						AddScore();
						IncreaseSnake();
						apple.setVisible(false);
						appleSpawned = false;
					}
				}
				else
					gameTimer.cancel();
			}
		}, 0, gameSpeed);
	}
	
	void SetSnakeColliders() {
		if(direction == 0)
			collider.setBounds(snake.getBounds().x, snake.getBounds().y - colliderH / 8, colliderW, colliderH);
		else if(direction == 1)
			collider.setBounds(snake.getBounds().x + colliderW / 8, snake.getBounds().y, colliderW, colliderH);
		else if(direction == 2)
			collider.setBounds(snake.getBounds().x, snake.getBounds().y + colliderH / 8, colliderW, colliderH);
		else if(direction == 3)
			collider.setBounds(snake.getBounds().x - colliderW / 8, snake.getBounds().y, colliderW, colliderH);
	}
	
	void SpawnApple() {
		Random rand = new Random();
		int randX = rand.nextInt(apple.getBounds().width, panelW - apple.getBounds().width - 50);
		int randY = rand.nextInt(apple.getBounds().height, panelH - apple.getBounds().height - 50);
		apple.setBounds(randX, randY, apple.getBounds().width, apple.getBounds().height);
		appleSpawned = true;
		apple.setVisible(true);
	}
	
	int Distance(Component c1, Component c2) {
		return (int) Math.sqrt((c2.getBounds().x - c1.getBounds().x) * (c2.getBounds().x - c1.getBounds().x) + (c2.getBounds().y - c1.getBounds().y) * (c2.getBounds().y - c1.getBounds().y));
	}
	
	boolean IsComponentInFront(Component c1, Component c2, int direction) {
		if(direction == 0 && c1.getBounds().y > c2.getBounds().y)
			return true;
		else if(direction == 1 && c1.getBounds().x < c2.getBounds().x)
			return true;
		else if(direction == 2 && c1.getBounds().y < c2.getBounds().y)
			return true;
		else if(direction == 3 && c1.getBounds().x > c2.getBounds().x)
			return true;
		else
			return false;
	}
	
	void IncreaseSnake() {
		for(int i = 0; i < snakeIncrease; i++) {
			JLabel part = new JLabel();
			part.setText("");
			part.setIcon(new ImageIcon(snakeBodyImage));	
			part.setVisible(true);
			
			JLabel parent;
			if(snakeBody.size() == 0)
				parent = snake;
			else
				parent = snakeBody.get(snakeBody.size() - 1);
			
			part.setBounds(parent.getBounds().x, parent.getBounds().y, snakeW, snakeH);
				
			snakeBody.add(part);
			frame.getContentPane().add(part);
		}
		frame.repaint();
	}
	
	void AddScore() {
		score += 10;
		scoreLabel.setText("Score: " + score);
	}
	
	void LoseGame() {
		snakeStep = 0;
		loseGameLabel.setVisible(true);
	}
	
	///////////////////////////////// GAME FUNCTIONS /////////////////////////////////
	
	void LoadImages() {
		try {
			snakeUpBuffered = ImageIO.read(Main.class.getResource("/res/SnakeUp.png"));
			snakeRightBuffered = ImageIO.read(Main.class.getResource("/res/SnakeRight.png"));
			snakeLeftBuffered = ImageIO.read(Main.class.getResource("/res/SnakeLeft.png"));
			snakeDownBuffered = ImageIO.read(Main.class.getResource("/res/SnakeDown.png"));
			snakeBodyBuffered = ImageIO.read(Main.class.getResource("/res/SnakeBody.png"));
			appleBuffered = ImageIO.read(Main.class.getResource("/res/Apple.png"));
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	void ResizeUI() {
		panelW = frame.getBounds().width;
		panelH = frame.getBounds().height;
		
		startButton.setBounds(panelW / 2 - 150, panelH / 2 - 75, 300, 150);
		scoreLabel.setBounds(panelW - 174, 0, 150, 50);
		wallUp.setBounds(0, 0, panelW, wallThickness);
		wallRight.setBounds(panelW - wallThickness - 15, 0, wallThickness, panelH);
		wallDown.setBounds(0, panelH - wallThickness - 39, panelW, wallThickness);
		wallLeft.setBounds(0, 0, wallThickness, panelH);
		loseGameLabel.setBounds(0, 0, panelW, panelH);
	}
	
	public Main() {
		initialize();
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(175, 255, 191));
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LoadImages();
		
		JButton auxBtn = new JButton();
		auxBtn.setBounds(0, 0, 0, 0);
		auxBtn.setVisible(true);
		auxBtn.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(snakeStep > 0) {
					if((e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_UP) && (snakeBody.size() == 0 || (direction != 0 && direction != 2))) {
						direction = 0;
						snake.setIcon(new ImageIcon(snakeUpImage));
					}
					if((e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT) && (snakeBody.size() == 0 || (direction != 1 && direction != 3))) {
						direction = 1;
						snake.setIcon(new ImageIcon(snakeRightImage));
					}
					if((e.getKeyChar() == 's' || e.getKeyCode() == KeyEvent.VK_DOWN) && (snakeBody.size() == 0 || (direction != 0 && direction != 2))) {
						direction = 2;
						snake.setIcon(new ImageIcon(snakeDownImage));
					}
					if((e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT) && (snakeBody.size() == 0 || (direction != 1 && direction != 3))) {
						direction = 3;
						snake.setIcon(new ImageIcon(snakeLeftImage));
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		frame.getContentPane().add(auxBtn);
		
		snake = new JLabel("");
		snake.setBounds(100, 100, snakeW, snakeW);
		snakeUpImage = snakeUpBuffered.getScaledInstance(snake.getBounds().width, snake.getBounds().height, Image.SCALE_SMOOTH);
		snakeRightImage = snakeRightBuffered.getScaledInstance(snake.getBounds().width, snake.getBounds().height, Image.SCALE_SMOOTH);
		snakeDownImage = snakeDownBuffered.getScaledInstance(snake.getBounds().width, snake.getBounds().height, Image.SCALE_SMOOTH);
		snakeLeftImage = snakeLeftBuffered.getScaledInstance(snake.getBounds().width, snake.getBounds().height, Image.SCALE_SMOOTH);
		snakeBodyImage = snakeBodyBuffered.getScaledInstance(snake.getBounds().width, snake.getBounds().height, Image.SCALE_SMOOTH);
		snake.setIcon(new ImageIcon(snakeUpImage));
		snake.setVisible(false);
		frame.getContentPane().add(snake);
		
		collider = new JLabel("");
		collider.setBounds(0, 0, colliderW, colliderH);
		collider.setIcon(new ImageIcon(snakeBodyImage));
		collider.setVisible(false);
		frame.getContentPane().add(collider);
		
		apple = new JLabel("");
		apple.setBounds(0, 0, snakeW, snakeH);
		appleImage = appleBuffered.getScaledInstance(apple.getBounds().width, apple.getBounds().height, Image.SCALE_SMOOTH);
		apple.setIcon(new ImageIcon(appleImage));
		apple.setVisible(false);
		frame.getContentPane().add(apple);
		
		startButton = new JButton();
		startButton.setForeground(new Color(11, 122, 33));
		startButton.setText("START GAME");
		startButton.setFont((new Font("Arial", Font.BOLD, 24)));
		startButton.setHorizontalTextPosition(SwingConstants.CENTER);
		startButton.setBounds(0, 0, 300, 150);
		startButton.setContentAreaFilled(false);
		startButton.setBorder(null);
		startButton.setVisible(true);
		startButton.setIcon(new ImageIcon(Main.class.getResource("/res/StartBtn.png")));
		startButton.setPressedIcon(new ImageIcon(Main.class.getResource("/res/StartBtnPressed.png")));
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startButton.setVisible(false);
				StartGame();
			}
		});
		frame.getContentPane().add(startButton);
		
		scoreLabel = new JLabel("Score: " + score);
		scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scoreLabel.setForeground(new Color(11, 122, 33));
		scoreLabel.setFont((new Font("Arial", Font.BOLD, 24)));
		scoreLabel.setVisible(true);
		frame.getContentPane().add(scoreLabel);
		
		wallUp = new JLabel("");
		wallUp.setBounds(0, 0, 0, 0);
		wallUp.setBackground(new Color(11, 122, 33));
		wallUp.setOpaque(true);
		wallUp.setVisible(true);
		frame.getContentPane().add(wallUp);
		
		wallRight = new JLabel("");
		wallRight.setBounds(0, 0, 0, 0);
		wallRight.setBackground(new Color(11, 122, 33));
		wallRight.setOpaque(true);
		wallRight.setVisible(true);
		frame.getContentPane().add(wallRight);
		
		wallDown = new JLabel("");
		wallDown.setBounds(0, 0, 0, 0);
		wallDown.setBackground(new Color(11, 122, 33));
		wallDown.setOpaque(true);
		wallDown.setVisible(true);
		frame.getContentPane().add(wallDown);
		
		wallLeft = new JLabel("");
		wallLeft.setBounds(0, 0, 0, 0);
		wallLeft.setBackground(new Color(11, 122, 33));
		wallLeft.setOpaque(true);
		wallLeft.setVisible(true);
		frame.getContentPane().add(wallLeft);
		
		loseGameLabel = new JLabel("You lost!");
		loseGameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loseGameLabel.setBounds(0, 0, panelW, panelH);
		loseGameLabel.setForeground(new Color(11, 122, 33));
		loseGameLabel.setFont((new Font("Arial", Font.BOLD, 36)));
		loseGameLabel.setVisible(false);
		frame.getContentPane().add(loseGameLabel);
		
		frame.getContentPane().setLayout(null);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() { 
				ResizeUI();
			}
		}, 0, 500);
	}
}

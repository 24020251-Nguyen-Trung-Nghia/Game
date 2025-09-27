import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Random;

class Position {
    int x, y;
    Rectangle bounds;

    public Position(int x, int y) {
        setPosition(x, y);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (bounds != null) {
            bounds.setLocation(this.x, this.y);
        }
    }

    public void setBoundSize(int width, int height) {
        bounds = new Rectangle(x, y, width, height);
    }
}

public class Game extends JPanel implements Runnable, KeyListener {
    final int WIDTH = 520;
    final int HEIGHT = 450;

    boolean isRunning;
    Thread thread;

    BufferedImage view;
    Graphics g;

    BufferedImage background, paddle, ball, block, gameOver;
    int n = 0;
    Position[] blocksPosition;
    Position paddlePosition;

    boolean right, left;
    int paddleX, paddleY;
    int ballX, ballY;
    int ballDx, ballDy;

    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            isRunning = true;
            thread.start();
        }
    }

    public static void main(String[] args) {
        JFrame w = new JFrame("Arkanoid");
        w.setResizable(false);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.add(new Game());
        w.pack();
        w.setLocationRelativeTo(null);
        w.setVisible(true);
    }

    public void start() {
        try {
            view = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            g = (Graphics2D) view.createGraphics();

            background = ImageIO.read(getClass().getResource("/images/background.jpg"));
            paddle = ImageIO.read(getClass().getResource("/images/paddle.png"));
            ball = ImageIO.read(getClass().getResource("/images/ball.png"));
            block = ImageIO.read(getClass().getResource("/images/block.png"));
            gameOver = ImageIO.read(getClass().getResource("/images/gameover.png"));

            blocksPosition = new Position[100];
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    blocksPosition[n] = new Position(i * 43, j * 20);
                    blocksPosition[n].setBoundSize(block.getWidth(), block.getHeight());
                    n++;
                }
            }

            paddleX = (WIDTH / 2) - (paddle.getWidth() / 2);
            paddleY = (HEIGHT - paddle.getHeight());
            paddlePosition = new Position(paddleX, paddleY);
            paddlePosition.setBoundSize( paddle.getWidth(), paddle.getHeight());

            ballX = (WIDTH / 2) - (ball.getWidth() / 2);
            ballY = (HEIGHT - paddle.getHeight());

            ballDx = -(new Random().nextInt(4) % 4 + 3);
            ballDy = -5;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        ballX += ballDx;
        for( int i =0; i< n; i++){
            if(new Rectangle((ballX + 3), (ballY+ 3), 6, 6).intersects(blocksPosition[i].bounds)){
                blocksPosition[i].setPosition(-100, 0);
                ballDx =- ballDx;
            }
        }

        ballY += ballDy;
        for( int i =0; i< n; i++){
            if(new Rectangle((ballX + 3),ballY +3, 6, 6 ).intersects(blocksPosition[i].bounds)){
                blocksPosition[i].setPosition(-100, 0);
                ballDy =- ballDy;
            }
        }

        if (ballX < 0 || ballX > WIDTH - ball.getWidth()) {
            ballDx = -ballDx;
        }
        if (ballY < 0 || ballY > HEIGHT - ball.getHeight()) {
            ballDy = -ballDy;
        }
        if(ballY > HEIGHT - ball.getHeight()){
            isRunning = false;
        }

        if (right) {
            paddleX += 6;
        }
        if (left) {
            paddleX -= 6;
        }

        if (paddleX >= WIDTH - paddle.getWidth()) {
            paddleX = WIDTH - paddle.getWidth();
        }
        if (paddleX <= 0) {
            paddleX = 0;
        }

        if(new Rectangle(ballX, ballY, 12, 12).intersects(paddlePosition.bounds)){
            ballDy = -(new Random().nextInt(4) % 4 + 3);
        }
        paddlePosition.setPosition(paddleX, paddleY);
    }

    public void draw() {
        g.drawImage(background, 0, 0, null);
        g.drawImage(ball, ballX, ballY, ball.getWidth(), ball.getHeight(), null);
        g.drawImage(paddle, paddleX, paddleY, paddle.getWidth(), paddle.getHeight(), null);

        for (int i = 0; i < n; i++) {
            g.drawImage(block, blocksPosition[i].x, blocksPosition[i].y, block.getWidth(), block.getHeight(), null);
        }

        if(!isRunning) {
            g.drawImage(gameOver,
                    (WIDTH/2) -(gameOver.getWidth()/2),
                    (HEIGHT/2) -  (gameOver.getHeight()/2),
                    gameOver.getWidth(),
                    gameOver.getHeight(),
                    null);
        }

        Graphics g2 = getGraphics();
        g2.drawImage(view, 0, 0, WIDTH, HEIGHT, null);
        g2.dispose();
    }

    @Override
    public void run() {
        requestFocus();
        start();
        while (isRunning) {
            update();
            draw();
            try {
                Thread.sleep(1000 / 70);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
        }
    }
}

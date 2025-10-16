package com.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main (Application)
 * ├── Game State Management
 * ├── Rendering System (3 Canvas layers)
 * ├── Physics Engine (Collision Detection)
 * ├── Audio System
 * ├── Input Handling
 * └── Entity System (Sprites)
 */

public class Main extends Application {
    private final Images images = new Images();
    private final AutoClips autoClips = new AutoClips();
    private final LoadImages loadImages = new LoadImages();
    private final LoadSounds loadSounds = new LoadSounds();
    private final DrawBackground drawBackground = new DrawBackground(this);
    private final HitTest hitTest = new HitTest(this);
    private final SetupBlocks setupBlocks = new SetupBlocks(this);
    private final GameOver gameOver = new GameOver(this);
    private final StartLevel startLevel = new StartLevel(this);
    private final UpdateAndDraw updateAndDraw = new UpdateAndDraw(this);
    private final DrawBorder drawBorder = new DrawBorder(this);


    // ************ GAME STATE VARIABLES ************************
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Biến điều khiển game
    private boolean running;           // Game đang chạy?


    private Instant gameStartTime;     // Thời điểm bắt đầu game
    private long levelStartTime;       // Thời điểm bắt đầu level

    private AnimationTimer timer;
    private long lastTimerCall;
    private long lastAnimCall;
    private long lastBonusAnimCall;
    private long lastEnemyUpdateCall;
    private long lastOneSecondCheck;
    private Canvas bkgCanvas;
    private GraphicsContext bkgCtx;
    private Canvas canvas;
    private GraphicsContext ctx;
    private Canvas brdrCanvas;
    private GraphicsContext brdrCtx;


    public GraphicsContext getBrdrCtx() {
        return brdrCtx;
    }

    // Các đối tượng game
    private Paddle paddle;                    // Paddle (ván đỡ)
    private List<Ball> balls;                // Danh sách bóng
    private List<Block> blocks;              // Danh sách block
    private List<BonusBlock> bonusBlocks;    // Danh sách power-up
    private List<Enemy> enemies;             // Danh sách kẻ địch

    private List<Torpedo> torpedoes;

    // Thống kê game
    private int noOfLifes = 3;        // Số mạng
    public long score = 0;           // Điểm số
    private long highscore;           // Điểm cao nhất
    private int level = 1;            // Level hiện tại

    private EnumDefinitions.PaddleState paddleState;

    // Trạng thái đặc biệt
    private boolean stickyPaddle = false;     // Bóng có dính vào paddle không?
    private boolean nextLevelDoorOpen = false;// Cửa qua level có mở không?
    public boolean movingPaddleOut = false;  // Paddle đang đi ra cửa?

    public int animateInc;
    private List<Blink> blinks;
    private double ballSpeed;
    private boolean readyLevelVisible;
    private int paddleResetCounter;
    private int speedResetCounter;
    private int nextLevelDoorCounter;
    private double nextLevelDoorAlpha;
    private OpenDoor openDoor;
    private boolean showStartHint;

    private int silverBlockMaxHits;
    public int blockCounter;

    private List<Explosion> explosions;
    private Pos enemySpawnPosition;
    private double topLeftDoorAlpha;
    private double topRightDoorAlpha;
    private FIFO<Block> blockFifo;
    private EventHandler<MouseEvent> mouseHandler;


    // ***************** Getter *****************************
    public HitTest getHitTest() {
        return hitTest;
    }

    public SetupBlocks getSetupBlocks() {
        return setupBlocks;
    }

    public GameOver getGameOver() {
        return gameOver;
    }

    public StartLevel getStartLevel() {
        return startLevel;
    }

    public EventHandler<MouseEvent> getMouseHandler() {
        return mouseHandler;
    }

    public boolean isRunning() {
        return running;
    }

    public GraphicsContext getCtx() {
        return ctx;
    }

    public GraphicsContext getBkgCtx() {
        return bkgCtx;
    }

    public Images getImages() {
        return images;
    }

    public long getHighscore() {
        return highscore;
    }

    public boolean isShowStartHint() {
        return showStartHint;
    }

    public AutoClips getAutoClips() {
        return autoClips;
    }

    public LoadImages getLoadImages() {
        return loadImages;
    }

    public LoadSounds getLoadSounds() {
        return loadSounds;
    }

    public DrawBackground getDrawBackground() {
        return drawBackground;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public Instant getGameStartTime() {
        return gameStartTime;
    }

    public long getLevelStartTime() {
        return levelStartTime;
    }

    public AnimationTimer getTimer() {
        return timer;
    }

    public long getLastTimerCall() {
        return lastTimerCall;
    }

    public long getLastAnimCall() {
        return lastAnimCall;
    }

    public long getLastBonusAnimCall() {
        return lastBonusAnimCall;
    }

    public long getLastEnemyUpdateCall() {
        return lastEnemyUpdateCall;
    }

    public long getLastOneSecondCheck() {
        return lastOneSecondCheck;
    }

    public Canvas getBkgCanvas() {
        return bkgCanvas;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Canvas getBrdrCanvas() {
        return brdrCanvas;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<BonusBlock> getBonusBlocks() {
        return bonusBlocks;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Torpedo> getTorpedoes() {
        return torpedoes;
    }

    public int getNoOfLifes() {
        return noOfLifes;
    }

    public long getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public EnumDefinitions.PaddleState getPaddleState() {
        return paddleState;
    }

    public boolean isStickyPaddle() {
        return stickyPaddle;
    }

    public boolean isNextLevelDoorOpen() {
        return nextLevelDoorOpen;
    }

    public boolean isMovingPaddleOut() {
        return movingPaddleOut;
    }

    public int getAnimateInc() {
        return animateInc;
    }

    public List<Blink> getBlinks() {
        return blinks;
    }

    public double getBallSpeed() {
        return ballSpeed;
    }

    public boolean isReadyLevelVisible() {
        return readyLevelVisible;
    }

    public int getPaddleResetCounter() {
        return paddleResetCounter;
    }

    public int getSpeedResetCounter() {
        return speedResetCounter;
    }

    public int getNextLevelDoorCounter() {
        return nextLevelDoorCounter;
    }

    public double getNextLevelDoorAlpha() {
        return nextLevelDoorAlpha;
    }

    public OpenDoor getOpenDoor() {
        return openDoor;
    }

    public int getSilverBlockMaxHits() {
        return silverBlockMaxHits;
    }

    public int getBlockCounter() {
        return blockCounter;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public Pos getEnemySpawnPosition() {
        return enemySpawnPosition;
    }

    public double getTopLeftDoorAlpha() {
        return topLeftDoorAlpha;
    }

    public double getTopRightDoorAlpha() {
        return topRightDoorAlpha;
    }

    public FIFO<Block> getBlockFifo() {
        return blockFifo;
    }

    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setGameStartTime(Instant gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public void setLevelStartTime(long levelStartTime) {
        this.levelStartTime = levelStartTime;
    }

    public void setTimer(AnimationTimer timer) {
        this.timer = timer;
    }

    public void setLastTimerCall(long lastTimerCall) {
        this.lastTimerCall = lastTimerCall;
    }

    public void setLastAnimCall(long lastAnimCall) {
        this.lastAnimCall = lastAnimCall;
    }

    public void setLastBonusAnimCall(long lastBonusAnimCall) {
        this.lastBonusAnimCall = lastBonusAnimCall;
    }

    public void setLastEnemyUpdateCall(long lastEnemyUpdateCall) {
        this.lastEnemyUpdateCall = lastEnemyUpdateCall;
    }

    public void setLastOneSecondCheck(long lastOneSecondCheck) {
        this.lastOneSecondCheck = lastOneSecondCheck;
    }

    public void setBkgCanvas(Canvas bkgCanvas) {
        this.bkgCanvas = bkgCanvas;
    }

    public void setBkgCtx(GraphicsContext bkgCtx) {
        this.bkgCtx = bkgCtx;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setCtx(GraphicsContext ctx) {
        this.ctx = ctx;
    }

    public void setBrdrCanvas(Canvas brdrCanvas) {
        this.brdrCanvas = brdrCanvas;
    }

    public void setBrdrCtx(GraphicsContext brdrCtx) {
        this.brdrCtx = brdrCtx;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void setBonusBlocks(List<BonusBlock> bonusBlocks) {
        this.bonusBlocks = bonusBlocks;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setTorpedoes(List<Torpedo> torpedoes) {
        this.torpedoes = torpedoes;
    }

    public void setNoOfLifes(int noOfLifes) {
        this.noOfLifes = noOfLifes;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setHighscore(long highscore) {
        this.highscore = highscore;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPaddleState(EnumDefinitions.PaddleState paddleState) {
        this.paddleState = paddleState;
    }

    public void setStickyPaddle(boolean stickyPaddle) {
        this.stickyPaddle = stickyPaddle;
    }

    public void setNextLevelDoorOpen(boolean nextLevelDoorOpen) {
        this.nextLevelDoorOpen = nextLevelDoorOpen;
    }

    public void setMovingPaddleOut(boolean movingPaddleOut) {
        this.movingPaddleOut = movingPaddleOut;
    }

    public void setAnimateInc(int animateInc) {
        this.animateInc = animateInc;
    }

    public void setBlinks(List<Blink> blinks) {
        this.blinks = blinks;
    }

    public void setBallSpeed(double ballSpeed) {
        this.ballSpeed = ballSpeed;
    }

    public void setReadyLevelVisible(boolean readyLevelVisible) {
        this.readyLevelVisible = readyLevelVisible;
    }

    public void setPaddleResetCounter(int paddleResetCounter) {
        this.paddleResetCounter = paddleResetCounter;
    }

    public void setSpeedResetCounter(int speedResetCounter) {
        this.speedResetCounter = speedResetCounter;
    }

    public void setNextLevelDoorCounter(int nextLevelDoorCounter) {
        this.nextLevelDoorCounter = nextLevelDoorCounter;
    }

    public void setNextLevelDoorAlpha(double nextLevelDoorAlpha) {
        this.nextLevelDoorAlpha = nextLevelDoorAlpha;
    }

    public void setOpenDoor(OpenDoor openDoor) {
        this.openDoor = openDoor;
    }

    public void setShowStartHint(boolean showStartHint) {
        this.showStartHint = showStartHint;
    }

    public void setSilverBlockMaxHits(int silverBlockMaxHits) {
        this.silverBlockMaxHits = silverBlockMaxHits;
    }

    public void setBlockCounter(int blockCounter) {
        this.blockCounter = blockCounter;
    }

    public void setExplosions(List<Explosion> explosions) {
        this.explosions = explosions;
    }

    public void setEnemySpawnPosition(Pos enemySpawnPosition) {
        this.enemySpawnPosition = enemySpawnPosition;
    }

    public void setTopLeftDoorAlpha(double topLeftDoorAlpha) {
        this.topLeftDoorAlpha = topLeftDoorAlpha;
    }

    public void setTopRightDoorAlpha(double topRightDoorAlpha) {
        this.topRightDoorAlpha = topRightDoorAlpha;
    }

    public void setBlockFifo(FIFO<Block> blockFifo) {
        this.blockFifo = blockFifo;
    }

    public void setMouseHandler(EventHandler<MouseEvent> mouseHandler) {
        this.mouseHandler = mouseHandler;
    }


    // ******************** Methods *******************************************
    @Override
    public void init() {
        running = false;
        paddleState = EnumDefinitions.PaddleState.STANDARD;
        highscore = PropertyManager.INSTANCE.getLong(Constants.HIGHSCORE_KEY, 0);
        level = 1;
        blinks = new ArrayList<>();
        ballSpeed = GameConstants.BALL_SPEED;
        readyLevelVisible = false;
        paddleResetCounter = 0;
        speedResetCounter = 0;
        nextLevelDoorCounter = 0;
        nextLevelDoorOpen = false;
        nextLevelDoorAlpha = 1.0;
        movingPaddleOut = false;
        openDoor = new OpenDoor(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565);
        showStartHint = false;
        silverBlockMaxHits = 2;
        blockCounter = 0;
        stickyPaddle = false;
        enemySpawnPosition = Pos.CENTER;
        topLeftDoorAlpha = 1.0;
        topRightDoorAlpha = 1.0;
        blockFifo = new FIFO<>(9);

        lastOneSecondCheck = System.nanoTime();
        lastTimerCall = System.nanoTime();
        lastAnimCall = System.nanoTime();
        lastBonusAnimCall = System.nanoTime();
        lastEnemyUpdateCall = System.nanoTime();

        // ***************** Game Loop ******************************************
        timer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                if (running) {
                    // 1 second check
                    if (now > lastOneSecondCheck + 1_000_000_000) {
                        // After 15 seconds in the level enemies will be spawned every 10 seconds if less 5 enemies in the game
                        long levelPlayTime = Instant.now().getEpochSecond() - levelStartTime;
                        if (levelPlayTime > 15 && enemies.size() < 5 && levelPlayTime % 10 == 0) {
                            enemySpawnPosition = GameConstants.RND.nextBoolean() ? Pos.TOP_LEFT : Pos.TOP_RIGHT;
                            switch (enemySpawnPosition) {
                                case TOP_LEFT -> topLeftDoorAlpha = 0.99;
                                case TOP_RIGHT -> topRightDoorAlpha = 0.99;
                            }
                        }

                        if (paddleResetCounter > 0) {
                            paddleResetCounter--;
                            if (paddleResetCounter == 0) {
                                paddleState = EnumDefinitions.PaddleState.STANDARD;
                            }
                        }
                        if (speedResetCounter > 0) {
                            speedResetCounter--;
                            if (speedResetCounter == 0) {
                                ballSpeed = GameConstants.BALL_SPEED;
                            }
                        }
                        if (nextLevelDoorCounter > 0) {
                            nextLevelDoorCounter--;
                            if (nextLevelDoorCounter == 0 && !movingPaddleOut) {
                                nextLevelDoorAlpha = 1.0;
                                nextLevelDoorOpen = false;
                                drawBorder.drawBorder();
                            }
                        }

                        lastOneSecondCheck = now;
                    }

                    // Animate bonus blocks and top doors
                    if (now > lastBonusAnimCall + 50_000_000) {
                        // Update bonus blocks
                        bonusBlocks.forEach(bonusBlock -> bonusBlock.update());

                        // Fade out top doors
                        if (topLeftDoorAlpha < 1) {
                            topLeftDoorAlpha -= 0.1;
                            if (topLeftDoorAlpha <= 0) {
                                spawnEnemy(Pos.TOP_LEFT);
                                topLeftDoorAlpha = 1;
                            }
                            drawBorder.drawBorder();
                        } else if (topRightDoorAlpha < 1) {
                            topRightDoorAlpha -= 0.1;
                            if (topRightDoorAlpha <= 0) {
                                spawnEnemy(Pos.TOP_RIGHT);
                                topRightDoorAlpha = 1;
                            }
                            drawBorder.drawBorder();
                        }
                        lastBonusAnimCall = now;
                    }

                    // Animate enemies
//                    if (now > lastEnemyUpdateCall + 100_000_000) {
//                        enemies.forEach(enemy -> enemy.update());
//                        explosions.forEach(explosion -> explosion.update());
//                        lastEnemyUpdateCall = now;
//                    }

                    // Animation of paddle glow
                    if (now > lastAnimCall + 5_000_000) {
                        animateInc++;
                        lastAnimCall = now;
                    }

                    // Main loop
                    if (now > lastTimerCall) {
                        hitTest.hitTests();
                        updateAndDraw.updateAndDraw();
                        if (nextLevelDoorOpen) {
                            drawBorder.drawBorder();
                        }
                        lastTimerCall = now;
                    }

                    if (movingPaddleOut) {
                        paddle.x += 1;
                        paddle.bounds.set(paddle.x, paddle.y, paddleState.width, paddle.height);
                        updateAndDraw.updateAndDraw();
                        if (paddle.x > GameConstants.WIDTH) {
                            level++;
                            if (level > Constants.LEVEL_MAP.size()) {
                                level = 1;
                            }
                            score += 10_000;
                            startLevel.startLevel(level);
                        }
                    }
                } else {
                    if (!showStartHint && Instant.now().getEpochSecond() - gameStartTime.getEpochSecond() > 8) {
                        showStartHint = true;
                        startScreen();
                    }
                }
            }
        };

        ////////// Setup canvas nodes //////////
        // Layer 1: Background
        bkgCanvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        bkgCtx = bkgCanvas.getGraphicsContext2D();

        // Layer 2: Game objects
        canvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        ctx = canvas.getGraphicsContext2D();

        // Layer 3: Border/UI
        brdrCanvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        brdrCtx = brdrCanvas.getGraphicsContext2D();
        brdrCanvas.setMouseTransparent(true);


        //////////// Sử lý chuột /////////////
        // Attach mouse dragging to paddle
        mouseHandler = e -> {
            EventType<MouseEvent> type = (EventType<MouseEvent>) e.getEventType();
            if (MouseEvent.MOUSE_DRAGGED.equals(type)) {
                double x = e.getSceneX() - paddleState.width * 0.5;
                if (x + paddleState.width > GameConstants.WIDTH - GameConstants.INSET) {
                    if (nextLevelDoorOpen && !movingPaddleOut) {
                        movingPaddleOut = true;
                    }
                    x = GameConstants.WIDTH - GameConstants.INSET - paddleState.width;
                }
                if (x < GameConstants.INSET) {
                    x = GameConstants.INSET;
                }
                paddle.x = x;
                paddle.bounds.set(x, paddle.y, paddleState.width, paddle.height);
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);


        // Load all images
        loadImages.loadImages(images, this);

        // Load all sounds
        loadSounds(autoClips, this);



        // Initialize paddles
        paddle = new Paddle(this);

        // Initialize level
        balls = new CopyOnWriteArrayList<>();
        blocks = new CopyOnWriteArrayList<>();
        bonusBlocks = new CopyOnWriteArrayList<>();
        enemies = new CopyOnWriteArrayList<>();
        explosions = new CopyOnWriteArrayList<>();
        torpedoes = new CopyOnWriteArrayList<>();
        noOfLifes = 3;
        score = 0;
    }


    // ****************** Keyboard Controls *****************************
    @Override
    public void start(final Stage stage) {
        gameStartTime = Instant.now();

        final StackPane pane = new StackPane(bkgCanvas, canvas, brdrCanvas);
        final Scene scene = new Scene(pane, GameConstants.WIDTH, GameConstants.HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (running) {
                if (movingPaddleOut) {
                    return;
                }
                switch (e.getCode()) {
                    case RIGHT, D -> movePaddleRight();
                    case LEFT, A -> movePaddleLeft();
                    case SPACE -> {
                        final long activeBalls = balls.stream().filter(ball -> ball.active).count();
                        if (activeBalls > 0) {
                            if (EnumDefinitions.PaddleState.LASER == paddleState) {
                                fire(paddle.bounds.centerX);
                            }
                        } else {
                            stickyPaddle = false;
                            balls.forEach(ball -> {
                                ball.active = true;
                                ball.bornTimestamp = Instant.now().getEpochSecond();
                            });
                        }
                    }
                }
            } else {
                // Block for the first 8 seconds to give it some time to play the game start song
                if (Instant.now().getEpochSecond() - gameStartTime.getEpochSecond() > 8) {
                    level = 1;
                    startLevel.startLevel(level);
                }
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case RIGHT -> stopPaddle();
                case LEFT -> stopPaddle();
            }
        });

        stage.setTitle("Arkanoid");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        playSound(autoClips.gameStartSnd);

        startScreen();

        timer.start();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }


    // ******************** Helper methods ************************************
    private void loadImages(Images images, Main main) {
        loadImages.loadImages(images, main);
    }

    private void loadSounds(AutoClips autoClips, Main main) {
        loadSounds.loadSounds(autoClips, main);
    }


    // ******************** Game control **************************************
    private void movePaddleRight() {
        paddle.vX = GameConstants.PADDLE_SPEED;
    }

    private void movePaddleLeft() {
        paddle.vX = -GameConstants.PADDLE_SPEED;
    }

    private void stopPaddle() {
        paddle.vX = 0;
    }

    private void fire(final double x) {
        if (torpedoes.size() > 0) {
            return;
        }
        torpedoes.add(new Torpedo(images.torpedoImg, x, GameConstants.HEIGHT - 50));
        playSound(autoClips.laserSnd);
    }


    // Play audio clips
    public void playSound(final AudioClip audioClip) {
        //audioClip.play();
    }


    // Spawn enemy
    private void spawnEnemy(final Pos position) {
        switch (position) {
            case TOP_LEFT ->
                    enemies.add(new Enemy(this,100 + images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
            case TOP_RIGHT ->
                    enemies.add(new Enemy(this,GameConstants.WIDTH - 100 - images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
        }
    }


    // Re-Spawn Ball
    public void spawnBall() {
        if (balls.size() > 0) {
            return;
        }
        balls.add(new Ball(this, images.ballImg, paddle.bounds.centerX, paddle.bounds.minY - images.ballImg.getHeight() * 0.5 - 1, (GameConstants.RND.nextDouble() * (2 * ballSpeed) - ballSpeed)));
    }


    // Start Screen
    public void startScreen() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        drawBackground.drawBackground(1);
        drawBorder.drawBorder();
    }


    // Start Level
    private void startLevel(final int level) {
        startLevel.startLevel(level);
    }


    // Game Over
    private void gameOver() {

        gameOver.gameOver();
    }


    // Setup blocks for given level
    private void setupBlocks(final int level) {
        setupBlocks.setupBlocks(level);
    }


    // ******************** HitTests ******************************************
    private void hitTests() {
        // torpedo hits block or enemy

        // paddle hits bonus blocks
        hitTest.hitTests();
    }


    // ******************** Redraw ********************************************
    public void drawBackground(final int level) {
        drawBackground.drawBackground(level);
    }

    public void updateAndDraw() {
        updateAndDraw.updateAndDraw();
    }

    public void drawBorder() {
        drawBorder.drawBorder();
    }


    // ******************** Start *********************************************
    public static void main(String[] args) {
        launch(args);
    }
}
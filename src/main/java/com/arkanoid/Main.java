package com.arkanoid;

import com.arkanoid.Constants.BlockType;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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


    // ************ GAME STATE VARIABLES ************************
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Biến điều khiển game
    private boolean running;           // Game đang chạy?
    public boolean isRunning() {
        return running;
    }

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

    public Images getImages() {
        return images;
    }

    public GraphicsContext getBrdrCtx() {
        return brdrCtx;
    }

    public GraphicsContext getCtx() {
        return ctx;
    }

    public GraphicsContext getBkgCtx() {
        return bkgCtx;
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
    private long score = 0;           // Điểm số
    private long highscore;           // Điểm cao nhất
    public long getHighscore() {
        return highscore;
    }
    private int level = 1;            // Level hiện tại

    private EnumDefinitions.PaddleState paddleState;

    // Trạng thái đặc biệt
    private boolean stickyPaddle = false;     // Bóng có dính vào paddle không?
    private boolean nextLevelDoorOpen = false;// Cửa qua level có mở không?
    private boolean movingPaddleOut = false;  // Paddle đang đi ra cửa?

    private int animateInc;
    private List<Blink> blinks;
    private double ballSpeed;
    private boolean readyLevelVisible;
    private int paddleResetCounter;
    private int speedResetCounter;
    private int nextLevelDoorCounter;
    private double nextLevelDoorAlpha;
    private OpenDoor openDoor;
    private boolean showStartHint;
    public boolean isShowStartHint() {
        return showStartHint;
    }

    private int silverBlockMaxHits;
    private int blockCounter;

    private List<Explosion> explosions;
    private Pos enemySpawnPosition;
    private double topLeftDoorAlpha;
    private double topRightDoorAlpha;
    private FIFO<Block> blockFifo;
    private EventHandler<MouseEvent> mouseHandler;


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

        // ***************** Game Loop ******************************************
        lastOneSecondCheck = System.nanoTime();
        lastTimerCall = System.nanoTime();
        lastAnimCall = System.nanoTime();
        lastBonusAnimCall = System.nanoTime();
        lastEnemyUpdateCall = System.nanoTime();

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
                                drawBorder();
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
                            drawBorder();
                        } else if (topRightDoorAlpha < 1) {
                            topRightDoorAlpha -= 0.1;
                            if (topRightDoorAlpha <= 0) {
                                spawnEnemy(Pos.TOP_RIGHT);
                                topRightDoorAlpha = 1;
                            }
                            drawBorder();
                        }
                        lastBonusAnimCall = now;
                    }

                    // Animate enemies
                    if (now > lastEnemyUpdateCall + 100_000_000) {
                        enemies.forEach(enemy -> enemy.update());
                        explosions.forEach(explosion -> explosion.update());
                        lastEnemyUpdateCall = now;
                    }

                    // Animation of paddle glow
                    if (now > lastAnimCall + 5_000_000) {
                        animateInc++;
                        lastAnimCall = now;
                    }

                    // Main loop
                    if (now > lastTimerCall) {
                        hitTests();
                        updateAndDraw();
                        if (nextLevelDoorOpen) {
                            drawBorder();
                        }
                        lastTimerCall = now;
                    }

                    if (movingPaddleOut) {
                        paddle.x += 1;
                        paddle.bounds.set(paddle.x, paddle.y, paddleState.width, paddle.height);
                        updateAndDraw();
                        if (paddle.x > GameConstants.WIDTH) {
                            level++;
                            if (level > Constants.LEVEL_MAP.size()) {
                                level = 1;
                            }
                            score += 10_000;
                            startLevel(level);
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
        paddle = new Paddle();

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
                    startLevel(level);
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
    private void playSound(final AudioClip audioClip) {
        audioClip.play();
    }


    // Spawn enemy
    private void spawnEnemy(final Pos position) {
        switch (position) {
            case TOP_LEFT ->
                    enemies.add(new Enemy(100 + images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
            case TOP_RIGHT ->
                    enemies.add(new Enemy(GameConstants.WIDTH - 100 - images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
        }
    }


    // Re-Spawn Ball
    private void spawnBall() {
        if (balls.size() > 0) {
            return;
        }
        balls.add(new Ball(images.ballImg, paddle.bounds.centerX, paddle.bounds.minY - images.ballImg.getHeight() * 0.5 - 1, (GameConstants.RND.nextDouble() * (2 * ballSpeed) - ballSpeed)));
    }


    // Start Screen
    private void startScreen() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        drawBackground.drawBackground(1);
        drawBorder();
    }


    // Start Level
    private void startLevel(final int level) {
        levelStartTime = Instant.now().getEpochSecond();
        blockCounter = 0;
        nextLevelDoorAlpha = 1.0;
        nextLevelDoorOpen = false;
        movingPaddleOut = false;
        paddle.countX = 0;
        paddle.countY = 0;
        animateInc = 0;
        paddle.x = GameConstants.WIDTH * 0.5 - paddleState.width * 0.5;
        paddle.bounds.minX = paddle.x - paddle.width * 0.5;
        readyLevelVisible = true;
        playSound(autoClips.startLevelSnd);
        setupBlocks(level);
        bonusBlocks.clear();
        balls.clear();
        enemies.clear();
        explosions.clear();
        spawnBall();
        if (!running) {
            running = true;
        }
        drawBackground.drawBackground(level);
        drawBorder();
        updateAndDraw();
        executor.schedule(() -> {
            readyLevelVisible = false;
        }, 2, TimeUnit.SECONDS);
    }


    // Game Over
    private void gameOver() {
        executor.schedule(() -> startScreen(), 5, TimeUnit.SECONDS);

        playSound(autoClips.gameOverSnd);

        running = false;
        balls.clear();
        torpedoes.clear();

        updateAndDraw();

        if (score > highscore) {
            PropertyManager.INSTANCE.setLong(Constants.HIGHSCORE_KEY, score);
            highscore = score;
        }
        PropertyManager.INSTANCE.storeProperties();
        score = 0;
        noOfLifes = 3;
        paddleState = EnumDefinitions.PaddleState.STANDARD;
    }


    // Setup blocks for given level
    private void setupBlocks(final int level) {
        blocks.clear();
        BlockType[][] level2 = Constants.LEVEL_MAP.get(level);
        silverBlockMaxHits = level % 8 == 0 ? silverBlockMaxHits + 1 : silverBlockMaxHits;
        for (int iy = 0; iy < level2.length; iy++) {
            for (int ix = 0; ix < level2[iy].length; ix++) {
                Block block;
                final BlockType blockType = level2[iy][ix];
                switch (blockType) {
                    case GOLD ->
                            block = new Block(images.goldBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 0, blockType.maxHits, blockType);
                    case GRAY ->
                            block = new Block(images.grayBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 0, silverBlockMaxHits, blockType);
                    case WHIT ->
                            block = new Block(images.whiteBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 10, blockType.maxHits, blockType);
                    case ORNG ->
                            block = new Block(images.orangeBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 60, blockType.maxHits, blockType);
                    case CYAN ->
                            block = new Block(images.cyanBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 70, blockType.maxHits, blockType);
                    case LIME ->
                            block = new Block(images.limeBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 80, blockType.maxHits, blockType);
                    case RUBY ->
                            block = new Block(images.redBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 90, blockType.maxHits, blockType);
                    case BLUE ->
                            block = new Block(images.blueBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 100, blockType.maxHits, blockType);
                    case MGNT ->
                            block = new Block(images.magentaBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 110, blockType.maxHits, blockType);
                    case YLLW ->
                            block = new Block(images.yellowBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 120, blockType.maxHits, blockType);
                    default -> block = null;
                }
                if (null == block) {
                    continue;
                }
                blocks.add(block);
            }
        }
    }


    // ******************** HitTests ******************************************
    private void hitTests() {
        // torpedo hits block or enemy
        for (Torpedo torpedo : torpedoes) {
            if (EnumDefinitions.PaddleState.LASER == paddleState) {
                for (Block block : blocks) {
                    if (block.bounds.intersects(torpedo.bounds)) {
                        block.hits++;
                        if (block.hits == block.maxHits) {
                            block.toBeRemoved = true;
                            score += block.value;
                        }
                        torpedo.toBeRemoved = true;
                        break;
                    }
                }
                for (Enemy enemy : enemies) {
                    if (enemy.bounds.intersects(torpedo.bounds)) {
                        enemy.toBeRemoved = true;
                        torpedo.toBeRemoved = true;
                        explosions.add(new Explosion(enemy.x, enemy.y, enemy.vX, enemy.vY, 1.0));
                        playSound(autoClips.explosionSnd);
                        break;
                    }
                }
            }
        }

        // paddle hits bonus blocks
        for (BonusBlock bonusBlock : bonusBlocks) {
            if (bonusBlock.bounds.intersects(paddle.bounds)) {
                bonusBlock.toBeRemoved = true;
                switch (bonusBlock.bonusType) {
                    case BONUS_C -> {
                        stickyPaddle = true;
                    }
                    case BONUS_D -> {
                        if (balls.size() == 1) {
                            Ball ball = balls.get(0);
                            double vX1 = (Math.sin(Math.toRadians(10)) * ball.vX);
                            double vY1 = (Math.cos(Math.toRadians(10)) * ball.vY);
                            double vX2 = (Math.sin(Math.toRadians(-10)) * ball.vX);
                            double vY2 = (Math.cos(Math.toRadians(-10)) * ball.vY);
                            balls.add(new Ball(images.ballImg, ball.x, ball.y, vX1, vY1));
                            balls.add(new Ball(images.ballImg, ball.x, ball.y, vX2, vY2));
                        }
                    }
                    case BONUS_F -> {
                        paddleResetCounter = 30;
                        paddleState = EnumDefinitions.PaddleState.WIDE;
                    }
                    case BONUS_L -> {
                        paddleResetCounter = 30;
                        paddleState = EnumDefinitions.PaddleState.LASER;
                    }
                    case BONUS_S -> {
                        speedResetCounter = 30;
                        ballSpeed = GameConstants.BALL_SPEED * 0.5;
                    }
                    case BONUS_B -> {
                        nextLevelDoorCounter = 5;
                        nextLevelDoorOpen = true;
                    }
                    case BONUS_P -> {
                        noOfLifes = Utils.clamp(2, 5, noOfLifes + 1);
                    }
                }
            }
        }
    }


    // ******************** Redraw ********************************************
    private void drawBackground(final int level) {

        drawBackground.drawBackground(level);
    }

    private void updateAndDraw() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        // Draw Torpedos
        for (Torpedo torpedo : torpedoes) {
            torpedo.update();
            ctx.drawImage(torpedo.image, torpedo.bounds.x, torpedo.bounds.y);
        }

        // Draw shadows
        ctx.save();
        ctx.translate(10, 10);

        // Draw block shadows
        blocks.forEach(block -> ctx.drawImage(images.blockShadowImg, block.x, block.y));

        // Draw bonus block shadows
        bonusBlocks.forEach(bonusBlock -> ctx.drawImage(images.bonusBlockShadowImg, bonusBlock.x, bonusBlock.y));

        // Draw paddle shadow
        if (noOfLifes > 0) {
            switch (paddleState) {
                case STANDARD -> ctx.drawImage(images.paddleStdShadowImg, paddle.bounds.minX, paddle.bounds.minY);
                case WIDE -> ctx.drawImage(images.paddleWideShadowImg, paddle.bounds.minX, paddle.bounds.minY);
                case LASER -> ctx.drawImage(images.paddleGunShadowImg, paddle.bounds.minX, paddle.bounds.minY);
            }
        }

        // Draw ball shadow
        balls.forEach(ball -> ctx.drawImage(images.ballShadowImg, ball.bounds.minX, ball.bounds.minY));
        ctx.restore();

        // Draw blocks
        blocks.forEach(block -> ctx.drawImage(block.image, block.x, block.y));

        // Draw bonus blocks
        bonusBlocks.forEach(bonusBlock -> {
            switch (bonusBlock.bonusType) {
                case BONUS_C ->
                        ctx.drawImage(images.bonusBlockCMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_F ->
                        ctx.drawImage(images.bonusBlockFMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_D ->
                        ctx.drawImage(images.bonusBlockDMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_L ->
                        ctx.drawImage(images.bonusBlockLMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_S ->
                        ctx.drawImage(images.bonusBlockSMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_B ->
                        ctx.drawImage(images.bonusBlockBMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_P ->
                        ctx.drawImage(images.bonusBlockPMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
            }
        });

        // Draw blinks
        blinks.forEach(blink -> ctx.drawImage(images.blinkMapImg, blink.countX * GameConstants.BLOCK_WIDTH, blink.countY * GameConstants.BLOCK_HEIGHT, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT, blink.x, blink.y, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT));

        // Draw enemies
        enemies.forEach(enemy -> {
            switch (enemy.enemyType) {
                case MOLECULE ->
                        ctx.drawImage(images.moleculeMapImg, enemy.countX * GameConstants.ENEMY_WIDTH, enemy.countY * GameConstants.ENEMY_HEIGHT, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, enemy.x, enemy.y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT);
            }
        });

        // Draw explosions
        explosions.forEach(explosion -> ctx.drawImage(images.explosionMapImg, explosion.countX * GameConstants.EXPLOSION_WIDTH, explosion.countY * GameConstants.EXPLOSION_HEIGHT, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT, explosion.x, explosion.y, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT));

        // Draw ball(s)
        balls.forEach(ball -> {
            ball.update();
            ctx.drawImage(images.ballImg, ball.bounds.x, ball.bounds.y);
        });

        // Draw paddle
        if (noOfLifes > 0) {
            if (!movingPaddleOut) {
                paddle.update();
            }
            switch (paddleState) {
                case STANDARD ->
                        ctx.drawImage(images.paddleMapStdImg, paddle.countX * paddleState.width, paddle.countY * paddleState.height, paddleState.width, paddleState.height, paddle.x, paddle.y, paddleState.width, paddleState.height);
                case WIDE ->
                        ctx.drawImage(images.paddleMapWideImg, paddle.countX * paddleState.width, paddle.countY * paddleState.height, paddleState.width, paddleState.height, paddle.x, paddle.y, paddleState.width, paddleState.height);
                case LASER ->
                        ctx.drawImage(images.paddleMapGunImg, paddle.countX * paddleState.width, paddle.countY * paddleState.height, paddleState.width, paddleState.height, paddle.x, paddle.y, paddleState.width, paddleState.height);
            }
        } else {
            ctx.setFill(GameConstants.TEXT_GRAY);
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.fillText("GAME OVER", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.75);
        }

        // Draw score
        ctx.setFill(Color.WHITE);
        ctx.setFont(GameConstants.SCORE_FONT);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.TOP);
        ctx.fillText(Long.toString(score), 140, 30);

        ctx.setFill(GameConstants.HIGH_SCORE_RED);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.fillText("HIGH SCORE", GameConstants.WIDTH * 0.5, 0);
        ctx.setFill(GameConstants.SCORE_WHITE);
        ctx.fillText(Long.toString(score > highscore ? score : highscore), GameConstants.WIDTH * 0.5, 30);

        // Draw no of lifes
        for (int i = 0; i < noOfLifes; i++) {
            ctx.drawImage(images.paddleMiniImg, GameConstants.INSET + 2 + 42 * i, GameConstants.HEIGHT - 30);
        }

        // Draw ready level label
        if (readyLevelVisible) {
            ctx.setFill(GameConstants.TEXT_GRAY);
            ctx.setFont(GameConstants.SCORE_FONT);
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.fillText("ROUND " + level, GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65);
            ctx.fillText("READY", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65 + 2 * GameConstants.SCORE_FONT.getSize());
        }

        // Remove sprites
        balls.removeIf(ball -> ball.toBeRemoved);
        blinks.removeIf(blink -> blink.toBeRemoved);
        blocks.removeIf(block -> block.toBeRemoved);
        bonusBlocks.removeIf(bonusBlock -> bonusBlock.toBeRemoved);
        enemies.removeIf(enemy -> enemy.toBeRemoved);
        explosions.removeIf(explosion -> explosion.toBeRemoved);
        torpedoes.removeIf(torpedo -> torpedo.toBeRemoved);

        // Respawn ball and check for game over
        if (!movingPaddleOut && balls.isEmpty() && noOfLifes > 0) {
            noOfLifes -= 1;
            if (noOfLifes == 0) {
                gameOver();
            }
            spawnBall();
        }

        // Update blinks
        blinks.forEach(blink -> blink.update());

        // Check for level completeness
        if (blocks.isEmpty() || blocks.stream().filter(block -> block.maxHits > -1).count() == 0) {
            level++;
            if (level > Constants.LEVEL_MAP.size()) {
                level = 1;
            }
            startLevel(level);
        }
    }

    private void drawBorder() {
        brdrCtx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        if (running) {
            // Draw top border
            brdrCtx.setFill(images.pipePatternFill);
            brdrCtx.fillRect(17, 68, 83, 17);
            brdrCtx.fillRect(100 + images.topDoorImg.getWidth(), 68, GameConstants.WIDTH - 200 - 2 * images.topDoorImg.getWidth(), 17);
            brdrCtx.fillRect(GameConstants.WIDTH - 100, 68, 83, 17);

            // Draw vertical border
            brdrCtx.setFill(images.borderPatternFill);
            brdrCtx.fillRect(0, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT - GameConstants.UPPER_INSET);
            if (nextLevelDoorOpen) {
                brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, 563);
                brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565 + images.borderPartVerticalImg.getHeight(), 20, 100);
            } else {
                brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT);
            }

            // Draw border corners
            brdrCtx.drawImage(images.ulCornerImg, 2.5, 67.5);
            brdrCtx.drawImage(images.urCornerImg, GameConstants.WIDTH - images.urCornerImg.getWidth() - 2.5, 67.5);

            // Draw next level door
            if (nextLevelDoorOpen) {
                for (int i = 0; i < 6; i++) {
                    brdrCtx.drawImage(images.borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    if (i < 5) {
                        brdrCtx.drawImage(images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                    }
                }
                if (nextLevelDoorAlpha > 0.01) {
                    nextLevelDoorAlpha -= 0.01;
                }
                brdrCtx.save();
                brdrCtx.setGlobalAlpha(nextLevelDoorAlpha);
                brdrCtx.drawImage(images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565);
                brdrCtx.restore();

                openDoor.update();
                ctx.drawImage(images.openDoorMapImg, openDoor.countX * 20, 0, 20, 71, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565, 20, 71);
            } else {
                for (int i = 0; i < 6; i++) {
                    brdrCtx.drawImage(images.borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    brdrCtx.drawImage(images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                }
            }

            // Draw upper doors
            brdrCtx.save();
            brdrCtx.setGlobalAlpha(topLeftDoorAlpha);
            brdrCtx.drawImage(images.topDoorImg, 100, 65);
            brdrCtx.setGlobalAlpha(topRightDoorAlpha);
            brdrCtx.drawImage(images.topDoorImg, GameConstants.WIDTH - 100 - images.topDoorImg.getWidth(), 65);
            brdrCtx.restore();
        }
    }


    private class Paddle extends AnimatedSprite {

        // ******************** Constructors **************************************
        public Paddle() {
            super(GameConstants.WIDTH * 0.5 - paddleState.width * 0.5, GameConstants.HEIGHT - GameConstants.PADDLE_OFFSET_Y, 0, 0, 8, 8, 1.0);
            init();
        }


        // ******************** Methods *******************************************
        @Override
        protected void init() {
            this.width = paddleState.width;
            this.height = paddleState.height;
            this.size = height;
            this.radius = size * 0.5;
            this.bounds.set(this.x, this.y, paddleState.width, this.height);
        }

        @Override
        public void respawn() {
            this.x = GameConstants.WIDTH * 0.5;
            this.bounds.set(this.x, this.y, paddleState.width, this.height);
            this.vX = 0;
            this.vY = 0;
        }

        @Override
        public void update() {
            x += vX;

            if (x + paddleState.width > GameConstants.WIDTH - GameConstants.INSET) {
                if (nextLevelDoorOpen && !movingPaddleOut) {
                    movingPaddleOut = true;
                }
                x = GameConstants.WIDTH - GameConstants.INSET - paddleState.width;
            }
            if (x < GameConstants.INSET) {
                x = GameConstants.INSET;
            }
            this.bounds.set(this.x, this.y, paddleState.width, this.height);

            countX = animateInc;
            if (countX == maxFrameX) {
                countX = 0;
                animateInc = 0;
                countY++;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class Blink extends AnimatedSprite {

        // ******************** Constructors **************************************
        public Blink(final double x, final double y) {
            super(x, y, 0, 0, 8, 3, 1.0);
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class Block extends Sprite {
        public int value;
        public int hits;
        public final int maxHits;
        public final BlockType blockType;


        // ******************** Constructors **************************************
        public Block(final Image image, final double x, final double y, final int value, final int maxHits, final BlockType blockType) {
            super(image);
            this.x = x;
            this.y = y;
            this.value = value;
            this.maxHits = maxHits;
            this.blockType = blockType;
            this.hits = 0;
            this.width = GameConstants.BLOCK_WIDTH;
            this.height = GameConstants.BLOCK_HEIGHT;
            this.bounds.set(x, y, width, height);
            init();
        }


        // ******************** Methods *******************************************
        @Override
        protected void init() {
            size = width > height ? width : height;
            radius = size * 0.5;

            // Velocity
            vX = 0;
            vY = 0;
        }

        @Override
        public void update() {
        }

        @Override
        public String toString() {
            return new StringBuilder().append(blockType).append("(").append(x).append(",").append(y).append(")").toString();
        }

        public boolean equals(final Block other) {
            return this.blockType == other.blockType &&
                    this.x == other.x &&
                    this.y == other.y &&
                    this.value == other.value;
        }
    }

    private class BonusBlock extends AnimatedSprite {
        public EnumDefinitions.BonusType bonusType;


        // ******************** Constructors **************************************
        public BonusBlock(final double x, final double y, final EnumDefinitions.BonusType bonusType) {
            super(x, y, 0, GameConstants.BONUS_BLOCK_SPEED, 5, 4, 1.0);
            this.bonusType = bonusType;
            this.width = GameConstants.BLOCK_WIDTH;
            this.height = GameConstants.BLOCK_HEIGHT;
            this.bounds.set(x, y, width, height);
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            y += vY;
            if (y > GameConstants.HEIGHT) {
                toBeRemoved = true;
            }
            countX++;
            if (countX == maxFrameX) {
                countY++;
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
            this.bounds.set(this.x, this.y, this.width, this.height);
        }
    }

    private class Ball extends Sprite {
        public boolean active;
        public long bornTimestamp;


        // ******************** Constructors **************************************
        public Ball(final Image image, final double x, final double y, final double vX) {
            this(image, x, y, vX, false);
        }

        public Ball(final Image image, final double x, final double y, final double vX, final boolean active) {
            super(image, paddle.bounds.centerX, paddle.bounds.minY - image.getHeight() * 0.5 - GameConstants.BALL_SPEED - 1, 0, -ballSpeed);
            this.vX = vX;
            this.active = active;
            this.bornTimestamp = Instant.now().getEpochSecond();
        }

        private Ball(final Image image, final double x, final double y, final double vX, final double vY) {
            super(image, x, y, vX, vY);
            this.active = true;
            this.bornTimestamp = Instant.now().getEpochSecond();
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            if (!active) {
                this.x = paddle.bounds.centerX;
                this.y = paddle.bounds.minY - image.getHeight() * 0.5 - GameConstants.BALL_SPEED - 1;
            } else { // We need to check if the ball hits a block
                double x0 = x;       // (x0, y0) = initial coordinates, (x1, y1) = final coordinates in case there is no hit
                double y0 = y;
                double x1 = x0 + vX;
                double y1 = y0 + vY; // (x1, y1) may need a correction if the ball hits a block

                while (true) { // Several iterations are possible, because even the corrected (x1, y1) might hit another block
                    // Capturing final values for the lambda expression below
                    double fx0 = x0;
                    double fy0 = y0;
                    double fx1 = x1;
                    double fy1 = y1;
                    BallHit ballHit = Stream.concat(blocks.stream().map(b -> b.bounds),                              // Iterating over all block bounds
                                    Stream.concat(Arrays.stream(GameConstants.BORDER_BOUNDS), Stream.of(paddle.bounds)))         // together with the borders and paddle bounds (processed identically)
                            .map(bounds -> bounds.computeBallHit(fx0, fy0, fx1, fy1, radius))        // computing a possible ball hit with the bounds (returns null if no hits)
                            .filter(Objects::nonNull)                                                // removing non-hits
                            // If that trajectory (x0, y0) -> (x1, y1) hits several blocks, we keep the first hit block
                            .min(Comparator.comparingDouble(ballHit1 -> ballHit1.beforeHitDistance)) // first hit = min hit distance (x0, y0) -> (xHit, yHit)
                            .orElse(null);                                                     // returning null if no hit
                    if (ballHit == null) { // If there is no hit, (x1, y1) doesn't need correction
                        this.x = x1;
                        this.y = y1;
                        break; // breaking the loop
                    }
                    // We have a ball hit when reaching this code.
                    // => inverting vX or Vy if needed
                    if (ballHit.inverseVx) {
                        vX = -vX;
                    }
                    if (ballHit.inverseVy) {
                        vY = -vY;
                    }
                    // Preparing the next loop iteration by updating (x0, y0) -> (x1, y1) to (xHit, yHit) -> (correctedX, correctedY)
                    x0 = ballHit.xHit;
                    y0 = ballHit.yHit;
                    x1 = ballHit.correctedX;
                    y1 = ballHit.correctedY;
                    // But before looping, we manage some special case when hitting the paddle or the blocks
                    if (ballHit.hitBounds == paddle.bounds) {
                        Bounds pb = paddle.bounds;
                        if (stickyPaddle) {
                            this.x = pb.centerX;
                            this.y = pb.minY - image.getHeight() * 0.5 - GameConstants.BALL_SPEED - 1;
                            this.active = false;
                            break;
                        } else {
                            // Influence vX of ball if vX of paddle != 0
                            if (paddle.vX != 0) {
                                double speedXY = Math.sqrt(vX * vX + vY * vY);
                                double posX = (x1 - pb.centerX) / (pb.width * 0.5);
                                double speedX = speedXY * posX * GameConstants.BALL_VX_INFLUENCE;
                                vX = speedX;
                                vY = -Math.sqrt(speedXY * speedXY - speedX * speedX);
                                // Recalculating (x1, y1) from this new speed
                                x1 = x0 + vX;
                                y1 = y0 + vY;
                            } else { // angle correction due to paddle round corners
                                double pcr = pb.height;                     // paddle corner radius
                                boolean hitLeftCorner = x0 < pb.minX + pcr; // Note: x0 is the hit X at this point
                                boolean hitRightCorner = x0 > pb.maxX - pcr;
                                if (hitLeftCorner || hitRightCorner) {
                                    double distanceToCorner = hitLeftCorner ? pb.minX + pcr - x0 : x0 - pb.maxX + pcr;
                                    double cornerAngleRad = Math.acos(distanceToCorner * 0.85 / pcr); // Note: 0.85 factor is because the corner is not as sharp as a circle at the edge
                                    double minRad = Math.PI / 180 * 35;                               // To avoid the speed to be too horizontal
                                    double maxRad = Math.PI / 180 * 75;                               // Better to keep the hit speed
                                    // Low angle correction => minRad
                                    if (Double.isNaN(cornerAngleRad) || cornerAngleRad < minRad) {
                                        cornerAngleRad = minRad;
                                    }
                                    // Keeping the hit speed if to closed to 90U+00b0
                                    if (cornerAngleRad < maxRad) {
                                        // Applying the new speed angle
                                        vY = GameConstants.BALL_SPEED * Math.sin(cornerAngleRad) * (y0 > pb.centerY ? 1 : -1); // Not inverting vY if the hit is too low (-> user will lose the ball, sorry)
                                        vX = GameConstants.BALL_SPEED * Math.cos(cornerAngleRad) * (hitLeftCorner ? -1 : 1);
                                        // Recalculating (x1, y1) from this new speed
                                        x1 = x0 + vX;
                                        y1 = y0 + vY;
                                    }
                                }
                            }
                        }
                        playSound(autoClips.ballPaddleSnd);
                    }
                    // We retrieve the block hit by the ball for the hit sound, block blink & score management:
                    Block block = blocks.stream().filter(b -> b.bounds == ballHit.hitBounds).findFirst().orElse(null);
                    if (block != null) { // Can be null if the ball hit something else (paddle or border)
                        switch (block.blockType) {
                            case GOLD -> {
                                playSound(autoClips.ballHardBlockSnd);
                                blinks.add(new Blink(block.bounds.minX, block.bounds.minY));
                            }
                            case GRAY -> {
                                block.hits++;
                                if (block.hits == block.maxHits) {
                                    score += level * 50;
                                    blockCounter += 1;
                                    block.toBeRemoved = true;
                                    playSound(autoClips.ballBlockSnd);
                                } else {
                                    playSound(autoClips.ballHardBlockSnd);
                                    blinks.add(new Blink(block.bounds.minX, block.bounds.minY));
                                }
                            }
                            default -> {
                                block.hits++;
                                if (block.hits >= block.maxHits) {
                                    score += block.value;
                                    blockCounter += 1;
                                    block.toBeRemoved = true;
                                    playSound(autoClips.ballBlockSnd);
                                    if (blockCounter % GameConstants.BONUS_BLOCK_INTERVAL == 0) {
                                        bonusBlocks.add(new BonusBlock(block.x, block.y, EnumDefinitions.BonusType.values()[GameConstants.RND.nextInt(EnumDefinitions.BonusType.values().length)]));
                                    }
                                }
                            }
                        }
                        blockFifo.add(block);
                        // Checking for bounds
                        final List<Block> items = blockFifo.getItems();
                        if (items.size() == 9) {
                            if (items.get(0).equals(items.get(6)) &&
                                    items.get(1).equals(items.get(5)) &&
                                    items.get(1).equals(items.get(7)) &&
                                    items.get(2).equals(items.get(4)) &&
                                    items.get(2).equals(items.get(8))) {
                                this.vX += 0.1;
                            } else if (items.get(0).equals(items.get(8)) &&
                                    items.get(1).equals(items.get(7)) &&
                                    items.get(2).equals(items.get(6)) &&
                                    items.get(3).equals(items.get(5))) {
                                this.vX += 0.1;
                            }
                        }
                    }
                }
            }

            this.bounds.set(this.x - this.width * 0.5, this.y - this.height * 0.5, this.width, this.height);

            // Hit test ball with enemies
            for (Enemy enemy : enemies) {
                boolean ballHitsEnemy = this.bounds.intersects(enemy.bounds);
                if (ballHitsEnemy) {
                    enemy.toBeRemoved = true;
                    explosions.add(new Explosion(enemy.x, enemy.y, enemy.vX, enemy.vY, 1.0));
                    playSound(autoClips.explosionSnd);
                    if (bounds.centerX > enemy.bounds.minX && bounds.centerX < enemy.bounds.maxX) {
                        // Top or Bottom hit
                        vY = -vY;
                    } else if (bounds.centerY > enemy.bounds.minY && bounds.centerY < enemy.bounds.maxY) {
                        // Left or Right hit
                        vX = -vX;
                    } else {
                        double dx = Math.abs(bounds.centerX - enemy.bounds.centerX) - enemy.bounds.width * 0.5;
                        double dy = Math.abs(bounds.centerY - enemy.bounds.centerY) - enemy.bounds.height * 0.5;
                        if (dx > dy) {
                            // Left or Right hit
                            vX = -vX;
                        } else {
                            // Top or Bottom hit
                            vY = -vY;
                        }
                    }
                    break;
                }
            }

            if (Double.compare(vX, 0) == 0) {
                vX = 0.5;
            }

            if (this.bounds.maxY > GameConstants.HEIGHT) {
                this.toBeRemoved = true;
            }
        }
    }

    private class Torpedo extends Sprite {

        // ******************** Constructors **************************************
        public Torpedo(final Image image, final double x, final double y) {
            super(image, x, y - image.getHeight(), 0, GameConstants.TORPEDO_SPEED);
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            y -= vY;
            this.bounds.set(this.x - this.width * 0.5, this.y - this.height * 0.5, this.width, this.height);
            if (bounds.minY < GameConstants.UPPER_INSET) {
                toBeRemoved = true;
            }
        }
    }

    private class OpenDoor extends AnimatedSprite {

        // ******************** Constructors **************************************
        public OpenDoor(final double x, final double y) {
            super(x, y, 0, 0, 3, 0, 1.0);
            this.bounds.set(x, y, width, height);
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            countX++;
            if (countX == maxFrameX) {
                countY++;
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class Enemy extends AnimatedSprite {
        public EnumDefinitions.EnemyType enemyType;
        private double initialVx;


        // ******************** Constructors **************************************
        public Enemy(final double x, final double y, final EnumDefinitions.EnemyType enemyType) {
            super(x, y, 0, GameConstants.ENEMY_SPEED, 8, 3, 1.0);
            this.initialVx = GameConstants.RND.nextDouble() * GameConstants.ENEMY_SPEED;
            this.vX = GameConstants.RND.nextBoolean() ? initialVx : -initialVx;
            this.enemyType = enemyType;
            this.width = GameConstants.ENEMY_WIDTH;
            this.height = GameConstants.ENEMY_HEIGHT;
            this.radius = Math.sqrt(GameConstants.ENEMY_HEIGHT * GameConstants.ENEMY_HEIGHT * 0.25 + GameConstants.ENEMY_WIDTH * GameConstants.ENEMY_WIDTH * 0.25);
            this.bounds.set(this.x - this.width * 0.5, this.y - this.height * 0.5, this.width, this.height);
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            x += vX;
            y += vY;

            if (bounds.maxX > GameConstants.WIDTH - GameConstants.INSET) {
                this.x = GameConstants.WIDTH - GameConstants.INSET - this.width;
                this.vX = -initialVx;
            }
            if (bounds.minX < GameConstants.INSET) {
                this.x = GameConstants.INSET;
                this.vX = Math.abs(initialVx);
            }
            if (bounds.minY < GameConstants.UPPER_INSET) {
                this.y = GameConstants.UPPER_INSET;
                this.vY = 3.0;
            }

            this.bounds.set(this.x, this.y, this.width, this.height);

            // Hit test enemy with blocks
            for (Block block : blocks) {
                boolean enemyHitsBlock = this.bounds.intersects(block.bounds);
                if (enemyHitsBlock) {
                    if (bounds.centerX > block.bounds.minX && bounds.centerX < block.bounds.maxX) {
                        // Top or Bottom hit
                        vY = -vY;
                    } else if (bounds.centerY > block.bounds.minY && bounds.centerY < block.bounds.maxY) {
                        // Left or Right hit
                        vX = -vX;
                    } else {
                        double dx = Math.abs(bounds.centerX - block.bounds.centerX) - block.bounds.width * 0.5;
                        double dy = Math.abs(bounds.centerY - block.bounds.centerY) - block.bounds.height * 0.5;
                        if (dx > dy) {
                            // Left or Right hit
                            vX = -vX;
                        } else {
                            // Top or Bottom hit
                            vY = -vY;
                        }
                    }
                    break;
                }
            }

            // Hit test enemy with paddle
            if (bounds.intersects(paddle.bounds)) {
                this.toBeRemoved = true;
                explosions.add(new Explosion(this.x, this.y, this.vX, this.vY, 1.0));
                playSound(autoClips.explosionSnd);
            }

            if (y > GameConstants.HEIGHT) {
                toBeRemoved = true;
            }

            countX++;
            if (countX == maxFrameX) {
                countY++;
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class Explosion extends AnimatedSprite {

        // ******************** Constructors **************************************
        public Explosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 4, 4, scale);
        }


        // ******************** Methods *******************************************
        @Override
        public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    public static class Bounds {
        public double x;
        public double y;
        public double width;
        public double height;
        public double minX;
        public double minY;
        public double maxX;
        public double maxY;
        public double centerX;
        public double centerY;


        // ******************** Constructors **************************************
        public Bounds() {
            this(0, 0, 0, 0);
        }

        public Bounds(final double width, final double height) {
            this(0, 0, width, height);
        }

        public Bounds(final double x, final double y, final double width, final double height) {
            set(x, y, width, height);
        }


        // ******************** Methods *******************************************
        public void set(final Bounds bounds) {
            set(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        public void set(final double x, final double y, final double width, final double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.minX = x;
            this.minY = y;
            this.maxX = x + width;
            this.maxY = y + height;
            this.centerX = x + width * 0.5;
            this.centerY = y + height * 0.5;
        }

        public boolean contains(final double x, final double y) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY;
        }

        public boolean intersects(final Bounds other) {
            return other.minX <= maxX && minX <= other.maxX && other.minY <= maxY && minY <= other.maxY;
        }

        private BallHit computeBallHit(double x0, double y0, double x1, double y1, double r) {
            // Increased +r bounds to simplify computation and focus only on ball center
            double minXr = minX - r, minYr = minY - r, maxXr = maxX + r, maxYr = maxY + r;
            double yHit = 0, xHit = 0;
            boolean hit = false, inverseVy = false, inverseVx = false;
            // Did the ball hit the bottom border?
            if (y0 >= maxYr && y1 <= maxYr) { // Means that the ball crossed the bottom line (while moving up)
                xHit = Utils.computeLineIntersectionX(-1, maxYr, 1, maxYr, x0, y0, x1, y1); // Where on X?
                hit = xHit >= minXr && xHit <= maxXr; // X condition for a hit
                if (hit) {
                    yHit = maxYr;
                    inverseVy = true;
                }
            }
            // If not, did it hit the top border?
            if (!hit && y0 <= minYr && y1 >= minYr) { // Means that the ball crossed the top line (while moving down)
                xHit = Utils.computeLineIntersectionX(-1, minYr, 1, minYr, x0, y0, x1, y1); // Where on X?
                hit = xHit >= minXr && xHit <= maxXr; // X condition for a hit
                if (hit) {
                    yHit = minYr;
                    inverseVy = true;
                }
            }
            // If not, did it hit the left border?
            if (!hit && x0 <= minXr && x1 >= minXr) { // Means that the ball crossed the left line (while moving to right)
                yHit = Utils.computeLineIntersectionY(minXr, 1, minXr, -1, x0, y0, x1, y1); // Where on Y?
                hit = yHit >= minYr && yHit <= maxYr; // Y condition for a hit
                if (hit) {
                    xHit = minXr;
                    inverseVx = true;
                }
            }
            // If not, did it hit the right border?
            if (!hit && x0 >= maxXr && x1 <= maxXr) { // Means that the ball crossed the right line (while moving to left)
                yHit = Utils.computeLineIntersectionY(maxXr, 1, maxXr, -1, x0, y0, x1, y1); // Where on Y?
                hit = yHit >= minYr && yHit <= maxYr; // Y condition for a hit
                if (hit) {
                    xHit = maxXr;
                    inverseVx = true;
                }
            }
            // If not, is the ball inside the bounds? (this may happen with the paddle moving quickly)
            if (!hit && contains(x1, y1)) {
                hit = true;
                xHit = Utils.computeLineIntersectionX(-1, minYr, 1, minYr, x0, y0, x1, y1); // Where on X?
                yHit = minYr;
                //x0 = x1 = xHit;
                //y0 = y1 = yHit;
                inverseVy = true;
            }
            if (!hit)
                return null;
            BallHit ballHit = new BallHit(this);
            ballHit.xHit = xHit;
            ballHit.yHit = yHit;
            ballHit.inverseVx = inverseVx;
            ballHit.inverseVy = inverseVy;
            ballHit.beforeHitDistance = Utils.distance(x0, y0, xHit, yHit);
            double afterHitDistance = Utils.distance(xHit, yHit, x1, y1);
            ballHit.correctedX = inverseVx ? xHit - (x1 - xHit) * afterHitDistance : x1;
            ballHit.correctedY = inverseVy ? yHit - (y1 - yHit) * afterHitDistance : y1;
            return ballHit;
        }
    }

    private static class BallHit {
        private final Bounds hitBounds;
        private double beforeHitDistance;
        private double xHit;
        private double yHit;
        private double correctedX;
        private double correctedY;
        private boolean inverseVx;
        private boolean inverseVy;


        public BallHit(Bounds hitBounds) {
            this.hitBounds = hitBounds;
        }
    }


    // ******************** Start *********************************************
    public static void main(String[] args) {
        launch(args);
    }
}
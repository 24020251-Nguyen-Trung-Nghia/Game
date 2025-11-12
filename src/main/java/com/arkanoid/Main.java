package com.arkanoid;

import com.arkanoid.config.PropertyManager;
import com.arkanoid.controllers.GameOver;
import com.arkanoid.controllers.StartLevel;
import com.arkanoid.graphics.MenuRenderer;
import com.arkanoid.graphics.GameRenderer;
import com.arkanoid.graphics.Update;
import com.arkanoid.models.*;
import com.arkanoid.models.Objects.*;
import com.arkanoid.resources.AutoClips;
import com.arkanoid.resources.Images;
import com.arkanoid.utils.FIFO;
import com.arkanoid.utils.HitTest;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends Application {
    // ==================== SINGLETON INSTANCES ====================
    public final Images images = Images.getInstance();
    public final AutoClips autoClips = AutoClips.getInstance();
    public final GameRenderer gameRenderer = new GameRenderer(this);
    public final HitTest hitTest = new HitTest(this);
    public final SetupBlocks setupBlocks = new SetupBlocks(this);
    public final GameOver gameOver = new GameOver(this);
    public StartLevel startLevel = new StartLevel(this);
    public final Update update = new Update(this);

    // ==================== GAME OBJECTS ====================
    public Paddle paddle;
    public List<Ball> balls;
    public List<Block> blocks;
    public List<BonusBlock> bonusBlocks;
    public List<Enemy> enemies;
    public List<Blink> blinks;
    public List<Torpedo> torpedoes;
    public List<Explosion> explosions;
    public FIFO<Block> blockFifo;
    public OpenDoor openDoor;

    // ==================== CANVAS LAYERS ====================
    public Canvas bkgCanvas;
    public GraphicsContext bkgCtx;
    public Canvas canvas;
    public GraphicsContext ctx;
    public Canvas brdrCanvas;
    public GraphicsContext brdrCtx;

    // ==================== INPUT HANDLING ====================
    public EventHandler<MouseEvent> mouseHandler;
    public ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public AnimationTimer timer;

    // ==================== GAME STATE VARIABLES ====================
    public boolean running;
    public boolean movingLeft = false;
    public boolean movingRight = false;
    public int noOfLifes = 3;
    public long score = 0;
    public long highscore;
    public int level = 1;
    public Instant gameStartTime;
    public long levelStartTime;
    public long lastTimerCall;
    public long lastAnimCall;
    public long lastBonusAnimCall;
    public long lastEnemyUpdateCall;
    public long lastOneSecondCheck;
    public EnumDefinitions.PaddleState paddleState;
    public boolean stickyPaddle = false;
    public boolean nextLevelDoorOpen = false;
    public boolean movingPaddleOut = false;
    public int animateInc;
    public double ballSpeed;
    public boolean readyLevelVisible;
    public int paddleResetCounter;
    public int speedResetCounter;
    public int nextLevelDoorCounter;
    public double nextLevelDoorAlpha;
    public boolean showStartHint;
    public int silverBlockMaxHits;
    public int blockCounter;
    public Pos enemySpawnPosition;
    public double topLeftDoorAlpha;
    public double topRightDoorAlpha;

    // ==================== MENU CONTROLLERS ====================
    int highest_level = 10;
    private Stage primaryStage;
    private Scene mainScene;
    private MainMenuController mainMenuController;
    private BoardSelectController boardSelectController;
    private LevelSelectController levelSelectController;
    private StackPane gameRootPane = new StackPane();

    // ==================== PADDLE TYPE SETTER ====================
    public void setPaddleType(EnumDefinitions.PaddleState paddleType) {
        System.out.println("Đã chọn ván: " + paddleType.name());
        this.paddleState = paddleType;
        if (this.paddle != null) {
            // Cập nhật kích thước paddle nếu đã được khởi tạo
            this.paddle.width = paddleType.width;
            this.paddle.bounds.width = paddleType.width;
        }
    }

    // ==================== KEY HANDLER FOR GAME ====================
    private void setGameKeyHandler(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (running) {
                if (movingPaddleOut) return;
                switch (e.getCode()) {
                    case RIGHT, D -> movePaddleRight();
                    case LEFT, A -> movePaddleLeft();
                    case SPACE -> handleSpaceKey();
                }
            } else {
                // Nếu game chưa chạy, bấm SPACE để bắt đầu
                if (e.getCode() == KeyCode.SPACE && noOfLifes > 0) {
                    startGame();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case RIGHT, D -> movingRight = false;
                case LEFT, A -> movingLeft = false;
            }
        });
    }

    private void handleSpaceKey() {
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

    private void startGame() {
        running = true;
        stickyPaddle = false;

        if (balls.isEmpty()) {
            spawnBall();
        } else {
            balls.forEach(ball -> {
                ball.active = true;
                ball.bornTimestamp = Instant.now().getEpochSecond();
            });
        }
        renderCurrentScreen();
    }

    // ==================== MENU NAVIGATION METHODS ====================
    public void showMainMenu() {
        try {
            mainMenuController = new MainMenuController(this);
            mainScene = mainMenuController.getScene();
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Arkanoid - Menu");

            // Tắt handler phím của game
            mainScene.setOnKeyPressed(null);
            mainScene.setOnKeyReleased(null);

            System.out.println("Hiển thị Main Menu");
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị Main Menu:");
            e.printStackTrace();
        }
    }

    public void showBoardSelect() {
        try {
            boardSelectController = new BoardSelectController(this);
            mainScene = boardSelectController.getScene();
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Arkanoid - Chọn Ván");

            System.out.println("Hiển thị Board Select");
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị Board Select:");
            e.printStackTrace();
        }
    }

    public void showLevelSelect() {
        try {
            levelSelectController = new LevelSelectController(this);
            mainScene = levelSelectController.getScene();
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Arkanoid - Chọn Level");

            System.out.println("Hiển thị Level Select");
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị Level Select:");
            e.printStackTrace();
        }
    }


    private void debugKeyEvent(KeyCode code, String type) {
        System.out.println("🔑 " + type + ": " + code +
                " | GameState: " + gameState +
                " | Running: " + running +
                " | MovingLeft: " + movingLeft +
                " | MovingRight: " + movingRight);
    }

    public void showGameSceneAndStart(int selectedLevel) {
        try {
            this.level = selectedLevel;

            // ✅ TẠO MỘT StackPane MỚI mỗi lần vào game
            StackPane newGameRootPane = new StackPane();

            // ✅ Khởi tạo canvas nếu chưa có
            if (bkgCanvas == null || canvas == null || brdrCanvas == null) {
                setupCanvas();
            }

            // ✅ Thêm canvas vào StackPane mới
            newGameRootPane.getChildren().clear();
            newGameRootPane.getChildren().addAll(bkgCanvas, canvas, brdrCanvas);
            newGameRootPane.setAlignment(Pos.CENTER);

            // ✅ LUÔN tạo Scene mới để tránh conflict
            Scene gameScene = new Scene(newGameRootPane, GameConstants.WIDTH, GameConstants.HEIGHT);

            // ✅ QUAN TRỌNG: XÓA tất cả key handlers cũ và chỉ dùng DIRECT HANDLERS
            gameScene.setOnKeyPressed(e -> {
                debugKeyEvent(e.getCode(), "GAME_PRESSED");

                // ✅ XỬ LÝ PAUSE_MENU TRƯỚC
                if (gameState == GameState.PAUSE_MENU) {
                    handlePauseMenuInput(e.getCode());
                    return;
                }

                // ✅ XỬ LÝ SAVE_CREDENTIALS
                if (gameState == GameState.SAVE_CREDENTIALS) {
                    handleSaveCredentialsInput(e.getCode());
                    return;
                }

                // ✅ Xử lý khi đang PLAYING
                if (gameState == GameState.PLAYING) {
                    if (running) {
                        if (movingPaddleOut) return;
                        switch (e.getCode()) {
                            case RIGHT, D -> {
                                movingRight = true;
                                movingLeft = false;
                            }
                            case LEFT, A -> {
                                movingLeft = true;
                                movingRight = false;
                            }
                            case SPACE -> handleSpaceKey();
                        }
                    } else {
                        if (e.getCode() == KeyCode.SPACE && noOfLifes > 0) {
                            startGame();
                        }
                    }

                    if (e.getCode() == KeyCode.ESCAPE) {
                        gameState = GameState.PAUSE_MENU;
                        pauseIndex = 0;
                        renderCurrentScreen();
                    }
                }
            });

            // ✅ THÊM XỬ LÝ KEY TYPED CHO SAVE_CREDENTIALS
            gameScene.setOnKeyTyped(e -> {
                String ch = e.getCharacter();

                if (gameState == GameState.SAVE_CREDENTIALS) {
                    if (enteringUsername) {
                        if ("\b".equals(ch)) {
                            if (!inputUsername.isEmpty())
                                inputUsername = inputUsername.substring(0, inputUsername.length() - 1);
                        } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                            inputUsername += ch;
                        }
                        renderCurrentScreen();
                    } else if (enteringPassword) {
                        if ("\b".equals(ch)) {
                            if (!inputPassword.isEmpty())
                                inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                        } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                            inputPassword += ch;
                        }
                        renderCurrentScreen();
                    }
                }
            });

            gameScene.setOnKeyReleased(e -> {
                debugKeyEvent(e.getCode(), "GAME_RELEASED");

                // ✅ CHỈ xử lý khi đang trong trạng thái PLAYING
                if (gameState != GameState.PLAYING) return;

                switch (e.getCode()) {
                    case RIGHT, D -> movingRight = false;
                    case LEFT, A -> movingLeft = false;
                }
            });

            // ✅ THÊM MOUSE HANDLER cho canvas
            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);

            // ✅ ĐẢM BẢO FOCUS
            canvas.setFocusTraversable(true);
            newGameRootPane.setFocusTraversable(true);

            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Arkanoid - Level " + selectedLevel);

            // ✅ CẬP NHẬT GAME STATE QUAN TRỌNG
            gameState = GameState.PLAYING;

            // Reset và bắt đầu game
            resetGameForNewLevel();
            setupBlocks.setupBlocks(selectedLevel);

            // ✅ REQUEST FOCUS SAU KHI SCENE ĐƯỢC SHOW
            Platform.runLater(() -> {
                newGameRootPane.requestFocus();
                gameRenderer.drawBackground(selectedLevel);
                gameRenderer.drawBorder();
                gameRenderer.drawGame();
                startLevel.startLevel(selectedLevel);
            });

            System.out.println("🎮 Bắt đầu Level " + selectedLevel);
            System.out.println("🔧 Game state: " + gameState);
            System.out.println("🎯 Key handler trực tiếp đã được kích hoạt");

        } catch (Exception e) {
            System.err.println("Lỗi khi bắt đầu game:");
            e.printStackTrace();
        }
    }

    public void unlockNextLevel(int currentLevel) {
        int highest = PropertyManager.INSTANCE.getInt(Constants.UNLOCKED_LEVEL_KEY, 1);
        int nextLevel = currentLevel + 1;

        if (nextLevel > highest && nextLevel <= Constants.LEVEL_MAP.size()) {
            PropertyManager.INSTANCE.setInt(Constants.UNLOCKED_LEVEL_KEY, nextLevel);
            PropertyManager.INSTANCE.storeProperties();
            System.out.println("Mở khóa thành công Level " + nextLevel);
        }
    }

    private void initializeGameRootPane() {
        gameRootPane.getChildren().clear();
        gameRootPane.getChildren().addAll(bkgCanvas, canvas, brdrCanvas);
        gameRootPane.setAlignment(Pos.CENTER);
    }

    private void resetGameForNewLevel() {
        running = false;
        noOfLifes = 3;
        score = 0;
        balls.clear();
        blocks.clear();
        bonusBlocks.clear();
        enemies.clear();
        explosions.clear();
        torpedoes.clear();
        blinks.clear();
        stickyPaddle = false;
        nextLevelDoorOpen = false;
        movingPaddleOut = false;
        ballSpeed = GameConstants.BALL_SPEED;
        readyLevelVisible = false;
        showStartHint = true;

        // Khởi tạo lại paddle
        paddle = new Paddle(this);

        // Đảm bảo paddle có vị trí hợp lệ
        paddle.x = GameConstants.WIDTH * 0.5 - paddleState.width * 0.5;
        paddle.y = GameConstants.HEIGHT - GameConstants.PADDLE_OFFSET_Y;
        paddle.bounds.set(paddle.x, paddle.y, paddleState.width, paddle.height);
    }

    // ==================== UI STATE VARIABLES ====================
    public enum GameState {
        START_MENU,
        LOGIN_SCREEN,
        SELECT_PADLE_AND_LEVEL,
        PLAYING,
        PAUSE_MENU,
        SAVE_CREDENTIALS
    }

    public GameState gameState = GameState.START_MENU;
    public int menuIndex = 0;
    public int loginMenuIndex = 0;
    public int pauseIndex = 0;
    public String inputUsername = "";
    public String inputPassword = "";
    public boolean enteringUsername = false;
    public boolean enteringPassword = false;
    public boolean loginFailed = false;
    public static final Path PLAYER_FILE = Path.of("Game", "players.txt");

    @Override
    public void init() {
        initializeGame();
        setupCanvas();
        setupEventHandlers();
        loadResources();
        initializeGameObjects();
    }

    private void initializeGame() {
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
    }

    private void setupCanvas() {
        bkgCanvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        bkgCtx = bkgCanvas.getGraphicsContext2D();
        canvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        ctx = canvas.getGraphicsContext2D();
        brdrCanvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        brdrCtx = brdrCanvas.getGraphicsContext2D();
        brdrCanvas.setMouseTransparent(true);
    }

    private void setupEventHandlers() {
        setupMouseHandler();
        setupGameLoop();
    }

    private void setupMouseHandler() {
        mouseHandler = e -> {
            if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
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
    }

    private void setupGameLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                if (running && gameState == GameState.PLAYING) {
                    updateGameState(now);
                } else {
                    handleIdleState();
                }
            }
        };
    }

    private void updateGameState(long now) {
        updateOneSecondChecks(now);
        updateAnimations(now);
        updateGameLogic(now);
    }

    private void updateOneSecondChecks(long now) {
        if (now > lastOneSecondCheck + 1_000_000_000) {
            handleOneSecondUpdates();
            lastOneSecondCheck = now;
        }
    }

    private void handleOneSecondUpdates() {
        long levelPlayTime = Instant.now().getEpochSecond() - levelStartTime;
        if (levelPlayTime > 15 && enemies.size() < 5 && levelPlayTime % 10 == 0) {
            enemySpawnPosition = GameConstants.RND.nextBoolean() ? Pos.TOP_LEFT : Pos.TOP_RIGHT;
            switch (enemySpawnPosition) {
                case TOP_LEFT -> topLeftDoorAlpha = 0.99;
                case TOP_RIGHT -> topRightDoorAlpha = 0.99;
            }
        }

        updateCounters();
    }

    private void updateCounters() {
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
                gameRenderer.drawBorder();
            }
        }
    }

    private void updateAnimations(long now) {
        if (now > lastBonusAnimCall + 50_000_000) {
            bonusBlocks.forEach(BonusBlock::update);
            updateTopDoorAnimations();
            lastBonusAnimCall = now;
        }

        if (now > lastEnemyUpdateCall + 100_000_000) {
            enemies.forEach(Enemy::update);
            explosions.forEach(Explosion::update);
            lastEnemyUpdateCall = now;
        }

        if (now > lastAnimCall + 5_000_000) {
            animateInc++;
            lastAnimCall = now;
        }
    }

    private void updateTopDoorAnimations() {
        if (topLeftDoorAlpha < 1) {
            topLeftDoorAlpha -= 0.1;
            if (topLeftDoorAlpha <= 0) {
                spawnEnemy(Pos.TOP_LEFT);
                topLeftDoorAlpha = 1;
            }
            gameRenderer.drawBorder();
        } else if (topRightDoorAlpha < 1) {
            topRightDoorAlpha -= 0.1;
            if (topRightDoorAlpha <= 0) {
                spawnEnemy(Pos.TOP_RIGHT);
                topRightDoorAlpha = 1;
            }
            gameRenderer.drawBorder();
        }
    }

    private void updateGameLogic(long now) {
        if (now > lastTimerCall) {
            hitTest.hitTests();
            handlePaddleMovement();
            update.updateGame();
            gameRenderer.drawGame();
            if (nextLevelDoorOpen) {
                gameRenderer.drawBorder();
            }
            lastTimerCall = now;
        }

        if (movingPaddleOut) {
            handlePaddleExit();
        }
    }

    private void handlePaddleMovement() {
        if (movingLeft) movePaddleLeft();
        else if (movingRight) movePaddleRight();
        else stopPaddle();
    }

    private void handlePaddleExit() {
        paddle.x += 1;
        paddle.bounds.set(paddle.x, paddle.y, paddleState.width, paddle.height);
        update.updateGame();
        gameRenderer.drawGame();
        if (paddle.x > GameConstants.WIDTH) {
            unlockNextLevel(level);
            level++;
            if (level > Constants.LEVEL_MAP.size()) {
                level = 1;
            }
            score += 10_000;

            // ✅ QUAN TRỌNG: RESET TRẠNG THÁI VÀ LOAD BLOCKS MỚI
            running = false;
            balls.clear();
            blocks.clear();
            bonusBlocks.clear();
            enemies.clear();
            explosions.clear();
            torpedoes.clear();
            blinks.clear();
            stickyPaddle = false;
            nextLevelDoorOpen = false;
            movingPaddleOut = false;
            ballSpeed = GameConstants.BALL_SPEED;
            readyLevelVisible = false;
            showStartHint = true;

            // ✅ RESET PADDLE
            paddle.x = GameConstants.WIDTH * 0.5 - paddleState.width * 0.5;
            paddle.y = GameConstants.HEIGHT - GameConstants.PADDLE_OFFSET_Y;
            paddle.bounds.set(paddle.x, paddle.y, paddleState.width, paddle.height);

            // ✅ LOAD BLOCKS MỚI CHO LEVEL MỚI
            setupBlocks.setupBlocks(level);

            System.out.println("🎯 Chuyển đến Level " + level + " - Đã load blocks mới");

            // ✅ BẮT ĐẦU LEVEL MỚI
            startLevel.startLevel(level);
        }
    }

    private void handleIdleState() {
        if (gameState.equals(GameState.PLAYING)) {
            showStartHint = true;
            startScreen();
        }
    }

    private void loadResources() {
        Images.loadImages(this);
        AutoClips.loadSounds(this);
        ensurePlayerFileExists();
    }

    private void initializeGameObjects() {
        paddle = new Paddle(this);
        balls = new CopyOnWriteArrayList<>();
        blocks = new CopyOnWriteArrayList<>();
        bonusBlocks = new CopyOnWriteArrayList<>();
        enemies = new CopyOnWriteArrayList<>();
        explosions = new CopyOnWriteArrayList<>();
        torpedoes = new CopyOnWriteArrayList<>();
        noOfLifes = 3;
        score = 0;
    }

    // ==================== KEYBOARD INPUT HANDLING ====================
    @Override
    public void start(final Stage stage) {
        this.primaryStage = stage;
        gameStartTime = Instant.now();

        // ✅ FIX: Khởi tạo gameRootPane và canvas ngay từ đầu
        setupCanvas(); // Đảm bảo canvas được tạo
        if (gameRootPane.getChildren().isEmpty()) {
            initializeGameRootPane();
        }

        // ✅ Tạo scene với gameRootPane
        mainScene = new Scene(gameRootPane, GameConstants.WIDTH, GameConstants.HEIGHT);

        mainScene.setOnKeyPressed(this::handleKeyPressed);
        mainScene.setOnKeyReleased(this::handleKeyReleased);
        mainScene.setOnKeyTyped(this::handleKeyTyped);

        stage.setTitle("Arkanoid");
        stage.setScene(mainScene);
        stage.show();
        stage.setResizable(false);

        // ✅ FIX: Render sau khi stage.show()
        Platform.runLater(() -> {
            if (gameState.equals(GameState.START_MENU)) {
                MenuRenderer.renderStartMenu(this, menuIndex);
                playSound(AutoClips.gameStartSnd);
            }
        });

        timer.start();
    }

    @Override
    public void stop() {
        if (timer != null) timer.stop();
        if (executor != null) executor.shutdownNow();
        Platform.exit();
    }

    private void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        debugKeyEvent(code, "GLOBAL_PRESSED");

        // ✅ Xử lý theo game state - CHỈ các state không phải game scene
        switch (gameState) {
            case START_MENU -> handleStartMenuInput(code);
            case LOGIN_SCREEN -> handleLoginScreenInput(code);
            // ❌ BỎ QUA PAUSE_MENU và SAVE_CREDENTIALS - để game scene handler xử lý
            case SELECT_PADLE_AND_LEVEL -> {
                // Không xử lý ở đây, để controller riêng xử lý
            }
            case PLAYING, PAUSE_MENU, SAVE_CREDENTIALS -> {
                // Các state này được xử lý bởi game scene handler
                return;
            }
        }
    }

    private void handleKeyReleased(KeyEvent e) {
        // ✅ KHÔNG xử lý key events toàn cục khi đang trong game
        if (gameState == GameState.PLAYING) {
            return;
        }
        KeyCode code = e.getCode();
        if (gameState == GameState.PLAYING) {
            switch (code) {
                case RIGHT, D -> movingRight = false;
                case LEFT, A -> movingLeft = false;
            }
        }
    }

    private void handleKeyTyped(KeyEvent e) {
        String ch = e.getCharacter();
        if (gameState == GameState.LOGIN_SCREEN) {
            if (enteringUsername) {
                if ("\b".equals(ch)) {
                    if (!inputUsername.isEmpty())
                        inputUsername = inputUsername.substring(0, inputUsername.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputUsername += ch;
                }
                renderCurrentScreen();
            } else if (enteringPassword) {
                if ("\b".equals(ch)) {
                    if (!inputPassword.isEmpty())
                        inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputPassword += ch;
                }
                renderCurrentScreen();
            }
        } else if (gameState == GameState.SAVE_CREDENTIALS) {
            if (enteringUsername) {
                if ("\b".equals(ch)) {
                    if (!inputUsername.isEmpty())
                        inputUsername = inputUsername.substring(0, inputUsername.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputUsername += ch;
                }
                renderCurrentScreen();
            } else if (enteringPassword) {
                if ("\b".equals(ch)) {
                    if (!inputPassword.isEmpty())
                        inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputPassword += ch;
                }
                renderCurrentScreen();
            }
        }
    }

    // ==================== INPUT HANDLING METHODS ====================
    private void handleStartMenuInput(KeyCode code) {
        switch (code) {
            case UP -> {
                menuIndex = (menuIndex - 1 + 2) % 2;
                renderCurrentScreen();
            }
            case DOWN -> {
                menuIndex = (menuIndex + 1) % 2;
                renderCurrentScreen();
            }
            case SPACE, ENTER -> {
                if (menuIndex == 0) {
                    // "New Player" - bắt đầu từ chọn ván
                    enterAsNewPlayer();
                } else {
                    // "Old Player" - vào màn hình đăng nhập
                    gameState = GameState.LOGIN_SCREEN;
                    inputUsername = "";
                    inputPassword = "";
                    enteringUsername = true;
                    enteringPassword = false;
                    loginMenuIndex = 0;
                    loginFailed = false;
                    renderCurrentScreen();
                }
            }
        }
    }

    private void handleLoginScreenInput(KeyCode code) {
        switch (code) {
            case UP -> {
                loginMenuIndex = (loginMenuIndex - 1 + 3) % 3;
                enteringUsername = loginMenuIndex == 0;
                enteringPassword = loginMenuIndex == 1;
                renderCurrentScreen();
            }
            case DOWN -> {
                loginMenuIndex = (loginMenuIndex + 1) % 3;
                enteringUsername = loginMenuIndex == 0;
                enteringPassword = loginMenuIndex == 1;
                renderCurrentScreen();
            }
            case SPACE -> {
                if (loginMenuIndex == 2) {
                    gameState = GameState.START_MENU;
                    menuIndex = 0;
                    renderCurrentScreen();
                } else {
                    if (loginMenuIndex == 0) enteringUsername = true;
                    if (loginMenuIndex == 1) enteringPassword = true;
                    renderCurrentScreen();
                }
            }
            case ENTER -> attemptLogin();
            case ESCAPE -> {
                gameState = GameState.START_MENU;
                menuIndex = 0;
                renderCurrentScreen();
            }
        }
    }

    private void handlePlayingInput(KeyCode code) {
        if (code == KeyCode.ESCAPE) {
            gameState = GameState.PAUSE_MENU;
            pauseIndex = 0;
            renderCurrentScreen();
            return;
        }

        // SỬA QUAN TRỌNG: Khi game over (noOfLifes == 0), bấm SPACE để quay lại chọn level
        if (noOfLifes == 0 && code == KeyCode.SPACE) {
            showLevelSelect();
            return;
        }

        if (!running && !movingPaddleOut && noOfLifes > 0) {
            // Nếu game chưa chạy nhưng vẫn còn mạng, bấm SPACE để bắt đầu
            if (code == KeyCode.SPACE) {
                startGame();
                return;
            }
        }

        if (running && !movingPaddleOut) {
            switch (code) {
                case RIGHT, D -> movingRight = true;
                case LEFT, A -> movingLeft = true;
                case SPACE -> handleSpaceKey();
            }
        }
    }

    private void handlePauseMenuInput(KeyCode code) {
        switch (code) {
            case UP -> {
                pauseIndex = (pauseIndex - 1 + 2) % 2;
                renderCurrentScreen();
            }
            case DOWN -> {
                pauseIndex = (pauseIndex + 1) % 2;
                renderCurrentScreen();
            }
            case SPACE, ENTER -> {
                if (pauseIndex == 1) {
                    exitWithoutSave();
                } else {
                    gameState = GameState.SAVE_CREDENTIALS;
                    inputUsername = "";
                    inputPassword = "";
                    enteringUsername = true;
                    enteringPassword = false;
                    renderCurrentScreen();
                }
            }
            case ESCAPE -> {
                gameState = GameState.PLAYING;
                renderCurrentScreen();
            }
        }
    }

    public void handleSaveCredentialsInput(KeyCode code) {
        System.out.println("💾 SAVE CREDENTIALS - Key pressed: " + code);
        System.out.println("   - Current state: enteringUsername=" + enteringUsername + ", enteringPassword=" + enteringPassword);
        System.out.println("   - Input: username='" + inputUsername + "', password='" + "*".repeat(inputPassword.length()) + "'");

        switch (code) {
            case UP -> {
                if (enteringPassword) {
                    enteringPassword = false;
                    enteringUsername = true;
                    System.out.println("   ↪ Chuyển sang nhập Username");
                }
                renderCurrentScreen();
            }
            case DOWN -> {
                if (enteringUsername) {
                    enteringUsername = false;
                    enteringPassword = true;
                    System.out.println("   ↪ Chuyển sang nhập Password");
                }
                renderCurrentScreen();
            }
            case ENTER -> {
                if (!inputUsername.isBlank() && !inputPassword.isBlank()) {
                    System.out.println("✅ Lưu thông tin player");
                    savePlayer(inputUsername.trim(), inputPassword.trim());
                    exitAfterSave();
                } else {
                    System.out.println("❌ Username hoặc password không được để trống");
                }
            }
            case ESCAPE -> {
                System.out.println("⎋ Quay lại Pause Menu");
                gameState = GameState.PAUSE_MENU;
                renderCurrentScreen();
            }
            case TAB -> {
                // Chuyển đổi giữa username và password field
                if (enteringUsername) {
                    enteringUsername = false;
                    enteringPassword = true;
                    System.out.println("   ↪ Tab: Chuyển sang nhập Password");
                } else {
                    enteringUsername = true;
                    enteringPassword = false;
                    System.out.println("   ↪ Tab: Chuyển sang nhập Username");
                }
                renderCurrentScreen();
            }
        }
    }

    // ==================== UI RENDERING METHODS ====================
    public void renderCurrentScreen() {
        switch (gameState) {
            case START_MENU -> MenuRenderer.renderStartMenu(this, menuIndex);
            case LOGIN_SCREEN -> MenuRenderer.renderLoginScreen(this, inputUsername, inputPassword,
                    enteringUsername, enteringPassword, loginMenuIndex, loginFailed);
            case PAUSE_MENU -> MenuRenderer.renderPauseMenu(this, pauseIndex);
            case SAVE_CREDENTIALS -> MenuRenderer.renderSaveCredentials(this, inputUsername, inputPassword,
                    enteringUsername, enteringPassword);
            case PLAYING -> renderDuringGame();
        }
    }

    private void renderDuringGame() {
        gameRenderer.drawBackground(level);
        gameRenderer.drawGame();
        gameRenderer.drawBorder();
    }

    // ==================== GAME CONTROL METHODS ====================
    private void enterAsNewPlayer() {
        resetGame();
        gameState = GameState.SELECT_PADLE_AND_LEVEL;
        level = 1;
        highest_level = 1;

        // ✅ HIỂN THỊ MÀN HÌNH CHỌN VÁN TRƯỚC, RỒI ĐẾN CHỌN LEVEL
        showBoardSelect();
    }

    private void attemptLogin() {
        if (checkLogin(inputUsername.trim(), inputPassword.trim())) {
            System.out.println("✅ Đăng nhập thành công - Chuyển đến chọn level");

            // ✅ THAY VÌ VÀO THẲNG GAME, CHUYỂN ĐẾN CHỌN LEVEL
            resetGame();
            gameState = GameState.SELECT_PADLE_AND_LEVEL;

            // ✅ HIỂN THỊ MÀN HÌNH CHỌN LEVEL
            showLevelSelect();

            // ✅ RESET INPUT FIELDS
            inputUsername = "";
            inputPassword = "";
            enteringUsername = false;
            enteringPassword = false;

        } else {
            loginFailed = true;
            inputPassword = "";
            renderCurrentScreen();
        }
    }

    private void resetGame() {
        running = false;
        noOfLifes = 3;
        score = 0;
        level = 1;
        balls.clear();
        blocks.clear();
        bonusBlocks.clear();
        enemies.clear();
        explosions.clear();
        torpedoes.clear();
        blinks.clear();
        paddleState = EnumDefinitions.PaddleState.STANDARD;
        stickyPaddle = false;
        nextLevelDoorOpen = false;
        movingPaddleOut = false;
        ballSpeed = GameConstants.BALL_SPEED;
        readyLevelVisible = false;
        showStartHint = false;

        // Khởi tạo lại paddle
        paddle = new Paddle(this);

        // QUAN TRỌNG: Setup lại blocks cho level hiện tại
        setupBlocks.setupBlocks(level);
    }

    private void exitWithoutSave() {
        stopAndExit();
    }

    private void exitAfterSave() {
        stopAndExit();
    }

    private void stopAndExit() {
        if (timer != null) timer.stop();
        if (executor != null) executor.shutdownNow();
        Platform.exit();
    }

    // ==================== GAME MECHANICS METHODS ====================
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
        if (!torpedoes.isEmpty()) {
            return;
        }
        torpedoes.add(new Torpedo(Images.torpedoImg, x, GameConstants.HEIGHT - 50));
        playSound(AutoClips.laserSnd);
    }

    public void playSound(final AudioClip audioClip) {
        if (audioClip == null) return;
        try {
            audioClip.play();
        } catch (Exception ex) {
            System.err.println("Audio play error: " + ex.getMessage());
        }
    }

    private void spawnEnemy(final Pos position) {
        switch (position) {
            case TOP_LEFT ->
                    enemies.add(new Enemy(this, 100 + Images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
            case TOP_RIGHT ->
                    enemies.add(new Enemy(this, GameConstants.WIDTH - 100 - Images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
        }
    }

    public void spawnBall() {
        if (!balls.isEmpty()) {
            return;
        }
        balls.add(new Ball(this, Images.ballImg, paddle.bounds.centerX, paddle.bounds.minY - Images.ballImg.getHeight() * 0.5 - 1, (GameConstants.RND.nextDouble() * (2 * ballSpeed) - ballSpeed)));

        // Đảm bảo running được set đúng khi spawn ball mới
        if (noOfLifes > 0) {
            running = true;
        }
    }

    public void startScreen() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        gameRenderer.drawBackground(1);
        gameRenderer.drawBorder();
    }

    // ==================== PLAYER MANAGEMENT ====================
    private void ensurePlayerFileExists() {
        try {
            if (!Files.exists(PLAYER_FILE)) {
                Files.createDirectories(PLAYER_FILE.getParent());
                Files.createFile(PLAYER_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkLogin(String user, String pass) {
        if (user == null || pass == null) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(PLAYER_FILE.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                if (parts.length == 2) {
                    String u = parts[0];
                    String p = parts[1];
                    if (u.equals(user) && p.equals(pass)) return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void savePlayer(String user, String pass) {
        if (user == null || pass == null || user.isBlank() || pass.isBlank()) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PLAYER_FILE.toFile(), true))) {
            bw.write(user + " " + pass);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        launch(args);
    }
}
package com.arkanoid;

import com.arkanoid.config.PropertyManager;
import com.arkanoid.controllers.GameOver;
import com.arkanoid.controllers.StartLevel;
import com.arkanoid.graphics.Draw;
import com.arkanoid.graphics.MenuRenderer;
import com.arkanoid.graphics.Update;
import com.arkanoid.models.*;
import com.arkanoid.models.Objects.*;
import com.arkanoid.resources.AutoClips;
import com.arkanoid.resources.Images;
import com.arkanoid.graphics.DrawBackground;
import com.arkanoid.graphics.DrawBorder;
import com.arkanoid.utils.FIFO;
import com.arkanoid.utils.HitTest;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
    public final DrawBackground drawBackground = new DrawBackground(this);
    public final HitTest hitTest = new HitTest(this);
    public final SetupBlocks setupBlocks = new SetupBlocks(this);
    public final GameOver gameOver = new GameOver(this);
    public StartLevel startLevel = new StartLevel(this);
    public final Draw draw = new Draw(this);
    public final Update update = new Update(this);
    public final DrawBorder drawBorder = new DrawBorder(this);

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

    // ==================== UI STATE VARIABLES ====================
    public enum GameState {
        START_MENU,
        LOGIN_SCREEN,
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
                drawBorder.drawBorder();
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
            drawBorder.drawBorder();
        } else if (topRightDoorAlpha < 1) {
            topRightDoorAlpha -= 0.1;
            if (topRightDoorAlpha <= 0) {
                spawnEnemy(Pos.TOP_RIGHT);
                topRightDoorAlpha = 1;
            }
            drawBorder.drawBorder();
        }
    }

    private void updateGameLogic(long now) {
        if (now > lastTimerCall) {
            hitTest.hitTests();
            handlePaddleMovement();
            update.updateGame();
            draw.drawGame();
            if (nextLevelDoorOpen) {
                drawBorder.drawBorder();
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
        draw.drawGame();
        if (paddle.x > GameConstants.WIDTH) {
            level++;
            if (level > Constants.LEVEL_MAP.size()) {
                level = 1;
            }
            score += 10_000;
            startLevel.startLevel(level);
        }
    }

    private void handleIdleState() {
        if (!showStartHint && Instant.now().getEpochSecond() - gameStartTime.getEpochSecond() > 8) {
            showStartHint = true;
            startScreen();
        }
    }

    private void loadResources() {
        Thread imageThread = new Thread(() -> {
            Images.loadImages(this);
        });
        imageThread.start();

        // Load âm thanh và dữ liệu khác song song
        AutoClips.loadSounds(this);
        ensurePlayerFileExists();

        try {
            imageThread.join(); // đợi ảnh load xong rồi mới tiếp tục
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        gameStartTime = Instant.now();

        final StackPane pane = new StackPane(bkgCanvas, canvas, brdrCanvas);
        final Scene scene = new Scene(pane, GameConstants.WIDTH, GameConstants.HEIGHT);

        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        scene.setOnKeyTyped(this::handleKeyTyped);

        stage.setTitle("Arkanoid");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        if (gameState.equals(GameState.START_MENU)) {
            MenuRenderer.renderStartMenu(this, menuIndex);
            playSound(AutoClips.gameStartSnd);
        } else if (Instant.now().getEpochSecond() - gameStartTime.getEpochSecond() > 8) {
            startLevel.startLevel(level);
        }

        timer.start();
        // thêm phần xử lý đa luồng
        new Thread(() -> {
            while (true) {
                if (running && gameState == GameState.PLAYING) {
                    Platform.runLater(() -> updateGameState(System.nanoTime()));
                }
                try { Thread.sleep(16); } catch (InterruptedException e) { }
            }
        }).start();

    }

    @Override
    public void stop() {
        if (timer != null) timer.stop();
        if (executor != null) executor.shutdownNow();
        Platform.exit();
    }

    private void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (gameState) {
            case START_MENU -> handleStartMenuInput(code);
            case LOGIN_SCREEN -> handleLoginScreenInput(code);
            case PLAYING -> handlePlayingInput(code);
            case PAUSE_MENU -> handlePauseMenuInput(code);
            case SAVE_CREDENTIALS -> handleSaveCredentialsInput(code);
        }
    }

    private void handleKeyReleased(KeyEvent e) {
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
                    enterAsNewPlayer();
                } else {
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

        if (running && !movingPaddleOut) {
            switch (code) {
                case RIGHT, D -> movingRight = true;
                case LEFT, A -> movingLeft = true;
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

    private void handleSaveCredentialsInput(KeyCode code) {
        switch (code) {
            case UP -> {
                if (enteringPassword) {
                    enteringPassword = false;
                    enteringUsername = true;
                }
                renderCurrentScreen();
            }
            case DOWN -> {
                if (enteringUsername) {
                    enteringUsername = false;
                    enteringPassword = true;
                }
                renderCurrentScreen();
            }
            case ENTER -> {
                if (!inputUsername.isBlank() && !inputPassword.isBlank()) {
                    savePlayer(inputUsername.trim(), inputPassword.trim());
                    exitAfterSave();
                }
            }
            case ESCAPE -> {
                gameState = GameState.PAUSE_MENU;
                renderCurrentScreen();
            }
        }
    }

    // ==================== UI RENDERING METHODS ====================
    private void renderCurrentScreen() {
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
        drawBackground.drawBackground(level);
        draw.drawGame();
        drawBorder.drawBorder();
    }

    // ==================== GAME CONTROL METHODS ====================
    private void enterAsNewPlayer() {
        running = true;
        gameState = GameState.PLAYING;
        level = 1;
        startLevel.startLevel(level);
        renderCurrentScreen();
    }

    private void attemptLogin() {
        if (checkLogin(inputUsername.trim(), inputPassword.trim())) {
            running = true;
            gameState = GameState.PLAYING;
            level = 1;
            startLevel.startLevel(level);
            renderCurrentScreen();
        } else {
            loginFailed = true;
            inputPassword = "";
            renderCurrentScreen();
        }
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

    // thêm thread đa luồng
    public void playSound(final AudioClip audioClip) {
        if (audioClip == null) return;
        new Thread(() -> {
            try {
                audioClip.play();
            } catch (Exception ex) {
                System.err.println("Audio play error: " + ex.getMessage());
            }
        }).start();
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
    }

    public void startScreen() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        drawBackground.drawBackground(1);
        drawBorder.drawBorder();
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
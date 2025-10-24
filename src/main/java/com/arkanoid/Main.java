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

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private boolean running;
    private Instant gameStartTime;
    private long levelStartTime;
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

    private Paddle paddle;
    private List<Ball> balls;
    private List<Block> blocks;
    private List<BonusBlock> bonusBlocks;
    private List<Enemy> enemies;
    private List<Torpedo> torpedoes;

    private int noOfLifes = 3;
    public long score = 0;
    private long highscore;
    private int level = 1;
    private EnumDefinitions.PaddleState paddleState;

    private boolean stickyPaddle = false;
    private boolean nextLevelDoorOpen = false;
    public boolean movingPaddleOut = false;

    private boolean movingLeft = false;
    private boolean movingRight = false;


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

    private enum GameState {
        START_MENU,
        LOGIN_SCREEN,
        PLAYING,
        PAUSE_MENU,
        SAVE_CREDENTIALS
    }

    private GameState gameState = GameState.START_MENU;
    private int menuIndex = 0;
    private int loginMenuIndex = 0;
    private int pauseIndex = 0;
    private String inputUsername = "";
    private String inputPassword = "";
    private boolean enteringUsername = false;
    private boolean enteringPassword = false;
    private boolean loginFailed = false;
    private static final Path PLAYER_FILE = Path.of("players.txt");

    public HitTest getHitTest() { return hitTest; }
    public SetupBlocks getSetupBlocks() { return setupBlocks; }
    public GameOver getGameOver() { return gameOver; }
    public StartLevel getStartLevel() { return startLevel; }
    public EventHandler<MouseEvent> getMouseHandler() { return mouseHandler; }
    public boolean isRunning() { return running; }
    public GraphicsContext getCtx() { return ctx; }
    public GraphicsContext getBkgCtx() { return bkgCtx; }
    public Images getImages() { return images; }
    public long getHighscore() { return highscore; }
    public boolean isShowStartHint() { return showStartHint; }
    public AutoClips getAutoClips() { return autoClips; }
    public LoadImages getLoadImages() { return loadImages; }
    public LoadSounds getLoadSounds() { return loadSounds; }
    public DrawBackground getDrawBackground() { return drawBackground; }
    public ScheduledExecutorService getExecutor() { return executor; }
    public Instant getGameStartTime() { return gameStartTime; }
    public long getLevelStartTime() { return levelStartTime; }
    public AnimationTimer getTimer() { return timer; }
    public long getLastTimerCall() { return lastTimerCall; }
    public long getLastAnimCall() { return lastAnimCall; }
    public long getLastBonusAnimCall() { return lastBonusAnimCall; }
    public long getLastEnemyUpdateCall() { return lastEnemyUpdateCall; }
    public long getLastOneSecondCheck() { return lastOneSecondCheck; }
    public Canvas getBkgCanvas() { return bkgCanvas; }
    public Canvas getCanvas() { return canvas; }
    public Canvas getBrdrCanvas() { return brdrCanvas; }
    public Paddle getPaddle() { return paddle; }
    public List<Ball> getBalls() { return balls; }
    public List<Block> getBlocks() { return blocks; }
    public List<BonusBlock> getBonusBlocks() { return bonusBlocks; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Torpedo> getTorpedoes() { return torpedoes; }
    public int getNoOfLifes() { return noOfLifes; }
    public long getScore() { return score; }
    public int getLevel() { return level; }
    public EnumDefinitions.PaddleState getPaddleState() { return paddleState; }
    public boolean isStickyPaddle() { return stickyPaddle; }
    public boolean isNextLevelDoorOpen() { return nextLevelDoorOpen; }
    public boolean isMovingPaddleOut() { return movingPaddleOut; }
    public int getAnimateInc() { return animateInc; }
    public List<Blink> getBlinks() { return blinks; }
    public double getBallSpeed() { return ballSpeed; }
    public boolean isReadyLevelVisible() { return readyLevelVisible; }
    public int getPaddleResetCounter() { return paddleResetCounter; }
    public int getSpeedResetCounter() { return speedResetCounter; }
    public int getNextLevelDoorCounter() { return nextLevelDoorCounter; }
    public double getNextLevelDoorAlpha() { return nextLevelDoorAlpha; }
    public OpenDoor getOpenDoor() { return openDoor; }
    public int getSilverBlockMaxHits() { return silverBlockMaxHits; }
    public int getBlockCounter() { return blockCounter; }
    public List<Explosion> getExplosions() { return explosions; }
    public Pos getEnemySpawnPosition() { return enemySpawnPosition; }
    public double getTopLeftDoorAlpha() { return topLeftDoorAlpha; }
    public double getTopRightDoorAlpha() { return topRightDoorAlpha; }
    public FIFO<Block> getBlockFifo() { return blockFifo; }
    public GraphicsContext getBrdrCtx() {
        return brdrCtx;
    }

    public void setExecutor(ScheduledExecutorService executor) { this.executor = executor; }
    public void setRunning(boolean running) { this.running = running; }
    public void setGameStartTime(Instant gameStartTime) { this.gameStartTime = gameStartTime; }
    public void setLevelStartTime(long levelStartTime) { this.levelStartTime = levelStartTime; }
    public void setTimer(AnimationTimer timer) { this.timer = timer; }
    public void setLastTimerCall(long lastTimerCall) { this.lastTimerCall = lastTimerCall; }
    public void setLastAnimCall(long lastAnimCall) { this.lastAnimCall = lastAnimCall; }
    public void setLastBonusAnimCall(long lastBonusAnimCall) { this.lastBonusAnimCall = lastBonusAnimCall; }
    public void setLastEnemyUpdateCall(long lastEnemyUpdateCall) { this.lastEnemyUpdateCall = lastEnemyUpdateCall; }
    public void setLastOneSecondCheck(long lastOneSecondCheck) { this.lastOneSecondCheck = lastOneSecondCheck; }
    public void setBkgCanvas(Canvas bkgCanvas) { this.bkgCanvas = bkgCanvas; }
    public void setBkgCtx(GraphicsContext bkgCtx) { this.bkgCtx = bkgCtx; }
    public void setCanvas(Canvas canvas) { this.canvas = canvas; }
    public void setCtx(GraphicsContext ctx) { this.ctx = ctx; }
    public void setBrdrCanvas(Canvas brdrCanvas) { this.brdrCanvas = brdrCanvas; }
    public void setBrdrCtx(GraphicsContext brdrCtx) { this.brdrCtx = brdrCtx; }
    public void setPaddle(Paddle paddle) { this.paddle = paddle; }
    public void setBalls(List<Ball> balls) { this.balls = balls; }
    public void setBlocks(List<Block> blocks) { this.blocks = blocks; }
    public void setBonusBlocks(List<BonusBlock> bonusBlocks) { this.bonusBlocks = bonusBlocks; }
    public void setEnemies(List<Enemy> enemies) { this.enemies = enemies; }
    public void setTorpedoes(List<Torpedo> torpedoes) { this.torpedoes = torpedoes; }
    public void setNoOfLifes(int noOfLifes) { this.noOfLifes = noOfLifes; }
    public void setScore(long score) { this.score = score; }
    public void setHighscore(long highscore) { this.highscore = highscore; }
    public void setLevel(int level) { this.level = level; }
    public void setPaddleState(EnumDefinitions.PaddleState paddleState) { this.paddleState = paddleState; }
    public void setStickyPaddle(boolean stickyPaddle) { this.stickyPaddle = stickyPaddle; }
    public void setNextLevelDoorOpen(boolean nextLevelDoorOpen) { this.nextLevelDoorOpen = nextLevelDoorOpen; }
    public void setMovingPaddleOut(boolean movingPaddleOut) { this.movingPaddleOut = movingPaddleOut; }
    public void setAnimateInc(int animateInc) { this.animateInc = animateInc; }
    public void setBlinks(List<Blink> blinks) { this.blinks = blinks; }
    public void setBallSpeed(double ballSpeed) { this.ballSpeed = ballSpeed; }
    public void setReadyLevelVisible(boolean readyLevelVisible) { this.readyLevelVisible = readyLevelVisible; }
    public void setPaddleResetCounter(int paddleResetCounter) { this.paddleResetCounter = paddleResetCounter; }
    public void setSpeedResetCounter(int speedResetCounter) { this.speedResetCounter = speedResetCounter; }
    public void setNextLevelDoorCounter(int nextLevelDoorCounter) { this.nextLevelDoorCounter = nextLevelDoorCounter; }
    public void setNextLevelDoorAlpha(double nextLevelDoorAlpha) { this.nextLevelDoorAlpha = nextLevelDoorAlpha; }
    public void setOpenDoor(OpenDoor openDoor) { this.openDoor = openDoor; }
    public void setShowStartHint(boolean showStartHint) { this.showStartHint = showStartHint; }
    public void setSilverBlockMaxHits(int silverBlockMaxHits) { this.silverBlockMaxHits = silverBlockMaxHits; }
    public void setBlockCounter(int blockCounter) { this.blockCounter = blockCounter; }
    public void setExplosions(List<Explosion> explosions) { this.explosions = explosions; }
    public void setEnemySpawnPosition(Pos enemySpawnPosition) { this.enemySpawnPosition = enemySpawnPosition; }
    public void setTopLeftDoorAlpha(double topLeftDoorAlpha) { this.topLeftDoorAlpha = topLeftDoorAlpha; }
    public void setTopRightDoorAlpha(double topRightDoorAlpha) { this.topRightDoorAlpha = topRightDoorAlpha; }
    public void setBlockFifo(FIFO<Block> blockFifo) { this.blockFifo = blockFifo; }
    public void setMouseHandler(EventHandler<MouseEvent> mouseHandler) { this.mouseHandler = mouseHandler; }

    @Override
    public void init() {
        running = false;
        paddleState = EnumDefinitions.PaddleState.STANDARD;
        highscore = PropertyManager.INSTANCE.getLong(Constants.HIGHSCORE_KEY, 0);
        level = 2;
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

        timer = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                if (running && gameState == GameState.PLAYING) {
                    if (now > lastOneSecondCheck + 1_000_000_000L) {
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

                    if (now > lastBonusAnimCall + 50_000_000L) {
                        bonusBlocks.forEach(bonusBlock -> bonusBlock.update());
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

                    if (now > lastAnimCall + 5_000_000L) {
                        animateInc++;
                        lastAnimCall = now;
                    }

                    if (now > lastTimerCall) {
                        hitTest.hitTests();
                        if (movingLeft) movePaddleLeft();
                        else if (movingRight) movePaddleRight();
                        else paddle.vX = 0;
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

        bkgCanvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        bkgCtx = bkgCanvas.getGraphicsContext2D();
        canvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        ctx = canvas.getGraphicsContext2D();
        brdrCanvas = new Canvas(GameConstants.WIDTH, GameConstants.HEIGHT);
        brdrCtx = brdrCanvas.getGraphicsContext2D();
        brdrCanvas.setMouseTransparent(true);

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

        loadImages.loadImages(images, this);

        paddle = new Paddle(this);
        balls = new CopyOnWriteArrayList<>();
        blocks = new CopyOnWriteArrayList<>();
        bonusBlocks = new CopyOnWriteArrayList<>();
        enemies = new CopyOnWriteArrayList<>();
        explosions = new CopyOnWriteArrayList<>();
        torpedoes = new CopyOnWriteArrayList<>();
        noOfLifes = 3;
        score = 0;
        ensurePlayerFileExists();
    }

    @Override
    public void start(final Stage stage) {
        gameStartTime = Instant.now();
        final StackPane pane = new StackPane(bkgCanvas, canvas, brdrCanvas);
        final Scene scene = new Scene(pane, GameConstants.WIDTH, GameConstants.HEIGHT);

        scene.setOnKeyPressed(e -> handleKeyPressed(e));
        scene.setOnKeyTyped(e -> handleKeyTyped(e));
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        scene.setOnKeyTyped(this::handleKeyTyped);

        stage.setTitle("Arkanoid");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        playSound(autoClips.gameStartSnd);

        renderStartMenu();
        timer.start();
    }

    private void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        if (gameState == GameState.START_MENU) {
            switch (code) {
                case UP -> {
                    menuIndex = (menuIndex - 1 + 2) % 2;
                    renderStartMenu();
                }
                case DOWN -> {
                    menuIndex = (menuIndex + 1) % 2;
                    renderStartMenu();
                }
                case SPACE -> {
                    if (menuIndex == 0) {
                        enterAsNewPlayer();
                    } else {
                        gameState = GameState.LOGIN_SCREEN;
                        inputUsername = "";
                        inputPassword = "";
                        enteringUsername = true;
                        enteringPassword = false;
                        loginMenuIndex = 0;
                        renderLoginScreen();
                    }
                }
            }
        } else if (gameState == GameState.LOGIN_SCREEN) {
            switch (code) {
                case UP -> {
                    loginMenuIndex = (loginMenuIndex - 1 + 3) % 3;
                    enteringUsername = loginMenuIndex == 0;
                    enteringPassword = loginMenuIndex == 1;
                    renderLoginScreen();
                }
                case DOWN -> {
                    loginMenuIndex = (loginMenuIndex + 1) % 3;
                    enteringUsername = loginMenuIndex == 0;
                    enteringPassword = loginMenuIndex == 1;
                    renderLoginScreen();
                }
                case SPACE -> {
                    if (loginMenuIndex == 2) {
                        gameState = GameState.START_MENU;
                        menuIndex = 0;
                        renderStartMenu();
                    } else {
                        if (loginMenuIndex == 0) enteringUsername = true;
                        if (loginMenuIndex == 1) enteringPassword = true;
                        renderLoginScreen();
                    }
                }
                case ENTER -> {
                    attemptLogin();
                }
                case ESCAPE -> {
                    gameState = GameState.START_MENU;
                    menuIndex = 0;
                    renderStartMenu();
                }
            }
        } else if (gameState == GameState.PLAYING) {
            if (code == KeyCode.ESCAPE) {
                gameState = GameState.PAUSE_MENU;
                pauseIndex = 0;
                renderPauseMenu();
            } else if (running && !movingPaddleOut) {
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


    } else if (gameState == GameState.PAUSE_MENU) {
            switch (code) {
                case UP -> {
                    pauseIndex = (pauseIndex - 1 + 2) % 2;
                    renderPauseMenu();
                }
                case DOWN -> {
                    pauseIndex = (pauseIndex + 1) % 2;
                    renderPauseMenu();
                }
                case SPACE -> {
                    if (pauseIndex == 1) {
                        exitWithoutSave();
                    } else {
                        gameState = GameState.SAVE_CREDENTIALS;
                        inputUsername = "";
                        inputPassword = "";
                        enteringUsername = true;
                        enteringPassword = false;
                        renderSaveCredentials();
                    }
                }
                case ESCAPE -> {
                    gameState = GameState.PLAYING;
                    renderDuringGame();
                }
            }
        } else if (gameState == GameState.SAVE_CREDENTIALS) {
            switch (code) {
                case UP -> {
                    if (enteringPassword) {
                        enteringPassword = false;
                        enteringUsername = true;
                    }
                    renderSaveCredentials();
                }
                case DOWN -> {
                    if (enteringUsername) {
                        enteringUsername = false;
                        enteringPassword = true;
                    }
                    renderSaveCredentials();
                }
                case ENTER -> {
                    if (!inputUsername.isBlank() && !inputPassword.isBlank()) {
                        savePlayer(inputUsername.trim(), inputPassword.trim());
                        exitAfterSave();
                    }
                }
                case ESCAPE -> {
                    gameState = GameState.PAUSE_MENU;
                    renderPauseMenu();
                }
            }
        }

    }


    private void handleKeyTyped(KeyEvent e) {
        String ch = e.getCharacter();
        if (gameState == GameState.LOGIN_SCREEN) {
            if (enteringUsername) {
                if ("\b".equals(ch)) {
                    if (!inputUsername.isEmpty()) inputUsername = inputUsername.substring(0, inputUsername.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputUsername += ch;
                }
                renderLoginScreen();
            } else if (enteringPassword) {
                if ("\b".equals(ch)) {
                    if (!inputPassword.isEmpty()) inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputPassword += ch;
                }
                renderLoginScreen();
            }
        } else if (gameState == GameState.SAVE_CREDENTIALS) {
            if (enteringUsername) {
                if ("\b".equals(ch)) {
                    if (!inputUsername.isEmpty()) inputUsername = inputUsername.substring(0, inputUsername.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputUsername += ch;
                }
                renderSaveCredentials();
            } else if (enteringPassword) {
                if ("\b".equals(ch)) {
                    if (!inputPassword.isEmpty()) inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                } else if (!"\r".equals(ch) && !"\n".equals(ch)) {
                    inputPassword += ch;
                }
                renderSaveCredentials();
            }
        } else if (gameState == GameState.START_MENU) {
            // ignore
        } else if (gameState == GameState.PLAYING) {
            // ignore typed chars during game
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


    private void enterAsNewPlayer() {
        running = true;
        gameState = GameState.PLAYING;
        level = 1;
        startLevel.startLevel(level);
        renderDuringGame();
    }

    private void attemptLogin() {
        if (checkLogin(inputUsername.trim(), inputPassword.trim())) {
            running = true;
            gameState = GameState.PLAYING;
            level = 1;
            startLevel.startLevel(level);
            renderDuringGame();
        } else {
            loginFailed = true;
            inputPassword = "";
            renderLoginScreen();
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
        System.exit(0);
    }

    private void renderStartMenu() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        ctx.setFont(GameConstants.UI_FONT);
        ctx.setGlobalAlpha(1.0);
        String s1 = "New Player";
        String s2 = "Old Player";
        double x = GameConstants.WIDTH / 2.0 - 100;;
        double y = 360;
        ctx.fillText(s1, x, y);
        ctx.fillText(s2, x, y + 40);
        if (menuIndex == 0) {
            ctx.fillText(">", x - 30, y);
        } else {
            ctx.fillText(">", x - 30, y + 40);
        }
        drawBorder.drawBorder();
    }

    private void renderLoginScreen() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        ctx.setFont(GameConstants.UI_FONT);
        double x = GameConstants.WIDTH / 2.0 - 100;;
        double y = 360;
        ctx.fillText("Username: " + inputUsername + (enteringUsername ? "_" : ""), x, y);
        ctx.fillText("Password: " + mask(inputPassword) + (enteringPassword ? "_" : ""), x, y + 40);
        ctx.fillText("New Player (back)", x, y + 100);
        if (loginMenuIndex == 0) ctx.fillText(">", x - 30, y);
        if (loginMenuIndex == 1) ctx.fillText(">", x - 30, y + 40);
        if (loginMenuIndex == 2) ctx.fillText(">", x - 30, y + 100);
        if (loginFailed) {
            ctx.fillText("Login failed. Try again.", x, y + 140);
        }
        drawBorder.drawBorder();
    }

    private void renderPauseMenu() {
        drawBorder.drawBorder();
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        drawBackground.drawBackground(0);
        ctx.setFont(GameConstants.UI_FONT);
        double x = GameConstants.WIDTH / 2.0 - 100;
        double y = 260;
        ctx.fillText("Save", x, y);
        ctx.fillText("Not Save", x, y + 40);
        ctx.fillText(">", x - 20, y + pauseIndex * 40);
        drawBorder.drawBorder();
    }

    private void renderSaveCredentials() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        drawBackground.drawBackground(0);
        ctx.setFont(GameConstants.UI_FONT);
        double x = GameConstants.WIDTH / 2.0 - 100;
        double y = 260;
        ctx.fillText("Enter username: " + inputUsername + (enteringUsername ? "_" : ""), x, y);
        ctx.fillText("Enter password: " + mask(inputPassword) + (enteringPassword ? "_" : ""), x, y + 40);
        ctx.fillText("Press Enter to save and exit", x, y + 100);
        drawBorder.drawBorder();
    }

    private void renderDuringGame() {
        drawBackground.drawBackground(level);
        updateAndDraw.updateAndDraw();
        drawBorder.drawBorder();
    }

    private String mask(String s) {
        return "*".repeat(Math.max(0, s.length()));
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
                    enemies.add(new Enemy(this,100 + images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
            case TOP_RIGHT ->
                    enemies.add(new Enemy(this,GameConstants.WIDTH - 100 - images.topDoorImg.getWidth() * 0.5 - GameConstants.ENEMY_WIDTH * 0.5, GameConstants.UPPER_INSET, EnumDefinitions.EnemyType.MOLECULE));
        }
    }

    public void spawnBall() {
        if (balls.size() > 0) {
            return;
        }
        balls.add(new Ball(this, images.ballImg, paddle.bounds.centerX, paddle.bounds.minY - images.ballImg.getHeight() * 0.5 - 1, (GameConstants.RND.nextDouble() * (2 * ballSpeed) - ballSpeed)));
    }

    public void startScreen() {
        ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        drawBackground.drawBackground(1);
        drawBorder.drawBorder();
    }

    private void startLevel(final int level) {
        startLevel.startLevel(level);
    }

    private void gameOver() {
        gameOver.gameOver();
    }

    private void setupBlocks(final int level) {
        setupBlocks.setupBlocks(level);
    }

    private void hitTests() {
        hitTest.hitTests();
    }

    public void drawBackground(final int level) {
        drawBackground.drawBackground(level);
    }

    public void updateAndDraw() {
        updateAndDraw.updateAndDraw();
    }

    public void drawBorder() {
        drawBorder.drawBorder();
    }

    private void ensurePlayerFileExists() {
        try {
            if (!Files.exists(PLAYER_FILE)) {
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

    public static void main(String[] args) {
        launch(args);
    }
}

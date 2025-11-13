package com.arkanoid.controllers;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import com.arkanoid.config.PropertyManager;
import com.arkanoid.models.Constants;
import com.arkanoid.models.EnumDefinitions;
import com.arkanoid.models.Objects.Paddle;
import com.arkanoid.resources.AutoClips;

import java.util.concurrent.TimeUnit;

public class GameOver {
    private final Main main;

    public GameOver(Main main) {
        this.main = main;
    }

    // Game Over
    public void gameOver() {
        main.executor.schedule(main::startScreen, 5, TimeUnit.SECONDS);

        main.playSound(AutoClips.gameOverSnd);

        // RESET TOÀN BỘ GAME STATE
        resetGameState();

        main.running = false;
        main.balls.clear();
        main.torpedoes.clear();
        main.needBackgroundRedraw = true;

        main.gameRenderer.drawGame();

        if (main.score > main.highscore) {
            PropertyManager.INSTANCE.setLong(Constants.HIGHSCORE_KEY, main.score);
            main.highscore = main.score;
        }
        PropertyManager.INSTANCE.storeProperties();

        // SETUP LẠI BLOCKS CHO LEVEL HIỆN TẠI
        main.setupBlocks.setupBlocks(main.level);

        // CHUẨN BỊ CHO LẦN CHƠI TIẾP THEO
        main.showStartHint = true;
    }

    private void resetGameState() {
        main.noOfLifes = 3;
        main.score = 0;
        main.balls.clear();
        main.blocks.clear();
        main.bonusBlocks.clear();
        main.enemies.clear();
        main.explosions.clear();
        main.torpedoes.clear();
        main.blinks.clear();
        main.needBackgroundRedraw = true;
        main.paddleState = EnumDefinitions.PaddleState.STANDARD;
        main.stickyPaddle = false;
        main.nextLevelDoorOpen = false;
        main.movingPaddleOut = false;
        main.ballSpeed = GameConstants.BALL_SPEED;
        main.readyLevelVisible = false;
        // Khởi tạo lại paddle
        main.paddle = new Paddle(main);
    }
}
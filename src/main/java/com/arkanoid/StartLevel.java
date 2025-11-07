package com.arkanoid;

import com.arkanoid.resources.AutoClips;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class StartLevel {
    private final Main main;

    public StartLevel(Main main) {
        this.main = main;
    }

    // Start Level
    public void startLevel(final int level) {
        main.levelStartTime = Instant.now().getEpochSecond();
        main.blockCounter = 0;
        main.nextLevelDoorAlpha = 1.0;
        main.nextLevelDoorOpen = false;
        main.movingPaddleOut = false;
        main.paddle.countX = 0;
        main.paddle.countY = 0;
        main.animateInc  = 0 ;
        main.paddle.x = GameConstants.WIDTH * 0.5 - main.paddleState.width * 0.5;
        main.paddle.bounds.minX = main.paddle.x+ main.paddle.width * 0.5;
        main.readyLevelVisible = true;
        main.playSound(AutoClips.startLevelSnd);
        main.setupBlocks.setupBlocks(level);
        main.bonusBlocks.clear();
        main.balls.clear();
        main.enemies.clear();
        main.explosions.clear();
        main.spawnBall();
        if (!main.running) {
            main.running = true;
        }
        main.drawBackground.drawBackground(level);
        main.drawBorder.drawBorder();
        main.updateAndDraw.updateAndDraw();
        main.executor.schedule(() -> {
            main.readyLevelVisible = false;
        }, 2, TimeUnit.SECONDS);
    }
}
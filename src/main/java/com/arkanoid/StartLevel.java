package com.arkanoid;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class StartLevel {
    private final Main main;

    public StartLevel(Main main) {
        this.main = main;
    }

    // Start Level
    void startLevel(final int level) {
        main.setLevelStartTime(Instant.now().getEpochSecond());
        main.setBlockCounter(0);
        main.setNextLevelDoorAlpha(1.0);
        main.setNextLevelDoorOpen(false);
        main.setMovingPaddleOut(false);
        main.getPaddle().countX = 0;
        main.getPaddle().countY = 0;
        main.setAnimateInc(0);
        main.getPaddle().x = GameConstants.WIDTH * 0.5 - main.getPaddleState().width * 0.5;
        main.getPaddle().bounds.minX = main.getPaddle().x - main.getPaddle().width * 0.5;
        main.setReadyLevelVisible(true);
        main.playSound(main.getAutoClips().startLevelSnd);
        main.getSetupBlocks().setupBlocks(level);
        main.getBonusBlocks().clear();
        main.getBalls().clear();
        main.getEnemies().clear();
        main.getExplosions().clear();
        main.spawnBall();
        if (!main.isRunning()) {
            main.setRunning(true);
        }
        main.getDrawBackground().drawBackground(level);
        main.drawBorder();
        main.updateAndDraw();
        main.getExecutor().schedule(() -> {
            main.setReadyLevelVisible(false);
        }, 2, TimeUnit.SECONDS);
    }
}
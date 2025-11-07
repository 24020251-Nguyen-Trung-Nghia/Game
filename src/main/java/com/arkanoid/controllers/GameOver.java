package com.arkanoid.controllers;

import com.arkanoid.Main;
import com.arkanoid.config.PropertyManager;
import com.arkanoid.models.Constants;
import com.arkanoid.models.EnumDefinitions;

import java.util.concurrent.TimeUnit;

public class GameOver {
    private final Main main;

    public GameOver(Main main) {
        this.main = main;
    }

    // Game Over
    public void gameOver() {
        main.executor.schedule(() -> main.startScreen(), 5, TimeUnit.SECONDS);

        main.playSound(main.autoClips.gameOverSnd);


        main.running = false;
        main.balls.clear();
        main.torpedoes.clear();

        main.draw.drawGame();

        if (main.score > main.highscore) {
            PropertyManager.INSTANCE.setLong(Constants.HIGHSCORE_KEY, main.score);
            main.highscore = main.score;
        }
        PropertyManager.INSTANCE.storeProperties();
        main.score = 0;
        main.noOfLifes = 3;
        main.paddleState = EnumDefinitions.PaddleState.STANDARD;
    }
}
package com.arkanoid;

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

        main.updateAndDraw.updateAndDraw();

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
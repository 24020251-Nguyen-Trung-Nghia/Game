package com.arkanoid;

import java.util.concurrent.TimeUnit;

public class GameOver {
    private final Main main;

    public GameOver(Main main) {
        this.main = main;
    }

    // Game Over
    void gameOver() {
        main.getExecutor().schedule(() -> main.startScreen(), 5, TimeUnit.SECONDS);

        main.playSound(main.getAutoClips().gameOverSnd);

        main.setRunning(false);
        main.getBalls().clear();
        main.getTorpedoes().clear();

        main.updateAndDraw();

        if (main.getScore() > main.getHighscore()) {
            PropertyManager.INSTANCE.setLong(Constants.HIGHSCORE_KEY, main.getScore());
            main.setHighscore(main.getScore());
        }
        PropertyManager.INSTANCE.storeProperties();
        main.setScore(0);
        main.setNoOfLifes(3);
        main.setPaddleState(EnumDefinitions.PaddleState.STANDARD);
    }
}
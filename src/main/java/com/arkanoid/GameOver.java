package com.arkanoid;

import java.util.concurrent.TimeUnit;

public class GameOver {
    private final Main main;

    public GameOver(Main main) {
        this.main = main;
    }

    // Game Over
    void gameOver() {
        // Đợi 3 giây rồi quay về Level Select
        main.getExecutor().schedule(() -> {
            try {
                main.showLevelSelect();
            } catch (Exception e) {
                System.err.println("Lỗi khi quay về Level Select:");
                e.printStackTrace();
            }
        }, 3, TimeUnit.SECONDS);

        main.playSound(main.getAutoClips().gameOverSnd);

        main.setRunning(false);
        main.getBalls().clear();
        main.getTorpedoes().clear();

        main.updateAndDraw();

        // Lưu high score nếu phá kỷ lục
        if (main.getScore() > main.getHighscore()) {
            PropertyManager.INSTANCE.setLong(Constants.HIGHSCORE_KEY, main.getScore());
            main.setHighscore(main.getScore());
        }
        PropertyManager.INSTANCE.storeProperties();

        // Reset game state
        main.setScore(0);
        main.setNoOfLifes(3);
        main.setPaddleState(EnumDefinitions.PaddleState.STANDARD);
    }
}

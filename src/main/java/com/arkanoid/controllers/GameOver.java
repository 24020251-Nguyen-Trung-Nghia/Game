package com.arkanoid.controllers;

import com.arkanoid.Main;
import com.arkanoid.config.PropertyManager;
import com.arkanoid.models.Constants;
import com.arkanoid.resources.AutoClips;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;

public class GameOver {
    private final Main main;

    public GameOver(Main main) {
        this.main = main;
    }

    // Game Over
    public void gameOver() {
        main.playSound(AutoClips.gameOverSnd);

        main.running = false;
        main.balls.clear();
        main.torpedoes.clear();

        // Cập nhật highscore
        if (main.score > main.highscore) {
            PropertyManager.INSTANCE.setLong(Constants.HIGHSCORE_KEY, main.score);
            main.highscore = main.score;
            PropertyManager.INSTANCE.storeProperties();
        }
        PropertyManager.INSTANCE.storeProperties();

        // Vẽ màn hình game over
        main.gameRenderer.drawGame();

        // Chuyển về màn hình chọn level sau 2 giây
        main.executor.schedule(() -> {
            Platform.runLater(() -> {
                System.out.println("🔄 Chuyển về màn hình chọn level sau Game Over");
                main.showLevelSelect();
            });
        }, 2, TimeUnit.SECONDS);
    }
}
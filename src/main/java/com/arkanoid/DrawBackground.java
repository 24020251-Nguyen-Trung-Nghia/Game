package com.arkanoid;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class DrawBackground {
    private final Main main;

    public DrawBackground(Main main) {
        this.main = main;
    }

    // ******************** Redraw ********************************************
    void drawBackground(final int level) {
        main.getBkgCtx().clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        main.getBkgCtx().setFill(Color.BLACK);
        main.getBkgCtx().fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        if (main.isRunning()) {
            // Use background pattern related to level
            if (level % 4 == 0) {
                main.getBkgCtx().setFill(main.getImages().bkgPatternFill4);
            } else if (level % 3 == 0) {
                main.getBkgCtx().setFill(main.getImages().bkgPatternFill3);
            } else if (level % 2 == 0) {
                main.getBkgCtx().setFill(main.getImages().bkgPatternFill2);
            } else {
                main.getBkgCtx().setFill(main.getImages().bkgPatternFill1);
            }
            main.getBkgCtx().fillRect(0, GameConstants.UPPER_INSET, GameConstants.WIDTH, GameConstants.HEIGHT);

            // Draw shadow
            main.getBkgCtx().setFill(Color.rgb(0, 0, 0, 0.3));
            main.getBkgCtx().fillRect(0, GameConstants.UPPER_INSET, 40, GameConstants.HEIGHT);
            main.getBkgCtx().fillRect(0, GameConstants.UPPER_INSET, GameConstants.WIDTH, 20);
        } else {
            main.getCtx().setFont(GameConstants.SCORE_FONT);
            main.getCtx().setTextBaseline(VPos.TOP);
            main.getCtx().setFill(GameConstants.HIGH_SCORE_RED);
            main.getCtx().setTextAlign(TextAlignment.CENTER);
            main.getCtx().fillText("HIGH SCORE", GameConstants.WIDTH * 0.5, 0);
            main.getCtx().setFill(GameConstants.SCORE_WHITE);
            main.getCtx().fillText(Long.toString(main.getHighscore()), GameConstants.WIDTH * 0.5, 30);

            if (main.isShowStartHint()) {
                main.getCtx().fillText("Hit space to start", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.6);
            }

            main.getBkgCtx().drawImage(main.getImages().logoImg, (GameConstants.WIDTH - main.getImages().logoImg.getWidth()) * 0.5, GameConstants.HEIGHT * 0.25);

            main.getBkgCtx().drawImage(main.getImages().copyrightImg, (GameConstants.WIDTH - main.getImages().copyrightImg.getWidth()) * 0.5, GameConstants.HEIGHT * 0.75);
        }
    }
}
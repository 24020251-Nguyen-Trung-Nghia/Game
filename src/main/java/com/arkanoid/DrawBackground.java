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
        main.bkgCtx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        main.bkgCtx.setFill(Color.BLACK);
        main.bkgCtx.fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        if (main.running) {
            // Use background pattern related to level
            if (level % 4 == 0) {
                main.bkgCtx.setFill(main.getImages().bkgPatternFill4);
            } else if (level % 3 == 0) {
                main.bkgCtx.setFill(main.getImages().bkgPatternFill3);
            } else if (level % 2 == 0) {
                main.bkgCtx.setFill(main.getImages().bkgPatternFill2);
            } else {
                main.bkgCtx.setFill(main.getImages().bkgPatternFill1);
            }
            main.bkgCtx.fillRect(0, GameConstants.UPPER_INSET, GameConstants.WIDTH, GameConstants.HEIGHT);

            // Draw shadow
            main.bkgCtx.setFill(Color.rgb(0, 0, 0, 0.3));
            main.bkgCtx.fillRect(0, GameConstants.UPPER_INSET, 40, GameConstants.HEIGHT);
            main.bkgCtx.fillRect(0, GameConstants.UPPER_INSET, GameConstants.WIDTH, 20);
        } else {
            main.ctx.setFont(GameConstants.SCORE_FONT);
            main.ctx.setTextBaseline(VPos.TOP);
            main.ctx.setFill(GameConstants.HIGH_SCORE_RED);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("HIGH SCORE", GameConstants.WIDTH * 0.5, 0);
            main.ctx.setFill(GameConstants.SCORE_WHITE);
            main.ctx.fillText(Long.toString(main.highscore), GameConstants.WIDTH * 0.5, 30);

            if (main.showStartHint) {
                main.ctx.fillText("Hit space to start", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.6);
            }

            main.bkgCtx.drawImage(main.getImages().logoImg, (GameConstants.WIDTH - main.getImages().logoImg.getWidth()) * 0.5, GameConstants.HEIGHT * 0.25);

            main.bkgCtx.drawImage(main.getImages().copyrightImg, (GameConstants.WIDTH - main.getImages().copyrightImg.getWidth()) * 0.5, GameConstants.HEIGHT * 0.75);
        }
    }
}
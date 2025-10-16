package com.arkanoid;

public class DrawBorder {
    private final Main main;

    public DrawBorder(Main main) {
        this.main = main;
    }

    public void drawBorder() {
        main.getBrdrCtx().clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        if (main.isRunning()) {
            // Draw top border
            main.getBrdrCtx().setFill(main.getImages().pipePatternFill);
            main.getBrdrCtx().fillRect(17, 68, 83, 17);
            main.getBrdrCtx().fillRect(100 + main.getImages().topDoorImg.getWidth(), 68, GameConstants.WIDTH - 200 - 2 * main.getImages().topDoorImg.getWidth(), 17);
            main.getBrdrCtx().fillRect(GameConstants.WIDTH - 100, 68, 83, 17);

            // Draw vertical border
            main.getBrdrCtx().setFill(main.getImages().borderPatternFill);
            main.getBrdrCtx().fillRect(0, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT - GameConstants.UPPER_INSET);
            if (main.isNextLevelDoorOpen()) {
                main.getBrdrCtx().fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, 563);
                main.getBrdrCtx().fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565 + main.getImages().borderPartVerticalImg.getHeight(), 20, 100);
            } else {
                main.getBrdrCtx().fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT);
            }

            // Draw border corners
            main.getBrdrCtx().drawImage(main.getImages().ulCornerImg, 2.5, 67.5);
            main.getBrdrCtx().drawImage(main.getImages().urCornerImg, GameConstants.WIDTH - main.getImages().urCornerImg.getWidth() - 2.5, 67.5);

            // Draw next level door
            if (main.isNextLevelDoorOpen()) {
                for (int i = 0; i < 6; i++) {
                    main.getBrdrCtx().drawImage(main.getImages().borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    if (i < 5) {
                        main.getBrdrCtx().drawImage(main.getImages().borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                    }
                }
                if (main.getNextLevelDoorAlpha() > 0.01) {
                    main.setNextLevelDoorAlpha(main.getNextLevelDoorAlpha() - 0.01);
                }
                main.getBrdrCtx().save();
                main.getBrdrCtx().setGlobalAlpha(main.getNextLevelDoorAlpha());
                main.getBrdrCtx().drawImage(main.getImages().borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565);
                main.getBrdrCtx().restore();

                main.getOpenDoor().update();
                main.getCtx().drawImage(main.getImages().openDoorMapImg, main.getOpenDoor().countX * 20, 0, 20, 71, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565, 20, 71);
            } else {
                for (int i = 0; i < 6; i++) {
                    main.getBrdrCtx().drawImage(main.getImages().borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    main.getBrdrCtx().drawImage(main.getImages().borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                }
            }

            // Draw upper doors
            main.getBrdrCtx().save();
            main.getBrdrCtx().setGlobalAlpha(main.getTopLeftDoorAlpha());
            main.getBrdrCtx().drawImage(main.getImages().topDoorImg, 100, 65);
            main.getBrdrCtx().setGlobalAlpha(main.getTopRightDoorAlpha());
            main.getBrdrCtx().drawImage(main.getImages().topDoorImg, GameConstants.WIDTH - 100 - main.getImages().topDoorImg.getWidth(), 65);
            main.getBrdrCtx().restore();
        }
    }
}
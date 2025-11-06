package com.arkanoid;

public class DrawBorder {
    private final Main main;

    public DrawBorder(Main main) {
        this.main = main;
    }

    public void drawBorder() {
        main.brdrCtx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        if (main.running) {
            // Draw top border
            main.brdrCtx.setFill(main.images.pipePatternFill);
            main.brdrCtx.fillRect(17, 68, 83, 17);
            main.brdrCtx.fillRect(100 + main.images.topDoorImg.getWidth(), 68, GameConstants.WIDTH - 200 - 2 * main.images.topDoorImg.getWidth(), 17);
            main.brdrCtx.fillRect(GameConstants.WIDTH - 100, 68, 83, 17);

            // Draw vertical border
            main.brdrCtx.setFill(main.images.borderPatternFill);
            main.brdrCtx.fillRect(0, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT - GameConstants.UPPER_INSET);
            if (main.nextLevelDoorOpen) {
                main.brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, 563);
                main.brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565 + main.images.borderPartVerticalImg.getHeight(), 20, 100);
            } else {
                main.brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT);
            }

            // Draw border corners
            main.brdrCtx.drawImage(main.images.ulCornerImg, 2.5, 67.5);
            main.brdrCtx.drawImage(main.images.urCornerImg, GameConstants.WIDTH - main.images.urCornerImg.getWidth() - 2.5, 67.5);

            // Draw next level door
            if (main.nextLevelDoorOpen) {
                for (int i = 0; i < 6; i++) {
                    main.brdrCtx.drawImage(main.images.borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    if (i < 5) {
                        main.brdrCtx.drawImage(main.images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                    }
                }
                if (main.nextLevelDoorAlpha > 0.01) {
                    main.nextLevelDoorAlpha= main.nextLevelDoorAlpha - 0.01f;
                }
                main.brdrCtx.save();
                main.brdrCtx.setGlobalAlpha(main.nextLevelDoorAlpha);
                main.brdrCtx.drawImage(main.images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565);
                main.brdrCtx.restore();

                main.openDoor.update();
                main.ctx.drawImage(main.images.openDoorMapImg, main.openDoor.countX * 20, 0, 20, 71, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565, 20, 71);
            } else {
                for (int i = 0; i < 6; i++) {
                    main.brdrCtx.drawImage(main.images.borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    main.brdrCtx.drawImage(main.images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                }
            }

            // Draw upper doors
            main.brdrCtx.save();
            main.brdrCtx.setGlobalAlpha(main.topLeftDoorAlpha);
            main.brdrCtx.drawImage(main.images.topDoorImg, 100, 65);
            main.brdrCtx.setGlobalAlpha(main.topRightDoorAlpha);
            main.brdrCtx.drawImage(main.images.topDoorImg, GameConstants.WIDTH - 100 - main.images.topDoorImg.getWidth(), 65);
            main.brdrCtx.restore();
        }
    }
}
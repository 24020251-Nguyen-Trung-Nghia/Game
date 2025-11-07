package com.arkanoid.utils;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import com.arkanoid.resources.Images;

public record DrawBorder(Main main) {

    public void drawBorder() {
        main.brdrCtx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        if (main.running) {
            // Draw top border
            main.brdrCtx.setFill(Images.pipePatternFill);
            main.brdrCtx.fillRect(17, 68, 83, 17);
            main.brdrCtx.fillRect(100 + Images.topDoorImg.getWidth(), 68, GameConstants.WIDTH - 200 - 2 * Images.topDoorImg.getWidth(), 17);
            main.brdrCtx.fillRect(GameConstants.WIDTH - 100, 68, 83, 17);

            // Draw vertical border
            main.brdrCtx.setFill(Images.borderPatternFill);
            main.brdrCtx.fillRect(0, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT - GameConstants.UPPER_INSET);
            if (main.nextLevelDoorOpen) {
                main.brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, 563);
                main.brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565 + Images.borderPartVerticalImg.getHeight(), 20, 100);
            } else {
                main.brdrCtx.fillRect(GameConstants.WIDTH - 20, GameConstants.UPPER_INSET, 20, GameConstants.HEIGHT);
            }

            // Draw border corners
            main.brdrCtx.drawImage(Images.ulCornerImg, 2.5, 67.5);
            main.brdrCtx.drawImage(Images.urCornerImg, GameConstants.WIDTH - Images.urCornerImg.getWidth() - 2.5, 67.5);

            // Draw next level door
            if (main.nextLevelDoorOpen) {
                for (int i = 0; i < 6; i++) {
                    main.brdrCtx.drawImage(Images.borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    if (i < 5) {
                        main.brdrCtx.drawImage(Images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                    }
                }
                if (main.nextLevelDoorAlpha > 0.01) {
                    main.nextLevelDoorAlpha = main.nextLevelDoorAlpha - 0.01f;
                }
                main.brdrCtx.save();
                main.brdrCtx.setGlobalAlpha(main.nextLevelDoorAlpha);
                main.brdrCtx.drawImage(Images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565);
                main.brdrCtx.restore();

                main.openDoor.update();
                main.ctx.drawImage(Images.openDoorMapImg, main.openDoor.countX * 20, 0, 20, 71, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + 565, 20, 71);
            } else {
                for (int i = 0; i < 6; i++) {
                    main.brdrCtx.drawImage(Images.borderPartVerticalImg, 0, GameConstants.UPPER_INSET + i * 113);
                    main.brdrCtx.drawImage(Images.borderPartVerticalImg, GameConstants.WIDTH - 20, GameConstants.UPPER_INSET + i * 113);
                }
            }

            // Draw upper doors
            main.brdrCtx.save();
            main.brdrCtx.setGlobalAlpha(main.topLeftDoorAlpha);
            main.brdrCtx.drawImage(Images.topDoorImg, 100, 65);
            main.brdrCtx.setGlobalAlpha(main.topRightDoorAlpha);
            main.brdrCtx.drawImage(Images.topDoorImg, GameConstants.WIDTH - 100 - Images.topDoorImg.getWidth(), 65);
            main.brdrCtx.restore();
        }
    }
}
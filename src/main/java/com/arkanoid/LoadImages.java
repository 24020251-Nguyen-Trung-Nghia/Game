package com.arkanoid;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class LoadImages {
    public LoadImages() {
    }

    // ******************** Helper methods ************************************
    void loadImages(Images images, Main main) {
        images.logoImg = new Image(main.getClass().getResourceAsStream("arkanoid_logo.png"), 460, 118, true, false);
        images.copyrightImg = new Image(main.getClass().getResourceAsStream("copyright.png"), 458, 115, true, false);
        images.bkgPatternImg1 = new Image(main.getClass().getResourceAsStream("backgroundPattern_1.png"), 68, 117, true, false);
        images.bkgPatternImg2 = new Image(main.getClass().getResourceAsStream("backgroundPattern_2.png"), 64, 64, true, false);
        images.bkgPatternImg3 = new Image(main.getClass().getResourceAsStream("backgroundPattern_3.png"), 64, 64, true, false);
        images.bkgPatternImg4 = new Image(main.getClass().getResourceAsStream("backgroundPattern_4.png"), 64, 64, true, false);
        images.borderVerticalImg = new Image(main.getClass().getResourceAsStream("borderVertical.png"), 20, 113, true, false);
        images.borderPartVerticalImg = new Image(main.getClass().getResourceAsStream("borderPartVertical.png"), 20, 71, true, false);
        images.topDoorImg = new Image(main.getClass().getResourceAsStream("topDoor.png"), 64, 23, true, false);
        images.ulCornerImg = new Image(main.getClass().getResourceAsStream("upperLeftCorner.png"), 15, 20, true, false);
        images.urCornerImg = new Image(main.getClass().getResourceAsStream("upperRightCorner.png"), 15, 20, true, false);
        images.pipeImg = new Image(main.getClass().getResourceAsStream("pipe.png"), 5, 17, true, false);
        images.paddleMapStdImg = new Image(main.getClass().getResourceAsStream("paddlemap_std.png"), 640, 176, false, false);
        images.paddleMapWideImg = new Image(main.getClass().getResourceAsStream("paddlemap_wide.png"), 960, 176, false, false);
        images.paddleMapGunImg = new Image(main.getClass().getResourceAsStream("paddlemap_gun.png"), 640, 176, false, false);
        images.blinkMapImg = new Image(main.getClass().getResourceAsStream("blink_map.png"), 304, 60, false, false);
        images.paddleMiniImg = new Image(main.getClass().getResourceAsStream("paddle_std.png"), 40, 11, true, false);
        images.paddleStdShadowImg = new Image(main.getClass().getResourceAsStream("paddle_std_shadow.png"), 80, 22, true, false);
        images.paddleWideShadowImg = new Image(main.getClass().getResourceAsStream("paddle_wide_shadow.png"), 121, 22, true, false);
        images.paddleGunShadowImg = new Image(main.getClass().getResourceAsStream("paddle_gun_shadow.png"), 80, 22, true, false);
        images.ballImg = new Image(main.getClass().getResourceAsStream("ball.png"), 12, 12, true, false);
        images.ballShadowImg = new Image(main.getClass().getResourceAsStream("ball_shadow.png"), 12, 12, true, false);
        images.torpedoImg = new Image(main.getClass().getResourceAsStream("torpedo.png"), 41, 23, true, false);
        images.goldBlockImg = new Image(main.getClass().getResourceAsStream("goldBlock.png"), 38, 20, true, false);
        images.grayBlockImg = new Image(main.getClass().getResourceAsStream("grayBlock.png"), 38, 20, true, false);
        images.whiteBlockImg = new Image(main.getClass().getResourceAsStream("whiteBlock.png"), 38, 20, true, false);
        images.orangeBlockImg = new Image(main.getClass().getResourceAsStream("orangeBlock.png"), 38, 20, true, false);
        images.cyanBlockImg = new Image(main.getClass().getResourceAsStream("cyanBlock.png"), 38, 20, true, false);
        images.limeBlockImg = new Image(main.getClass().getResourceAsStream("limeBlock.png"), 38, 20, true, false);
        images.redBlockImg = new Image(main.getClass().getResourceAsStream("redBlock.png"), 38, 20, true, false);
        images.blueBlockImg = new Image(main.getClass().getResourceAsStream("blueBlock.png"), 38, 20, true, false);
        images.magentaBlockImg = new Image(main.getClass().getResourceAsStream("magentaBlock.png"), 38, 20, true, false);
        images.yellowBlockImg = new Image(main.getClass().getResourceAsStream("yellowBlock.png"), 38, 20, true, false);
        images.blockShadowImg = new Image(main.getClass().getResourceAsStream("block_shadow.png"), 38, 20, true, false);
        images.bonusBlockCMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_c.png"), 190, 72, true, false);
        images.bonusBlockFMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_f.png"), 190, 72, true, false);
        images.bonusBlockDMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_d.png"), 190, 72, true, false);
        images.bonusBlockSMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_s.png"), 190, 72, true, false);
        images.bonusBlockLMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_l.png"), 190, 72, true, false);
        images.bonusBlockBMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_b.png"), 190, 72, true, false);
        images.bonusBlockPMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_p.png"), 190, 72, true, false);
        images.openDoorMapImg = new Image(main.getClass().getResourceAsStream("open_door_map.png"), 120, 71, true, false);
        images.moleculeMapImg = new Image(main.getClass().getResourceAsStream("molecule_map.png"), 256, 96, true, false);
        images.explosionMapImg = new Image(main.getClass().getResourceAsStream("explosion_map.png"), 128, 128, true, false);
        images.bonusBlockShadowImg = new Image(main.getClass().getResourceAsStream("bonus_block_shadow.png"), 38, 18, true, false);

        images.bkgPatternFill1 = new ImagePattern(images.bkgPatternImg1, 0, 0, 68, 117, false);
        images.bkgPatternFill2 = new ImagePattern(images.bkgPatternImg2, 0, 0, 64, 64, false);
        images.bkgPatternFill3 = new ImagePattern(images.bkgPatternImg3, 0, 0, 64, 64, false);
        images.bkgPatternFill4 = new ImagePattern(images.bkgPatternImg4, 0, 0, 64, 64, false);
        images.borderPatternFill = new ImagePattern(images.borderVerticalImg, 0, 0, 20, 113, false);
        images.pipePatternFill = new ImagePattern(images.pipeImg, 0, 0, 5, 17, false);
    }
}
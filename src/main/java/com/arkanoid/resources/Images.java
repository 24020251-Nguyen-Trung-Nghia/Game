package com.arkanoid.resources;

import com.arkanoid.Main;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Images {
    private static Images instance;

    // Các biến hình ảnh
    public static Image logoImg;
    public static Image copyrightImg;
    public static Image bkgPatternImg1;
    public static Image bkgPatternImg2;
    public static Image bkgPatternImg3;
    public static Image bkgPatternImg4;
    public static ImagePattern bkgPatternFill1;
    public static ImagePattern bkgPatternFill2;
    public static ImagePattern bkgPatternFill3;
    public static ImagePattern bkgPatternFill4;
    public static ImagePattern borderPatternFill;
    public static Image borderVerticalImg;
    public static Image borderPartVerticalImg;
    public static Image topDoorImg;
    public static Image ulCornerImg;
    public static Image urCornerImg;
    public static Image pipeImg;
    public static ImagePattern pipePatternFill;
    public static Image paddleMiniImg;
    public static Image paddleStdShadowImg;
    public static Image paddleWideShadowImg;
    public static Image paddleGunShadowImg;
    public static Image paddleMapStdImg;
    public static Image paddleMapWideImg;
    public static Image paddleMapGunImg;
    public static Image blinkMapImg;
    public static Image ballImg;
    public static Image ballShadowImg;
    public static Image torpedoImg;
    public static Image goldBlockImg;
    public static Image grayBlockImg;
    public static Image whiteBlockImg;
    public static Image orangeBlockImg;
    public static Image cyanBlockImg;
    public static Image limeBlockImg;
    public static Image redBlockImg;
    public static Image blueBlockImg;
    public static Image magentaBlockImg;
    public static Image yellowBlockImg;
    public static Image bonusBlockCMapImg;
    public static Image bonusBlockFMapImg;
    public static Image bonusBlockDMapImg;
    public static Image bonusBlockSMapImg;
    public static Image bonusBlockLMapImg;
    public static Image bonusBlockBMapImg;
    public static Image bonusBlockPMapImg;
    public static Image openDoorMapImg;
    public static Image moleculeMapImg;
    public static Image blockShadowImg;
    public static Image bonusBlockShadowImg;
    public static Image explosionMapImg;
    public static Image loginBgImg;
    public static  Image startBgImg;
    public static Image saveBgImg;
    public static Image pauseBgImg;
    public static Image startscreenBgImg;



    // Constructor private để ngăn việc khởi tạo từ bên ngoài
    private Images() {}

    // Phương thức static để truy cập instance duy nhất
    public static Images getInstance() {
        if (instance == null) {
            instance = new Images();
        }
        return instance;
    }

    public static void loadImages(Main main) {
        logoImg = new Image(main.getClass().getResourceAsStream("arkanoid_logo.png"), 460, 118, true, false);
        copyrightImg = new Image(main.getClass().getResourceAsStream("copyright.png"), 458, 115, true, false);
        bkgPatternImg1 = new Image(main.getClass().getResourceAsStream("backgroundPattern_1.png"), 68, 117, true, false);
        bkgPatternImg2 = new Image(main.getClass().getResourceAsStream("backgroundPattern_2.png"), 64, 64, true, false);
        bkgPatternImg3 = new Image(main.getClass().getResourceAsStream("backgroundPattern_3.png"), 64, 64, true, false);
        bkgPatternImg4 = new Image(main.getClass().getResourceAsStream("backgroundPattern_4.png"), 64, 64, true, false);
        borderVerticalImg = new Image(main.getClass().getResourceAsStream("borderVertical.png"), 20, 113, true, false);
        borderPartVerticalImg = new Image(main.getClass().getResourceAsStream("borderPartVertical.png"), 20, 71, true, false);
        topDoorImg = new Image(main.getClass().getResourceAsStream("topDoor.png"), 64, 23, true, false);
        ulCornerImg = new Image(main.getClass().getResourceAsStream("upperLeftCorner.png"), 15, 20, true, false);
        urCornerImg = new Image(main.getClass().getResourceAsStream("upperRightCorner.png"), 15, 20, true, false);
        pipeImg = new Image(main.getClass().getResourceAsStream("pipe.png"), 5, 17, true, false);
        paddleMapStdImg = new Image(main.getClass().getResourceAsStream("paddlemap_std.png"), 640, 176, false, false);
        paddleMapWideImg = new Image(main.getClass().getResourceAsStream("paddlemap_wide.png"), 960, 176, false, false);
        paddleMapGunImg = new Image(main.getClass().getResourceAsStream("paddlemap_gun.png"), 640, 176, false, false);
        blinkMapImg = new Image(main.getClass().getResourceAsStream("blink_map.png"), 304, 60, false, false);
        paddleMiniImg = new Image(main.getClass().getResourceAsStream("paddle_std.png"), 40, 11, true, false);
        paddleStdShadowImg = new Image(main.getClass().getResourceAsStream("paddle_std_shadow.png"), 80, 22, true, false);
        paddleWideShadowImg = new Image(main.getClass().getResourceAsStream("paddle_wide_shadow.png"), 121, 22, true, false);
        paddleGunShadowImg = new Image(main.getClass().getResourceAsStream("paddle_gun_shadow.png"), 80, 22, true, false);
        ballImg = new Image(main.getClass().getResourceAsStream("ball.png"), 12, 12, true, false);
        ballShadowImg = new Image(main.getClass().getResourceAsStream("ball_shadow.png"), 12, 12, true, false);
        torpedoImg = new Image(main.getClass().getResourceAsStream("torpedo.png"), 41, 23, true, false);
        goldBlockImg = new Image(main.getClass().getResourceAsStream("goldBlock.png"), 38, 20, true, false);
        grayBlockImg = new Image(main.getClass().getResourceAsStream("grayBlock.png"), 38, 20, true, false);
        whiteBlockImg = new Image(main.getClass().getResourceAsStream("whiteBlock.png"), 38, 20, true, false);
        orangeBlockImg = new Image(main.getClass().getResourceAsStream("orangeBlock.png"), 38, 20, true, false);
        cyanBlockImg = new Image(main.getClass().getResourceAsStream("cyanBlock.png"), 38, 20, true, false);
        limeBlockImg = new Image(main.getClass().getResourceAsStream("limeBlock.png"), 38, 20, true, false);
        redBlockImg = new Image(main.getClass().getResourceAsStream("redBlock.png"), 38, 20, true, false);
        blueBlockImg = new Image(main.getClass().getResourceAsStream("blueBlock.png"), 38, 20, true, false);
        magentaBlockImg = new Image(main.getClass().getResourceAsStream("magentaBlock.png"), 38, 20, true, false);
        yellowBlockImg = new Image(main.getClass().getResourceAsStream("yellowBlock.png"), 38, 20, true, false);
        blockShadowImg = new Image(main.getClass().getResourceAsStream("block_shadow.png"), 38, 20, true, false);
        bonusBlockCMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_c.png"), 190, 72, true, false);
        bonusBlockFMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_f.png"), 190, 72, true, false);
        bonusBlockDMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_d.png"), 190, 72, true, false);
        bonusBlockSMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_s.png"), 190, 72, true, false);
        bonusBlockLMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_l.png"), 190, 72, true, false);
        bonusBlockBMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_b.png"), 190, 72, true, false);
        bonusBlockPMapImg = new Image(main.getClass().getResourceAsStream("block_map_bonus_p.png"), 190, 72, true, false);
        openDoorMapImg = new Image(main.getClass().getResourceAsStream("open_door_map.png"), 120, 71, true, false);
        moleculeMapImg = new Image(main.getClass().getResourceAsStream("molecule_map.png"), 256, 96, true, false);
        explosionMapImg = new Image(main.getClass().getResourceAsStream("explosion_map.png"), 128, 128, true, false);
        bonusBlockShadowImg = new Image(main.getClass().getResourceAsStream("bonus_block_shadow.png"), 38, 18, true,false);
        bkgPatternFill1 = new ImagePattern(bkgPatternImg1, 0, 0, 68, 117, false);
        bkgPatternFill2 = new ImagePattern(bkgPatternImg2, 0, 0, 64, 64, false);
        bkgPatternFill3 = new ImagePattern(bkgPatternImg3, 0, 0, 64, 64, false);
        bkgPatternFill4 = new ImagePattern(bkgPatternImg4, 0, 0, 64, 64, false);
        borderPatternFill = new ImagePattern(borderVerticalImg, 0, 0, 20, 113, false);
        pipePatternFill = new ImagePattern(pipeImg, 0, 0, 5, 17, false);
        saveBgImg = new Image(main.getClass().getResourceAsStream("save_background.png"), 560,740, false, false);
        pauseBgImg = new Image(main.getClass().getResourceAsStream("pause_background.png"),560,740,false,false);
        loginBgImg = new Image(main.getClass().getResourceAsStream("loginscreen.png"),560,740,false,false);
        startBgImg = new Image(main.getClass().getResourceAsStream("start.png"),560,740,false,false);
        startscreenBgImg= new Image(main.getClass().getResourceAsStream("startscreen.png"),560,740,false,false);

    }

}
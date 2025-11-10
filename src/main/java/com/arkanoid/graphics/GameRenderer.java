package com.arkanoid.graphics;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import com.arkanoid.models.Objects.Blink;
import com.arkanoid.models.Objects.Torpedo;
import com.arkanoid.resources.Images;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class GameRenderer {
    private final Main main;

    public GameRenderer(Main main) {
        this.main = main;
    }

    // ==================== BACKGROUND RENDERING ====================
    public void drawBackground(final int level) {
        main.bkgCtx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        main.bkgCtx.setFill(Color.BLACK);
        main.bkgCtx.fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        // Hiển thị start screen khi: chưa running HOẶC đã game over
        boolean shouldShowStartScreen = (!main.running && main.gameState == Main.GameState.PLAYING) || main.noOfLifes == 0;

        if (main.running && main.noOfLifes > 0) {
            // Game đang chạy - vẽ background level
            if (level % 4 == 0) {
                main.bkgCtx.setFill(Images.bkgPatternFill4);
            } else if (level % 3 == 0) {
                main.bkgCtx.setFill(Images.bkgPatternFill3);
            } else if (level % 2 == 0) {
                main.bkgCtx.setFill(Images.bkgPatternFill2);
            } else {
                main.bkgCtx.setFill(Images.bkgPatternFill1);
            }
            main.bkgCtx.fillRect(0, GameConstants.UPPER_INSET, GameConstants.WIDTH, GameConstants.HEIGHT);

            // Draw shadow
            main.bkgCtx.setFill(Color.rgb(0, 0, 0, 0.3));
            main.bkgCtx.fillRect(0, GameConstants.UPPER_INSET, 40, GameConstants.HEIGHT);
            main.bkgCtx.fillRect(0, GameConstants.UPPER_INSET, GameConstants.WIDTH, 20);
        } else if (shouldShowStartScreen) {
            // Start screen hoặc Game Over screen
            main.ctx.setFont(GameConstants.SCORE_FONT);
            main.ctx.setTextBaseline(VPos.TOP);
            main.ctx.setFill(GameConstants.HIGH_SCORE_RED);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("HIGH SCORE", GameConstants.WIDTH * 0.5, 0);
            main.ctx.setFill(GameConstants.SCORE_WHITE);
            main.ctx.fillText(Long.toString(main.highscore), GameConstants.WIDTH * 0.5, 30);

            if (main.showStartHint && main.noOfLifes > 0) {
                main.ctx.fillText("Hit space to start", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.6);
            }

            main.bkgCtx.drawImage(Images.logoImg, (GameConstants.WIDTH - Images.logoImg.getWidth()) * 0.5, GameConstants.HEIGHT * 0.25);
            main.bkgCtx.drawImage(Images.copyrightImg, (GameConstants.WIDTH - Images.copyrightImg.getWidth()) * 0.5, GameConstants.HEIGHT * 0.75);
        }
    }

    // ==================== BORDER RENDERING ====================
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

    // ==================== GAME OBJECTS RENDERING ====================
    public void drawGame() {
        main.ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        drawTorpedos();
        drawShadows();
        drawBlocks();
        drawBonusBlocks();
        drawBlinks();
        drawEnemies();
        drawExplosions();
        drawBalls();
        drawPaddle();
        drawUI();
    }

    private void drawTorpedos() {
        for (Torpedo torpedo : main.torpedoes) {
            main.ctx.drawImage(torpedo.image, torpedo.bounds.x, torpedo.bounds.y);
        }
    }

    private void drawShadows() {
        main.ctx.save();
        main.ctx.translate(10, 10);

        // Draw block shadows
        main.blocks.forEach(block -> main.ctx.drawImage(Images.blockShadowImg, block.x, block.y));
        main.bonusBlocks.forEach(bonusBlock -> main.ctx.drawImage(Images.bonusBlockShadowImg, bonusBlock.x, bonusBlock.y));

        // Draw paddle shadow
        if (main.noOfLifes > 0) {
            switch (main.paddleState) {
                case STANDARD -> main.ctx.drawImage(Images.paddleStdShadowImg, main.paddle.bounds.minX, main.paddle.bounds.minY);
                case WIDE -> main.ctx.drawImage(Images.paddleWideShadowImg, main.paddle.bounds.minX, main.paddle.bounds.minY);
                case LASER -> main.ctx.drawImage(Images.paddleGunShadowImg, main.paddle.bounds.minX, main.paddle.bounds.minY);
            }
        }

        // Draw ball shadow
        main.balls.forEach(ball -> main.ctx.drawImage(Images.ballShadowImg, ball.bounds.minX, ball.bounds.minY));
        main.ctx.restore();
    }

    private void drawBlocks() {
        main.blocks.forEach(block -> main.ctx.drawImage(block.image, block.x, block.y));
    }

    private void drawBonusBlocks() {
        main.bonusBlocks.forEach(bonusBlock -> {
            switch (bonusBlock.bonusType) {
                case BONUS_C ->
                        main.ctx.drawImage(Images.bonusBlockCMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_F ->
                        main.ctx.drawImage(Images.bonusBlockFMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_D ->
                        main.ctx.drawImage(Images.bonusBlockDMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_L ->
                        main.ctx.drawImage(Images.bonusBlockLMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_S ->
                        main.ctx.drawImage(Images.bonusBlockSMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_B ->
                        main.ctx.drawImage(Images.bonusBlockBMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_P ->
                        main.ctx.drawImage(Images.bonusBlockPMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
            }
        });
    }

    private void drawBlinks() {
        main.blinks.forEach(blink -> main.ctx.drawImage(Images.blinkMapImg, blink.countX * GameConstants.BLOCK_WIDTH, blink.countY * GameConstants.BLOCK_HEIGHT, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT, blink.x, blink.y, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT));
    }

    private void drawEnemies() {
        main.enemies.forEach(enemy -> {
            switch (enemy.enemyType) {
                case MOLECULE ->
                        main.ctx.drawImage(Images.moleculeMapImg, enemy.countX * GameConstants.ENEMY_WIDTH, enemy.countY * GameConstants.ENEMY_HEIGHT, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, enemy.x, enemy.y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT);
            }
        });
    }

    private void drawExplosions() {
        main.explosions.forEach(explosion -> main.ctx.drawImage(Images.explosionMapImg, explosion.countX * GameConstants.EXPLOSION_WIDTH, explosion.countY * GameConstants.EXPLOSION_HEIGHT, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT, explosion.x, explosion.y, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT));
    }

    private void drawBalls() {
        main.balls.forEach(ball -> {
            main.ctx.drawImage(Images.ballImg, ball.bounds.x, ball.bounds.y);
        });
    }

    private void drawPaddle() {
        if (main.noOfLifes > 0) {
            switch (main.paddleState) {
                case STANDARD ->
                        main.ctx.drawImage(Images.paddleMapStdImg, main.paddle.countX * main.paddleState.width, main.paddle.countY * main.paddleState.height, main.paddleState.width, main.paddleState.height, main.paddle.x, main.paddle.y, main.paddleState.width, main.paddleState.height);
                case WIDE ->
                        main.ctx.drawImage(Images.paddleMapWideImg, main.paddle.countX * main.paddleState.width, main.paddle.countY * main.paddleState.height, main.paddleState.width, main.paddleState.height, main.paddle.x, main.paddle.y, main.paddleState.width, main.paddleState.height);
                case LASER ->
                        main.ctx.drawImage(Images.paddleMapGunImg, main.paddle.countX * main.paddleState.width, main.paddle.countY * main.paddleState.height, main.paddleState.width, main.paddleState.height, main.paddle.x, main.paddle.y, main.paddleState.width, main.paddleState.height);
            }
        } else {
            // Game Over screen
            main.ctx.setFill(GameConstants.TEXT_GRAY);
            main.ctx.setFont(GameConstants.SCORE_FONT);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("GAME OVER", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.6);
            main.ctx.fillText("Press SPACE to play again", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.6 + 40);
        }
    }

    private void drawUI() {
        // Draw score
        main.ctx.setFill(Color.WHITE);
        main.ctx.setFont(GameConstants.SCORE_FONT);
        main.ctx.setTextAlign(TextAlignment.RIGHT);
        main.ctx.setTextBaseline(VPos.TOP);
        main.ctx.fillText(Long.toString(main.score), 140, 30);

        main.ctx.setFill(GameConstants.HIGH_SCORE_RED);
        main.ctx.setTextAlign(TextAlignment.CENTER);
        main.ctx.fillText("HIGH SCORE", GameConstants.WIDTH * 0.5, 0);
        main.ctx.setFill(GameConstants.SCORE_WHITE);
        main.ctx.fillText(Long.toString(Math.max(main.score, main.highscore)), GameConstants.WIDTH * 0.5, 30);

        // Draw no of lifes
        for (int i = 0; i < main.noOfLifes; i++) {
            main.ctx.drawImage(Images.paddleMiniImg, GameConstants.INSET + 2 + 42 * i, GameConstants.HEIGHT - 30);
        }

        // Draw ready level label
        if (main.readyLevelVisible) {
            main.ctx.setFill(GameConstants.TEXT_GRAY);
            main.ctx.setFont(GameConstants.SCORE_FONT);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("ROUND " + main.level, GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65);
            main.ctx.fillText("READY", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65 + 2 * GameConstants.SCORE_FONT.getSize());
        }
    }
}
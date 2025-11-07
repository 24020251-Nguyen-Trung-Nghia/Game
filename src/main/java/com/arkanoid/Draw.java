package com.arkanoid.graphics;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import com.arkanoid.models.Objects.Blink;
import com.arkanoid.models.Objects.Torpedo;
import com.arkanoid.resources.Images;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Draw {
    private final Main main;

    public Draw(Main main) {
        this.main = main;
    }

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

        // Draw bonus block shadows
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
            main.ctx.setFill(GameConstants.TEXT_GRAY);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("GAME OVER", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.75);
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
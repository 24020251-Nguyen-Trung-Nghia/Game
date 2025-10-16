package com.arkanoid;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class UpdateAndDraw {
    private final Main main;

    public UpdateAndDraw(Main main) {
        this.main = main;
    }

    private void updateAndDraw() {
        main.getCtx().clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        // Draw Torpedos
        for (Main.Torpedo torpedo : main.getTorpedoes()) {
            torpedo.update();
            main.getCtx().drawImage(torpedo.image, torpedo.bounds.x, torpedo.bounds.y);
        }

        // Draw shadows
        main.getCtx().save();
        main.getCtx().translate(10, 10);

        // Draw block shadows
        main.getBlocks().forEach(block -> main.getCtx().drawImage(main.getImages().blockShadowImg, block.x, block.y));

        // Draw bonus block shadows
        main.getBonusBlocks().forEach(bonusBlock -> main.getCtx().drawImage(main.getImages().bonusBlockShadowImg, bonusBlock.x, bonusBlock.y));

        // Draw paddle shadow
        if (main.getNoOfLifes() > 0) {
            switch (main.getPaddleState()) {
                case STANDARD -> main.getCtx().drawImage(main.getImages().paddleStdShadowImg, main.getPaddle().bounds.minX, main.getPaddle().bounds.minY);
                case WIDE -> main.getCtx().drawImage(main.getImages().paddleWideShadowImg, main.getPaddle().bounds.minX, main.getPaddle().bounds.minY);
                case LASER -> main.getCtx().drawImage(main.getImages().paddleGunShadowImg, main.getPaddle().bounds.minX, main.getPaddle().bounds.minY);
            }
        }

        // Draw ball shadow
        main.getBalls().forEach(ball -> main.getCtx().drawImage(main.getImages().ballShadowImg, ball.bounds.minX, ball.bounds.minY));
        main.getCtx().restore();

        // Draw blocks
        main.getBlocks().forEach(block -> main.getCtx().drawImage(block.image, block.x, block.y));

        // Draw bonus blocks
        main.getBonusBlocks().forEach(bonusBlock -> {
            switch (bonusBlock.bonusType) {
                case BONUS_C ->
                        main.getCtx().drawImage(main.getImages().bonusBlockCMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_F ->
                        main.getCtx().drawImage(main.getImages().bonusBlockFMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_D ->
                        main.getCtx().drawImage(main.getImages().bonusBlockDMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_L ->
                        main.getCtx().drawImage(main.getImages().bonusBlockLMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_S ->
                        main.getCtx().drawImage(main.getImages().bonusBlockSMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_B ->
                        main.getCtx().drawImage(main.getImages().bonusBlockBMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_P ->
                        main.getCtx().drawImage(main.getImages().bonusBlockPMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
            }
        });

        // Draw blinks
        main.getBlinks().forEach(blink -> main.getCtx().drawImage(main.getImages().blinkMapImg, blink.countX * GameConstants.BLOCK_WIDTH, blink.countY * GameConstants.BLOCK_HEIGHT, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT, blink.x, blink.y, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT));

        // Draw enemies
        main.getEnemies().forEach(enemy -> {
            switch (enemy.enemyType) {
                case MOLECULE ->
                        main.getCtx().drawImage(main.getImages().moleculeMapImg, enemy.countX * GameConstants.ENEMY_WIDTH, enemy.countY * GameConstants.ENEMY_HEIGHT, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, enemy.x, enemy.y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT);
            }
        });

        // Draw explosions
        main.getExplosions().forEach(explosion -> main.getCtx().drawImage(main.getImages().explosionMapImg, explosion.countX * GameConstants.EXPLOSION_WIDTH, explosion.countY * GameConstants.EXPLOSION_HEIGHT, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT, explosion.x, explosion.y, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT));

        // Draw ball(s)
        main.getBalls().forEach(ball -> {
            ball.update();
            main.getCtx().drawImage(main.getImages().ballImg, ball.bounds.x, ball.bounds.y);
        });

        // Draw paddle
        if (main.getNoOfLifes() > 0) {
            if (!main.isMovingPaddleOut()) {
                main.getPaddle().update();
            }
            switch (main.getPaddleState()) {
                case STANDARD ->
                        main.getCtx().drawImage(main.getImages().paddleMapStdImg, main.getPaddle().countX * main.getPaddleState().width, main.getPaddle().countY * main.getPaddleState().height, main.getPaddleState().width, main.getPaddleState().height, main.getPaddle().x, main.getPaddle().y, main.getPaddleState().width, main.getPaddleState().height);
                case WIDE ->
                        main.getCtx().drawImage(main.getImages().paddleMapWideImg, main.getPaddle().countX * main.getPaddleState().width, main.getPaddle().countY * main.getPaddleState().height, main.getPaddleState().width, main.getPaddleState().height, main.getPaddle().x, main.getPaddle().y, main.getPaddleState().width, main.getPaddleState().height);
                case LASER ->
                        main.getCtx().drawImage(main.getImages().paddleMapGunImg, main.getPaddle().countX * main.getPaddleState().width, main.getPaddle().countY * main.getPaddleState().height, main.getPaddleState().width, main.getPaddleState().height, main.getPaddle().x, main.getPaddle().y, main.getPaddleState().width, main.getPaddleState().height);
            }
        } else {
            main.getCtx().setFill(GameConstants.TEXT_GRAY);
            main.getCtx().setTextAlign(TextAlignment.CENTER);
            main.getCtx().fillText("GAME OVER", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.75);
        }

        // Draw score
        main.getCtx().setFill(Color.WHITE);
        main.getCtx().setFont(GameConstants.SCORE_FONT);
        main.getCtx().setTextAlign(TextAlignment.RIGHT);
        main.getCtx().setTextBaseline(VPos.TOP);
        main.getCtx().fillText(Long.toString(main.getScore()), 140, 30);

        main.getCtx().setFill(GameConstants.HIGH_SCORE_RED);
        main.getCtx().setTextAlign(TextAlignment.CENTER);
        main.getCtx().fillText("HIGH SCORE", GameConstants.WIDTH * 0.5, 0);
        main.getCtx().setFill(GameConstants.SCORE_WHITE);
        main.getCtx().fillText(Long.toString(main.getScore() > main.getHighscore() ? main.getScore() : main.getHighscore()), GameConstants.WIDTH * 0.5, 30);

        // Draw no of lifes
        for (int i = 0; i < main.getNoOfLifes(); i++) {
            main.getCtx().drawImage(main.getImages().paddleMiniImg, GameConstants.INSET + 2 + 42 * i, GameConstants.HEIGHT - 30);
        }

        // Draw ready level label
        if (main.isReadyLevelVisible()) {
            main.getCtx().setFill(GameConstants.TEXT_GRAY);
            main.getCtx().setFont(GameConstants.SCORE_FONT);
            main.getCtx().setTextAlign(TextAlignment.CENTER);
            main.getCtx().fillText("ROUND " + main.getLevel(), GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65);
            main.getCtx().fillText("READY", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65 + 2 * GameConstants.SCORE_FONT.getSize());
        }

        // Remove sprites
        main.getBalls().removeIf(ball -> ball.toBeRemoved);
        main.getBlinks().removeIf(blink -> blink.toBeRemoved);
        main.getBlocks().removeIf(block -> block.toBeRemoved);
        main.getBonusBlocks().removeIf(bonusBlock -> bonusBlock.toBeRemoved);
        main.getEnemies().removeIf(enemy -> enemy.toBeRemoved);
        main.getExplosions().removeIf(explosion -> explosion.toBeRemoved);
        main.getTorpedoes().removeIf(torpedo -> torpedo.toBeRemoved);

        // Respawn ball and check for game over
        if (!main.isMovingPaddleOut() && main.getBalls().isEmpty() && main.getNoOfLifes() > 0) {
            main.setNoOfLifes(main.getNoOfLifes() - 1);
            if (main.getNoOfLifes() == 0) {
                main.gameOver();
            }
            main.spawnBall();
        }

        // Update blinks
        main.getBlinks().forEach(blink -> blink.update());

        // Check for level completeness
        if (main.getBlocks().isEmpty() || main.getBlocks().stream().filter(block -> block.maxHits > -1).count() == 0) {
            main.setLevel(main.getLevel() + 1);
            if (main.getLevel() > Constants.LEVEL_MAP.size()) {
                main.setLevel(1);
            }
            main.startLevel(main.getLevel());
        }
    }
}
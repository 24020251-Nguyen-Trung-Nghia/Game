package com.arkanoid;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class UpdateAndDraw {
    private final Main main;

    public UpdateAndDraw(Main main) {
        this.main = main;
    }

    public void updateAndDraw() {
        main.ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        // Draw Torpedos
        for (Torpedo torpedo : main.torpedoes) {
            torpedo.update();
            main.ctx.drawImage(torpedo.image, torpedo.bounds.x, torpedo.bounds.y);
        }

        // Draw shadows
        main.ctx.save();
        main.ctx.translate(10, 10);

        // Draw block shadows
        main.blocks.forEach(block -> main.ctx.drawImage(main.getImages().blockShadowImg, block.x, block.y));

        // Draw bonus block shadows
        main.bonusBlocks.forEach(bonusBlock -> main.ctx.drawImage(main.getImages().bonusBlockShadowImg, bonusBlock.x, bonusBlock.y));

        // Draw paddle shadow
        if (main.noOfLifes > 0) {
            switch (main.paddleState) {
                case STANDARD -> main.ctx.drawImage(main.getImages().paddleStdShadowImg, main.paddle.bounds.minX, main.paddle.bounds.minY);
                case WIDE -> main.ctx.drawImage(main.getImages().paddleWideShadowImg, main.paddle.bounds.minX, main.paddle.bounds.minY);
                case LASER -> main.ctx.drawImage(main.getImages().paddleGunShadowImg, main.paddle.bounds.minX, main.paddle.bounds.minY);
            }
        }

        // Draw ball shadow
        main.balls.forEach(ball -> main.ctx.drawImage(main.getImages().ballShadowImg, ball.bounds.minX, ball.bounds.minY));
        main.ctx.restore();

        // Draw blocks
        main.blocks.forEach(block -> main.ctx.drawImage(block.image, block.x, block.y));

        // Draw bonus blocks
        main.bonusBlocks.forEach(bonusBlock -> {
            switch (bonusBlock.bonusType) {
                case BONUS_C ->
                        main.ctx.drawImage(main.getImages().bonusBlockCMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_F ->
                        main.ctx.drawImage(main.getImages().bonusBlockFMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_D ->
                        main.ctx.drawImage(main.getImages().bonusBlockDMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_L ->
                        main.ctx.drawImage(main.getImages().bonusBlockLMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_S ->
                        main.ctx.drawImage(main.getImages().bonusBlockSMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_B ->
                        main.ctx.drawImage(main.getImages().bonusBlockBMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
                case BONUS_P ->
                        main.ctx.drawImage(main.getImages().bonusBlockPMapImg, bonusBlock.countX * GameConstants.BONUS_BLOCK_WIDTH, bonusBlock.countY * GameConstants.BONUS_BLOCK_HEIGHT, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT, bonusBlock.x, bonusBlock.y, GameConstants.BONUS_BLOCK_WIDTH, GameConstants.BONUS_BLOCK_HEIGHT);
            }
        });

        // Draw blinks
        main.blinks.forEach(blink -> main.ctx.drawImage(main.getImages().blinkMapImg, blink.countX * GameConstants.BLOCK_WIDTH, blink.countY * GameConstants.BLOCK_HEIGHT, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT, blink.x, blink.y, GameConstants.BLOCK_WIDTH, GameConstants.BLOCK_HEIGHT));

        // Draw enemies
        main.enemies.forEach(enemy -> {
            switch (enemy.enemyType) {
                case MOLECULE ->
                        main.ctx.drawImage(main.getImages().moleculeMapImg, enemy.countX * GameConstants.ENEMY_WIDTH, enemy.countY * GameConstants.ENEMY_HEIGHT, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT, enemy.x, enemy.y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT);
            }
        });

        // Draw explosions
        main.explosions.forEach(explosion -> main.ctx.drawImage(main.getImages().explosionMapImg, explosion.countX * GameConstants.EXPLOSION_WIDTH, explosion.countY * GameConstants.EXPLOSION_HEIGHT, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT, explosion.x, explosion.y, GameConstants.EXPLOSION_WIDTH, GameConstants.EXPLOSION_HEIGHT));

        // Draw ball(s)
        main.balls.forEach(ball -> {
            ball.update();
            main.ctx.drawImage(main.getImages().ballImg, ball.bounds.x, ball.bounds.y);
        });

        // Draw paddle
        if (main.noOfLifes > 0) {
            if (!main.movingPaddleOut) {
                main.paddle.update();
            }
            switch (main.paddleState) {
                case STANDARD ->
                        main.ctx.drawImage(main.getImages().paddleMapStdImg, main.paddle.countX * main.paddle.width, main.paddle.countY * main.paddleState.height, main.paddleState.width, main.paddleState.height, main.paddle.x, main.paddle.y, main.paddleState.width, main.paddleState.height);
                case WIDE ->
                        main.ctx.drawImage(main.getImages().paddleMapWideImg, main.paddle.countX * main.paddle.width, main.paddle.countY * main.paddleState.height, main.paddleState.width, main.paddleState.height, main.paddle.x, main.paddle.y, main.paddleState.width, main.paddleState.height);
                case LASER ->
                        main.ctx.drawImage(main.getImages().paddleMapGunImg, main.paddle.countX * main.paddleState.width, main.paddle.countY * main.paddleState.height, main.paddleState.width, main.paddleState.height, main.paddle.x, main.paddle.y, main.paddleState.width, main.paddleState.height);
            }
        } else {
            main.ctx.setFill(GameConstants.TEXT_GRAY);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("GAME OVER", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.75);
        }

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
        main.ctx.fillText(Long.toString(main.score > main.highscore ? main.score : main.highscore), GameConstants.WIDTH * 0.5, 30);

        // Draw no of lifes
        for (int i = 0; i < main.noOfLifes; i++) {
            main.ctx.drawImage(main.getImages().paddleMiniImg, GameConstants.INSET + 2 + 42 * i, GameConstants.HEIGHT - 30);
        }

        // Draw ready level label
        if (main.readyLevelVisible) {
            main.ctx.setFill(GameConstants.TEXT_GRAY);
            main.ctx.setFont(GameConstants.SCORE_FONT);
            main.ctx.setTextAlign(TextAlignment.CENTER);
            main.ctx.fillText("ROUND " + main.level, GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65);
            main.ctx.fillText("READY", GameConstants.WIDTH * 0.5, GameConstants.HEIGHT * 0.65 + 2 * GameConstants.SCORE_FONT.getSize());
        }

        // Remove sprites
        main.balls.removeIf(ball -> ball.toBeRemoved);
        main.blinks.removeIf(blink -> blink.toBeRemoved);
        main.blocks.removeIf(block -> block.toBeRemoved);
        main.bonusBlocks.removeIf(bonusBlock -> bonusBlock.toBeRemoved);
        main.enemies.removeIf(enemy -> enemy.toBeRemoved);
        main.explosions.removeIf(explosion -> explosion.toBeRemoved);
        main.torpedoes.removeIf(torpedo -> torpedo.toBeRemoved);

        // Respawn ball and check for game over
        if (!main.movingPaddleOut && main.balls.isEmpty() && main.noOfLifes > 0) {
            main.noOfLifes -=1;
            if (main.noOfLifes == 0) {
                main.getGameOver().gameOver();
            }
            main.spawnBall();
        }

        // Update blinks
        main.blinks.forEach(blink -> blink.update());

        // Check for level completeness
        if (main.blocks.isEmpty() || main.blocks.stream().filter(block -> block.maxHits > -1).count() == 0) {
            main.level +=1;
            if (main.level > Constants.LEVEL_MAP.size()) {
                main.level = 1; // Trở lại màn 1
            }
            main.getStartLevel().startLevel(main.level);
        }
    }
}
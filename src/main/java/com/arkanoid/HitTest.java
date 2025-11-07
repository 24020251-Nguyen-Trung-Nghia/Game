package com.arkanoid;

import com.arkanoid.core.Ball;
import com.arkanoid.core.Block;
import com.arkanoid.core.BonusBlock;
import com.arkanoid.core.Objects.Enemy;
import com.arkanoid.core.Torpedo;
import com.arkanoid.resources.AutoClips;

public class HitTest {
    private final Main main;

    public HitTest(Main main) {
        this.main = main;
    }

    // ******************** HitTests ******************************************
    void hitTests() {
        // torpedo hits block or enemy
        for (Torpedo torpedo : main.torpedoes) {
            if (EnumDefinitions.PaddleState.LASER == main.paddleState) {
                for (Block block : main.blocks) {
                    if (block.bounds.intersects(torpedo.bounds)) {
                        block.hits++;
                        if (block.hits == block.maxHits) {
                            block.toBeRemoved = true;
                            //main.setScore(main.getScore() + block.value);
                            main.score = main.score + block.value;
                        }
                        torpedo.toBeRemoved = true;
                        break;
                    }
                }
                for (Enemy enemy : main.enemies) {
                    if (enemy.bounds.intersects(torpedo.bounds)) {
                        enemy.toBeRemoved = true;
                        torpedo.toBeRemoved = true;
                        main.explosions.add(new Explosion(enemy.x, enemy.y, enemy.vX, enemy.vY, 1.0));
                        main.playSound(AutoClips.explosionSnd);
                        break;
                    }
                }
            }
        }

        // paddle hits bonus blocks
        for (BonusBlock bonusBlock : main.bonusBlocks) {
            if (bonusBlock.bounds.intersects(main.paddle.bounds)) {
                bonusBlock.toBeRemoved = true;
                switch (bonusBlock.bonusType) {
                    case BONUS_C -> {
                        main.stickyPaddle = true;
                    }
                    case BONUS_D -> {
                        if (main.balls.size() == 1) {
                            Ball ball = main.balls.get(0);
                            double vX1 = (Math.sin(Math.toRadians(10)) * ball.vX);
                            double vY1 = (Math.cos(Math.toRadians(10)) * ball.vY);
                            double vX2 = (Math.sin(Math.toRadians(-10)) * ball.vX);
                            double vY2 = (Math.cos(Math.toRadians(-10)) * ball.vY);
                            main.balls.add(new Ball(main, main.images.ballImg, ball.x, ball.y, vX1, vY1));
                            main.balls.add(new Ball(main, main.images.ballImg, ball.x, ball.y, vX2, vY2));
                        }
                    }
                    case BONUS_F -> {
                        main.paddleResetCounter= 30;
                        main.paddleState = EnumDefinitions.PaddleState.WIDE;
                    }
                    case BONUS_L -> {
                        main.paddleResetCounter= 30;
                        main.paddleState = EnumDefinitions.PaddleState.LASER;
                    }
                    case BONUS_S -> {
                        main.paddleResetCounter= 30;
                        // Tốc độ khôn bị giới hạn nhỏ nhất
                        main.ballSpeed = GameConstants.BALL_SPEED * 0.5;
                    }
                    case BONUS_B -> {
                        main.paddleResetCounter= 5;
                        main.nextLevelDoorOpen = true;
                    }
                    case BONUS_P -> {
                        main.noOfLifes = Helper.clamp(2, 5, main.noOfLifes + 1);

                    }
                }
            }
        }
    }
}
package com.arkanoid;

public class HitTest {
    private final Main main;

    public HitTest(Main main) {
        this.main = main;
    }

    // ******************** HitTests ******************************************
    void hitTests() {
        // torpedo hits block or enemy
        for (Torpedo torpedo : main.getTorpedoes()) {
            if (EnumDefinitions.PaddleState.LASER == main.getPaddleState()) {
                for (Block block : main.getBlocks()) {
                    if (block.bounds.intersects(torpedo.bounds)) {
                        block.hits++;
                        if (block.hits == block.maxHits) {
                            block.toBeRemoved = true;
                            main.setScore(main.getScore() + block.value);
                        }
                        torpedo.toBeRemoved = true;
                        break;
                    }
                }
                for (Enemy enemy : main.getEnemies()) {
                    if (enemy.bounds.intersects(torpedo.bounds)) {
                        enemy.toBeRemoved = true;
                        torpedo.toBeRemoved = true;
                        main.getExplosions().add(new Explosion(enemy.x, enemy.y, enemy.vX, enemy.vY, 1.0));
                        main.playSound(main.getAutoClips().explosionSnd);
                        break;
                    }
                }
            }
        }

        // paddle hits bonus blocks
        for (BonusBlock bonusBlock : main.getBonusBlocks()) {
            if (bonusBlock.bounds.intersects(main.getPaddle().bounds)) {
                bonusBlock.toBeRemoved = true;
                switch (bonusBlock.bonusType) {
                    case BONUS_C -> {
                        main.setStickyPaddle(true);
                    }
                    case BONUS_D -> {
                        if (main.getBalls().size() == 1) {
                            Ball ball = main.getBalls().get(0);
                            double vX1 = (Math.sin(Math.toRadians(10)) * ball.vX);
                            double vY1 = (Math.cos(Math.toRadians(10)) * ball.vY);
                            double vX2 = (Math.sin(Math.toRadians(-10)) * ball.vX);
                            double vY2 = (Math.cos(Math.toRadians(-10)) * ball.vY);
                            main.getBalls().add(new Ball(main, main.getImages().ballImg, ball.x, ball.y, vX1, vY1));
                            main.getBalls().add(new Ball(main, main.getImages().ballImg, ball.x, ball.y, vX2, vY2));
                        }
                    }
                    case BONUS_F -> {
                        main.setPaddleResetCounter(30);
                        main.setPaddleState(EnumDefinitions.PaddleState.WIDE);
                    }
                    case BONUS_L -> {
                        main.setPaddleResetCounter(30);
                        main.setPaddleState(EnumDefinitions.PaddleState.LASER);
                    }
                    case BONUS_S -> {
                        main.setSpeedResetCounter(30);
                        main.setBallSpeed(GameConstants.BALL_SPEED * 0.5);
                    }
                    case BONUS_B -> {
                        main.setNextLevelDoorCounter(5);
                        main.setNextLevelDoorOpen(true);
                    }
                    case BONUS_P -> {
                        main.setNoOfLifes(Utils.clamp(2, 5, main.getNoOfLifes() + 1));
                    }
                }
            }
        }
    }
}
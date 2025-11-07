package com.arkanoid.graphics;

import com.arkanoid.models.Constants;
import com.arkanoid.Main;
import com.arkanoid.models.Objects.Blink;
import com.arkanoid.models.Objects.Torpedo;

public class Update {
    private final Main main;

    public Update(Main main) {
        this.main = main;
    }

    public void updateGame() {
        updateTorpedos();
        updateBalls();
        updatePaddle();
        updateBlinks();
        cleanupObjects();
        checkBallRespawn();
        checkLevelComplete();
    }

    private void updateTorpedos() {
        for (Torpedo torpedo : main.torpedoes) {
            torpedo.update();
        }
    }

    private void updateBalls() {
        for (var ball : main.balls) {
            ball.update();
        }
    }

    private void updatePaddle() {
        if (!main.movingPaddleOut) {
            main.paddle.update();
        }
    }

    private void updateBlinks() {
        main.blinks.forEach(Blink::update);
    }

    private void cleanupObjects() {
        main.balls.removeIf(ball -> ball.toBeRemoved);
        main.blinks.removeIf(blink -> blink.toBeRemoved);
        main.blocks.removeIf(block -> block.toBeRemoved);
        main.bonusBlocks.removeIf(bonusBlock -> bonusBlock.toBeRemoved);
        main.enemies.removeIf(enemy -> enemy.toBeRemoved);
        main.explosions.removeIf(explosion -> explosion.toBeRemoved);
        main.torpedoes.removeIf(torpedo -> torpedo.toBeRemoved);
    }

    private void checkBallRespawn() {
        if (!main.movingPaddleOut && main.balls.isEmpty() && main.noOfLifes > 0) {
            main.noOfLifes -= 1;
            if (main.noOfLifes == 0) {
                main.gameOver.gameOver();
            }
            main.spawnBall();
        }
    }

    private void checkLevelComplete() {
        if (main.blocks.isEmpty() || main.blocks.stream().noneMatch(block -> block.maxHits > -1)) {
            main.level += 1;
            if (main.level > Constants.LEVEL_MAP.size()) {
                main.level = 1; // Trở lại màn 1
            }
            main.startLevel.startLevel(main.level);
        }
    }
}
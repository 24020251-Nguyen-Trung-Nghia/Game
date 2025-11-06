package com.arkanoid;

public class Enemy extends AnimatedSprite {
    private final Main main;
    public EnumDefinitions.EnemyType enemyType;
    private final double initialVx;

    // ******************** Constructors **************************************
    public Enemy(Main main, final double x, final double y, final EnumDefinitions.EnemyType enemyType) {
        super(x, y, 0, GameConstants.ENEMY_SPEED, 8, 3, 1.0);
        this.main = main;
        this.initialVx = GameConstants.RND.nextDouble() * GameConstants.ENEMY_SPEED;
        this.vX = GameConstants.RND.nextBoolean() ? initialVx : -initialVx;
        this.enemyType = enemyType;
        this.width = GameConstants.ENEMY_WIDTH;
        this.height = GameConstants.ENEMY_HEIGHT;
        this.radius = Math.sqrt(GameConstants.ENEMY_HEIGHT * GameConstants.ENEMY_HEIGHT * 0.25 + GameConstants.ENEMY_WIDTH * GameConstants.ENEMY_WIDTH * 0.25);
        getBounds().set(this.x - this.width * 0.5, this.y - this.height * 0.5, this.width, this.height);
    }

    // ******************** Methods *******************************************
    @Override
    public void update() {
        x += vX;
        y += vY;

        // Sử dụng getter methods thay vì truy cập trực tiếp
        if (getBounds().maxX > GameConstants.WIDTH - GameConstants.INSET) {
            this.x = GameConstants.WIDTH - GameConstants.INSET - this.width;
            this.vX = -initialVx;
        }
        if (getBounds().minX < GameConstants.INSET) {
            this.x = GameConstants.INSET;
            this.vX = Math.abs(initialVx);
        }
        if (getBounds().minY < GameConstants.UPPER_INSET) {
            this.y = GameConstants.UPPER_INSET;
            this.vY = 3.0;
        }

        getBounds().set(this.x, this.y, this.width, this.height);

        // Kiểm tra va chạm enemy với blocks - sử dụng getter
        for (Block block : main.blocks) {
            boolean enemyHitsBlock = getBounds().intersects(block.getBounds());
            if (enemyHitsBlock) {
                if (getBounds().centerX > block.getBounds().minX && getBounds().centerX < block.getBounds().maxX) {
                    // Va chạm trên hoặc dưới
                    vY = -vY;
                } else if (getBounds().centerY > block.getBounds().minY && getBounds().centerY < block.getBounds().maxY) {
                    // Va chạm trái hoặc phải
                    vX = -vX;
                } else {
                    double dx = Math.abs(getBounds().centerX - block.getBounds().centerX) - block.getBounds().width * 0.5;
                    double dy = Math.abs(getBounds().centerY - block.getBounds().centerY) - block.getBounds().height * 0.5;
                    if (dx > dy) {
                        // Va chạm trái hoặc phải
                        vX = -vX;
                    } else {
                        // Va chạm trên hoặc dưới
                        vY = -vY;
                    }
                }
                break;
            }
        }

        // Kiểm tra va chạm enemy với paddle - sử dụng getter
        if (getBounds().intersects(main.paddle.getBounds())) {
            this.toBeRemoved = true;
            main.explosions.add(new Explosion(this.x, this.y, this.vX, this.vY, 1.0));
            main.playSound(main.autoClips.explosionSnd);
        }

        if (y > GameConstants.HEIGHT) {
            toBeRemoved = true;
        }

        countX++;
        if (countX == maxFrameX) {
            countY++;
            countX = 0;
            if (countY == maxFrameY) {
                countY = 0;
            }
        }
    }
}

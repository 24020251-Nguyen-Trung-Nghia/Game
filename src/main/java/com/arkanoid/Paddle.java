package com.arkanoid;

class Paddle extends AnimatedSprite {

    private final Main main;

    // ******************** Constructors **************************************
    public Paddle(Main main) {
        super(GameConstants.WIDTH * 0.5 - main.getPaddleState().width * 0.5, GameConstants.HEIGHT - GameConstants.PADDLE_OFFSET_Y, 0, 0, 8, 8, 1.0);
        this.main = main;
        init();
    }


    // ******************** Methods *******************************************
    @Override
    protected void init() {
        this.width = main.getPaddleState().width;
        this.height = main.getPaddleState().height;
        this.size = height;
        this.radius = size * 0.5;
        this.bounds.set(this.x, this.y, main.getPaddleState().width, this.height);
    }

    @Override
    public void respawn() {
        this.x = GameConstants.WIDTH * 0.5;
        this.bounds.set(this.x, this.y, main.getPaddleState().width, this.height);
        this.vX = 0;
        this.vY = 0;
    }

    @Override
    public void update() {
        x += vX;

        if (x + main.getPaddleState().width > GameConstants.WIDTH - GameConstants.INSET) {
            if (main.isNextLevelDoorOpen() && !main.isMovingPaddleOut()) {
                main.movingPaddleOut = true;
            }
            x = GameConstants.WIDTH - GameConstants.INSET - main.getPaddleState().width;
        }
        if (x < GameConstants.INSET) {
            x = GameConstants.INSET;
        }
        this.bounds.set(this.x, this.y, main.getPaddleState().width, this.height);

        countX = main.getAnimateInc();
        if (countX == maxFrameX) {
            countX = 0;
            main.animateInc = 0;
            countY++;
            if (countY == maxFrameY) {
                countY = 0;
            }
        }
    }
}

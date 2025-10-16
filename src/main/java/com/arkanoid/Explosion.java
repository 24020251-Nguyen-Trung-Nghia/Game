package com.arkanoid;

class Explosion extends AnimatedSprite {

    // ******************** Constructors **************************************
    public Explosion(final double x, final double y, final double vX, final double vY, final double scale) {
        super(x, y, vX, vY, 4, 4, scale);
    }


    // ******************** Methods *******************************************
    @Override
    public void update() {
        x += vX;
        y += vY;

        countX++;
        if (countX == maxFrameX) {
            countY++;
            if (countX == maxFrameX && countY == maxFrameY) {
                toBeRemoved = true;
            }
            countX = 0;
            if (countY == maxFrameY) {
                countY = 0;
            }
        }
    }
}

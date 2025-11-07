package com.arkanoid.core;

public class Blink extends AnimatedSprite {

    // ******************** Constructors **************************************
    public Blink(final double x, final double y) {
        super(x, y, 0, 0, 8, 3, 1.0);
    }


    // ******************** Methods *******************************************
    @Override
    public void update() {
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

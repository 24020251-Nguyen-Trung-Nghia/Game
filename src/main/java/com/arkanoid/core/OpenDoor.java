package com.arkanoid.core;

public class OpenDoor extends AnimatedSprite {

    // ******************** Constructors **************************************
    public OpenDoor(final double x, final double y) {
        super(x, y, 0, 0, 3, 0, 1.0);
        this.bounds.set(x, y, width, height);
    }


    // ******************** Methods *******************************************
    @Override
    public void update() {
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

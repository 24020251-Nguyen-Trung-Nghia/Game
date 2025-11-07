package com.arkanoid.models.Objects;

public abstract class AnimatedSprite extends Sprite {
    protected final int maxFrameX;
    protected final int maxFrameY;
    protected double scale;
    public int countX;
    public int countY;


    // ******************** Constructors **************************************
    public AnimatedSprite(final int maxFrameX, final int maxFrameY, final double scale) {
        this(0, 0, 0, 0, 0, 0, maxFrameX, maxFrameY, scale);
    }

    public AnimatedSprite(final double x, final double y, final double vX, final double vY, final int maxFrameX, final int maxFrameY, final double scale) {
        this(x, y, 0, vX, vY, 0, maxFrameX, maxFrameY, scale);
    }

    public AnimatedSprite(final double x, final double y, final double r, final double vX, final double vY, final double vR, final int maxFrameX, final int maxFrameY, final double scale) {
        super(null, x, y, r, vX, vY, vR);
        this.maxFrameX = maxFrameX;
        this.maxFrameY = maxFrameY;
        this.scale = scale;
        this.countX = 0;
        this.countY = 0;
    }


    // ******************** Methods *******************************************
    @Override
    public void update() {
        // Di chuyển đối tượng theo cả x và y
        x += vX;
        y += vY;

        // Cập nhật animation
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

package com.arkanoid.core;

import com.arkanoid.Bounds;
import javafx.scene.image.Image;

// ******************** Inner Classes *************************************
/*
Sprite (abstract) - Lớp cơ sở cho tất cả đối tượng
├── AnimatedSprite (abstract) - Đối tượng có animation
│   ├── Paddle - Ván đỡ
│   ├── Blink - Hiệu ứng nhấp nháy khi phá block
│   ├── BonusBlock - Vật phẩm đặc biệt
│   ├── Enemy - Kẻ địch
│   ├── Explosion - Hiệu ứng nổ
│   └── OpenDoor - Cửa qua level
├── Ball - Bóng
├── Block - Gạch
└── Torpedo - Đạn laser
*/
abstract class Sprite {
    public Image image;

    public Bounds bounds;
    public double x; // Center of Sprite in x-direction
    public double y; // Center of Sprite in y-direction
    public double r;
    public double vX;
    public double vY;
    public double vR;
    public double width;
    public double height;
    public double size;

    public double radius;
    public boolean toBeRemoved;


    // ******************** Constructors **************************************
    public Sprite() {
        this(null, 0, 0, 0, 0, 0, 0);
    }

    public Sprite(final Image image) {
        this(image, 0, 0, 0, 0, 0, 0);
    }

    public Sprite(final Image image, final double x, final double y) {
        this(image, x, y, 0, 0, 0, 0);
    }

    public Sprite(final Image image, final double x, final double y, final double vX, final double vY) {
        this(image, x, y, 0, vX, vY, 0);
    }

    public Sprite(final Image image, final double x, final double y, final double r, final double vX, final double vY) {
        this(image, x, y, r, vX, vY, 0);
    }

    public Sprite(final Image image, final double x, final double y, final double r, final double vX, final double vY, final double vR) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.r = r;
        this.vX = vX;
        this.vY = vY;
        this.vR = vR;
        this.width = null == image ? 0 : image.getWidth();
        this.height = null == image ? 0 : image.getHeight();
        this.size = Math.max(this.width, this.height);
        this.radius = this.size * 0.5;
        this.toBeRemoved = false;
        this.bounds = null == image ? new Bounds(0, 0, 0, 0) : new Bounds(x - image.getWidth() * 0.5, y - image.getHeight() * 0.5, image.getWidth(), image.getHeight());
    }


    // ******************** Methods *******************************************
    protected void init() {
    }

    public void respawn() {
    }

    public abstract void update();

    public Bounds getBounds() {
        if (this.bounds == null) {
            this.bounds = new Bounds(0, 0, 0, 0);
        }
        return this.bounds;
    }

    public double getRadius() {
        return radius;
    }
}

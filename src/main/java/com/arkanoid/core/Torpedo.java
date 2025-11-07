package com.arkanoid.core;

import com.arkanoid.GameConstants;
import javafx.scene.image.Image;

public class Torpedo extends Sprite {

    // ******************** Constructors **************************************
    public Torpedo(final Image image, final double x, final double y) {
        super(image, x, y - image.getHeight(), 0, GameConstants.TORPEDO_SPEED);
    }


    // ******************** Methods *******************************************
    @Override
    public void update() {
        y -= vY;
        this.bounds.set(this.x - this.width * 0.5, this.y - this.height * 0.5, this.width, this.height);
        if (bounds.minY < GameConstants.UPPER_INSET) {
            toBeRemoved = true;
        }
    }
}

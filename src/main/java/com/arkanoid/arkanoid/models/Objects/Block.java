package com.arkanoid.models.Objects;

import com.arkanoid.models.Constants;
import com.arkanoid.GameConstants;
import javafx.scene.image.Image;

public class Block extends Sprite {
    public int value;
    public int hits;
    public final int maxHits;
    public final Constants.BlockType blockType;


    // ******************** Constructors **************************************
    public Block(final Image image, final double x, final double y, final int value, final int maxHits, final Constants.BlockType blockType) {
        super(image);
        this.x = x;
        this.y = y;
        this.value = value;
        this.maxHits = maxHits;
        this.blockType = blockType;
        this.hits = 0;
        this.width = GameConstants.BLOCK_WIDTH;
        this.height = GameConstants.BLOCK_HEIGHT;
        this.bounds.set(x, y, width, height);
        init();
    }


    // ******************** Methods *******************************************
    @Override
    protected void init() {
        size = width > height ? width : height;
        radius = size * 0.5;

        // Velocity
        vX = 0;
        vY = 0;
    }

    @Override
    public void update() {
    }

    @Override
    public String toString() {
        return new StringBuilder().append(blockType).append("(").append(x).append(",").append(y).append(")").toString();
    }

    public boolean equals(final Block other) {
        return this.blockType == other.blockType &&
                this.x == other.x &&
                this.y == other.y &&
                this.value == other.value;
    }
}

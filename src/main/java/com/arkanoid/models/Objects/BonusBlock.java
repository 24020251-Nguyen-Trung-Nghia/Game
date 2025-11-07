package com.arkanoid.models.Objects;

import com.arkanoid.models.EnumDefinitions;
import com.arkanoid.GameConstants;

public class BonusBlock extends AnimatedSprite {
    public EnumDefinitions.BonusType bonusType;


    // ******************** Constructors **************************************
    public BonusBlock(final double x, final double y, final EnumDefinitions.BonusType bonusType) {
        super(x, y, 0, GameConstants.BONUS_BLOCK_SPEED, 5, 4, 1.0);
        this.bonusType = bonusType;
        this.width = GameConstants.BLOCK_WIDTH;
        this.height = GameConstants.BLOCK_HEIGHT;
        this.bounds.set(x, y, width, height);
    }


    // ******************** Methods *******************************************
    // Tạo hiệu ứng xoay
    @Override
    public void update() {
        y += vY;
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
        this.bounds.set(this.x, this.y, this.width, this.height);
    }
}

package com.arkanoid.models;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import com.arkanoid.models.Objects.Block;
import com.arkanoid.resources.Images;

public class SetupBlocks {
    private final Main main;

    public SetupBlocks(Main main) {
        this.main = main;
    }

<<<<<<< HEAD
    // Setup blocks cho các level
=======
    // Thiết lập gạch cho các level
>>>>>>> main
    public void setupBlocks(final int level) {
        main.blocks.clear();
        Constants.BlockType[][] currentLevel = Constants.LEVEL_MAP.get(level);
        main.silverBlockMaxHits = (level % 8 == 0 ? main.silverBlockMaxHits + 1 : main.silverBlockMaxHits); // mỗi 8 level gạch bạc tăng thêm 1 hit
        for (int iy = 0; iy < currentLevel.length; iy++) {
            for (int ix = 0; ix < currentLevel[iy].length; ix++) {
                Block block;
                final Constants.BlockType blockType = currentLevel[iy][ix];
                switch (blockType) {
                    case GOLD ->
                            block = new Block(Images.goldBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 0, blockType.maxHits, blockType);
                    case GRAY ->
                            block = new Block(Images.grayBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 0, main.silverBlockMaxHits, blockType);
                    case WHIT ->
                            block = new Block(Images.whiteBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 10, blockType.maxHits, blockType);
                    case ORNG ->
                            block = new Block(Images.orangeBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 60, blockType.maxHits, blockType);
                    case CYAN ->
                            block = new Block(Images.cyanBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 70, blockType.maxHits, blockType);
                    case LIME ->
                            block = new Block(Images.limeBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 80, blockType.maxHits, blockType);
                    case RUBY ->
                            block = new Block(Images.redBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 90, blockType.maxHits, blockType);
                    case BLUE ->
                            block = new Block(Images.blueBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 100, blockType.maxHits, blockType);
                    case MGNT ->
                            block = new Block(Images.magentaBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 110, blockType.maxHits, blockType);
                    case YLLW ->
                            block = new Block(Images.yellowBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 120, blockType.maxHits, blockType);
                    default -> block = null;
                }
                if (null == block) {
                    continue;
                }
                main.blocks.add(block);
            }
        }
    }
}
package com.arkanoid;

public class SetupBlocks {
    private final Main main;

    public SetupBlocks(Main main) {
        this.main = main;
    }

    // Setup blocks for given level
    void setupBlocks(final int level) {
        main.getBlocks().clear();
        Constants.BlockType[][] level2 = Constants.LEVEL_MAP.get(level);
        main.setSilverBlockMaxHits(level % 8 == 0 ? main.getSilverBlockMaxHits() + 1 : main.getSilverBlockMaxHits());
        for (int iy = 0; iy < level2.length; iy++) {
            for (int ix = 0; ix < level2[iy].length; ix++) {
                Block block;
                final Constants.BlockType blockType = level2[iy][ix];
                switch (blockType) {
                    case GOLD ->
                            block = new Block(main.getImages().goldBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 0, blockType.maxHits, blockType);
                    case GRAY ->
                            block = new Block(main.getImages().grayBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 0, main.getSilverBlockMaxHits(), blockType);
                    case WHIT ->
                            block = new Block(main.getImages().whiteBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 10, blockType.maxHits, blockType);
                    case ORNG ->
                            block = new Block(main.getImages().orangeBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 60, blockType.maxHits, blockType);
                    case CYAN ->
                            block = new Block(main.getImages().cyanBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 70, blockType.maxHits, blockType);
                    case LIME ->
                            block = new Block(main.getImages().limeBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 80, blockType.maxHits, blockType);
                    case RUBY ->
                            block = new Block(main.getImages().redBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 90, blockType.maxHits, blockType);
                    case BLUE ->
                            block = new Block(main.getImages().blueBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 100, blockType.maxHits, blockType);
                    case MGNT ->
                            block = new Block(main.getImages().magentaBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 110, blockType.maxHits, blockType);
                    case YLLW ->
                            block = new Block(main.getImages().yellowBlockImg, GameConstants.INSET + ix * GameConstants.BLOCK_STEP_X, GameConstants.INSET + 110 + iy * GameConstants.BLOCK_STEP_Y, 120, blockType.maxHits, blockType);
                    default -> block = null;
                }
                if (null == block) {
                    continue;
                }
                main.getBlocks().add(block);
            }
        }
    }
}
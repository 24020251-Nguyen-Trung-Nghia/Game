package com.arkanoid;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

public class GameConstants {
    static final Random RND = new Random();
    public static final Font UI_FONT = Font.font("Arial", 24);
    // Các hằng số cơ bản
    static final double WIDTH = 560;
    static final double HEIGHT = 740;
    static final double INSET = 22;
    static final double UPPER_INSET = 85;
    static final double PADDLE_OFFSET_Y = 68;
    static final double PADDLE_SPEED = 8;
    static final double TORPEDO_SPEED = 12;
    static final double BLOCK_WIDTH = 38;
    static final double BLOCK_HEIGHT = 20;
    static final double BLOCK_STEP_X = 40;
    static final double BLOCK_STEP_Y = 22;
    static final double BONUS_BLOCK_WIDTH = 38;
    static final double BONUS_BLOCK_HEIGHT = 18;
    static final double ENEMY_WIDTH = 32;
    static final double ENEMY_HEIGHT = 32;
    static final double EXPLOSION_WIDTH = 32;
    static final double EXPLOSION_HEIGHT = 32;
    static final double BALL_VX_INFLUENCE = 0.75;
    static final Font SCORE_FONT = Fonts.emulogic(20);
    static final Color HIGH_SCORE_RED = Color.rgb(229, 2, 1);
    static final Color TEXT_GRAY = Color.rgb(216, 216, 216);
    static final int BONUS_BLOCK_INTERVAL = 1;  // Cứ 10 cái thì 1 buff
    final static double BIG_VALUE = 100_000;

    // Các biến cần khởi tạo từ PropertyManager
    static final double BALL_SPEED;
    static final double BONUS_BLOCK_SPEED;
    static final double ENEMY_SPEED;
    static final Color SCORE_WHITE;
    final static Bounds[] BORDER_BOUNDS;

    // Static initializer block
    static {
        BALL_SPEED = Utils.clamp(0.1, 10, PropertyManager.INSTANCE.getDouble(Constants.BALL_SPEED_KEY, 3));
        BONUS_BLOCK_SPEED = Utils.clamp(0.1, 5, PropertyManager.INSTANCE.getDouble(Constants.BONUS_BLOCK_SPEED_KEY, 3));
        ENEMY_SPEED = Utils.clamp(0.1, 5, PropertyManager.INSTANCE.getDouble(Constants.ENEMY_SPEED_KEY, 3));
        SCORE_WHITE = Color.WHITE;

        BORDER_BOUNDS = new Bounds[]{
                new Bounds(-BIG_VALUE + INSET * 0.8, -BIG_VALUE, BIG_VALUE, 2 * BIG_VALUE),
                new Bounds(WIDTH - INSET * 0.8, -BIG_VALUE, BIG_VALUE, 2 * BIG_VALUE),
                new Bounds(-BIG_VALUE, -BIG_VALUE + UPPER_INSET, 2 * BIG_VALUE, BIG_VALUE)
        };
    }

    // Không cần constructor nữa
    private GameConstants() {
        // Private constructor để ngăn khởi tạo
    }
}
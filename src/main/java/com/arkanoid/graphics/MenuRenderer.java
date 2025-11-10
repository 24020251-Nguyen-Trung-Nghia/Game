package com.arkanoid.graphics;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import javafx.scene.paint.Color;

public final class MenuRenderer {

    private MenuRenderer() {
        // Utility class - không cho phép khởi tạo
    }

    // Constants for menu configuration
    private static final double CENTER_X_OFFSET = -100;
    private static final double START_MENU_Y = 360;
    private static final double PAUSE_MENU_Y = 260;
    private static final double ITEM_SPACING = 40;

    private static void clearAndSetupMenu(Main main) {
        main.ctx.clearRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        main.gameRenderer.drawBackground(0);
        main.ctx.setFont(GameConstants.UI_FONT);
        main.ctx.setGlobalAlpha(1.0);
        main.ctx.setFill(Color.WHITE);
    }

    private static void drawMenuCursor(Main main, double x, double y) {
        main.ctx.fillText(">", x - 30, y);
    }

    private static void drawMenuItem(Main main, String text, double x, double y, boolean isSelected) {
        if (isSelected) {
            drawMenuCursor(main, x - 30, y);
        }
        main.ctx.fillText(text, x, y);
    }

    private static String maskPassword(String password) {
        return "*".repeat(password.length());
    }

    public static void renderStartMenu(Main main, int menuIndex) {
        clearAndSetupMenu(main);

        double centerX = GameConstants.WIDTH / 2.0 + CENTER_X_OFFSET;
        double startY = START_MENU_Y;

        String[] menuItems = {"New Player", "Old Player"};

        for (int i = 0; i < menuItems.length; i++) {
            drawMenuItem(main, menuItems[i], centerX, startY + (i * ITEM_SPACING), menuIndex == i);
        }

        main.gameRenderer.drawBorder();
    }

    public static void renderLoginScreen(Main main,
                                         String inputUsername, String inputPassword,
                                         boolean enteringUsername, boolean enteringPassword,
                                         int loginMenuIndex, boolean loginFailed) {
        clearAndSetupMenu(main);

        double centerX = GameConstants.WIDTH / 2.0 + CENTER_X_OFFSET;
        double startY = START_MENU_Y;

        // Username field
        String usernameText = "Username: " + inputUsername + (enteringUsername ? "_" : "");
        drawMenuItem(main, usernameText, centerX, startY, loginMenuIndex == 0);

        // Password field
        String passwordText = "Password: " + maskPassword(inputPassword) + (enteringPassword ? "_" : "");
        drawMenuItem(main, passwordText, centerX, startY + ITEM_SPACING, loginMenuIndex == 1);

        // Back option
        drawMenuItem(main, "New Player (back)", centerX, startY + 2 * ITEM_SPACING, loginMenuIndex == 2);

        // Error message
        if (loginFailed) {
            main.ctx.setFill(Color.RED);
            main.ctx.fillText("Login failed. Try again.", centerX, startY + 3 * ITEM_SPACING);
        }

        main.gameRenderer.drawBorder();
    }

    public static void renderPauseMenu(Main main, int pauseIndex) {
        clearAndSetupMenu(main);

        double centerX = GameConstants.WIDTH / 2.0 + CENTER_X_OFFSET;
        double startY = PAUSE_MENU_Y;

        String[] pauseItems = {"Save", "Not Save"};

        for (int i = 0; i < pauseItems.length; i++) {
            drawMenuItem(main, pauseItems[i], centerX, startY + (i * ITEM_SPACING), pauseIndex == i);
        }

        main.gameRenderer.drawBorder();
    }

    public static void renderSaveCredentials(Main main,
                                             String inputUsername, String inputPassword,
                                             boolean enteringUsername, boolean enteringPassword) {
        clearAndSetupMenu(main);

        double centerX = GameConstants.WIDTH / 2.0 + CENTER_X_OFFSET;
        double startY = PAUSE_MENU_Y;

        main.ctx.fillText("Enter username: " + inputUsername + (enteringUsername ? "_" : ""), centerX, startY);
        main.ctx.fillText("Enter password: " + maskPassword(inputPassword) + (enteringPassword ? "_" : ""), centerX, startY + ITEM_SPACING);
        main.ctx.fillText("Press Enter to save and exit", centerX, startY + 2 * ITEM_SPACING);

        main.gameRenderer.drawBorder();
    }

    public static void renderDuringGame(Main main, int level) {
        main.gameRenderer.drawBackground(level);
        main.gameRenderer.drawGame();
        main.gameRenderer.drawBorder();
    }
}
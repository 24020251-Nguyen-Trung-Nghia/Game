package com.arkanoid.graphics;

import com.arkanoid.GameConstants;
import com.arkanoid.Main;
import javafx.scene.paint.Color;
import com.arkanoid.resources.Images;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;



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

    public static void renderStartScreen(Main main, boolean showHint) {


        // === Vẽ ảnh nền cho màn hình start ===
        main.bkgCtx.drawImage(Images.startscreenBgImg, 0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        double centerX = GameConstants.WIDTH / 2.0;
        double startY = GameConstants.HEIGHT * 0.25;

        // === Thiết lập font chữ nổi bật ===
        Font titleFont = Font.font("Arial Black", FontWeight.EXTRA_BOLD, 40);
        Font scoreFont = Font.font("Impact", FontWeight.BOLD, 40);
        Font hintFont = Font.font("Comic Sans MS", FontWeight.BOLD, 50);

        // === Vẽ HIGH SCORE tiêu đề ===
        main.ctx.setFont(titleFont);
        main.ctx.setTextAlign(TextAlignment.CENTER);
        main.ctx.setTextBaseline(VPos.TOP);

        main.ctx.setFill(Color.GOLD);
        main.ctx.setStroke(Color.BLACK);
        main.ctx.setLineWidth(4);
        main.ctx.strokeText("HIGH SCORE", centerX, startY);
        main.ctx.fillText("HIGH SCORE", centerX, startY);

        // === Vẽ điểm số ===
        main.ctx.setFont(scoreFont);
        main.ctx.setFill(Color.WHITE);
        main.ctx.setStroke(Color.BLACK);
        main.ctx.setLineWidth(3);
        main.ctx.strokeText(Long.toString(main.highscore), centerX, startY + 70);
        main.ctx.fillText(Long.toString(main.highscore), centerX, startY + 70);

        // === Vẽ hint "Hit space to start" nếu cần ===
        if (showHint) {
            main.ctx.setFont(hintFont);
            main.ctx.setFill(Color.LIGHTGREEN);
            main.ctx.setStroke(Color.DARKGREEN);
            main.ctx.setLineWidth(2);
            main.ctx.strokeText("Hit space to start", centerX, GameConstants.HEIGHT * 0.6);
            main.ctx.fillText("Hit space to start", centerX, GameConstants.HEIGHT * 0.6);
        }

        // === Vẽ logo game ở trên ===
        main.bkgCtx.drawImage(Images.logoImg,
                (GameConstants.WIDTH - Images.logoImg.getWidth()) * 0.5-30,
                GameConstants.HEIGHT * 0.05
        );
    }



    public static void renderStartMenu(Main main, int menuIndex) {
        clearAndSetupMenu(main);

        // === Ảnh nền riêng cho Start Menu ===
        main.bkgCtx.drawImage(Images.startBgImg, 0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        double centerX = GameConstants.WIDTH / 2.0;
        double startY = GameConstants.HEIGHT * 0.45;

        // === Căn giữa chữ ===
        main.ctx.setTextAlign(TextAlignment.CENTER);
        main.ctx.setTextBaseline(VPos.TOP);
        main.ctx.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        main.ctx.setLineWidth(2);

        // === Tiêu đề ===
        main.ctx.setFill(Color.WHITE);
        main.ctx.setStroke(Color.BLACK);
        main.ctx.strokeText("START MENU", centerX, startY - 100);
        main.ctx.fillText("START MENU", centerX, startY - 100);

        // === Các lựa chọn menu ===
        String[] menuItems = {"New Player", "Old Player"};

        for (int i = 0; i < menuItems.length; i++) {
            boolean selected = (menuIndex == i);
            String text = menuItems[i];

            if (selected) {
                // Màu chữ nổi bật khi được chọn
                main.ctx.setFill(Color.YELLOW);
                main.ctx.setStroke(Color.ORANGE);
            } else {
                main.ctx.setFill(Color.WHITE);
                main.ctx.setStroke(Color.BLACK);
            }

            main.ctx.strokeText(text, centerX, startY + i * 60);
            main.ctx.fillText(text, centerX, startY + i * 60);
        }

        // === Vẽ khung viền ngoài cùng ===
        main.gameRenderer.drawBorder();
    }


    public static void renderLoginScreen(Main main,
                                         String inputUsername, String inputPassword,
                                         boolean enteringUsername, boolean enteringPassword,
                                         int loginMenuIndex, boolean loginFailed) {
        clearAndSetupMenu(main);

        main.bkgCtx.drawImage(Images.loginBgImg, 0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        // === Căn giữa chữ ===
        double centerX = GameConstants.WIDTH / 2.0;
        double startY = GameConstants.HEIGHT * 0.4;

        main.ctx.setTextAlign(TextAlignment.CENTER);
        main.ctx.setTextBaseline(VPos.TOP);
        main.ctx.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        main.ctx.setLineWidth(2);

        // === Tiêu đề ===
        main.ctx.setFill(Color.WHITE);
        main.ctx.setStroke(Color.BLACK);
        main.ctx.strokeText("LOGIN", centerX, startY - 100);
        main.ctx.fillText("LOGIN", centerX, startY - 100);

        // === Các dòng nhập ===
        String usernameText = "Username: " + inputUsername + (enteringUsername ? "_" : "");
        String passwordText = "Password: " + maskPassword(inputPassword) + (enteringPassword ? "_" : "");
        String[] loginItems = { usernameText, passwordText, "New Player (back)" };

        for (int i = 0; i < loginItems.length; i++) {
            boolean selected = (loginMenuIndex == i);
            String text = loginItems[i];

            if (selected) {
                main.ctx.setFill(Color.YELLOW);
                main.ctx.setStroke(Color.ORANGE);
            } else {
                main.ctx.setFill(Color.WHITE);
                main.ctx.setStroke(Color.BLACK);
            }

            main.ctx.strokeText(text, centerX, startY + i * 60);
            main.ctx.fillText(text, centerX, startY + i * 60);
        }

        // === Hiển thị thông báo lỗi (nếu có) ===
        if (loginFailed) {
            main.ctx.setFill(Color.RED);
            main.ctx.setStroke(Color.BLACK);
            main.ctx.strokeText("Login failed. Try again.", centerX, startY + 3 * 60);
            main.ctx.fillText("Login failed. Try again.", centerX, startY + 3 * 60);
        }

    }

    public static void renderPauseMenu(Main main, int pauseIndex) {
        clearAndSetupMenu(main);

        // Vẽ ảnh nền riêng cho pause menu
        main.bkgCtx.drawImage(Images.pauseBgImg, 0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        double centerX = GameConstants.WIDTH / 2.0;
        double startY = GameConstants.HEIGHT * 0.45;

        // Thiết lập font và căn lề
        main.ctx.setTextAlign(TextAlignment.CENTER);
        main.ctx.setTextBaseline(VPos.TOP);
        main.ctx.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        main.ctx.setFill(Color.WHITE);
        main.ctx.setStroke(Color.BLACK);
        main.ctx.setLineWidth(2);

        // Tiêu đề
        main.ctx.strokeText("PAUSED", centerX, startY - 100);
        main.ctx.fillText("PAUSED", centerX, startY - 100);

        // Các lựa chọn menu
        String[] pauseItems = {"Save", "Not Save"};

        for (int i = 0; i < pauseItems.length; i++) {
            boolean selected = (pauseIndex == i);
            String text = pauseItems[i];

            if (selected) {
                // Màu khác khi chọn
                main.ctx.setFill(Color.YELLOW);
                main.ctx.setStroke(Color.ORANGE);
            } else {
                main.ctx.setFill(Color.WHITE);
                main.ctx.setStroke(Color.BLACK);
            }

            main.ctx.strokeText(text, centerX, startY + i * 60);
            main.ctx.fillText(text, centerX, startY + i * 60);
        }


    }


    public static void renderSaveCredentials(Main main,
                                             String inputUsername, String inputPassword,
                                             boolean enteringUsername, boolean enteringPassword) {
        clearAndSetupMenu(main);

// Thêm đoạn này để vẽ ảnh nền riêng
        main.bkgCtx.drawImage(Images.saveBgImg, 0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        double centerX = GameConstants.WIDTH / 2.0 + CENTER_X_OFFSET;
        double startY = PAUSE_MENU_Y;

        main.ctx.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        main.ctx.setFill(Color.WHITE);
        main.ctx.setStroke(Color.BLACK);
        main.ctx.setLineWidth(2);

        main.ctx.strokeText("Enter username: " + inputUsername + (enteringUsername ? "_" : ""), centerX, startY);
        main.ctx.fillText("Enter username: " + inputUsername + (enteringUsername ? "_" : ""), centerX, startY);

        main.ctx.strokeText("Enter password: " + maskPassword(inputPassword) + (enteringPassword ? "_" : ""), centerX, startY + ITEM_SPACING);
        main.ctx.fillText("Enter password: " + maskPassword(inputPassword) + (enteringPassword ? "_" : ""), centerX, startY + ITEM_SPACING);

        main.ctx.strokeText("Press Enter to save and exit", centerX, startY + 2 * ITEM_SPACING);
        main.ctx.fillText("Press Enter to save and exit", centerX+80, startY + 2 * ITEM_SPACING);

    }

    public static void renderDuringGame(Main main, int level) {
        main.gameRenderer.drawBackground(level);
        main.gameRenderer.drawGame();
        main.gameRenderer.drawBorder();
    }
}
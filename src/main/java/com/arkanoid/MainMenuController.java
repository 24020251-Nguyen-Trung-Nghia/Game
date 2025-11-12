package com.arkanoid;

import com.arkanoid.graphics.Fonts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MainMenuController {
    private final Main main;
    private Scene scene;

    public MainMenuController(Main main) {
        this.main = main;
        createScene();
    }

    private Button createImageButton(String imagePath, double width, double height) {
        Button button = new Button();
        button.setPrefSize(width, height);
        button.setMaxSize(width, height);
        button.setMinSize(width, height);

        // Set transparent background và padding = 0
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 0;"
        );

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true); // Làm mượt ảnh

            button.setGraphic(imageView);

            // Hiệu ứng hover - Dùng DropShadow hoặc Glow
            javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
            glow.setColor(Color.CYAN);
            glow.setRadius(20);
            glow.setSpread(0.6);

            button.setOnMouseEntered(e -> {
                imageView.setEffect(glow); // Thêm hiệu ứng phát sáng
                imageView.setScaleX(1.05);  // Phóng to nhẹ 5%
                imageView.setScaleY(1.05);
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 0;"
                );
            });

            button.setOnMouseExited(e -> {
                imageView.setEffect(null); // Bỏ hiệu ứng
                imageView.setScaleX(1.0);  // Trở về kích thước bình thường
                imageView.setScaleY(1.0);
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-padding: 0;"
                );
            });

            // Hiệu ứng khi click
            button.setOnMousePressed(e -> {
                imageView.setScaleX(0.95);
                imageView.setScaleY(0.95);
            });

            button.setOnMouseReleased(e -> {
                imageView.setScaleX(1.05);
                imageView.setScaleY(1.05);
            });

        } catch (Exception e) {
            // Fallback: nếu không có ảnh, dùng button với chữ
            button.setText("BUTTON");
            button.setFont(Fonts.emulogic(16));
            button.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black;");
            System.err.println("Không tìm thấy ảnh button: " + imagePath + " - " + e.getMessage());
        }

        return button;
    }

    private void createScene() {
        // Root container với StackPane để chồng background và content
        StackPane root = new StackPane();
        root.setPrefSize(GameConstants.WIDTH, GameConstants.HEIGHT);

        // Background
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("backgroundmenu.png"));
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(GameConstants.WIDTH);
            background.setFitHeight(GameConstants.HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        } catch (Exception e) {
            // Nếu không có ảnh, dùng màu nền
            root.setStyle("-fx-background-color: #1a1a2e;");
        }

        // Content container
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        // Logo/Title
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/arkanoid/arkanoid_logo.png"));
            ImageView logo = new ImageView(logoImage);
            logo.setPreserveRatio(true);
            logo.setFitWidth(400); // Điều chỉnh kích thước phù hợp
            content.getChildren().add(logo);
        } catch (Exception e) {
            // Nếu không có ảnh logo, dùng label như cũ
            Label title = new Label("ARKANOID");
            title.setFont(Fonts.emulogic(40));
            title.setTextFill(Color.CYAN);
            content.getChildren().add(title);
            System.err.println("Không tìm thấy ảnh logo: " + e.getMessage());
        }

        // Play button
        Button playButton = createImageButton("/com/arkanoid/play.png", 300, 50);
        playButton.setOnAction(e -> main.showBoardSelect());

        // Guide button
        Button guideButton = createImageButton("/com/arkanoid/guide.png", 300, 50);
        guideButton.setOnAction(e -> System.out.println("Chức năng Hướng dẫn chưa làm!"));

        // Exit button
        Button exitButton = createImageButton("/com/arkanoid/exit.png", 300, 50);
        exitButton.setOnAction(e -> main.stop());

        content.getChildren().addAll(playButton, guideButton, exitButton);
        root.getChildren().add(content);

        scene = new Scene(root, GameConstants.WIDTH, GameConstants.HEIGHT);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Fonts.emulogic(16));
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setStyle(
                "-fx-background-color: #00ff00;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: #00aa00;" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;"
        );

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #00ff88;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: #00ffff;" +
                        "-fx-border-width: 3;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #00ff00;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: #00aa00;" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;"
        ));

        return button;
    }

    public Scene getScene() {
        return scene;
    }
}

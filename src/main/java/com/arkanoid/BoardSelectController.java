package com.arkanoid;

import com.arkanoid.graphics.Fonts;
import com.arkanoid.models.EnumDefinitions;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BoardSelectController {
    private final Main main;
    private Scene scene;
    private EnumDefinitions.PaddleState selectedPaddle = EnumDefinitions.PaddleState.STANDARD;

    public BoardSelectController(Main main) {
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
            imageView.setSmooth(true);

            button.setGraphic(imageView);

            // Hiệu ứng hover
            javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
            glow.setColor(Color.CYAN);
            glow.setRadius(20);
            glow.setSpread(0.6);

            button.setOnMouseEntered(e -> {
                imageView.setEffect(glow);
                imageView.setScaleX(1.05);
                imageView.setScaleY(1.05);
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 0;"
                );
            });

            button.setOnMouseExited(e -> {
                imageView.setEffect(null);
                imageView.setScaleX(1.0);
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
            button.setText("TIẾP TỤC");
            button.setFont(Fonts.emulogic(16));
            button.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black;");
            System.err.println("Không tìm thấy ảnh button: " + imagePath + " - " + e.getMessage());
        }

        return button;
    }

    private void createScene() {
        StackPane root = new StackPane();
        root.setPrefSize(GameConstants.WIDTH, GameConstants.HEIGHT);

        // Background
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("background.png"));
            ImageView background = new ImageView(bgImage);
            background.setFitWidth(GameConstants.WIDTH);
            background.setFitHeight(GameConstants.HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #1a1a2e;");
        }

        // Content
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        // Title
        try {
            Image titleImage = new Image(getClass().getResourceAsStream("/com/arkanoid/selectpaddle.png"));
            ImageView titleView = new ImageView(titleImage);
            titleView.setPreserveRatio(true);
            titleView.setFitWidth(400); // Điều chỉnh kích thước phù hợp
            content.getChildren().add(titleView);
        } catch (Exception e) {
            // Fallback: nếu không có ảnh, dùng label như cũ
            Label title = new Label("CHỌN LOẠI VÁN");
            title.setFont(Fonts.emulogic(24));
            title.setTextFill(Color.CYAN);
            content.getChildren().add(title);
            System.err.println("Không tìm thấy ảnh title: " + e.getMessage());
        }

        // Paddle selection container
        HBox paddleContainer = new HBox(30);
        paddleContainer.setAlignment(Pos.CENTER);

        // Create paddle boxes
        VBox standardBox = createPaddleBox("STANDARD", EnumDefinitions.PaddleState.STANDARD, "paddle_std.png");
        VBox wideBox = createPaddleBox("WIDE", EnumDefinitions.PaddleState.WIDE, "paddle_wide.png");
        VBox laserBox = createPaddleBox("LASER", EnumDefinitions.PaddleState.LASER, "paddle_gun.png");

        paddleContainer.getChildren().addAll(standardBox, wideBox, laserBox);

        // Highlight default selection
        highlightPaddleBox(standardBox);

        // Next button
        Button nextButton = createImageButton("/com/arkanoid/continue.png", 300, 50);
        nextButton.setOnAction(e -> {
            main.setPaddleType(selectedPaddle);
            main.showLevelSelect();
        });

        content.getChildren().addAll(paddleContainer, nextButton);
        root.getChildren().add(content);

        scene = new Scene(root, GameConstants.WIDTH, GameConstants.HEIGHT);
    }

    private VBox createPaddleBox(String name, EnumDefinitions.PaddleState paddleState, String imageName) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(150, 150);
        box.setPadding(new Insets(10));
        box.setStyle(
                "-fx-background-color: rgba(42, 42, 42, 0.8);" +
                        "-fx-border-color: #555;" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;"
        );

        // Paddle image
        try {
            Image paddleImg = new Image(getClass().getResourceAsStream(imageName));
            ImageView imageView = new ImageView(paddleImg);
            imageView.setFitWidth(100);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            box.getChildren().add(imageView);
        } catch (Exception e) {
            // Nếu không có ảnh, hiển thị text
            Label placeholder = new Label("[" + name + "]");
            placeholder.setFont(Fonts.emulogic(12));
            placeholder.setTextFill(Color.WHITE);
            box.getChildren().add(placeholder);
        }

        // Label
        Label label = new Label(name);
        label.setFont(Fonts.emulogic(12));
        label.setTextFill(Color.WHITE);
        box.getChildren().add(label);

        // Click handler
        box.setOnMouseClicked(e -> {
            selectedPaddle = paddleState;
            // Remove highlight from all boxes
            ((HBox) box.getParent()).getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    ((VBox) node).setStyle(
                            "-fx-background-color: rgba(42, 42, 42, 0.8);" +
                                    "-fx-border-color: #555;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-cursor: hand;"
                    );
                }
            });
            // Highlight selected box
            highlightPaddleBox(box);
            System.out.println("🎯 Đã chọn: " + name);
        });

        // Hover effect
        box.setOnMouseEntered(e -> {
            if (selectedPaddle != paddleState) {
                box.setStyle(
                        "-fx-background-color: rgba(58, 58, 58, 0.9);" +
                                "-fx-border-color: #00ffff;" +
                                "-fx-border-width: 2;" +
                                "-fx-cursor: hand;"
                );
            }
        });

        box.setOnMouseExited(e -> {
            if (selectedPaddle != paddleState) {
                box.setStyle(
                        "-fx-background-color: rgba(42, 42, 42, 0.8);" +
                                "-fx-border-color: #555;" +
                                "-fx-border-width: 2;" +
                                "-fx-cursor: hand;"
                );
            }
        });

        return box;
    }

    private void highlightPaddleBox(VBox box) {
        box.setStyle(
                "-fx-background-color: rgba(58, 58, 58, 0.9);" +
                        "-fx-border-color: #00ff00;" +
                        "-fx-border-width: 3;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, lime, 15, 0.8, 0, 0);"
        );
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

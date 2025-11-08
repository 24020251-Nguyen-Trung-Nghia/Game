package com.arkanoid;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public class LevelSelectController {
    private final Main main;
    private Scene scene;
    private int selectedLevel = 1;
    private int highestLevelUnlocked = 1;
    private VBox[] levelBoxes;

    public LevelSelectController(Main main) {
        this.main = main;
        highestLevelUnlocked = PropertyManager.INSTANCE.getInt(Constants.UNLOCKED_LEVEL_KEY, 1);
        createScene();
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

        // Content container
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(20));

        // Title
        try {
            Image titleImage = new Image(getClass().getResourceAsStream("/com/arkanoid/selectlevel.png"));
            ImageView titleView = new ImageView(titleImage);
            titleView.setPreserveRatio(true);
            titleView.setFitWidth(400);

            StackPane titleContainer = new StackPane(titleView);
            titleContainer.setPadding(new Insets(10, 0, 20, 0));
            BorderPane.setAlignment(titleContainer, Pos.CENTER);
            content.setTop(titleContainer);
        } catch (Exception e) {
            Label title = new Label("CHỌN LEVEL");
            title.setFont(Fonts.emulogic(24));
            title.setTextFill(Color.CYAN);
            BorderPane.setAlignment(title, Pos.CENTER);
            BorderPane.setMargin(title, new Insets(10, 0, 20, 0));
            content.setTop(title);
        }

        // Level grid
        TilePane levelGrid = new TilePane();
        levelGrid.setAlignment(Pos.CENTER);
        levelGrid.setHgap(15);
        levelGrid.setVgap(15);
        levelGrid.setPrefColumns(8);
        levelGrid.setPadding(new Insets(20));
        levelGrid.setStyle("-fx-background-color: transparent;");

        // Create level boxes
        levelBoxes = new VBox[32];
        for (int i = 1; i <= 32; i++) {
            VBox levelBox = createLevelBox(i);
            levelBoxes[i - 1] = levelBox;
            levelGrid.getChildren().add(levelBox);
        }

        // ScrollPane for level grid
        ScrollPane scrollPane = new ScrollPane(levelGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        content.setCenter(scrollPane);

        root.getChildren().add(content);

        scene = new Scene(root, GameConstants.WIDTH, GameConstants.HEIGHT);
    }

    private VBox createLevelBox(int levelNumber) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(70, 70);

        // BỎ KHUNG - CHỈ DÙNG TRANSPARENT
        box.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0;"
        );

        boolean isUnlocked = levelNumber <= highestLevelUnlocked;

        // Level image or number
        ImageView imageView = null;
        try {
            Image levelImg = new Image(getClass().getResourceAsStream("level" + levelNumber + ".png"));
            imageView = new ImageView(levelImg);
            imageView.setFitWidth(70);  // Kích thước ảnh
            imageView.setFitHeight(70);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            box.getChildren().add(imageView);

            // Nếu bị khóa thì làm mờ
            if (!isUnlocked) {
                imageView.setOpacity(0.3);
            }

        } catch (Exception e) {
            // Fallback to text nếu không có ảnh
            Label levelLabel = new Label(String.valueOf(levelNumber));
            levelLabel.setFont(Fonts.emulogic(18));
            levelLabel.setTextFill(isUnlocked ? Color.CYAN : Color.GRAY);
            box.getChildren().add(levelLabel);
        }

        if (isUnlocked && imageView != null) {
            ImageView finalImageView = imageView; // Để dùng trong lambda

            // Tạo hiệu ứng glow
            javafx.scene.effect.DropShadow glowEffect = new javafx.scene.effect.DropShadow();
            glowEffect.setColor(Color.CYAN);
            glowEffect.setRadius(15);
            glowEffect.setSpread(0.6);

            javafx.scene.effect.DropShadow selectedEffect = new javafx.scene.effect.DropShadow();
            selectedEffect.setColor(Color.LIME);
            selectedEffect.setRadius(20);
            selectedEffect.setSpread(0.8);

            // Click handler
            box.setOnMouseClicked(e -> {
                selectedLevel = levelNumber;
                highlightLevel(levelNumber);
                System.out.println("🎮 Đã chọn Level " + levelNumber);

                // Tự động bắt đầu game
                main.showGameSceneAndStart(levelNumber);
            });

            // Hover effect - CHỈ TRÊN ẢNH
            box.setOnMouseEntered(e -> {
                if (selectedLevel != levelNumber) {
                    finalImageView.setEffect(glowEffect);
                    finalImageView.setScaleX(1.1);
                    finalImageView.setScaleY(1.1);
                    box.setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-cursor: hand;"
                    );
                }
            });

            box.setOnMouseExited(e -> {
                if (selectedLevel != levelNumber) {
                    finalImageView.setEffect(null);
                    finalImageView.setScaleX(1.0);
                    finalImageView.setScaleY(1.0);
                    box.setStyle("-fx-background-color: transparent;");
                }
            });

            // Click effect
            box.setOnMousePressed(e -> {
                finalImageView.setScaleX(0.95);
                finalImageView.setScaleY(0.95);
            });

            box.setOnMouseReleased(e -> {
                if (selectedLevel == levelNumber) {
                    finalImageView.setScaleX(1.1);
                    finalImageView.setScaleY(1.1);
                } else {
                    finalImageView.setScaleX(1.0);
                    finalImageView.setScaleY(1.0);
                }
            });
        }

        return box;
    }

    private void highlightLevel(int levelNumber) {
        for (int i = 0; i < levelBoxes.length; i++) {
            VBox box = levelBoxes[i];
            int levelNum = i + 1;

            if (levelNum <= highestLevelUnlocked) {
                // Lấy ImageView từ VBox
                if (box.getChildren().size() > 0 && box.getChildren().get(0) instanceof ImageView) {
                    ImageView imageView = (ImageView) box.getChildren().get(0);

                    if (levelNum == levelNumber) {
                        // Level được chọn - Hiệu ứng xanh lá sáng + phóng to
                        javafx.scene.effect.DropShadow selectedEffect = new javafx.scene.effect.DropShadow();
                        selectedEffect.setColor(Color.LIME);
                        selectedEffect.setRadius(20);
                        selectedEffect.setSpread(0.8);

                        imageView.setEffect(selectedEffect);
                        imageView.setScaleX(1.1);
                        imageView.setScaleY(1.1);
                    } else {
                        // Level khác - Bỏ hiệu ứng
                        imageView.setEffect(null);
                        imageView.setScaleX(1.0);
                        imageView.setScaleY(1.0);
                    }
                }

                box.setStyle("-fx-background-color: transparent;");
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}

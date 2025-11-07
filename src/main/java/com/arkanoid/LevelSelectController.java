package com.arkanoid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LevelSelectController implements Initializable {

    @FXML
    private TilePane levelSelectionPane;

    // Khai báo tất cả 32 VBox từ FXML
    @FXML
    private VBox level1Box, level2Box, level3Box, level4Box, level5Box, level6Box, level7Box, level8Box;
    @FXML
    private VBox level9Box, level10Box, level11Box, level12Box, level13Box, level14Box, level15Box, level16Box;
    @FXML
    private VBox level17Box, level18Box, level19Box, level20Box, level21Box, level22Box, level23Box, level24Box;
    @FXML
    private VBox level25Box, level26Box, level27Box, level28Box, level29Box, level30Box, level31Box, level32Box;

    private int selectedLevel = 1;
    private Main mainApp;
    private int highestLevelUnlocked = 1;

    // Mảng chứa tất cả các VBox để dễ thao tác
    private VBox[] levelBoxes;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setMainApplication(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Đọc level đã mở khóa
        highestLevelUnlocked = PropertyManager.INSTANCE.getInt(Constants.UNLOCKED_LEVEL_KEY, 1);

        // Tạo mảng chứa tất cả các VBox
        levelBoxes = new VBox[]{
                level1Box, level2Box, level3Box, level4Box, level5Box, level6Box, level7Box, level8Box,
                level9Box, level10Box, level11Box, level12Box, level13Box, level14Box, level15Box, level16Box,
                level17Box, level18Box, level19Box, level20Box, level21Box, level22Box, level23Box, level24Box,
                level25Box, level26Box, level27Box, level28Box, level29Box, level30Box, level31Box, level32Box
        };

        // Áp dụng trạng thái khóa/mở cho từng level
        applyLockingToBoxes();

        // Highlight level đầu tiên
        //highlightLevel(selectedLevel);
    }

    private void applyLockingToBoxes() {
        for (int i = 0; i < levelBoxes.length; i++) {
            VBox box = levelBoxes[i];
            int levelNumber = i + 1;

            if (levelNumber <= highestLevelUnlocked) {
                // Level đã mở khóa
                box.setDisable(false);
                box.setOpacity(1.0);
                box.setStyle("-fx-cursor: hand;");

                // Thêm hiệu ứng hover
                addHoverEffect(box, levelNumber);
            } else {
                // Level bị khóa
                box.setDisable(true);
                box.setOpacity(0.3);
                box.setStyle("-fx-cursor: default;");
            }
        }
    }

    private void addHoverEffect(VBox box, int levelNumber) {
        box.setOnMouseEntered(e -> {
            if (!box.isDisabled() && selectedLevel != levelNumber) {
                box.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, cyan, 10, 0.5, 0, 0);");
            }
        });

        box.setOnMouseExited(e -> {
            if (!box.isDisabled() && selectedLevel != levelNumber) {
                box.setStyle("-fx-cursor: hand;");
            }
        });
    }

    @FXML
    private void handleLevelClicked(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof VBox)) {
            return;
        }

        VBox clickedBox = (VBox) source;

        // Tìm level number từ VBox được click
        for (int i = 0; i < levelBoxes.length; i++) {
            if (levelBoxes[i] == clickedBox) {
                int levelNumber = i + 1;

                // Kiểm tra level có được mở khóa không
                if (levelNumber <= highestLevelUnlocked) {
                    selectedLevel = levelNumber;
                    highlightLevel(levelNumber);
                    System.out.println("Đã chọn Level " + levelNumber);

                    // TỰ ĐỘNG BẮT ĐẦU GAME NGAY KHI CLICK
                    startGame();
                } else {
                    System.out.println("Level " + levelNumber + " đang bị khóa!");
                }
                break;
            }
        }
    }

    private void highlightLevel(int levelToHighlight) {
        // Reset tất cả các box
        for (int i = 0; i < levelBoxes.length; i++) {
            VBox box = levelBoxes[i];
            int levelNum = i + 1;

            if (!box.isDisabled()) {
                if (levelNum == levelToHighlight) {
                    // Level được chọn - viền xanh lá sáng
                    box.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, lime, 15, 0.8, 0, 0);");
                } else {
                    // Level khác - bỏ viền
                    box.setStyle("-fx-cursor: hand;");
                }
            }
        }
    }

    private void startGame() {
        if (mainApp != null) {
            System.out.println("Bắt đầu chơi Level " + selectedLevel);
            mainApp.showGameSceneAndStart(selectedLevel);
        } else {
            System.err.println("Lỗi: mainApp chưa được gán!");
        }
    }
}

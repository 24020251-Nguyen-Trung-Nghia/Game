package com.arkanoid;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {

    @FXML
    private Button playButton;

    @FXML
    private Button guideButton;

    @FXML
    private Button exitButton;

    // Biến để giữ tham chiếu đến lớp Main (trình quản lý)
    private Main mainApp;

    /**
     * Hàm này được Main.java gọi để "tiêm" chính nó vào.
     */
    public void setMainApplication(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    void handlePlayButton(ActionEvent event) {
        // Yêu cầu Main chuyển sang cảnh chọn ván
        if (mainApp != null) {
            mainApp.showBoardSelect();
        }
    }

    @FXML
    void handleGuideButton(ActionEvent event) {
        // Sau này bạn có thể gọi mainApp.showGuideScene()
        System.out.println("Chức năng Hướng dẫn chưa làm!");
    }

    @FXML
    void handleExitButton(ActionEvent event) {
        // Thoát game
        Platform.exit();
    }
}

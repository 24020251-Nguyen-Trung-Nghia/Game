package com.arkanoid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import com.arkanoid.EnumDefinitions.PaddleState; // Import Enum

import java.net.URL;
import java.util.ResourceBundle;

public class BoardSelectController implements Initializable {

    @FXML
    private TilePane paddleSelectionPane;
    @FXML
    private Button nextButton;
    private Main mainApp;
    private PaddleState selectedPaddle = PaddleState.STANDARD;

    public void setMainApplication(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (PaddleState state : PaddleState.values()) {
            VBox paddleBox = createPaddleBox(state);
            paddleSelectionPane.getChildren().add(paddleBox);
        }
        highlightPaddle(selectedPaddle);
    }

    private VBox createPaddleBox(PaddleState paddle) {
        VBox box = new VBox(5); // Spacing 5
        box.setAlignment(Pos.CENTER);
        String imageName = paddle.name().toLowerCase();
        Image img = null;
        try {
            // === SỬA ĐƯỜNG DẪN Ở ĐÂY (BỎ DẤU /) ===
            // Nó sẽ tìm trong cùng package với Controller
            img = new Image(getClass().getResourceAsStream("paddle_" + imageName + ".png"));
            // =====================================
            if (img == null || img.isError()) throw new NullPointerException(); // Ném lỗi nếu không tìm thấy
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: paddle_" + imageName + ".png" + ". Dùng ảnh placeholder.");
            try {
                // === VÀ CẢ Ở ĐÂY (ẢNH PLACEHOLDER) ===
                img = new Image(getClass().getResourceAsStream("placeholder.png"));
                // ======================================
            } catch (Exception e2) {
                System.err.println("LỖI NGHIÊM TRỌNG: Không tìm thấy cả ảnh placeholder.png!");
                // Bạn có thể tạo ảnh mặc định bằng code ở đây nếu muốn
            }
        }
        return box;
    }

    private void highlightPaddle(PaddleState paddle) {
        for (var node : paddleSelectionPane.getChildren()) {
            node.setStyle("-fx-border-color: transparent;");
        }
        int index = paddle.ordinal();
        if (index < paddleSelectionPane.getChildren().size()) {
            paddleSelectionPane.getChildren().get(index).setStyle("-fx-border-color: #00FFFF; -fx-border-width: 3;");
        }
    }

    @FXML
    void handleNextButton(ActionEvent event) {
        if (mainApp != null) {
            mainApp.setPaddleType(this.selectedPaddle); // Báo cho Main
            mainApp.showLevelSelect(); // Chuyển sang chọn level
        } else {
            System.err.println("LỖI: mainApp chưa được kết nối!");
        }
    }
}

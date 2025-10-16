package com.arkanoid;

public class EnumDefinitions {
    // Trạng thái của paddle (ván đỡ)
    public enum PaddleState {
        STANDARD(80, 22),
        WIDE(120, 22),
        LASER(80, 22);

        protected final double width;
        protected final double height;

        PaddleState(final double width, final double height) {
            this.width = width;
            this.height = height;
        }
    }// Các loại power-up (vật phẩm đặc biệt)

    public enum BonusType {
        BONUS_C,  // Catch Ball (màu lime) - Bóng dính vào paddle
        BONUS_D,  // 3-Balls (cyan) - Nhân đôi thành 3 bóng
        BONUS_F,  // Wide (dark blue) - Paddle mở rộng
        BONUS_L,  // Laser (red) - Paddle có súng laser
        BONUS_S,  // Slow (dark yellow) - Làm chậm bóng
        BONUS_B,  // Next Level (magenta) - Qua level tiếp theo
        BONUS_P;  // Additional life (gray) - Thêm 1 mạng
    }// Loại kẻ địch (hiện chỉ có 1 loại)

    public enum EnemyType {
        MOLECULE; // Phân tử - kẻ địch di chuyển
    }
}
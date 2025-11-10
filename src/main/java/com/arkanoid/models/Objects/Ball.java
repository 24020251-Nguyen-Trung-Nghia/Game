package com.arkanoid.models.Objects;

import  com.arkanoid.GameConstants;
import com.arkanoid.Main;
import com.arkanoid.graphics.Bounds;
import com.arkanoid.models.EnumDefinitions;
import com.arkanoid.resources.AutoClips;
import com.arkanoid.utils.BallHit;
import javafx.scene.image.Image;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Ball extends Sprite {
    private final Main GameStateManager;
    public boolean active;
    public long bornTimestamp;

    // ******************** Constructors **************************************
    public Ball(Main main, final Image image, final double x, final double y, final double vX) {
        this(main, image, x, y, vX, false);
    }

    public Ball(Main main, final Image image, final double x, final double y, final double vX, final boolean active) {
        super(image, main.paddle.bounds.centerX, main.paddle.bounds.minY - image.getHeight() * 0.5 - GameConstants.BALL_SPEED - 1, 0, -main.ballSpeed);
        this.GameStateManager = main;
        this.vX = vX;
        this.active = active;
        this.bornTimestamp = Instant.now().getEpochSecond();
    }

    public Ball(Main main, final Image image, final double x, final double y, final double vX, final double vY) {
        super(image, x, y, vX, vY);
        this.GameStateManager = main;
        this.active = true;
        this.bornTimestamp = Instant.now().getEpochSecond();
    }

    // ******************** Methods *******************************************
    @Override
    public void update() {
        if (!active) {
            // Nếu bóng không active, đặt vị trí bóng trên paddle
            this.x = GameStateManager.paddle.bounds.centerX;
            this.y = GameStateManager.paddle.bounds.minY - image.getHeight() * 0.5 - GameConstants.BALL_SPEED - 1;
        } else {
            // Cần kiểm tra nếu bóng chạm vào block
            double x0 = x;       // (x0, y0) = tọa độ ban đầu
            double y0 = y;
            double x1 = x0 + vX; // (x1, y1) = tọa độ cuối nếu không có va chạm
            double y1 = y0 + vY; // (x1, y1) có thể cần điều chỉnh nếu bóng chạm block

            while (true) {
                // Lưu giá trị cuối cho biểu thức lambda
                double fx0 = x0;
                double fy0 = y0;
                double fx1 = x1;
                double fy1 = y1;

                // Tính toán va chạm bóng với tất cả các bounds
                BallHit ballHit = Stream.concat(GameStateManager.blocks.stream().map(b -> b.bounds), // Duyệt qua tất cả block bounds
                                Stream.concat(Arrays.stream(GameConstants.BORDER_BOUNDS), Stream.of(GameStateManager.paddle.bounds))) // cùng với borders và paddle bounds
                        .map(bounds -> bounds.computeBallHit(fx0, fy0, fx1, fy1, radius)) // tính toán va chạm (trả về null nếu không va chạm)
                        .filter(Objects::nonNull) // loại bỏ các trường hợp không va chạm
                        // Nếu quỹ đạo (x0, y0) -> (x1, y1) chạm nhiều blocks, giữ lại block đầu tiên
                        .min(Comparator.comparingDouble(ballHit1 -> ballHit1.beforeHitDistance)) // va chạm đầu tiên = khoảng cách va chạm nhỏ nhất
                        .orElse(null); // trả về null nếu không có va chạm

                if (ballHit == null) { // Nếu không có va chạm, (x1, y1) không cần điều chỉnh
                    this.x = x1;
                    this.y = y1;
                    break; // thoát vòng lặp
                }

                // Có va chạm bóng khi chạy đến đoạn code này
                // => Đảo ngược vX hoặc vY nếu cần
                if (ballHit.inverseVx) {
                    vX = -vX;
                }
                if (ballHit.inverseVy) {
                    vY = -vY;
                }

                // Chuẩn bị cho vòng lặp tiếp theo bằng cách cập nhật (x0, y0) -> (x1, y1) thành (xHit, yHit) -> (correctedX, correctedY)
                x0 = ballHit.xHit;
                y0 = ballHit.yHit;
                x1 = ballHit.correctedX;
                y1 = ballHit.correctedY;

                // Trước khi lặp, xử lý trường hợp đặc biệt khi chạm paddle hoặc blocks
                if (ballHit.hitBounds == GameStateManager.paddle.bounds) {
                    Bounds pb = GameStateManager.paddle.bounds;
                    if (GameStateManager.stickyPaddle) {
                        // Chế độ paddle dính: bóng dính vào paddle
                        this.x = pb.centerX;
                        this.y = pb.minY - image.getHeight() * 0.5 - GameConstants.BALL_SPEED - 1;
                        this.active = false;
                        break;
                    } else {
                        // Ảnh hưởng vX của bóng nếu vX của paddle != 0
                        if (GameStateManager.paddle.vX != 0) {
                            double speedXY = Math.sqrt(vX * vX + vY * vY);
                            double posX = (x1 - pb.centerX) / (pb.width * 0.5);
                            double speedX = speedXY * posX * GameConstants.BALL_VX_INFLUENCE;
                            vX = speedX;
                            vY = -Math.sqrt(speedXY * speedXY - speedX * speedX);
                            // Tính lại (x1, y1) từ tốc độ mới
                            x1 = x0 + vX;
                            y1 = y0 + vY;
                        } else {
                            // Điều chỉnh góc do góc tròn của paddle
                            double pcr = pb.height;                     // bán kính góc paddle
                            boolean hitLeftCorner = x0 < pb.minX + pcr; // Lưu ý: x0 là tọa độ X va chạm
                            boolean hitRightCorner = x0 > pb.maxX - pcr;

                            if (hitLeftCorner || hitRightCorner) {
                                double distanceToCorner = hitLeftCorner ? pb.minX + pcr - x0 : x0 - pb.maxX + pcr;
                                double cornerAngleRad = Math.acos(distanceToCorner * 0.85 / pcr); // Hệ số 0.85 vì góc không sắc như hình tròn
                                double minRad = Math.PI / 180 * 35;    // Để tránh tốc độ quá ngang
                                double maxRad = Math.PI / 180 * 75;    // Giữ tốc độ va chạm

                                // Điều chỉnh góc thấp => minRad
                                if (Double.isNaN(cornerAngleRad) || cornerAngleRad < minRad) {
                                    cornerAngleRad = minRad;
                                }

                                // Giữ tốc độ va chạm nếu quá gần 90 độ
                                if (cornerAngleRad < maxRad) {
                                    // Áp dụng góc tốc độ mới
                                    vY = GameConstants.BALL_SPEED * Math.sin(cornerAngleRad) * (y0 > pb.centerY ? 1 : -1);
                                    vX = GameConstants.BALL_SPEED * Math.cos(cornerAngleRad) * (hitLeftCorner ? -1 : 1);
                                    // Tính lại (x1, y1) từ tốc độ mới
                                    x1 = x0 + vX;
                                    y1 = y0 + vY;
                                }
                            }
                        }
                    }
                    GameStateManager.playSound(AutoClips.ballPaddleSnd);
                }

                // Lấy block bị bóng chạm để xử lý âm thanh, hiệu ứng nhấp nháy & tính điểm
                Block block = GameStateManager.blocks.stream()
                        .filter(b -> b.bounds == ballHit.hitBounds)
                        .findFirst()
                        .orElse(null);

                if (block != null) { // Có thể null nếu bóng chạm thứ khác (paddle hoặc border)
                    switch (block.blockType) {
                        case GOLD -> {
                            GameStateManager.playSound(AutoClips.ballHardBlockSnd);
                            GameStateManager.blinks.add(new Blink(block.bounds.minX, block.bounds.minY));
                        }
                        case GRAY -> {
                            block.hits++;
                            if (block.hits == block.maxHits) {
                                // GIỮ NGUYÊN LOGIC CŨ - trực tiếp thay đổi score và blockCounter
                                GameStateManager.score += GameStateManager.level * 50L;
                                GameStateManager.blockCounter += 1;
                                block.toBeRemoved = true;
                                GameStateManager.playSound(AutoClips.ballBlockSnd);
                            } else {
                                GameStateManager.playSound(AutoClips.ballHardBlockSnd);
                                GameStateManager.blinks.add(new Blink(block.bounds.minX, block.bounds.minY));
                            }
                        }
                        default -> {
                            block.hits++;
                            if (block.hits >= block.maxHits) {
                                // GIỮ NGUYÊN LOGIC CŨ - trực tiếp thay đổi score và blockCounter
                                GameStateManager.score += block.value;
                                GameStateManager.blockCounter += 1;
                                block.toBeRemoved = true;
                                GameStateManager.playSound(AutoClips.ballBlockSnd);
                                GameStateManager.bonusBlocks.add(new BonusBlock(block.x, block.y,
                                        EnumDefinitions.BonusType.values()[GameConstants.RND.nextInt(EnumDefinitions.BonusType.values().length)]));
                            }
                        }
                    }
                    GameStateManager.blockFifo.add(block);

                    // Kiểm tra pattern để tăng tốc
                    final List<Block> items = GameStateManager.blockFifo.getItems();
                    if (items.size() == 9) {
                        if (items.get(0).equals(items.get(6)) &&
                                items.get(1).equals(items.get(5)) &&
                                items.get(1).equals(items.get(7)) &&
                                items.get(2).equals(items.get(4)) &&
                                items.get(2).equals(items.get(8))) {
                            this.vX += 0.1;
                        } else if (items.get(0).equals(items.get(8)) &&
                                items.get(1).equals(items.get(7)) &&
                                items.get(2).equals(items.get(6)) &&
                                items.get(3).equals(items.get(5))) {
                            this.vX += 0.1;
                        }
                    }
                }
            }
        }

        this.bounds.set(this.x - this.width * 0.5, this.y - this.height * 0.5, this.width, this.height);

        // Kiểm tra va chạm bóng với kẻ địch
        for (Enemy enemy : GameStateManager.enemies) {
            boolean ballHitsEnemy = this.bounds.intersects(enemy.bounds);
            if (ballHitsEnemy) {
                enemy.toBeRemoved = true;
                GameStateManager.explosions.add(new Explosion(enemy.x, enemy.y, enemy.vX, enemy.vY, 1.0));
                GameStateManager.playSound(AutoClips.explosionSnd);

                if (bounds.centerX > enemy.bounds.minX && bounds.centerX < enemy.bounds.maxX) {
                    // Va chạm trên hoặc dưới
                    vY = -vY;
                } else if (bounds.centerY > enemy.bounds.minY && bounds.centerY < enemy.bounds.maxY) {
                    // Va chạm trái hoặc phải
                    vX = -vX;
                } else {
                    double dx = Math.abs(bounds.centerX - enemy.bounds.centerX) - enemy.bounds.width * 0.5;
                    double dy = Math.abs(bounds.centerY - enemy.bounds.centerY) - enemy.bounds.height * 0.5;
                    if (dx > dy) {
                        // Va chạm trái hoặc phải
                        vX = -vX;
                    } else {
                        // Va chạm trên hoặc dưới
                        vY = -vY;
                    }
                }
                break;
            }
        }

        // Đảm bảo bóng không có vX = 0
        if (Double.compare(vX, 0) == 0) {
            vX = 0.5;
        }

        // Kiểm tra nếu bóng rơi ra khỏi màn hình
        if (this.bounds.maxY > GameConstants.HEIGHT) {
            this.toBeRemoved = true;
        }
    }
}

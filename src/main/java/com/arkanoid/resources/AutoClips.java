package com.arkanoid.resources;

import com.arkanoid.Main;
import javafx.scene.media.AudioClip;

public class AutoClips {
    private static AutoClips instance;

    // Các biến âm thanh static
    public static AudioClip gameStartSnd;
    public static AudioClip startLevelSnd;
    public static AudioClip ballPaddleSnd;
    public static AudioClip ballBlockSnd;
    public static AudioClip ballHardBlockSnd;
    public static AudioClip laserSnd;
    public static AudioClip explosionSnd;
    public static AudioClip gameOverSnd;

    // Constructor private để ngăn việc khởi tạo từ bên ngoài
    private AutoClips() {}

    // Phương thức static để truy cập instance duy nhất
    public static AutoClips getInstance() {
        if (instance == null) {
            instance = new AutoClips();
        }
        return instance;
    }

    public static void loadSounds(Main main) {
        gameStartSnd = new AudioClip(main.getClass().getResource("game_start.wav").toExternalForm());
        startLevelSnd = new AudioClip(main.getClass().getResource("level_ready.wav").toExternalForm());
        ballPaddleSnd = new AudioClip(main.getClass().getResource("ball_paddle.wav").toExternalForm());
        ballBlockSnd = new AudioClip(main.getClass().getResource("ball_block.wav").toExternalForm());
        ballHardBlockSnd = new AudioClip(main.getClass().getResource("ball_hard_block.wav").toExternalForm());
        laserSnd = new AudioClip(main.getClass().getResource("gun.wav").toExternalForm());
        explosionSnd = new AudioClip(main.getClass().getResource("explosion.wav").toExternalForm());
        gameOverSnd = new AudioClip(main.getClass().getResource("game_over.wav").toExternalForm());
    }
}
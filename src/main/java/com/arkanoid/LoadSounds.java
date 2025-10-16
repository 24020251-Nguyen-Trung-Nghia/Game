package com.arkanoid;

import javafx.scene.media.AudioClip;

public class LoadSounds {
    public void loadSounds(AutoClips autoClips, Main main) {
        autoClips.gameStartSnd = new AudioClip(main.getClass().getResource("game_start.wav").toExternalForm());
        autoClips.startLevelSnd = new AudioClip(main.getClass().getResource("level_ready.wav").toExternalForm());
        autoClips.ballPaddleSnd = new AudioClip(main.getClass().getResource("ball_paddle.wav").toExternalForm());
        autoClips.ballBlockSnd = new AudioClip(main.getClass().getResource("ball_block.wav").toExternalForm());
        autoClips.ballHardBlockSnd = new AudioClip(main.getClass().getResource("ball_hard_block.wav").toExternalForm());
        autoClips.laserSnd = new AudioClip(main.getClass().getResource("gun.wav").toExternalForm());
        autoClips.explosionSnd = new AudioClip(main.getClass().getResource("explosion.wav").toExternalForm());
        autoClips.gameOverSnd = new AudioClip(main.getClass().getResource("game_over.wav").toExternalForm());
    }
}

package com.arkanoid.graphics;

import javafx.scene.text.Font;

/**
 * Một class hỗ trợ.
 * Class này giữ một font duy nhất là emulogic (Tạm thời game này chỉ dùng 1 font).
 */
public class Fonts {
    private static final String EMULOGIC_NAME;
    private static       String emulogicName;

    //private Fonts() {}


    static {
        try {
            emulogicName  = Font.loadFont(Fonts.class.getResourceAsStream("/com/arkanoid/Emulogic-zrEw.ttf"), 10).getName();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        EMULOGIC_NAME   = emulogicName;
    }


    // ******************** Methods *******************************************

    /**
     * khi ta cần lấy Font cho một đối tượng nào đó, font là emulogic và kích cỡ chữ tùy theo ta muốn
     */
    public static Font emulogic(final double size) { return new Font(EMULOGIC_NAME, size); }

}

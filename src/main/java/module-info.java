module com.arkanoid {
    // Java
    requires java.base;

    // Java-FX
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.media;

    exports com.arkanoid to javafx.base,javafx.graphics,javafx.controls,javafx.media;
}
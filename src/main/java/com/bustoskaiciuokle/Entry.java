package com.bustoskaiciuokle;

import com.bustoskaiciuokle.front.MainScene;

import javafx.application.Application;
import javafx.stage.Stage;

final public class Entry extends Application {
    @Override public void start(Stage stage) {
        stage.setTitle("Paskolu skaiciuotuvas");

        m_mainScene = new MainScene(400, 600);
        stage.setScene(m_mainScene.getScene());
        stage.show();
    }
    public static void run() {
        launch();
    }

    private static MainScene m_mainScene;
}
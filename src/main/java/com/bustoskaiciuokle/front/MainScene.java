package com.bustoskaiciuokle.front;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.geometry.Insets;

import com.bustoskaiciuokle.back.Calculator;

final public class MainScene {
    public MainScene(int windowWidth, int windowHeight) {
        m_root = new Group();
        m_scene = new Scene(m_root);

        InputNode node = new InputNode((Calculator calculator) -> {
            GraphWindow win = new GraphWindow(calculator);
            win.setTitle("Test");
            win.show();
        });
        node.setPadding(new Insets(10, 10, 10, 10));
        m_root.getChildren().add(node);
    }
    public Scene getScene() {
        return m_scene;
    }

    private Scene m_scene;
    private Group m_root;
}

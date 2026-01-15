package com.cgvsu;

import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.OrbitCameraController;

public class GuiController {

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private OrbitCameraController cameraController;

    private Timeline timeline;
    
    private Set<KeyCode> pressedKeys = new HashSet<>();

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        cameraController = new OrbitCameraController(camera);

        setupMouseHandlers();
        setupKeyboardHandlers();

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            handleContinuousKeyInput();

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    private void setupMouseHandlers() {
        canvas.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                cameraController.onMousePressed(event.getX(), event.getY());
            }
        });

        canvas.setOnMouseReleased(event -> {
            cameraController.onMouseReleased();
        });

        canvas.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                cameraController.onMouseDragged(event.getX(), event.getY());
            }
        });

        canvas.setOnScroll(event -> {
            cameraController.onMouseScroll(event.getDeltaY());
        });
    }

    private void setupKeyboardHandlers() {
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        canvas.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
        });

        canvas.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
        });
    }

    private void handleContinuousKeyInput() {
        if (pressedKeys.contains(KeyCode.W)) {
            cameraController.moveForward();
        }
        if (pressedKeys.contains(KeyCode.S)) {
            cameraController.moveBackward();
        }
        if (pressedKeys.contains(KeyCode.A)) {
            cameraController.moveLeft();
        }
        if (pressedKeys.contains(KeyCode.D)) {
            cameraController.moveRight();
        }
        if (pressedKeys.contains(KeyCode.Q)) {
            cameraController.moveUp();
        }
        if (pressedKeys.contains(KeyCode.E)) {
            cameraController.moveDown();
        }
        if (pressedKeys.contains(KeyCode.R)) {
            cameraController.reset();
        }
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        cameraController.moveForward();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        cameraController.moveBackward();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        cameraController.moveLeft();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        cameraController.moveRight();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        cameraController.moveUp();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        cameraController.moveDown();
    }

    @FXML
    public void handleCameraReset(ActionEvent actionEvent) {
        cameraController.reset();
    }
}
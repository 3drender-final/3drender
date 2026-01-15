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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.OrbitCameraController;
import com.cgvsu.transformations.AffineTransformation;
import com.cgvsu.transformations.ModelTransformer;

public class GuiController {

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField scaleXField;
    @FXML
    private TextField scaleYField;
    @FXML
    private TextField scaleZField;
    @FXML
    private TextField rotateXField;
    @FXML
    private TextField rotateYField;
    @FXML
    private TextField rotateZField;
    @FXML
    private TextField translateXField;
    @FXML
    private TextField translateYField;
    @FXML
    private TextField translateZField;

    private Model originalMesh = null;
    private Model mesh = null;
    private Matrix4f currentModelMatrix = null;

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
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, currentModelMatrix);
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
            originalMesh = ObjReader.read(fileContent);
            mesh = copyModel(originalMesh);
            currentModelMatrix = AffineTransformation.identity();
            resetTransformationFields();
        } catch (IOException exception) {
            showError("Ошибка загрузки модели", "Не удалось прочитать файл: " + exception.getMessage());
        } catch (ObjReaderException exception) {
            showError("Ошибка парсинга модели", exception.getMessage());
        } catch (Exception exception) {
            showError("Ошибка загрузки модели", "Неожиданная ошибка: " + exception.getMessage());
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        if (originalMesh == null) {
            showError("Нет модели", "Сначала загрузите модель");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model (Original)");

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            ObjWriter.write(originalMesh, file.getAbsolutePath());
        } catch (IOException exception) {
            showError("Ошибка сохранения модели", exception.getMessage());
        }
    }

    @FXML
    private void onSaveTransformedModelMenuItemClick() {
        if (mesh == null) {
            showError("Нет модели", "Сначала загрузите модель");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model (Transformed)");

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            Model transformedModel = copyModel(originalMesh);
            if (currentModelMatrix != null) {
                ModelTransformer.transformMatrix(transformedModel, currentModelMatrix);
            }
            ObjWriter.write(transformedModel, file.getAbsolutePath());
        } catch (IOException exception) {
            showError("Ошибка сохранения модели", exception.getMessage());
        }
    }

    @FXML
    private void handleApplyTransformations() {
        if (mesh == null) {
            showError("Нет модели", "Сначала загрузите модель");
            return;
        }

        try {
            float scaleX = Float.parseFloat(scaleXField.getText());
            float scaleY = Float.parseFloat(scaleYField.getText());
            float scaleZ = Float.parseFloat(scaleZField.getText());
            
            float rotateX = (float) Math.toRadians(Double.parseDouble(rotateXField.getText()));
            float rotateY = (float) Math.toRadians(Double.parseDouble(rotateYField.getText()));
            float rotateZ = (float) Math.toRadians(Double.parseDouble(rotateZField.getText()));
            
            float translateX = Float.parseFloat(translateXField.getText());
            float translateY = Float.parseFloat(translateYField.getText());
            float translateZ = Float.parseFloat(translateZField.getText());

            Matrix4f scale = AffineTransformation.scale(scaleX, scaleY, scaleZ);
            Matrix4f rotate = AffineTransformation.rotateX(rotateX)
                    .multiply(AffineTransformation.rotateY(rotateY))
                    .multiply(AffineTransformation.rotateZ(rotateZ));
            Matrix4f translate = AffineTransformation.translate(translateX, translateY, translateZ);

            currentModelMatrix = translate.multiply(rotate).multiply(scale);
        } catch (NumberFormatException exception) {
            showError("Ошибка ввода", "Проверьте правильность введенных значений");
        }
    }

    @FXML
    private void handleResetTransformations() {
        currentModelMatrix = AffineTransformation.identity();
        resetTransformationFields();
    }

    private void resetTransformationFields() {
        if (scaleXField != null) {
            scaleXField.setText("1.0");
            scaleYField.setText("1.0");
            scaleZField.setText("1.0");
            rotateXField.setText("0.0");
            rotateYField.setText("0.0");
            rotateZField.setText("0.0");
            translateXField.setText("0.0");
            translateYField.setText("0.0");
            translateZField.setText("0.0");
        }
    }

    private Model copyModel(Model source) {
        Model copy = new Model();
        for (Vector3f v : source.vertices) {
            copy.vertices.add(new Vector3f(v));
        }
        for (com.cgvsu.math.Vector2f v : source.textureVertices) {
            copy.textureVertices.add(new com.cgvsu.math.Vector2f(v));
        }
        for (Vector3f v : source.normals) {
            copy.normals.add(new Vector3f(v));
        }
        for (com.cgvsu.model.Polygon p : source.polygons) {
            com.cgvsu.model.Polygon polyCopy = new com.cgvsu.model.Polygon();
            polyCopy.setVertexIndices(new ArrayList<>(p.getVertexIndices()));
            polyCopy.setTextureVertexIndices(new ArrayList<>(p.getTextureVertexIndices()));
            polyCopy.setNormalIndices(new ArrayList<>(p.getNormalIndices()));
            copy.polygons.add(polyCopy);
        }
        return copy;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
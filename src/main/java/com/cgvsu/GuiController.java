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
import javafx.scene.control.ListView;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
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
import com.cgvsu.transformations.ModelMatrixBuilder;
import com.cgvsu.ui.SceneModel;
import com.cgvsu.model.ModelTransform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    @FXML
    private ListView<String> modelsListView;
    
    @FXML
    private CheckBox modelActiveCheckBox;
    
    @FXML
    private Label sceneModelInfoLabel;
    
    @FXML
    private Label scenePositionLabel;
    
    @FXML
    private Label sceneRotationLabel;
    
    @FXML
    private Label sceneScaleLabel;

    private final java.util.List<SceneModel> sceneModels = new ArrayList<>();
    private final ObservableList<String> modelNames = FXCollections.observableArrayList();
    private int selectedModelIndex = -1;

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
        setupSceneModelsUI();

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            handleContinuousKeyInput();

            // Рендерим все активные модели
            for (SceneModel sceneModel : sceneModels) {
                if (sceneModel != null && sceneModel.isActive()) {
                    Matrix4f modelMatrix = ModelMatrixBuilder.build(sceneModel.getTransform());
                    RenderEngine.render(canvas.getGraphicsContext2D(), camera, sceneModel.getModel(), (int) width, (int) height, modelMatrix);
                }
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
            Model loadedModel = ObjReader.read(fileContent);
            
            SceneModel sceneModel = new SceneModel(loadedModel, file.getName());
            sceneModels.add(sceneModel);
            modelNames.add(sceneModel.getName());
            
            if (modelsListView != null && modelsListView.getItems() != modelNames) {
                modelsListView.setItems(modelNames);
            }
            
            selectedModelIndex = sceneModels.size() - 1;
            if (modelsListView != null) {
                modelsListView.getSelectionModel().select(selectedModelIndex);
            }
            
            resetTransformationFields();
            updateTransformationFields();
            updateSceneInfo();
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
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            showError("Нет модели", "Сначала выберите модель");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialFileName(current.getName());

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            ObjWriter.write(current.getModel(), file.getAbsolutePath());
        } catch (IOException exception) {
            showError("Ошибка сохранения модели", exception.getMessage());
        }
    }

    @FXML
    private void onSaveTransformedModelMenuItemClick() {
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            showError("Нет модели", "Сначала выберите модель");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model (Transformed)");
        fileChooser.setInitialFileName(current.getName());

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            Model transformedModel = copyModel(current.getModel());
            Matrix4f modelMatrix = ModelMatrixBuilder.build(current.getTransform());
            ModelTransformer.transformMatrix(transformedModel, modelMatrix);
            ObjWriter.write(transformedModel, file.getAbsolutePath());
        } catch (IOException exception) {
            showError("Ошибка сохранения модели", exception.getMessage());
        }
    }

    @FXML
    private void handleApplyTransformations() {
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            showError("Нет модели", "Сначала выберите модель");
            return;
        }

        try {
            float scaleX = Float.parseFloat(scaleXField.getText());
            float scaleY = Float.parseFloat(scaleYField.getText());
            float scaleZ = Float.parseFloat(scaleZField.getText());
            
            float rotateX = Float.parseFloat(rotateXField.getText());
            float rotateY = Float.parseFloat(rotateYField.getText());
            float rotateZ = Float.parseFloat(rotateZField.getText());
            
            float translateX = Float.parseFloat(translateXField.getText());
            float translateY = Float.parseFloat(translateYField.getText());
            float translateZ = Float.parseFloat(translateZField.getText());

            ModelTransform transform = current.getTransform();
            transform.setScale(new Vector3f(scaleX, scaleY, scaleZ));
            transform.setRotation(new Vector3f(rotateX, rotateY, rotateZ));
            transform.setPosition(new Vector3f(translateX, translateY, translateZ));
            updateTransformationFields();
            updateSceneInfo();
        } catch (NumberFormatException exception) {
            showError("Ошибка ввода", "Проверьте правильность введенных значений");
        }
    }

    @FXML
    private void handleResetTransformations() {
        SceneModel current = getSelectedSceneModel();
        if (current != null) {
            current.getTransform().reset();
            updateTransformationFields();
        }
    }
    
    private SceneModel getSelectedSceneModel() {
        if (selectedModelIndex < 0 || selectedModelIndex >= sceneModels.size()) {
            return null;
        }
        return sceneModels.get(selectedModelIndex);
    }
    
    private void updateTransformationFields() {
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            resetTransformationFields();
            return;
        }
        
        ModelTransform transform = current.getTransform();
        Vector3f scale = transform.getScale();
        Vector3f rotation = transform.getRotation();
        Vector3f position = transform.getPosition();
        
        if (scaleXField != null) {
            scaleXField.setText(String.valueOf(scale.x));
            scaleYField.setText(String.valueOf(scale.y));
            scaleZField.setText(String.valueOf(scale.z));
            rotateXField.setText(String.valueOf(rotation.x));
            rotateYField.setText(String.valueOf(rotation.y));
            rotateZField.setText(String.valueOf(rotation.z));
            translateXField.setText(String.valueOf(position.x));
            translateYField.setText(String.valueOf(position.y));
            translateZField.setText(String.valueOf(position.z));
        }
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
    
    private void setupSceneModelsUI() {
        if (modelsListView != null) {
            modelsListView.setItems(modelNames);
            modelsListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
                selectedModelIndex = newVal.intValue();
                updateTransformationFields();
                updateSceneInfo();
                updateModelActiveCheckBox();
            });
        }

        if (modelActiveCheckBox != null) {
            modelActiveCheckBox.setOnAction(e -> {
                SceneModel current = getSelectedSceneModel();
                if (current != null) {
                    current.setActive(modelActiveCheckBox.isSelected());
                }
            });
        }

        updateModelActiveCheckBox();
    }
    
    private void updateModelActiveCheckBox() {
        if (modelActiveCheckBox == null) return;
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            modelActiveCheckBox.setSelected(false);
            modelActiveCheckBox.setDisable(true);
        } else {
            modelActiveCheckBox.setDisable(false);
            modelActiveCheckBox.setSelected(current.isActive());
        }
    }
    
    private void updateSceneInfo() {
        SceneModel current = getSelectedSceneModel();

        if (scenePositionLabel != null) {
            if (current != null) {
                Vector3f pos = current.getTransform().getPosition();
                scenePositionLabel.setText(String.format("Position: (%.2f, %.2f, %.2f)", pos.x, pos.y, pos.z));
            } else {
                scenePositionLabel.setText("Position: —");
            }
        }
        if (sceneRotationLabel != null) {
            if (current != null) {
                Vector3f rot = current.getTransform().getRotation();
                sceneRotationLabel.setText(String.format("Rotation: (%.1f°, %.1f°, %.1f°)", rot.x, rot.y, rot.z));
            } else {
                sceneRotationLabel.setText("Rotation: —");
            }
        }
        if (sceneScaleLabel != null) {
            if (current != null) {
                Vector3f scale = current.getTransform().getScale();
                sceneScaleLabel.setText(String.format("Scale: (%.2f, %.2f, %.2f)", scale.x, scale.y, scale.z));
            } else {
                sceneScaleLabel.setText("Scale: —");
            }
        }
        if (sceneModelInfoLabel != null) {
            if (current != null) {
                int vertexCount = current.getModel().vertices.size();
                int polygonCount = current.getModel().polygons.size();
                sceneModelInfoLabel.setText(String.format("Model: %s\nVertices: %d\nPolygons: %d",
                        current.getName(), vertexCount, polygonCount));
            } else {
                sceneModelInfoLabel.setText("No model selected");
            }
        }
    }
}
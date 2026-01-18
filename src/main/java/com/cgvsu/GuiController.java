package com.cgvsu;

import com.cgvsu.render_engine.*;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.transformations.AffineTransformation;
import com.cgvsu.transformations.ModelTransformer;
import com.cgvsu.transformations.ModelMatrixBuilder;
import com.cgvsu.ui.SceneModel;
import com.cgvsu.model.ModelTransform;
import com.cgvsu.model.ModelEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Scene;

import static com.cgvsu.render_engine.RenderEngine.shouldRender;

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
    
    private boolean isDarkTheme = false;
    private static final String LIGHT_THEME = "/com/cgvsu/styles/light-theme.css";
    private static final String DARK_THEME = "/com/cgvsu/styles/dark-theme.css";

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                canvas.setWidth(newValue.doubleValue());
            }
        });
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                canvas.setHeight(newValue.doubleValue());
            }
        });

        cameraController = new OrbitCameraController(camera);

        setupMouseHandlers();
        setupKeyboardHandlers();
        setupSceneModelsUI();

        // Применяем тему после того, как scene будет установлена
        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && newScene.getStylesheets().isEmpty()) {
                setupTheme();
            }
        });

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
                if (sceneModel != null && sceneModel.isActive() && shouldRender()) {
                    Model.preprocess(sceneModel.getModel());
                    Matrix4f modelMatrix = ModelMatrixBuilder.build(sceneModel.getTransform());
                    RenderEngine.render(canvas.getGraphicsContext2D(), camera, sceneModel.getModel(), (int) width, (int) height, null, null, Color.GRAY, null, new RenderingModes());
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
            // Проверка существования файла
            if (!file.exists()) {
                showError("Ошибка загрузки", "Файл не существует: " + file.getName());
                return;
            }
            
            // Проверка размера файла (предупреждение для очень больших файлов)
            long fileSize = file.length();
            if (fileSize > 50 * 1024 * 1024) { // 50 MB
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Большой файл");
                confirmAlert.setHeaderText("Файл очень большой");
                confirmAlert.setContentText(String.format("Размер файла: %.2f MB. Загрузка может занять некоторое время. Продолжить?", fileSize / (1024.0 * 1024.0)));
                if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.CANCEL) {
                    return;
                }
            }
            
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
        } catch (java.nio.file.NoSuchFileException exception) {
            showError("Ошибка загрузки", "Файл не найден: " + file.getName());
        } catch (java.nio.file.AccessDeniedException exception) {
            showError("Ошибка загрузки", "Нет доступа к файлу: " + file.getName() + "\nВозможно, файл открыт в другой программе.");
        } catch (IOException exception) {
            String errorMessage = exception.getMessage();
            if (errorMessage != null && errorMessage.contains("Permission denied")) {
                showError("Ошибка загрузки", "Нет доступа к файлу. Возможно, файл открыт в другой программе.");
            } else {
                showError("Ошибка загрузки модели", "Не удалось прочитать файл: " + (errorMessage != null ? errorMessage : "Неизвестная ошибка"));
            }
        } catch (ObjReaderException exception) {
            showError("Ошибка парсинга модели", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            showError("Ошибка загрузки", "Некорректные данные: " + exception.getMessage());
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
            // Проверка модели перед сохранением
            Model model = current.getModel();
            if (model.vertices.isEmpty()) {
                showError("Ошибка сохранения", "Модель не содержит вершин. Невозможно сохранить пустую модель.");
                return;
            }
            if (model.polygons.isEmpty()) {
                showError("Ошибка сохранения", "Модель не содержит полигонов. Невозможно сохранить модель без полигонов.");
                return;
            }
            
            ObjWriter.write(model, file.getAbsolutePath());
            showSuccess("Модель сохранена", "Модель успешно сохранена в файл:\n" + file.getName());
        } catch (IllegalArgumentException exception) {
            showError("Ошибка сохранения", "Некорректные данные модели: " + exception.getMessage());
        } catch (IOException exception) {
            String errorMessage = exception.getMessage();
            if (errorMessage != null && errorMessage.contains("Permission denied")) {
                showError("Ошибка сохранения", "Нет доступа к файлу. Возможно, файл открыт в другой программе или нет прав на запись.");
            } else if (errorMessage != null && errorMessage.contains("No space")) {
                showError("Ошибка сохранения", "Недостаточно места на диске для сохранения файла.");
            } else {
                showError("Ошибка сохранения", "Не удалось сохранить файл: " + (errorMessage != null ? errorMessage : "Неизвестная ошибка"));
            }
        } catch (Exception exception) {
            showError("Ошибка сохранения", "Неожиданная ошибка при сохранении: " + exception.getMessage());
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
            Model originalModel = current.getModel();
            if (originalModel.vertices.isEmpty() || originalModel.polygons.isEmpty()) {
                showError("Ошибка сохранения", "Модель не содержит данных для сохранения.");
                return;
            }
            
            Model transformedModel = copyModel(originalModel);
            Matrix4f modelMatrix = ModelMatrixBuilder.build(current.getTransform());
            ModelTransformer.transformMatrix(transformedModel, modelMatrix);
            
            ObjWriter.write(transformedModel, file.getAbsolutePath());
            showSuccess("Модель сохранена", "Трансформированная модель успешно сохранена в файл:\n" + file.getName());
        } catch (IllegalArgumentException exception) {
            showError("Ошибка сохранения", "Ошибка при применении трансформаций: " + exception.getMessage());
        } catch (IOException exception) {
            String errorMessage = exception.getMessage();
            if (errorMessage != null && errorMessage.contains("Permission denied")) {
                showError("Ошибка сохранения", "Нет доступа к файлу. Возможно, файл открыт в другой программе или нет прав на запись.");
            } else if (errorMessage != null && errorMessage.contains("No space")) {
                showError("Ошибка сохранения", "Недостаточно места на диске для сохранения файла.");
            } else {
                showError("Ошибка сохранения", "Не удалось сохранить файл: " + (errorMessage != null ? errorMessage : "Неизвестная ошибка"));
            }
        } catch (Exception exception) {
            showError("Ошибка сохранения", "Неожиданная ошибка при сохранении: " + exception.getMessage());
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
            // Проверка на пустые поля
            if (scaleXField.getText().trim().isEmpty() || scaleYField.getText().trim().isEmpty() || scaleZField.getText().trim().isEmpty() ||
                rotateXField.getText().trim().isEmpty() || rotateYField.getText().trim().isEmpty() || rotateZField.getText().trim().isEmpty() ||
                translateXField.getText().trim().isEmpty() || translateYField.getText().trim().isEmpty() || translateZField.getText().trim().isEmpty()) {
                showError("Ошибка ввода", "Все поля должны быть заполнены. Пожалуйста, введите значения во все поля.");
                return;
            }
            
            float scaleX = Float.parseFloat(scaleXField.getText().trim());
            float scaleY = Float.parseFloat(scaleYField.getText().trim());
            float scaleZ = Float.parseFloat(scaleZField.getText().trim());
            
            // Проверка масштаба на валидность
            if (scaleX <= 0 || scaleY <= 0 || scaleZ <= 0) {
                showError("Ошибка ввода", "Масштаб должен быть больше нуля. Введенные значения: X=" + scaleX + ", Y=" + scaleY + ", Z=" + scaleZ);
                return;
            }
            
            float rotateX = Float.parseFloat(rotateXField.getText().trim());
            float rotateY = Float.parseFloat(rotateYField.getText().trim());
            float rotateZ = Float.parseFloat(rotateZField.getText().trim());
            
            float translateX = Float.parseFloat(translateXField.getText().trim());
            float translateY = Float.parseFloat(translateYField.getText().trim());
            float translateZ = Float.parseFloat(translateZField.getText().trim());

            // Проверка на NaN и Infinity
            if (Float.isNaN(scaleX) || Float.isNaN(scaleY) || Float.isNaN(scaleZ) ||
                Float.isNaN(rotateX) || Float.isNaN(rotateY) || Float.isNaN(rotateZ) ||
                Float.isNaN(translateX) || Float.isNaN(translateY) || Float.isNaN(translateZ)) {
                showError("Ошибка ввода", "Введены некорректные значения (NaN). Пожалуйста, введите числовые значения.");
                return;
            }
            
            if (Float.isInfinite(scaleX) || Float.isInfinite(scaleY) || Float.isInfinite(scaleZ) ||
                Float.isInfinite(rotateX) || Float.isInfinite(rotateY) || Float.isInfinite(rotateZ) ||
                Float.isInfinite(translateX) || Float.isInfinite(translateY) || Float.isInfinite(translateZ)) {
                showError("Ошибка ввода", "Введены некорректные значения (Infinity). Пожалуйста, введите конечные числовые значения.");
                return;
            }

            ModelTransform transform = current.getTransform();
            transform.setScale(new Vector3f(scaleX, scaleY, scaleZ));
            transform.setRotation(new Vector3f(rotateX, rotateY, rotateZ));
            transform.setPosition(new Vector3f(translateX, translateY, translateZ));
            updateTransformationFields();
            updateSceneInfo();
        } catch (NumberFormatException exception) {
            showError("Ошибка ввода", "Некорректный формат числа. Пожалуйста, введите числовые значения (например: 1.0, 0.5, -10).");
        } catch (Exception exception) {
            showError("Ошибка", "Неожиданная ошибка при применении трансформаций: " + exception.getMessage());
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
        if (source == null) {
            throw new IllegalArgumentException("Source model cannot be null");
        }
        
        Model copy = new Model();
        if (source.vertices != null) {
            for (Vector3f v : source.vertices) {
                if (v != null) {
                    copy.vertices.add(new Vector3f(v));
                }
            }
        }
        if (source.textureVertices != null) {
            for (com.cgvsu.math.Vector2f v : source.textureVertices) {
                if (v != null) {
                    copy.textureVertices.add(new com.cgvsu.math.Vector2f(v));
                }
            }
        }
        if (source.normals != null) {
            for (Vector3f v : source.normals) {
                if (v != null) {
                    copy.normals.add(new Vector3f(v));
                }
            }
        }
        if (source.polygons != null) {
            for (com.cgvsu.model.Polygon p : source.polygons) {
                if (p != null) {
                    com.cgvsu.model.Polygon polyCopy = new com.cgvsu.model.Polygon();
                    if (p.getVertexIndices() != null) {
                        polyCopy.setVertexIndices(new ArrayList<>(p.getVertexIndices()));
                    }
                    if (p.getTextureVertexIndices() != null) {
                        polyCopy.setTextureVertexIndices(new ArrayList<>(p.getTextureVertexIndices()));
                    }
                    if (p.getNormalIndices() != null) {
                        polyCopy.setNormalIndices(new ArrayList<>(p.getNormalIndices()));
                    }
                    copy.polygons.add(polyCopy);
                }
            }
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
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
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
    
    @FXML
    private void handleToggleTheme() {
        Scene scene = canvas.getScene();
        if (scene == null) {
            return;
        }
        
        // Удаляем текущую тему
        scene.getStylesheets().clear();
        
        // Применяем новую тему
        if (isDarkTheme) {
            scene.getStylesheets().add(getClass().getResource(LIGHT_THEME).toExternalForm());
            isDarkTheme = false;
        } else {
            scene.getStylesheets().add(getClass().getResource(DARK_THEME).toExternalForm());
            isDarkTheme = true;
        }
    }
    
    private void setupTheme() {
        Scene scene = canvas.getScene();
        if (scene != null && scene.getStylesheets().isEmpty()) {
            // Применяем светлую тему по умолчанию
            scene.getStylesheets().add(getClass().getResource(LIGHT_THEME).toExternalForm());
        }
    }
    
    @FXML
    private void handleDeleteSelectedVertices() {
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            showError("Нет модели", "Сначала выберите модель");
            return;
        }
        
        Model model = current.getModel();
        if (model.vertices.isEmpty()) {
            showError("Ошибка", "В модели нет вершин для удаления");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление вершин");
        dialog.setHeaderText("Введите индексы вершин для удаления");
        dialog.setContentText("Индексы (через запятую, например: 0,1,2):");
        
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                ArrayList<Integer> indices = parseIndices(result.get(), model.vertices.size());
                if (indices.isEmpty()) {
                    showError("Ошибка", "Не указаны индексы для удаления");
                    return;
                }
                
                // Подтверждение удаления
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Подтверждение удаления");
                confirmAlert.setHeaderText("Удаление вершин");
                confirmAlert.setContentText("Будет удалено " + indices.size() + " вершин(ы) и все связанные полигоны. Продолжить?");
                
                if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    ModelEditor.deleteVertices(model, indices);
                    
                    if (!ModelEditor.validateModel(model)) {
                        showError("Ошибка", "После удаления модель стала некорректной. Возможно, удалено слишком много вершин.");
                        return;
                    }
                    
                    updateSceneInfo();
                    showSuccess("Вершины удалены", "Удалено вершин: " + indices.size());
                }
            } catch (IllegalArgumentException exception) {
                showError("Ошибка ввода", exception.getMessage());
            } catch (Exception exception) {
                showError("Ошибка", "Не удалось удалить вершины: " + exception.getMessage());
            }
        }
    }
    
    @FXML
    private void handleDeleteSelectedPolygons() {
        SceneModel current = getSelectedSceneModel();
        if (current == null) {
            showError("Нет модели", "Сначала выберите модель");
            return;
        }
        
        Model model = current.getModel();
        if (model.polygons.isEmpty()) {
            showError("Ошибка", "В модели нет полигонов для удаления");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление полигонов");
        dialog.setHeaderText("Введите индексы полигонов для удаления");
        dialog.setContentText("Индексы (через запятую, например: 0,1,2):");
        
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                ArrayList<Integer> indices = parseIndices(result.get(), model.polygons.size());
                if (indices.isEmpty()) {
                    showError("Ошибка", "Не указаны индексы для удаления");
                    return;
                }
                
                // Подтверждение удаления
                Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
                confirmAlert.setTitle("Подтверждение удаления");
                confirmAlert.setHeaderText("Удаление полигонов");
                confirmAlert.setContentText("Будет удалено " + indices.size() + " полигон(ов). Продолжить?");
                
                if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    ModelEditor.deletePolygons(model, indices);
                    
                    if (!ModelEditor.validateModel(model)) {
                        showError("Ошибка", "После удаления модель стала некорректной.");
                        return;
                    }
                    
                    updateSceneInfo();
                    showSuccess("Полигоны удалены", "Удалено полигонов: " + indices.size());
                }
            } catch (IllegalArgumentException exception) {
                showError("Ошибка ввода", exception.getMessage());
            } catch (Exception exception) {
                showError("Ошибка", "Не удалось удалить полигоны: " + exception.getMessage());
            }
        }
    }
    
    /**
     * Парсит строку с индексами, разделенными запятыми.
     * 
     * @param input строка с индексами (например: "0,1,2" или "0, 1, 2")
     * @param maxIndex максимальный допустимый индекс
     * @return список индексов
     * @throws IllegalArgumentException если формат некорректен или индексы выходят за границы
     */
    private ArrayList<Integer> parseIndices(String input, int maxIndex) {
        ArrayList<Integer> indices = new ArrayList<>();
        
        if (input == null || input.trim().isEmpty()) {
            return indices;
        }
        
        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                int index = Integer.parseInt(part.trim());
                if (index < 0 || index >= maxIndex) {
                    throw new IllegalArgumentException("Индекс " + index + " выходит за границы (максимум: " + (maxIndex - 1) + ")");
                }
                if (!indices.contains(index)) {
                    indices.add(index);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректный формат индекса: " + part.trim());
            }
        }
        
        return indices;
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
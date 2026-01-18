package com.cgvsu.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Утилита для редактирования модели: удаление вершин и полигонов.
 * 
 * <p>Предоставляет методы для:
 * <ul>
 *   <li>Удаления вершин по индексам (с автоматическим удалением связанных полигонов)</li>
 *   <li>Удаления полигонов по индексам</li>
 *   <li>Валидации модели после редактирования</li>
 * </ul>
 * 
 * @author CGVSU Team
 * @version 1.0
 */
public class ModelEditor {
    
    private ModelEditor() {
    }
    
    /**
     * Удаляет вершины по указанным индексам.
     * 
     * <p>Автоматически удаляет все полигоны, которые ссылаются на удаляемые вершины.
     * После удаления обновляет индексы в оставшихся полигонах.
     * 
     * @param model модель для редактирования
     * @param vertexIndices индексы вершин для удаления (должны быть отсортированы по убыванию)
     * @throws IllegalArgumentException если model == null или индексы некорректны
     */
    public static void deleteVertices(Model model, ArrayList<Integer> vertexIndices) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (vertexIndices == null || vertexIndices.isEmpty()) {
            return;
        }
        
        // Сортируем индексы по убыванию для безопасного удаления
        ArrayList<Integer> sortedIndices = new ArrayList<>(vertexIndices);
        Collections.sort(sortedIndices, Collections.reverseOrder());
        
        // Проверяем валидность индексов
        for (Integer index : sortedIndices) {
            if (index < 0 || index >= model.vertices.size()) {
                throw new IllegalArgumentException("Invalid vertex index: " + index);
            }
        }
        
        // Находим все полигоны, которые ссылаются на удаляемые вершины
        Set<Integer> polygonsToDelete = new HashSet<>();
        for (int i = 0; i < model.polygons.size(); i++) {
            Polygon polygon = model.polygons.get(i);
            ArrayList<Integer> polygonVertexIndices = polygon.getVertexIndices();
            
            for (Integer vertexIndex : sortedIndices) {
                if (polygonVertexIndices.contains(vertexIndex)) {
                    polygonsToDelete.add(i);
                    break;
                }
            }
        }
        
        // Удаляем полигоны (сначала удаляем с большими индексами)
        ArrayList<Integer> polygonsToDeleteList = new ArrayList<>(polygonsToDelete);
        Collections.sort(polygonsToDeleteList, Collections.reverseOrder());
        for (Integer polygonIndex : polygonsToDeleteList) {
            model.polygons.remove(polygonIndex.intValue());
        }
        
        // Удаляем вершины (удаляем с конца, чтобы индексы не сдвигались)
        for (Integer index : sortedIndices) {
            model.vertices.remove(index.intValue());
        }
        
        // Обновляем индексы вершин в оставшихся полигонах
        updateVertexIndicesAfterDeletion(model, sortedIndices);
    }
    
    /**
     * Удаляет полигоны по указанным индексам.
     * 
     * @param model модель для редактирования
     * @param polygonIndices индексы полигонов для удаления (должны быть отсортированы по убыванию)
     * @throws IllegalArgumentException если model == null или индексы некорректны
     */
    public static void deletePolygons(Model model, ArrayList<Integer> polygonIndices) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (polygonIndices == null || polygonIndices.isEmpty()) {
            return;
        }
        
        // Сортируем индексы по убыванию для безопасного удаления
        ArrayList<Integer> sortedIndices = new ArrayList<>(polygonIndices);
        Collections.sort(sortedIndices, Collections.reverseOrder());
        
        // Проверяем валидность индексов
        for (Integer index : sortedIndices) {
            if (index < 0 || index >= model.polygons.size()) {
                throw new IllegalArgumentException("Invalid polygon index: " + index);
            }
        }
        
        // Удаляем полигоны (удаляем с конца, чтобы индексы не сдвигались)
        for (Integer index : sortedIndices) {
            model.polygons.remove(index.intValue());
        }
    }
    
    /**
     * Обновляет индексы вершин в полигонах после удаления вершин.
     * 
     * <p>Уменьшает индексы вершин в полигонах на количество удаленных вершин,
     * которые были до текущей вершины.
     * 
     * @param model модель для обновления
     * @param deletedIndices отсортированные по убыванию индексы удаленных вершин
     */
    private static void updateVertexIndicesAfterDeletion(Model model, ArrayList<Integer> deletedIndices) {
        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> updatedIndices = new ArrayList<>();
            
            for (Integer vertexIndex : vertexIndices) {
                int newIndex = vertexIndex;
                // Подсчитываем, сколько удаленных вершин было до текущей
                for (Integer deletedIndex : deletedIndices) {
                    if (deletedIndex < vertexIndex) {
                        newIndex--;
                    }
                }
                updatedIndices.add(newIndex);
            }
            
            polygon.setVertexIndices(updatedIndices);
        }
    }
    
    /**
     * Проверяет валидность модели после редактирования.
     * 
     * @param model модель для проверки
     * @return true если модель валидна, false если есть проблемы
     */
    public static boolean validateModel(Model model) {
        if (model == null) {
            return false;
        }
        
        if (model.vertices.isEmpty()) {
            return false;
        }
        
        if (model.polygons.isEmpty()) {
            return false;
        }
        
        // Проверяем, что все индексы вершин в полигонах валидны
        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices == null || vertexIndices.size() < 3) {
                return false;
            }
            
            for (Integer index : vertexIndices) {
                if (index < 0 || index >= model.vertices.size()) {
                    return false;
                }
            }
        }
        
        return true;
    }


}

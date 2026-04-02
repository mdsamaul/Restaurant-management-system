package com.rms.service;
import com.rms.dto.request.MenuCategoryRequest;
import com.rms.dto.request.MenuItemRequest;
import com.rms.dto.response.MenuCategoryResponse;
import com.rms.dto.response.MenuItemResponse;
import java.util.List;
public interface MenuService {
    MenuCategoryResponse createCategory(MenuCategoryRequest req);
    MenuCategoryResponse updateCategory(Long id, MenuCategoryRequest req);
    void deleteCategory(Long id);
    List<MenuCategoryResponse> getAllCategories();
    MenuItemResponse createItem(MenuItemRequest req);
    MenuItemResponse updateItem(Long id, MenuItemRequest req);
    void deleteItem(Long id);
    MenuItemResponse toggleAvailability(Long id);
    List<MenuItemResponse> getAllItems();
    List<MenuItemResponse> getItemsByCategory(Long categoryId);
    MenuItemResponse getItemById(Long id);
    List<MenuItemResponse> searchItems(String keyword);
}

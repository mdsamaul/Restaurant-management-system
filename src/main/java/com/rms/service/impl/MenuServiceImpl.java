package com.rms.service.impl;
import com.rms.dto.request.MenuCategoryRequest;
import com.rms.dto.request.MenuItemRequest;
import com.rms.dto.response.MenuCategoryResponse;
import com.rms.dto.response.MenuItemResponse;
import com.rms.entity.MenuCategory;
import com.rms.entity.MenuItem;
import com.rms.exception.BadRequestException;
import com.rms.exception.ResourceNotFoundException;
import com.rms.repository.MenuCategoryRepository;
import com.rms.repository.MenuItemRepository;
import com.rms.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class MenuServiceImpl implements MenuService {
    private final MenuCategoryRepository categoryRepo;
    private final MenuItemRepository itemRepo;

    @Override public MenuCategoryResponse createCategory(MenuCategoryRequest req) {
        if (categoryRepo.existsByName(req.getName()))
            throw new BadRequestException("Category already exists: " + req.getName());
        MenuCategory cat = MenuCategory.builder().name(req.getName())
            .description(req.getDescription()).sortOrder(req.getSortOrder()).build();
        return MenuCategoryResponse.from(categoryRepo.save(cat));
    }

    @Override public MenuCategoryResponse updateCategory(Long id, MenuCategoryRequest req) {
        MenuCategory cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        cat.setName(req.getName()); cat.setDescription(req.getDescription());
        if (req.getSortOrder() != null) cat.setSortOrder(req.getSortOrder());
        return MenuCategoryResponse.from(categoryRepo.save(cat));
    }

    @Override public void deleteCategory(Long id) {
        MenuCategory cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        cat.setIsActive(false); categoryRepo.save(cat);
    }

    @Override @Transactional(readOnly=true)
    public List<MenuCategoryResponse> getAllCategories() {
        return categoryRepo.findByIsActiveTrueOrderBySortOrderAsc().stream()
            .map(MenuCategoryResponse::from).collect(Collectors.toList());
    }

    @Override public MenuItemResponse createItem(MenuItemRequest req) {
        MenuCategory cat = categoryRepo.findById(req.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
        MenuItem item = MenuItem.builder().category(cat).name(req.getName())
            .description(req.getDescription()).price(req.getPrice())
            .imageUrl(req.getImageUrl()).isAvailable(req.getIsAvailable()).build();
        return MenuItemResponse.from(itemRepo.save(item));
    }

    @Override public MenuItemResponse updateItem(Long id, MenuItemRequest req) {
        MenuItem item = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));
        MenuCategory cat = categoryRepo.findById(req.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
        item.setCategory(cat); item.setName(req.getName());
        item.setDescription(req.getDescription()); item.setPrice(req.getPrice());
        item.setImageUrl(req.getImageUrl());
        if (req.getIsAvailable() != null) item.setIsAvailable(req.getIsAvailable());
        return MenuItemResponse.from(itemRepo.save(item));
    }

    @Override public void deleteItem(Long id) {
        MenuItem item = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));
        item.setIsAvailable(false); itemRepo.save(item);
    }

    @Override public MenuItemResponse toggleAvailability(Long id) {
        MenuItem item = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));
        item.setIsAvailable(!item.getIsAvailable());
        return MenuItemResponse.from(itemRepo.save(item));
    }

    @Override @Transactional(readOnly=true)
    public List<MenuItemResponse> getAllItems() {
        return itemRepo.findByIsAvailableTrue().stream()
            .map(MenuItemResponse::from).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly=true)
    public List<MenuItemResponse> getItemsByCategory(Long categoryId) {
        return itemRepo.findByCategoryIdAndIsAvailableTrue(categoryId).stream()
            .map(MenuItemResponse::from).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly=true)
    public MenuItemResponse getItemById(Long id) {
        return MenuItemResponse.from(itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id)));
    }

    @Override @Transactional(readOnly=true)
    public List<MenuItemResponse> searchItems(String keyword) {
        return itemRepo.searchByKeyword(keyword).stream()
            .map(MenuItemResponse::from).collect(Collectors.toList());
    }
}

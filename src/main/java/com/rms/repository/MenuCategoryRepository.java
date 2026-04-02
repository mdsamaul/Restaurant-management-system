package com.rms.repository;
import com.rms.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findByIsActiveTrueOrderBySortOrderAsc();
    boolean existsByName(String name);
}

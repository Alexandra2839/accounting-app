package com.cydeo.repository;

import com.cydeo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByIdAndIsDeleted(Long id, boolean deleted);

    @Query("select c from Category c where c.company.id= ?1 order by c.description")
    List<Category> getAllProductsByCompanySorted(Long id);

    Optional<Category> findCategoriesByDescriptionAndCompanyTitle(String description, String title);
}

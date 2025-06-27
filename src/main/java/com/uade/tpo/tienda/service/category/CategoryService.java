package com.uade.tpo.tienda.service.category;

import java.util.List;
import java.util.Optional;

import com.uade.tpo.tienda.dto.CategoryRequest;
import com.uade.tpo.tienda.entity.Categoria;

public interface CategoryService {
    Optional<Categoria> getCategoryById(Long id);
    public Categoria createCategory(Categoria category);
    List<Categoria> getCategory();
    public Optional<Categoria> updateCategory(Long id, CategoryRequest request);
}
package com.uade.tpo.tienda.service.category;

import java.util.Optional;
import com.uade.tpo.tienda.entity.Categoria;

public interface CategoryService {
    Optional<Categoria> getCategoryById(Long id);
    public Categoria createCategory(Categoria category) ;
}
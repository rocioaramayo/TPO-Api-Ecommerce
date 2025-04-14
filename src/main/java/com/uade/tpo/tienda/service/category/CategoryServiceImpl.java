package com.uade.tpo.tienda.service.category;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Categoria createCategory(Categoria category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public Optional<Categoria> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}

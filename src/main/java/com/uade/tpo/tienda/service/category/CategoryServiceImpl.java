package com.uade.tpo.tienda.service.category;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.tienda.entity.Categoria;
import com.uade.tpo.tienda.exceptions.CategoriaDuplicadaException;
import com.uade.tpo.tienda.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public Categoria createCategory(Categoria category) {
        if (!categoryRepository.findByNombre(category.getNombre()).isEmpty()) {
            throw new CategoriaDuplicadaException(); // lanza excepción si el nombre está duplicado
        }
        return categoryRepository.save(category); // guarda si está todo ok
    }

    @Override
    public Optional<Categoria> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Categoria> getCategory() {
        return categoryRepository.findAll();
    }
}
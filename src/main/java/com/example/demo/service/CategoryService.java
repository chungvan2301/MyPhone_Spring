package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {
    @Autowired
    CategoryRepo categoryRepo;
    public void addCategory (Category category) {
        categoryRepo.save(category);
    }

    public List <Category> getAllCategories () {
        return categoryRepo.findAll();
    }

    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    public Category getCategory(Long id){
        return categoryRepo.findById(id).orElse(null);
    }

    public void addCategory(String name, String image) {
        Category category = new Category();
        category.setName(name);
        category.setImage(image);
        categoryRepo.save(category);
    }
    public Category getOneCategory(Long id) {
        return categoryRepo.findById(id).orElse(null);
    }
}

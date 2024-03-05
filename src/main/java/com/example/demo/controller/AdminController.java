package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UploadImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UploadImageService uploadImageService;
    @GetMapping("/admin")
    public String getAdminDashboard(){
        return "adminDashboard";
    }
    @GetMapping("/admin/category")
    public String getAdminCategory(Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", new Category());
        return "category";
    }
    @PostMapping("/admin/category/add")
    public String addAdminCategory(@RequestParam String name, @RequestParam MultipartFile image) {
        String imageUrl = uploadImageService.uploadImage(image);
        categoryService.addCategory(name,imageUrl);
        return "redirect:/admin/category";
    }
    @PostMapping("/admin/category/delete")
    public String deleteAdminCategory(@RequestParam Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }

    @GetMapping("/admin/product")
    public String getAdminProduct(Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productService.getAllProduct());
        return "product";
    }
    @GetMapping("/admin/product/add")
    public String getAdminProductAdd(Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productDTO", new ProductDTO());
        return "product-add";
    }
    @PostMapping("/admin/product/add")
    public String addNewProduct(@ModelAttribute("productDTO") ProductDTO productDTO, @RequestParam MultipartFile productImage){
        String image = uploadImageService.uploadImage(productImage);
        productService.addProduct(productDTO,image);
        return "redirect:/admin/product";
    }
    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable(required = true) Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/product";
    }
    @GetMapping("/admin/product/edit/{id}")
    public String editProduct(@PathVariable(required = true) Long id, Model model) {
        ProductDTO productDTO = new ProductDTO();
        Product product = productService.getProduct(id);
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setBranch(product.getBranch());
        productDTO.setColor(product.getColor());
        productDTO.setType(product.getType());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());
        productDTO.setQuantityAdd(product.getQuantityAdd());

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productDTO",productDTO);

        return "product-add";
    }
}

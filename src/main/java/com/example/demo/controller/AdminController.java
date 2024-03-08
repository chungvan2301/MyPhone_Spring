package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Receipt;
import com.example.demo.model.User;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    CartService cartService;
    @Autowired
    ReceiptService receiptService;
    @Autowired
    UploadImageService uploadImageService;
    @GetMapping("/admin")
    public String getAdminDashboard(Model model){
        model.addAttribute("user", userService.getUser());
        return "adminDashboard";
    }
    @GetMapping("/admin/category")
    public String getAdminCategory(Model model){
        model.addAttribute("user", userService.getUser());
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
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productService.getAllProduct());
        return "product";
    }
    @GetMapping("/admin/product/add")
    public String getAdminProductAdd(Model model){
        model.addAttribute("user", userService.getUser());
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

        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productDTO",productDTO);

        return "product-add";
    }

    @GetMapping("/admin/receipt")
    public String getAllReceiptOfUserPage (Model model) {
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("user", userService.getUser());
        model.addAttribute("receipts", receiptService.getAllReceipt());
        return "receipt-admin";
    }
    @GetMapping("/admin/receipt/{receiptCode}")
    public String getOneReceipt (Model model, @PathVariable String receiptCode) {
        User user = userService.getUser();

        Receipt receipt = receiptService.getOneReceiptOfUserAdmin(receiptCode);
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("user", user);
        model.addAttribute("receipt", receipt);
        if (receipt==null) return "pageNotFound";
        else {
            model.addAttribute("addressDefault", receiptService.getDefaultAddressInReceiptAdmin(receiptCode));
            return "receipt-admin-view-detail";
        }
    }

    //Thống kê
    @GetMapping("/admin/analytic")
    public String getAnalytic (Model model) {
        User user = userService.getUser();
        model.addAttribute("revenueData", receiptService.getRevenueDataForLast7Days());
        model.addAttribute("totalProfit", receiptService.totalProfit());
        model.addAttribute("revenueDataOrder", receiptService.getRevenueDataForLast7DaysOrders());
        model.addAttribute("totalOrder", receiptService.totalOrder());
        model.addAttribute("user", user);
        return "analytic";
    }

    @GetMapping("/admin/analytic/date")
    public String getAnalyticDate (Model model, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User user = userService.getUser();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        model.addAttribute("revenueData", receiptService.getRevenueDataForDays(startDateTime,endDateTime));
        model.addAttribute("totalProfit", receiptService.totalProfit());
        model.addAttribute("revenueDataOrder", receiptService.getRevenueDataForOrders(startDateTime,endDateTime));
        model.addAttribute("totalOrder", receiptService.totalOrder());
        model.addAttribute("user", user);
        return "analytic";
    }
}

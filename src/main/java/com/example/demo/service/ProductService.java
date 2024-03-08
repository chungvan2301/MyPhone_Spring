package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.model.ProductRating;
import com.example.demo.repository.CategoryRepo;
import com.example.demo.repository.ProductRatingRepo;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Set<String> productAllStatuses = new HashSet<>(Arrays.asList("new",""));
    private static final Set<String> productStatusNew = new HashSet<>(Arrays.asList("new"));

    @Autowired
    UserRepo userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    ProductRatingRepo productRatingRepo;

    public List<Product> getAllProduct () {
        return productRepo.findAll();
    }
    public List<Product> getProductByCategory (Long id) {
        return productRepo.findByCategoryId(id);
    }
    public void addProduct (ProductDTO productDTO, String image) {
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setCategory(categoryRepo.findById(productDTO.getCategoryId()).orElse(null));
        product.setBranch(productDTO.getBranch());
        if (productDTO.getType().isEmpty()) {product.setType(null);}
            else {product.setType(productDTO.getType());}
        if (productDTO.getColor().isEmpty()) {product.setColor(null);}
            else {product.setColor(productDTO.getColor());}
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setAddDate(LocalDateTime.now());
        product.setImageName(image);
        product.setProductStatus("new");
        product.setQuantityAdd(productDTO.getQuantityAdd());
        product.setQuantityInStore(productDTO.getQuantityAdd());
        product.setQuantitySold(0);
        productRepo.save(product);
    }
    public void deleteProduct (Long id) {
        productRepo.deleteById(id);
    }
    public Product getProduct (Long id) {
        return productRepo.findById(id).orElse(null);
    }
    public List<Product> getNewProduct() {
        return productRepo.findDistinctTop6ByProductStatusOrderByAddDateDesc("new");
    }
    public List<Product> getBestSaleProduct() {
        return productRepo.findDistinctTop6ByQuantitySoldGreaterThanEqualOrderByAddDateDesc(200);
    }
    public Product getOneProduct(Long id) {
        return productRepo.findById(id).orElse(null);
    }
    public List<String> getProductByColor(Long id) {
        String name = productRepo.findById(id).orElse(null).getName();;
        List<String> colors = productRepo.findDistinctColorByName(name);
        if (colors.get(0) == null) {
            System.out.println(colors.size());
            return Collections.emptyList();
        } else return colors;
    }
    public List<String> getProductByType(Long id) {
        String name = productRepo.findById(id).orElse(null).getName();
        List<String> types = productRepo.findDistinctTypeByName(name);
        if (types.get(0) == null) {
            return Collections.emptyList();
        } else return types;
    }
    public Product getProductByTypeAndColor(Long id, String type, String color) {
        String name = productRepo.findById(id).orElse(null).getName();
        return productRepo.findByNameAndTypeAndColor(name,type,color);
    }

    public List<Product> getProductByCategory(Long id, String branch, int statusProduct, int minPrice, int maxPrice) {
        Set<String> productBranches = productRepo.findDistinctBranchByCategoryId(id);
        if (!branch.equals("none")) {productBranches = new HashSet<>(Arrays.asList(branch));};
        System.out.println(productRepo.findDistinctNameByCategoryIdAndBranchInAndPriceBetweenAndProductStatusIn(id,productBranches,minPrice,maxPrice,statusProduct==1 ? productStatusNew : productAllStatuses).size());
        return productRepo.findDistinctNameByCategoryIdAndBranchInAndPriceBetweenAndProductStatusIn(id, productBranches,minPrice,maxPrice,statusProduct==1 ? productStatusNew : productAllStatuses);
    }
    public List<Product> getProductByCategoryPagination(Long id, String branch, int statusProduct, int minPrice, int maxPrice, Pageable pageable) {
        Set<String> productBranches = productRepo.findDistinctBranchByCategoryId(id);
        if (!branch.equals("none")) {productBranches = new HashSet<>(Arrays.asList(branch));};
        return productRepo.findDistinctNameByCategoryIdAndBranchInAndPriceBetweenAndProductStatusIn(id,productBranches,minPrice,maxPrice,statusProduct==1 ? productStatusNew : productAllStatuses,pageable);
    }

    public List<Product> getProductByCategoryArrangement(Long id, String branch, int page, int size, int sortField, int sortDirection,
                                                         int statusProduct,
                                                         int minPrice,
                                                         int maxPrice){
        List<Product> products = null;
        Sort sort = Sort.unsorted();
        switch (sortField) {
            case 1:
                sort = Sort.by(Sort.Direction.DESC, "quantity_sold");
                break;
            case 2:
                sort = Sort.by(sortDirection == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, "price");
                break;
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        products = this.getProductByCategoryPagination(id,branch,statusProduct,minPrice,maxPrice,pageable);
        return products;
    }

    public Set<String> getBranchFromCategory (Long categoryId) {
        return productRepo.findDistinctBranchByCategoryId(categoryId);
    }

    public static String sapXep(int sortField, int sortDirection) {
        String sapxep = new String();
        switch (sortField) {
            case 0:
                sapxep="Xếp theo: Mặc định";
                break;
            case 1	:
                sapxep="Xếp theo: Nổi bật";
                break;
            case 2:
                if (sortDirection==0) {
                    sapxep="Xếp theo: Giá cao đến thấp";
                } else {
                    sapxep="Xếp theo: Giá thấp đến cao";
                };
                break;
        }
        return sapxep;
    }

    //Rating**********
    public void saveRatingProduct(Long productId, Long userId, int rating, String comment) {
        if (productRatingRepo.findByProductIdAndUserId(productId,userId)==null) {
            ProductRating productRating = new ProductRating();
            productRating.setProductId(productId);
            productRating.setUser(userRepo.findById(userId).orElse(null));
            productRating.setRating(rating);
            productRating.setComment(comment);
            productRating.setReportVotes(0);
            productRating.setDateTime(LocalDateTime.now());
            productRatingRepo.save(productRating);
        } else {
            ProductRating productRating = productRatingRepo.findByProductIdAndUserId(productId,userId);
            productRating.setProductId(productId);
            productRating.setUser(userRepo.findById(userId).orElse(null));
            productRating.setRating(rating);
            productRating.setComment(comment);
            productRating.setDateTime(LocalDateTime.now());
            productRatingRepo.save(productRating);
        }
        this.saveAvgProductRating(productId);
    }
    public void saveAvgProductRating(Long productId) {
        Product product = productRepo.findById(productId).orElse(null);
        List<ProductRating> allProductRating = productRatingRepo.findAllByProductIdOrderByDateTimeDesc(productId);
        if (allProductRating.isEmpty()) {
            product.setAvgRating(0);
            productRepo.save(product);
        }
        else {
            int sumRating = allProductRating.stream().collect(Collectors.summingInt(ProductRating::getRating));
            int countUserRating = allProductRating.size();
            double avgRating = (double) sumRating/countUserRating;
            product.setAvgRating(avgRating);
            productRepo.save(product);
        }
    }
    public void removeUserRating (Long productId, Long userId) {
        //productRatingRepo.deleteByProductIdAndUserId(productId,userId);
        productRatingRepo.deleteById(productRatingRepo.findByProductIdAndUserId(productId,userId).getId());
        this.saveAvgProductRating(productId);
    }
    public List<ProductRating> getListRating(Long productid) {
        return productRatingRepo.findAllByProductIdOrderByDateTimeDesc(productid);
    }
    public void reportRating (Long id) {
        ProductRating productRating = productRatingRepo.findById(id).orElse(null);
        int Votes = productRating.getReportVotes();
        Votes++;
        productRating.setReportVotes(Votes);
        productRatingRepo.save(productRating);
    }

    //Search basic
    public List<Product> getProductBySearch(String keyword) {
        List<Product> products = productRepo.findDistinctProductsByNameContainingIgnoreCase(keyword);
        return products;
    }
    public List<Product> getProductBySearchAndCategory(String keyword, Long id) {
        List<Product> products = productRepo.findDistinctProductsByNameContainingIgnoreCaseAndCategory_Id(keyword, id);
        return products;
    }


}

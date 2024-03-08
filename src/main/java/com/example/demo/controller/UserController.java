package com.example.demo.controller;

import com.example.demo.configuration.JwtTokenProvider;
import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.model.*;
import com.example.demo.repository.CustomUserDetails;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.*;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserController {
    private static int countProducts = 0;
    public static int getCount() {
        return countProducts;
    }
    private final static String pageSize = "12";
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    CartService cartService;
    @Autowired
    ReceiptService receiptService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    //Home page
    @GetMapping("/")
    public String getHomePage(Model model){
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productsOutstanding", productService.getNewProduct());
        model.addAttribute("productsBestSale", productService.getBestSaleProduct());
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        return "home";
    }
    @GetMapping("/contact")
    public String getContactPage(Model model){
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "contact";
    }
    @PostMapping("/contact")
    public String sendUserMessage (@RequestParam String email, @RequestParam String message, RedirectAttributes redirectAttributes) {
        userService.sendEmailToShop(email,message);
        redirectAttributes.addFlashAttribute("success","success");
        return "redirect:/contact";
    }
    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }
    @PostMapping("/register")
    public String addNewUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes){
        if (userService.addNewUser(user)) {
            redirectAttributes.addFlashAttribute("message", "successRegister");
            return "redirect:/login";
        }
        else {
            redirectAttributes.addFlashAttribute("message", "failRegister");
            return "redirect:/register";
        }
    }

    //Product page
    @GetMapping("/shop/view-product/{id}")
    public String getOneProduct(Model model, @PathVariable Long id) {
        List<ProductRating> listRating = productService.getListRating(id);
        model.addAttribute("listRating", listRating);
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("product", productService.getOneProduct(id));
        model.addAttribute("productColor", productService.getProductByColor(id));
        model.addAttribute("productType", productService.getProductByType(id));
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }

        return "view-product";
    }
    @PostMapping("/shop/view-product")
    public String redirectToViewProduct(@RequestParam String id, @RequestParam(required = false) String type, @RequestParam(required = false) String color) {
        if(type.isEmpty()) type=null;
        if(color.isEmpty()) color=null;
        Long longId = Long.valueOf(id);
        Long newId = productService.getProductByTypeAndColor(longId,type,color).getId();
        return "redirect:/shop/view-product/" + newId;
    }
    @GetMapping("/shop/category/{id}")
    public String shopPage(Model model,
                           @PathVariable Long id,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "none") String branch,
                           @RequestParam(defaultValue = pageSize) int size,
                           @RequestParam(defaultValue = "0") int sortField,
                           @RequestParam(defaultValue = "0") int sortDirection,
                           @RequestParam(defaultValue = "0") int statusProduct,
                           @RequestParam(defaultValue = "0") int minPrice,
                           @RequestParam(defaultValue = "100000000") int maxPrice
    ) {
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productService.getProductByCategoryArrangement(id,branch,page,size,sortField,sortDirection,statusProduct,minPrice,maxPrice));
        model.addAttribute("productsSize", productService.getProductByCategory(id,branch,statusProduct,minPrice,maxPrice).size());
        model.addAttribute("sapxep", productService.sapXep(sortField,sortDirection));
        model.addAttribute("categoryDef", categoryService.getOneCategory(id));
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("branch", branch);
        model.addAttribute("statusProduct", statusProduct);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("branches", productService.getBranchFromCategory(id));
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }

        return "shop";
    }
    @GetMapping("/shop/category/products/{id}")
    public String shopPagePagination(Model model,
                           @PathVariable Long id,
                           @RequestParam int page,
                           @RequestParam int size,
                           @RequestParam int sortField,
                           @RequestParam int sortDirection,
                           @RequestParam(defaultValue = "none") String branch,
                           @RequestParam(defaultValue = "0") int statusProduct,
                           @RequestParam(defaultValue = "0") int minPrice,
                           @RequestParam(defaultValue = "100000000") int maxPrice
    ) {
        model.addAttribute("user", userService.getUser());
        List<Product> products = productService.getProductByCategoryArrangement(id,branch,page,size,sortField,sortDirection,statusProduct,minPrice,maxPrice);
        model.addAttribute("products", products);
        countProducts+=products.size();
        if(countProducts+Integer.valueOf(pageSize)==productService.getProductByCategory(id,branch,statusProduct,minPrice,maxPrice).size()) {
            countProducts=0;
            model.addAttribute("coutProducts", countProducts);
        }
        return "shop-pagination";
    }

    //Rating
    @PostMapping("/shop/view-product/rating/add")
    public String userRatingProduct(@RequestParam Long productId,
                                    @RequestParam Long userId,
                                    @RequestParam int rating,
                                    @RequestParam String comment) {
        productService.saveRatingProduct(productId,userId,rating,comment);
        return "redirect:/shop/view-product/" + productId;
    }

    @PostMapping("shop/view-product/rating/delete")
    public String deleteUserRatingProduct(@RequestParam Long productId,
                                          @RequestParam Long userId) {
        productService.removeUserRating(productId,userId);
        return "redirect:/shop/view-product/" + productId;
    }

    @PostMapping("/shop/view-product/rating/report")
    public String reportRating(@RequestParam Long id,
                               @RequestParam Long productId,
                               RedirectAttributes redirectAttributes) {
        productService.reportRating(id);
        redirectAttributes.addFlashAttribute("messageReport", "messageReport");
        return "redirect:/shop/view-product/" + productId;
    }
    //Giỏ hàng **************************************
    @GetMapping("/cart")
    public String viewCart(HttpServletRequest request, Model model) {
        User user = userService.getUser();
        cartService.updateCartSold(user.getId());
        model.addAttribute("carts", cartService.viewCart(user.getId()));
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sum", cartService.sumPriceProductsInCart(user.getId()));
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam int quantity, Model model, RedirectAttributes redirectAttributes) {
        cartService.addProductToCart(userId,productId,quantity);
        User userNew = userRepo.findById(userId).orElse(null);
        redirectAttributes.addFlashAttribute("message", "success");
        model.addAttribute("user", "userNew");
        return "redirect:/shop/view-product/" + productId;
    }

    @PostMapping("/cart/update/{cartId}")
    public String updateCartQuantity(@PathVariable Long cartId, @RequestParam int quantity, Model model) {
        Long idUser = cartService.findCartById(cartId).getUser().getId();
        Cart cart = cartService.findCartById(cartId);
        if (cart != null) {
            cart.setQuantity(quantity);
            cartService.saveCart(cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/delete/{id}")
    public String deleteProductInCart(@PathVariable Long id) {
        Long idUser = cartService.findCartById(id).getUser().getId();
        cartService.deleteProductInCart(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/sold/{cartId}")
    public ResponseEntity<?> updateSelectSoldCart(@PathVariable Long cartId) {
        cartService.updateSoldCart(cartId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart updated to sold");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/cart/not-sold/{cartId}")
    public ResponseEntity<?> updateSelectNotSoldCart(@PathVariable Long cartId) {
        cartService.updateNotSoldCart(cartId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart updated to not sold");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/cart/update-sold-cart/{userId}")
    public String updateCartSold(@PathVariable Long userId) {
        cartService.updateCartSold(userId);
        return "redirect:/cart";
    }

    //Search
    @GetMapping("/shop/search")
    public String viewSearchPage(Model model, @RequestParam String keyword, @RequestParam Long categoryId) {

        if (categoryId==0) {
            model.addAttribute("products", productService.getProductBySearch(keyword));
        } else {
            model.addAttribute("products", productService.getProductBySearchAndCategory(keyword,categoryId));
            model.addAttribute("categoryDef", categoryService.getCategory(categoryId));
        }
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        return "search";
    }

    //User infor ****************************************
    @GetMapping("/user")
    public String userPage(Model model) {
        model.addAttribute("user", userService.getUser());
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        return "user";
    }
    @PostMapping("/user/update")
    public String userUpdate(@RequestParam Long userId, @RequestParam String firstName, @RequestParam String lastName, RedirectAttributes redirectAttributes) {
        User user = userRepo.findById(userId).orElse(null);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("messageInfor","successInforChange");
        return "redirect:/user";
    }
    @GetMapping("/user/user-change-password")
    public String passwordChange(Model model) {
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("user", userService.getUser());
        return "user-change-password";
    }

    @PostMapping("/user/password/update")
    public String passwordSubmit(@RequestParam String passwordOld, @RequestParam String passwordFinal, @RequestParam Long userId, RedirectAttributes redirectAttributes) {
        User user = userRepo.findById(userId).orElse(null);
        if (!passwordEncoder.matches(passwordOld,user.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "fail");
            return "redirect:/user/user-change-password";
        } else {
            user.setPassword(passwordEncoder.encode(passwordFinal));
            userRepo.save(user);
            redirectAttributes.addFlashAttribute("messagePass", "successPassChange");
            return "redirect:/user";
        }
    }

    //Address
    @GetMapping("/thanhtoan/diachi")
    public String getAddressPage(HttpServletRequest request, Model model) {
        User user = userService.getUser();
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("carts", cartService.viewCart(user.getId()));
        model.addAttribute("sum", cartService.sumPriceProductsInCart(user.getId()));
        model.addAttribute("addressList", receiptService.getUserAddresses(user.getId()));
        model.addAttribute("addressListName", receiptService.getUserAddressesWithName(user.getId()));
        model.addAttribute("addressDefault", receiptService.getDefaultAddress(user.getId()));
        return "address";
    }

    @PostMapping("/thanhtoan/diachi/addAddress")
    public String addUserAddress(@RequestParam Long userId,
                                 @RequestParam String nameReceiver,
                                 @RequestParam String province,
                                 @RequestParam String district,
                                 @RequestParam String ward,
                                 @RequestParam String streetAndDepartment,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String type
    ) {
        receiptService.addUserAddress(userId,nameReceiver,province,district,ward,streetAndDepartment,phoneNumber,type);
        return "redirect:/thanhtoan/diachi";
    }

    @PostMapping("/thanhtoan/diachi/editAddress")
    public String editUserAddress(@RequestParam Long addressId,
                                  @RequestParam String nameReceiver,
                                  @RequestParam String province,
                                  @RequestParam String district,
                                  @RequestParam String ward,
                                  @RequestParam String streetAndDepartment,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String type
    ) {
        receiptService.editUserAddress(addressId,nameReceiver,province,district,ward,streetAndDepartment,phoneNumber,type);
        return "redirect:/thanhtoan/diachi";
    }

    @PostMapping("/thanhtoan/diachi/deleteAddress")
    public String deleteUserAddress(@RequestParam Long addressId,
                                    @RequestParam Long userId) {
        receiptService.deleteUserAddress(addressId,userId);
        return "redirect:/thanhtoan/diachi";
    }

    @PostMapping("/thanhtoan/diachi/setUserAddressDefault")
    public String setUserAddressDefault(@RequestParam Long addressId,
                                        @RequestParam Long userId) {
        receiptService.setUserAddressDefault(userId,addressId);
        return "redirect:/thanhtoan";
    }
    @GetMapping("/thanhtoan")
    public String getReceiptPage(Model model) {
        User user = userService.getUser();
        List <Cart> carts = receiptService.limitProductNames(cartService.viewCartSold(user.getId()));
        Address addressDefault = receiptService.getDefaultAddress(user.getId());
        Long addressDefaultId = (addressDefault==null) ? 0 : addressDefault.getId();
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("carts", carts);
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sum", cartService.sumPriceProductsInCart(user.getId()));
        model.addAttribute("addressList", receiptService.getUserAddresses(user.getId()));
        model.addAttribute("addressDefault", addressDefault);
        model.addAttribute("shippingFee", receiptService.getShippingFee(user.getId()));
        model.addAttribute("dayShipping", receiptService.getDayShipping());
        model.addAttribute("receiptCode", receiptService.generateRandomReceiptCode());
        model.addAttribute("orderRequestDTO", new OrderRequestDTO());
        receiptService.getReceiptOfUser(user.getId());
        return "checkout";
    }
    @PostMapping("/thanhtoan/setShipping")
    public String setShpipingFee (Long userId, int option, RedirectAttributes redirectAttributes) {
        double shippingFeeResult = receiptService.addShippingFee(userId,option);
        List<String> shippingDayResult = receiptService.addDayShipping(option);
        redirectAttributes.addFlashAttribute("shippingFeeResult", shippingFeeResult);
        redirectAttributes.addFlashAttribute("shippingDayResult", shippingDayResult);
        redirectAttributes.addFlashAttribute("optionResult", option);
        return "redirect:/thanhtoan";
    }

    @PostMapping("/thanhtoan/receipt")
    public String addReceipt (@RequestParam List<Long> cartIds,
                              @RequestParam Long userId,
                              @RequestParam Long addressDefaultId,
                              @RequestParam double goodsFee,
                              @RequestParam double transportFee,
                              @RequestParam double totalPrice,
                              @RequestParam String receiptCode,
                              @RequestParam String paymentMethod,
                              @RequestParam String dayReceived,
                              RedirectAttributes redirectAttributes) {
        receiptService.addReceipt(cartIds,userId,addressDefaultId,goodsFee,transportFee,totalPrice,receiptCode,paymentMethod,dayReceived);
        redirectAttributes.addFlashAttribute("sucessfullPaid", "sucessfullPaid");
        return "redirect:/receipt";
    }
    @GetMapping("/receipt")
    public String getAllReceiptOfUserPage (Model model) {
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("user", userService.getUser());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("receipts", receiptService.getReceiptOfUser(userService.getUser().getId()));
        return "receipt";
    }

    @GetMapping("/receipt/{index}")
    public String getOneReceipt (HttpServletRequest request, Model model, @PathVariable int index) {
        User user = userService.getUser();

        Receipt receipt = receiptService.getOneReceiptOfUser(user.getId(),index);
        if (userService.getUser()!=null) {
            model.addAttribute("cartSize", cartService.getCartSize(userService.getUser().getId()));
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("user", user);
        model.addAttribute("receipt", receipt);
        model.addAttribute("index", index);
        if (receipt==null) return "pageNotFound";
        else {
            model.addAttribute("addressDefault", receiptService.getDefaultAddressInReceipt(user.getId(),index));
            return "receipt-view-detail";
        }
    }
    @PostMapping("/receipt/set-status")
    public String setReceiptStatus(@RequestParam Long id, @RequestParam int index) {
        receiptService.setStatusReceipt(id);
        return "redirect:/receipt/" + index;
    }
}


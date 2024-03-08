package com.example.demo.service;

import com.example.demo.dto.AddressDTO;
import com.example.demo.model.Address;
import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.Receipt;
import com.example.demo.repository.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReceiptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptService.class);
    private static final String JSON_FILE_PATH = "static/json/address.json";
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CartRepo cartRepo;
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    ReceiptRepo receiptRepo;

    public Address getDefaultAddress (Long userId) {
        Address address = addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null);
        if (address!=null) {
            String province = this.getNameById(address.getProvince(),"province");
            String district = this.getNameById(address.getDistrict(),"district");
            String ward = this.getNameById(address.getWard(),"commune");
            address.setProvince(province);
            address.setDistrict(district);
            address.setWard(ward);
        }
        return address;
    }

    public List<Address> getUserAddresses (Long userId) {
        this.updateUserAddressDefault(userId);
        List <Address> userAddress = addressRepo.findByUserIdOrderByAddressDefaultDesc(userId) ;
        return userAddress;
    }

    public List<AddressDTO> getUserAddressesWithName (Long userId) {
        List <Address> userAddress = this.getUserAddresses(userId) ;
        List <AddressDTO> userAddressName = new ArrayList<>();
        for (Address address : userAddress) {
            String province = this.getNameById(address.getProvince(),"province");
            String district = this.getNameById(address.getDistrict(),"district");
            String ward = this.getNameById(address.getWard(),"commune");

            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(address.getId());
            addressDTO.setUserId(userId);
            addressDTO.setNameReceiver(address.getNameReceiver());
            addressDTO.setProvince(province);
            addressDTO.setDistrict(district);
            addressDTO.setWard(ward);
            addressDTO.setStreetAndDepartment(address.getStreetAndDepartment());
            addressDTO.setAddressDefault(address.getAddressDefault());
            addressDTO.setPhoneNumber(address.getPhoneNumber());
            addressDTO.setType(address.getType());
            addressDTO.setProvinceId(address.getProvince());
            addressDTO.setDistrictId(address.getDistrict());
            addressDTO.setWardId(address.getWard());

            userAddressName.add(addressDTO);
        }

        return userAddressName;
    }
    public void setUserAddressDefault (Long userId, Long addressId) {
        Address address = addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null);
        if (address!=null) {
            address.setAddressDefault(false);
            addressRepo.save(address);
            address = addressRepo.findById(addressId).orElse(null);
            address.setAddressDefault(true);
            addressRepo.save(address);
        } else {
            System.out.println("Lỗi ở dòng này");
        }
    }
    public void addUserAddress (Long userId, String nameReceiver, String province, String district, String ward, String streetAndDepartment, String phoneNumber, String type) {
        Address address = new Address();
        List<Address> addresses = addressRepo.findByUserIdOrderByAddressDefaultDesc(userId);
        if (addresses.isEmpty()) {
            address.setAddressDefault(true);
        } else {
            address.setAddressDefault(false);
        }
        address.setUser(userRepo.findById(userId).orElse(null));
        address.setNameReceiver(nameReceiver);
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);
        address.setStreetAndDepartment(streetAndDepartment);
        address.setPhoneNumber(phoneNumber);
        address.setType(type);
        addressRepo.save(address);
        System.out.println(address.getId());
        this.setUserAddressDefault(userId, address.getId());
    }

    public void editUserAddress (Long addressId, String nameReceiver, String province, String district, String ward, String streetAndDepartment, String phoneNumber, String type) {
        Address address = addressRepo.findById(addressId).orElse(null);
        address.setNameReceiver(nameReceiver);
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);
        address.setStreetAndDepartment(streetAndDepartment);
        address.setPhoneNumber(phoneNumber);
        address.setType(type);
        addressRepo.save(address);
    }
    public String getNameById(String id, String type) {
        try {
            ClassPathResource resource = new ClassPathResource(JSON_FILE_PATH);
            InputStream inputStream = resource.getInputStream();

            JsonFactory factory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonParser parser = factory.createParser(inputStream);

            // Tìm tên tương ứng dựa trên ID
            while (!parser.isClosed()) {
                JsonNode node = objectMapper.readTree(parser);
                JsonNode listNode = node.get(type.toLowerCase());

                if (listNode != null && listNode.isArray()) {
                    Iterator<JsonNode> elements = listNode.elements();
                    while (elements.hasNext()) {
                        JsonNode item = elements.next();
                        if (id.equals(item.get("id" + type.substring(0, 1).toUpperCase() + type.substring(1)).asText())) {
                            return item.get("name").asText();
                        }
                    }
                }
                // Di chuyển tới phần tiếp theo trong file
                parser.nextToken();
            }
            inputStream.close(); // Đóng luồng sau khi sử dụng
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ, ví dụ: log hoặc thông báo lỗi
        }
        return null;
    }
    public void updateUserAddressDefault (Long userId) {
        if (addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null) == null) {
            List<Address> addresses = addressRepo.findByUserIdOrderByAddressDefaultDesc(userId);
            if (!addresses.isEmpty()) {
                Address addressModify = addresses.get(0);
                addressModify.setAddressDefault(true);
                addressRepo.save(addressModify);
            }
        }
    }
    public void deleteUserAddress (Long id, Long userId) {
        addressRepo.deleteById(id);
    }
    public String generateRandomReceiptCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = Integer.toString(randomNumber);
        Receipt receipt = receiptRepo.findByReceiptCode(code).orElse(null);
        while (receipt!=null) {
            randomNumber = random.nextInt(900000) + 100000;
            code = Integer.toString(randomNumber);
            receipt = receiptRepo.findByReceiptCode(code).orElse(null);
        }
        return code;
    }
    public List<Cart> limitProductNames(List<Cart> carts) {
        for (Cart cart : carts) {
            if (cart.getProduct() != null) {
                String originalName = cart.getProduct().getName();
                String limitedName = limitWords(originalName, 15);
                cart.getProduct().setName(limitedName);
            }
        }
        return carts;
    }
    public List<Double> getShippingFee (Long userId) {
        Address addressUserDefault = addressRepo.findByUserIdAndAddressDefault(userId, true).orElse(null);
        List<Double> result = new ArrayList<>();
        if (addressUserDefault!=null && addressUserDefault.getProvince().equals("Thành phố Hồ Chí Minh")) {
            result.add(12000.0);
            result.add(18000.0);
        } else if (addressUserDefault!=null) {
            result.add(22000.0);
            result.add(30000.0);
        } else {
            result.add(0.0);
            result.add(1.0);
        }
        return result;
    }

    public List<String> getDayShipping () {
        List<String> dayShippings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dayShippings.add(now.plusDays(3).toLocalDate().format(formatter));
        dayShippings.add(now.plusDays(6).toLocalDate().format(formatter));
        dayShippings.add(now.plusDays(2).toLocalDate().format(formatter));
        dayShippings.add(now.plusDays(4).toLocalDate().format(formatter));
        return dayShippings;
    }

    public double addShippingFee (Long userId, int option) {
        Address addressUserDefault = addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null);

        double result = 0;
        switch (option) {
            case 0:
                if (addressUserDefault.getProvince().equals("79")) {
                    result = 12000;
                } else {
                    result = 22000;
                }
                break;
            case 1:
                if (addressUserDefault.getProvince().equals("79")) {
                    result = 18000;
                } else {
                    result = 30000;
                }
                break;
        }
        return result;
    }

    public List<String> addDayShipping (int option) {
        List<String> listDay = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        switch (option) {
            case 0:
                listDay.add(now.plusDays(3).toLocalDate().format(formatter));
                listDay.add(now.plusDays(6).toLocalDate().format(formatter));
                break;
            case 1:
                listDay.add(now.plusDays(2).toLocalDate().format(formatter));
                listDay.add(now.plusDays(4).toLocalDate().format(formatter));
                break;
        }
        return listDay;
    }

    public void addReceipt (List<Long> cartsId, Long userId,
                            Long addressDefaultId,
                            double goodsFee,
                            double transportFee,
                            double totalPrice,
                            String receiptCode,
                            String paymentMethod,
                            String dayReceived) {
        Receipt receipt = new Receipt();
        List<Cart> cartsList = new ArrayList<>();
        for (Long id : cartsId) {
            Cart cart = cartRepo.findById(id).orElse(null);
            Product product = cart.getProduct();

            cart.setSold(2);
            cartRepo.save(cart);
            cartsList.add(cart);

            int newQuantitySold = product.getQuantitySold() + cart.getQuantity();
            int newQuantityInStore = product.getQuantityAdd() - newQuantitySold;
            product.setQuantitySold(newQuantitySold);
            product.setQuantityInStore(newQuantityInStore);
            productRepo.save(product);
        }
        receipt.setCartsList(cartsList);
        receipt.setUser(userRepo.findById(userId).orElse(null));

        LocalDateTime dateCreated = LocalDateTime.now();
        String formattedDate = dateCreated.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        receipt.setDateCreated(formattedDate);
        receipt.setDateCreatedDateFormat(dateCreated);

        receipt.setAddress(addressRepo.findById(addressDefaultId).orElse(null));
        receipt.setGoodsFee(goodsFee);
        receipt.setTransportFee(transportFee);
        receipt.setTotalPrice(totalPrice);
        receipt.setReceiptCode(receiptCode);
        receipt.setPaymentMethod(paymentMethod);
        receipt.setDayReceived(dayReceived);
        receipt.setStatus("Đang giao hàng");
        receiptRepo.save(receipt);
    }

    public List<Receipt> getReceiptOfUser (Long userId) {
        List<Receipt> receipts = new ArrayList<>();
        receipts = receiptRepo.findByUserIdOrderByDateCreatedDateFormatDesc(userId);
        return receipts;
    }

    public Receipt getOneReceiptOfUser (Long userId, int index) {
        List<Receipt> receipts = new ArrayList<>();
        receipts = receiptRepo.findByUserIdOrderByDateCreatedDateFormatDesc(userId);
        Receipt receipt = null;
        if (!receipts.isEmpty()) {
            receipt = receipts.get(index);
        }
        return receipt;
    }
    public Receipt getOneReceiptOfUserAdmin (String receiptCode) {
        return receiptRepo.findByReceiptCode(receiptCode).orElse(null);
    }

    public Address getDefaultAddressInReceipt (Long userId, int index) {
        List<Receipt> receipts = receiptRepo.findByUserIdOrderByDateCreatedDateFormatDesc(userId);
        Receipt receipt = receipts.get(index);
        Address address = receipt.getAddress();

        String province = this.getNameById(address.getProvince(),"province");
        String district = this.getNameById(address.getDistrict(),"district");
        String ward = this.getNameById(address.getWard(),"commune");
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);

        return address;
    }
    public Address getDefaultAddressInReceiptAdmin (String receiptCode) {
        Receipt receipt = receiptRepo.findByReceiptCode(receiptCode).orElse(null);
        Address address = receipt.getAddress();
        String province = this.getNameById(address.getProvince(),"province");
        String district = this.getNameById(address.getDistrict(),"district");
        String ward = this.getNameById(address.getWard(),"commune");
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);

        return address;
    }

    private static String limitWords(String input, int wordLimit) {
        String[] words = input.split("\\s+");
        if (words.length > wordLimit) {
            StringBuilder limitedText = new StringBuilder();
            for (int i = 0; i < wordLimit; i++) {
                limitedText.append(words[i]).append(" ");
            }
            return limitedText.toString().trim() + " ...";
        }
        return input;
    }
    public void setStatusReceipt (Long id) {
        Receipt receipt = receiptRepo.findById(id).orElse(null);
        if (receipt!=null) {
            receipt.setStatus("Đã nhận");
            receiptRepo.save(receipt);
        }
    }
    public List<Receipt> getAllReceipt () {
        return receiptRepo.findAllByOrderByDateCreatedDateFormatDesc();
    }

    public List<Receipt> findReceiptsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return receiptRepo.findByDateCreatedDateFormatBetween(startDate,endDate);
    }

    public Map<LocalDate, Double> getRevenueDataForLast7Days() {
        Map<LocalDate, Double> revenueData = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 4; i >= 0; i--) {
            LocalDateTime startDate = now.minusDays(i).with(LocalTime.MIN);
            LocalDateTime endDate = now.minusDays(i).with(LocalTime.MAX);
            List<Receipt> receipts = findReceiptsByDate(startDate, endDate);
            double totalRevenue = receipts.stream().mapToDouble(Receipt::getTotalPrice).sum();
            revenueData.put(startDate.toLocalDate(), totalRevenue);
        }
        return revenueData;
    }
    public Map<LocalDate, Integer> getRevenueDataForLast7DaysOrders() {
        Map<LocalDate, Integer> revenueData = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 4; i >= 0; i--) {
            LocalDateTime startDate = now.minusDays(i).with(LocalTime.MIN);
            LocalDateTime endDate = now.minusDays(i).with(LocalTime.MAX);
            List<Receipt> receipts = findReceiptsByDate(startDate, endDate);
            Integer totalRevenue = receipts.size();
            revenueData.put(startDate.toLocalDate(), totalRevenue);
        }
        return revenueData;
    }
    public double totalProfit () {
        if (this.getAllReceipt().isEmpty()) return 0;
        return this.getAllReceipt().stream().mapToDouble(Receipt::getTotalPrice).sum();
    }
    public int totalOrder () {
        if (this.getAllReceipt().isEmpty()) return 0;
        return this.getAllReceipt().size();
    }

    //Analytic by day
    public Map<LocalDate, Double> getRevenueDataForDays(LocalDateTime startDate, LocalDateTime endDate) {
        Map<LocalDate, Double> revenueData = new LinkedHashMap<>();
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());

        for (long i = 0; i <= numOfDaysBetween - 1; i++) {
            LocalDateTime startOfDay = startDate.plusDays(i).with(LocalTime.MIN);
            LocalDateTime endOfDay = startDate.plusDays(i).with(LocalTime.MAX);
            List<Receipt> receipts = findReceiptsByDate(startOfDay, endOfDay);
            double totalRevenue = receipts.stream().mapToDouble(Receipt::getTotalPrice).sum();
            revenueData.put(startOfDay.toLocalDate(), totalRevenue);
        }
        return revenueData;
    }
    public Map<LocalDate, Integer> getRevenueDataForOrders(LocalDateTime startDate, LocalDateTime endDate) {
        Map<LocalDate, Integer> revenueData = new LinkedHashMap<>();
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());

        for (long i = 0; i <= numOfDaysBetween - 1; i++) {
            LocalDateTime startOfDay = startDate.plusDays(i).with(LocalTime.MIN);
            LocalDateTime endOfDay = startDate.plusDays(i).with(LocalTime.MAX);
            List<Receipt> receipts = findReceiptsByDate(startOfDay, endOfDay);
            Integer totalRevenue = receipts.size();
            revenueData.put(startOfDay.toLocalDate(), totalRevenue);
        }
        return revenueData;
    }

}

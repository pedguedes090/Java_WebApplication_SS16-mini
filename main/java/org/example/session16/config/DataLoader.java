package org.example.session16.config;

import org.example.session16.model.entity.Category;
import org.example.session16.model.entity.Product;
import org.example.session16.model.entity.User;
import org.example.session16.repository.CategoryRepository;
import org.example.session16.repository.ProductRepository;
import org.example.session16.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataLoader(UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmailAndRole("admin@example.com", "ADMIN").isEmpty()) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password("admin123")
                    .phone("0912345678")
                    .address("123 Đường Admin")
                    .role("ADMIN")
                    .build();

            userRepository.save(admin);
            System.out.println("Tạo admin thành công!");
        }

        // 2. Tạo CATEGORY
        if (categoryRepository.count() == 0) {
            Category c1 = Category.builder().name("Điện thoại").description("Smartphone").build();
            Category c2 = Category.builder().name("Laptop").description("Máy tính xách tay").build();
            Category c3 = Category.builder().name("Phụ kiện").description("Đồ công nghệ").build();

            categoryRepository.saveAll(List.of(c1, c2, c3));
            System.out.println("Tạo danh mục thành công!");
        }

        // 3. Tạo PRODUCT
        if (productRepository.count() == 0) {
            List<Category> categories = categoryRepository.findAll();

            Category phone = categories.get(0);
            Category laptop = categories.get(1);
            Category accessory = categories.get(2);

            List<Product> products = List.of(
                    // Điện thoại
                    Product.builder().name("iPhone 15").price(new BigDecimal("20000000")).stock(10).category(phone).build(),
                    Product.builder().name("Samsung S24").price(new BigDecimal("18000000")).stock(15).category(phone).build(),
                    Product.builder().name("Xiaomi 14").price(new BigDecimal("12000000")).stock(20).category(phone).build(),

                    // Laptop
                    Product.builder().name("MacBook Air M2").price(new BigDecimal("28000000")).stock(5).category(laptop).build(),
                    Product.builder().name("Dell XPS 13").price(new BigDecimal("25000000")).stock(7).category(laptop).build(),
                    Product.builder().name("HP Pavilion").price(new BigDecimal("15000000")).stock(12).category(laptop).build(),

                    // Phụ kiện
                    Product.builder().name("Tai nghe Bluetooth").price(new BigDecimal("500000")).stock(50).category(accessory).build(),
                    Product.builder().name("Chuột không dây").price(new BigDecimal("300000")).stock(40).category(accessory).build(),
                    Product.builder().name("Bàn phím cơ").price(new BigDecimal("1000000")).stock(30).category(accessory).build()
            );

            productRepository.saveAll(products);
            System.out.println("Tạo sản phẩm thành công!");
        }
    }
}
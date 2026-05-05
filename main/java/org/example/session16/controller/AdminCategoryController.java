package org.example.session16.controller;

import org.example.session16.model.entity.Category;
import org.example.session16.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }

    @PostMapping("/save")
    public String saveCategory(
            @ModelAttribute Category category,
            RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được thêm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tìm thấy"));
        model.addAttribute("category", category);
        return "admin/categories/form";
    }

    @PostMapping("/{id}/update")
    public String updateCategory(
            @PathVariable Long id,
            @ModelAttribute Category category,
            RedirectAttributes redirectAttributes) {
        try {
            category.setId(id);
            categoryService.updateCategory(category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được cập nhật thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra xem danh mục có sản phẩm không
            long productCount = categoryService.countProductsInCategory(id);
            if (productCount > 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể xóa danh mục này vì nó có " + productCount + " sản phẩm");
                return "redirect:/admin/categories";
            }

            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được xóa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}


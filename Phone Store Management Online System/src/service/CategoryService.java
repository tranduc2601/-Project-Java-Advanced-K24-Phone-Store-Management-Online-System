package service;

import dao.CategoryDAO;
import model.Category;
import util.InputValidator;

import java.util.List;

/**
 * Service xử lý nghiệp vụ quản lý danh mục.
 */
public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public List<Category> getActiveCategories() {
        return categoryDAO.findActive();
    }

    public Category getCategoryById(int id) {
        return categoryDAO.findById(id);
    }

    /**
     * Thêm danh mục mới (kiểm tra trùng tên)
     */
    public boolean addCategory(String name, String description) {
        if (!InputValidator.isNotEmpty(name)) {
            System.out.println("⚠️ Tên danh mục không được để trống!");
            return false;
        }
        // Kiểm tra trùng tên
        Category existing = categoryDAO.findByName(name.trim());
        if (existing != null) {
            System.out.println("⚠️ Tên danh mục '" + name + "' đã tồn tại!");
            return false;
        }

        Category category = new Category(name.trim(), description != null ? description.trim() : "");
        return categoryDAO.add(category);
    }

    /**
     * Cập nhật danh mục (kiểm tra ID tồn tại, kiểm tra trùng tên)
     */
    public boolean updateCategory(int categoryId, String newName, String newDescription) {
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            System.out.println("⚠️ Không tìm thấy danh mục với ID = " + categoryId);
            return false;
        }
        if (!InputValidator.isNotEmpty(newName)) {
            System.out.println("⚠️ Tên danh mục không được để trống!");
            return false;
        }
        // Kiểm tra trùng tên (trừ chính nó)
        Category dup = categoryDAO.findByName(newName.trim());
        if (dup != null && dup.getCategoryId() != categoryId) {
            System.out.println("⚠️ Tên danh mục '" + newName + "' đã tồn tại!");
            return false;
        }

        existing.setCategoryName(newName.trim());
        existing.setDescription(newDescription != null ? newDescription.trim() : existing.getDescription());
        return categoryDAO.update(existing);
    }

    /**
     * Xóa mềm danh mục (Soft Delete)
     */
    public boolean softDeleteCategory(int categoryId) {
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            System.out.println("⚠️ Không tìm thấy danh mục với ID = " + categoryId);
            return false;
        }
        if (!existing.isStatus()) {
            System.out.println("⚠️ Danh mục này đã bị ẩn rồi!");
            return false;
        }
        return categoryDAO.softDelete(categoryId);
    }

    /**
     * Khôi phục danh mục đã xóa mềm
     */
    public boolean restoreCategory(int categoryId) {
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            System.out.println("⚠️ Không tìm thấy danh mục với ID = " + categoryId);
            return false;
        }
        return categoryDAO.restore(categoryId);
    }
}


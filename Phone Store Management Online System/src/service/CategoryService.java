package service;

import dao.CategoryDAO;
import model.Category;
import util.InputValidator;

import java.util.List;

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

    public boolean addCategory(String name, String description) {
        if (!InputValidator.isNotEmpty(name)) {
            System.out.println("Ten danh muc khong duoc de trong!");
            return false;
        }
        Category existing = categoryDAO.findByName(name.trim());
        if (existing != null) {
            System.out.println("Ten danh muc '" + name + "' da ton tai!");
            return false;
        }
        Category category = new Category(name.trim(), description != null ? description.trim() : "");
        return categoryDAO.add(category);
    }

    public boolean updateCategory(int categoryId, String newName, String newDescription) {
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            System.out.println("Khong tim thay danh muc voi ID = " + categoryId);
            return false;
        }
        if (!InputValidator.isNotEmpty(newName)) {
            System.out.println("Ten danh muc khong duoc de trong!");
            return false;
        }
        Category dup = categoryDAO.findByName(newName.trim());
        if (dup != null && dup.getCategoryId() != categoryId) {
            System.out.println("Ten danh muc '" + newName + "' da ton tai!");
            return false;
        }
        existing.setCategoryName(newName.trim());
        existing.setDescription(newDescription != null ? newDescription.trim() : existing.getDescription());
        return categoryDAO.update(existing);
    }

    public boolean softDeleteCategory(int categoryId) {
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            System.out.println("Khong tim thay danh muc voi ID = " + categoryId);
            return false;
        }
        if (!existing.isStatus()) {
            System.out.println("Danh muc nay da bi an roi!");
            return false;
        }
        return categoryDAO.softDelete(categoryId);
    }

    public boolean restoreCategory(int categoryId) {
        Category existing = categoryDAO.findById(categoryId);
        if (existing == null) {
            System.out.println("Khong tim thay danh muc voi ID = " + categoryId);
            return false;
        }
        return categoryDAO.restore(categoryId);
    }
}

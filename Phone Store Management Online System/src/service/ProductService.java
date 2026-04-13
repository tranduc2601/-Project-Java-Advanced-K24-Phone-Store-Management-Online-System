package service;

import dao.ProductDAO;
import model.Product;
import util.InputValidator;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> getProductsPaginated(int page, int pageSize) {
        return productDAO.findAllPaginated(page, pageSize);
    }

    public int countActiveProducts() {
        return productDAO.countActive();
    }

    public List<Product> getAllActiveProducts() {
        return productDAO.findAllActive();
    }

    public List<Product> getInStockProducts() {
        return productDAO.findInStock();
    }

    public Product getProductById(int id) {
        return productDAO.findById(id);
    }

    public List<Product> searchByName(String keyword) {
        return productDAO.searchByName(keyword);
    }

    public List<Product> sortByPrice(boolean ascending) {
        return productDAO.findAllSortedByPrice(ascending);
    }

    public boolean addProduct(String name, int categoryId, BigDecimal price, int stock,
                              String color, String storageCapacity) {
        if (!InputValidator.isNotEmpty(name)) {
            System.out.println("Tên sản phẩm không được để trống!");
            return false;
        }
        if (!InputValidator.isPositiveDecimal(price)) {
            System.out.println("Giá sản phẩm phải lớn hơn 0!");
            return false;
        }
        if (!InputValidator.isPositiveInt(stock)) {
            System.out.println("Số lượng tồn kho phải lớn hơn 0!");
            return false;
        }

        Product product = new Product(name.trim(), categoryId, price, stock,
                color != null ? color.trim() : "", storageCapacity != null ? storageCapacity.trim() : "");
        return productDAO.add(product);
    }

    public boolean updateProduct(int productId, String name, int categoryId, BigDecimal price,
                                 int stock, String color, String storageCapacity) {
        Product existing = productDAO.findById(productId);
        if (existing == null) {
            System.out.println("Không tìm thấy sản phẩm với ID = " + productId);
            return false;
        }
        if (!InputValidator.isNotEmpty(name)) {
            System.out.println("Tên sản phẩm không được để trống!");
            return false;
        }
        if (!InputValidator.isPositiveDecimal(price)) {
            System.out.println("Giá sản phẩm phải lớn hơn 0!");
            return false;
        }
        if (!InputValidator.isNonNegativeInt(stock)) {
            System.out.println("Số lượng tồn kho phải >= 0!");
            return false;
        }

        existing.setProductName(name.trim());
        existing.setCategoryId(categoryId);
        existing.setPrice(price);
        existing.setStock(stock);
        existing.setColor(color != null ? color.trim() : existing.getColor());
        existing.setStorageCapacity(storageCapacity != null ? storageCapacity.trim() : existing.getStorageCapacity());
        return productDAO.update(existing);
    }

    public boolean deleteProduct(int productId) {
        Product existing = productDAO.findById(productId);
        if (existing == null) {
            System.out.println("Không tìm thấy sản phẩm với ID = " + productId);
            return false;
        }
        return productDAO.delete(productId);
    }
}

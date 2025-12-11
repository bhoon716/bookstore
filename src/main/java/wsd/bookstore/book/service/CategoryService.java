package wsd.bookstore.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Category;
import wsd.bookstore.book.repository.CategoryRepository;
import wsd.bookstore.book.request.CategoryRequest;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wsd.bookstore.book.response.CategoryResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }
        Category category = new Category(request.getName());
        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }

        category.update(request.getName());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));
        categoryRepository.delete(category);
    }

    public Page<CategoryResponse> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryResponse::from);
    }

    public CategoryResponse getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));
        return CategoryResponse.from(category);
    }
}

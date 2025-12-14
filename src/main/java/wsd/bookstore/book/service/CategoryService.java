package wsd.bookstore.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Category;
import wsd.bookstore.book.repository.CategoryRepository;
import wsd.bookstore.book.request.CategoryRequest;
import wsd.bookstore.book.response.CategoryResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getCategories() {
        log.info("카테고리 목록 조회 요청");
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse getCategory(Long categoryId) {
        log.info("카테고리 단건 조회 요청: id={}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));
        return CategoryResponse.from(category);
    }

    @Transactional
    public Long createCategory(CategoryRequest request) {
        log.info("카테고리 생성 요청: name={}", request.getName());
        if (categoryRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }
        Category category = new Category(request.getName());
        Category savedCategory = categoryRepository.save(category);
        log.info("카테고리 생성 완료: id={}", savedCategory.getId());
        return savedCategory.getId();
    }

    @Transactional
    public void updateCategory(Long categoryId, CategoryRequest request) {
        log.info("카테고리 수정 요청: id={}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }

        category.update(request.getName());
        log.info("카테고리 수정 완료: id={}", categoryId);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("카테고리 삭제 요청: id={}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));
        categoryRepository.delete(category);
        log.info("카테고리 삭제 완료: id={}", categoryId);
    }
}

package wsd.bookstore.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import wsd.bookstore.book.entity.Category;
import wsd.bookstore.book.repository.CategoryRepository;
import wsd.bookstore.book.request.CategoryRequest;
import wsd.bookstore.book.response.CategoryResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("카테고리 목록 조회 테스트")
    class GetCategoriesTest {

        @Test
        @DisplayName("성공: 카테고리 목록을 성공적으로 조회해야 한다")
        void success() {
            // given
            Category category1 = new Category("Category1");
            Category category2 = new Category("Category2");
            given(categoryRepository.findAll()).willReturn(List.of(category1, category2));

            // when
            List<CategoryResponse> responses = categoryService.getCategories();

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getName()).isEqualTo(category1.getName());
            assertThat(responses.get(1).getName()).isEqualTo(category2.getName());
        }
    }

    @Nested
    @DisplayName("카테고리 단건 조회 테스트")
    class GetCategoryTest {

        @Test
        @DisplayName("성공: 카테고리를 성공적으로 조회해야 한다")
        void success() {
            // given
            Long categoryId = 1L;
            Category category = new Category("Category1");
            ReflectionTestUtils.setField(category, "id", categoryId);

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            CategoryResponse response = categoryService.getCategory(categoryId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(categoryId);
            assertThat(response.getName()).isEqualTo(category.getName());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 카테고리 조회 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long categoryId = 1L;
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.getCategory(categoryId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CATEGORY);
        }
    }

    @Nested
    @DisplayName("카테고리 생성 테스트")
    class CreateCategoryTest {

        @Test
        @DisplayName("성공: 카테고리를 성공적으로 생성해야 한다")
        void success() {
            // given
            CategoryRequest request = new CategoryRequest("NewCategory");
            Category savedCategory = new Category(request.getName());
            Long categoryId = 1L;
            ReflectionTestUtils.setField(savedCategory, "id", categoryId);

            given(categoryRepository.existsByName(request.getName())).willReturn(false);
            given(categoryRepository.save(any(Category.class))).willReturn(savedCategory);

            // when
            Long resultId = categoryService.createCategory(request);

            // then
            assertThat(resultId).isEqualTo(categoryId);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("실패: 중복된 카테고리 이름으로 생성 시 예외가 발생해야 한다")
        void fail_duplicateName() {
            // given
            CategoryRequest request = new CategoryRequest("DuplicateCategory");
            given(categoryRepository.existsByName(request.getName())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> categoryService.createCategory(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_CATEGORY);
        }
    }

    @Nested
    @DisplayName("카테고리 수정 테스트")
    class UpdateCategoryTest {

        @Test
        @DisplayName("성공: 카테고리 정보를 성공적으로 수정해야 한다")
        void success() {
            // given
            Long categoryId = 1L;
            CategoryRequest request = new CategoryRequest("UpdatedCategory");
            Category category = new Category("OldCategory");
            ReflectionTestUtils.setField(category, "id", categoryId);

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
            given(categoryRepository.existsByName(request.getName())).willReturn(false);

            // when
            categoryService.updateCategory(categoryId, request);

            // then
            assertThat(category.getName()).isEqualTo(request.getName());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 카테고리 수정 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long categoryId = 1L;
            CategoryRequest request = new CategoryRequest("UpdatedCategory");
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(categoryId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CATEGORY);
        }

        @Test
        @DisplayName("실패: 중복된 카테고리 이름으로 수정 시 예외가 발생해야 한다")
        void fail_duplicateName() {
            // given
            Long categoryId = 1L;
            CategoryRequest request = new CategoryRequest("DuplicateCategory");
            Category category = new Category("OldCategory");
            ReflectionTestUtils.setField(category, "id", categoryId);

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
            given(categoryRepository.existsByName(request.getName())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(categoryId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_CATEGORY);
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteCategoryTest {

        @Test
        @DisplayName("성공: 카테고리를 성공적으로 삭제해야 한다")
        void success() {
            // given
            Long categoryId = 1L;
            Category category = new Category("DeleteCategory");
            ReflectionTestUtils.setField(category, "id", categoryId);

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            categoryService.deleteCategory(categoryId);

            // then
            verify(categoryRepository).delete(category);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 카테고리 삭제 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long categoryId = 1L;
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CATEGORY);
        }
    }
}

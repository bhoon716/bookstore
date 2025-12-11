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
import wsd.bookstore.book.entity.Author;
import wsd.bookstore.book.repository.AuthorRepository;
import wsd.bookstore.book.request.AuthorRequest;
import wsd.bookstore.book.response.AuthorResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Nested
    @DisplayName("작가 목록 조회 테스트")
    class GetAuthorsTest {

        @Test
        @DisplayName("성공: 작가 목록을 성공적으로 조회해야 한다")
        void success() {
            // given
            Author author1 = new Author("Author1", "Bio1");
            Author author2 = new Author("Author2", "Bio2");
            given(authorRepository.findAll()).willReturn(List.of(author1, author2));

            // when
            List<AuthorResponse> responses = authorService.getAuthors();

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getName()).isEqualTo(author1.getName());
            assertThat(responses.get(1).getName()).isEqualTo(author2.getName());
        }
    }

    @Nested
    @DisplayName("작가 단건 조회 테스트")
    class GetAuthorTest {

        @Test
        @DisplayName("성공: 작가를 성공적으로 조회해야 한다")
        void success() {
            // given
            Long authorId = 1L;
            Author author = new Author("Author1", "Bio1");
            ReflectionTestUtils.setField(author, "id", authorId);

            given(authorRepository.findById(authorId)).willReturn(Optional.of(author));

            // when
            AuthorResponse response = authorService.getAuthor(authorId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(authorId);
            assertThat(response.getName()).isEqualTo(author.getName());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 작가 조회 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long authorId = 1L;
            given(authorRepository.findById(authorId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authorService.getAuthor(authorId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_AUTHOR);
        }
    }

    @Nested
    @DisplayName("작가 생성 테스트")
    class CreateAuthorTest {

        @Test
        @DisplayName("성공: 작가를 성공적으로 생성해야 한다")
        void success() {
            // given
            AuthorRequest request = new AuthorRequest("NewAuthor", "NewBio");
            Author savedAuthor = new Author(request.getName(), request.getBio());
            Long authorId = 1L;
            ReflectionTestUtils.setField(savedAuthor, "id", authorId);

            given(authorRepository.save(any(Author.class))).willReturn(savedAuthor);

            // when
            Long resultId = authorService.createAuthor(request);

            // then
            assertThat(resultId).isEqualTo(authorId);
            verify(authorRepository).save(any(Author.class));
        }
    }

    @Nested
    @DisplayName("작가 수정 테스트")
    class UpdateAuthorTest {

        @Test
        @DisplayName("성공: 작가 정보를 성공적으로 수정해야 한다")
        void success() {
            // given
            Long authorId = 1L;
            AuthorRequest request = new AuthorRequest("UpdatedName", "UpdatedBio");
            Author author = new Author("OldName", "OldBio");
            ReflectionTestUtils.setField(author, "id", authorId);

            given(authorRepository.findById(authorId)).willReturn(Optional.of(author));

            // when
            authorService.updateAuthor(authorId, request);

            // then
            assertThat(author.getName()).isEqualTo(request.getName());
            assertThat(author.getBio()).isEqualTo(request.getBio());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 작가 수정 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long authorId = 1L;
            AuthorRequest request = new AuthorRequest("UpdatedName", "UpdatedBio");
            given(authorRepository.findById(authorId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authorService.updateAuthor(authorId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_AUTHOR);
        }
    }

    @Nested
    @DisplayName("작가 삭제 테스트")
    class DeleteAuthorTest {

        @Test
        @DisplayName("성공: 작가를 성공적으로 삭제해야 한다")
        void success() {
            // given
            Long authorId = 1L;
            Author author = new Author("DeleteName", "DeleteBio");
            ReflectionTestUtils.setField(author, "id", authorId);

            given(authorRepository.findById(authorId)).willReturn(Optional.of(author));

            // when
            authorService.deleteAuthor(authorId);

            // then
            verify(authorRepository).delete(author);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 작가 삭제 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long authorId = 1L;
            given(authorRepository.findById(authorId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authorService.deleteAuthor(authorId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_AUTHOR);
        }
    }
}

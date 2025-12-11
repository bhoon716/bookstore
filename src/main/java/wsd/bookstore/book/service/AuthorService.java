package wsd.bookstore.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Author;
import wsd.bookstore.book.repository.AuthorRepository;
import wsd.bookstore.book.request.AuthorRequest;
import wsd.bookstore.book.response.AuthorResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorResponse> getAuthors() {
        log.info("작가 목록 조회 요청");
        return authorRepository.findAll().stream()
                .map(AuthorResponse::from)
                .toList();
    }

    public AuthorResponse getAuthor(Long authorId) {
        log.info("작가 단건 조회 요청: id={}", authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_AUTHOR));
        return AuthorResponse.from(author);
    }

    @Transactional
    public Long createAuthor(AuthorRequest request) {
        log.info("작가 생성 요청: name={}", request.getName());
        Author author = new Author(request.getName(), request.getBio());
        Author savedAuthor = authorRepository.save(author);
        log.info("작가 생성 완료: id={}", savedAuthor.getId());
        return savedAuthor.getId();
    }

    @Transactional
    public void updateAuthor(Long authorId, AuthorRequest request) {
        log.info("작가 수정 요청: id={}", authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_AUTHOR));
        author.update(request.getName(), request.getBio());
        log.info("작가 수정 완료: id={}", authorId);
    }

    @Transactional
    public void deleteAuthor(Long authorId) {
        log.info("작가 삭제 요청: id={}", authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_AUTHOR));
        authorRepository.delete(author);
        log.info("작가 삭제 완료: id={}", authorId);
    }
}

package wsd.bookstore.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wsd.bookstore.book.entity.Author;
import wsd.bookstore.book.repository.AuthorRepository;
import wsd.bookstore.book.request.AuthorRequest;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import wsd.bookstore.book.response.AuthorResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public Long createAuthor(AuthorRequest request) {
        Author author = new Author(request.getName(), request.getBio());
        Author savedAuthor = authorRepository.save(author);
        return savedAuthor.getId();
    }

    @Transactional
    public void updateAuthor(Long authorId, AuthorRequest request) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_AUTHOR));
        author.update(request.getName(), request.getBio());
    }

    @Transactional
    public void deleteAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_AUTHOR));
        authorRepository.delete(author);
    }

    public List<AuthorResponse> getAuthors() {
        return authorRepository.findAll().stream()
                .map(AuthorResponse::from)
                .toList();
    }

    public AuthorResponse getAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_AUTHOR));
        return AuthorResponse.from(author);
    }
}

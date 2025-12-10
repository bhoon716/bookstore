package wsd.bookstore.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Publisher;
import wsd.bookstore.book.repository.PublisherRepository;
import wsd.bookstore.book.request.PublisherRequest;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Transactional
    public void createPublisher(PublisherRequest request) {
        if (publisherRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_PUBLISHER);
        }
        Publisher publisher = new Publisher(request.getName());
        publisherRepository.save(publisher);
    }

    @Transactional
    public void updatePublisher(Long publisherId, PublisherRequest request) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PUBLISHER));

        if (!publisher.getName().equals(request.getName()) && publisherRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_PUBLISHER);
        }

        publisher.update(request.getName());
    }

    @Transactional
    public void deletePublisher(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PUBLISHER));
        publisherRepository.delete(publisher);
    }
}

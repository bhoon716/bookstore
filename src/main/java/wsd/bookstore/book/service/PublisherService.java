package wsd.bookstore.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsd.bookstore.book.entity.Publisher;
import wsd.bookstore.book.repository.PublisherRepository;
import wsd.bookstore.book.request.PublisherRequest;
import wsd.bookstore.book.response.PublisherResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public List<PublisherResponse> getPublishers() {
        log.info("출판사 목록 조회 요청");
        return publisherRepository.findAll().stream()
                .map(PublisherResponse::from)
                .toList();
    }

    public PublisherResponse getPublisher(Long publisherId) {
        log.info("출판사 단건 조회 요청: id={}", publisherId);
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PUBLISHER));
        return PublisherResponse.from(publisher);
    }

    @Transactional
    public Long createPublisher(PublisherRequest request) {
        log.info("출판사 생성 요청: name={}", request.getName());
        if (publisherRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_PUBLISHER);
        }
        Publisher publisher = new Publisher(request.getName());
        Publisher savedPublisher = publisherRepository.save(publisher);
        log.info("출판사 생성 완료: id={}", savedPublisher.getId());
        return savedPublisher.getId();
    }

    @Transactional
    public void updatePublisher(Long publisherId, PublisherRequest request) {
        log.info("출판사 수정 요청: id={}", publisherId);
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PUBLISHER));

        if (!publisher.getName().equals(request.getName()) && publisherRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_PUBLISHER);
        }

        publisher.update(request.getName());
        log.info("출판사 수정 완료: id={}", publisherId);
    }

    @Transactional
    public void deletePublisher(Long publisherId) {
        log.info("출판사 삭제 요청: id={}", publisherId);
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PUBLISHER));
        publisherRepository.delete(publisher);
        log.info("출판사 삭제 완료: id={}", publisherId);
    }
}

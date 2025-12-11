package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import wsd.bookstore.book.response.PublisherResponse;
import wsd.bookstore.book.request.PublisherRequest;
import wsd.bookstore.book.service.PublisherService;
import wsd.bookstore.common.response.ApiResponse;

@RestController
@RequestMapping("/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PublisherResponse>>> getPublishers(Pageable pageable) {
        Page<PublisherResponse> response = publisherService.getPublishers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "출판사 목록 조회 성공"));
    }

    @GetMapping("/{publisherId}")
    public ResponseEntity<ApiResponse<PublisherResponse>> getPublisher(@PathVariable Long publisherId) {
        PublisherResponse response = publisherService.getPublisher(publisherId);
        return ResponseEntity.ok(ApiResponse.success(response, "출판사 상세 조회 성공"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createPublisher(@Valid @RequestBody PublisherRequest request) {
        publisherService.createPublisher(request);
        return ResponseEntity.ok(ApiResponse.noContent("출판사 등록 성공"));
    }

    @PutMapping("/{publisherId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updatePublisher(
            @PathVariable Long publisherId,
            @Valid @RequestBody PublisherRequest request) {
        publisherService.updatePublisher(publisherId, request);
        return ResponseEntity.ok(ApiResponse.noContent("출판사 수정 성공"));
    }

    @DeleteMapping("/{publisherId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePublisher(@PathVariable Long publisherId) {
        publisherService.deletePublisher(publisherId);
        return ResponseEntity.ok(ApiResponse.noContent("출판사 삭제 성공"));
    }
}

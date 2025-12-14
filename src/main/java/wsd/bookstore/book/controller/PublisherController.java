package wsd.bookstore.book.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wsd.bookstore.book.response.PublisherResponse;
import wsd.bookstore.book.request.PublisherRequest;
import wsd.bookstore.book.service.PublisherService;
import wsd.bookstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Tag(name = "Publishers", description = "출판사 관리 API")
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    @Operation(summary = "출판사 목록 조회", description = "전체 출판사 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "출판사 목록 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "출판사 목록 조회 성공",
                "payload": [
                    {
                        "publisherId": 1,
                        "name": "인사이트"
                    },
                    {
                        "publisherId": 2,
                        "name": "위키북스"
                    }
                ]
            }
            """)))
    public ResponseEntity<ApiResponse<List<PublisherResponse>>> getPublishers() {
        List<PublisherResponse> response = publisherService.getPublishers();
        return ApiResponse.ok(response, "출판사 목록 조회 성공");
    }

    @GetMapping("/{publisherId}")
    @Operation(summary = "출판사 상세 조회", description = "출판사 ID로 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "출판사 상세 조회 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "출판사 상세 조회 성공",
                "payload": {
                    "publisherId": 1,
                    "name": "인사이트"
                }
            }
            """)))
    public ResponseEntity<ApiResponse<PublisherResponse>> getPublisher(@PathVariable Long publisherId) {
        PublisherResponse response = publisherService.getPublisher(publisherId);
        return ApiResponse.ok(response, "출판사 상세 조회 성공");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "출판사 등록", description = "관리자가 새로운 출판사를 등록합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "출판사 등록 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "출판사 등록 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> createPublisher(@Valid @RequestBody PublisherRequest request) {
        Long publisherId = publisherService.createPublisher(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(publisherId)
                .toUri();
        return ApiResponse.created(null, location, "출판사 등록 성공");
    }

    @PutMapping("/{publisherId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "출판사 수정", description = "관리자가 출판사 정보를 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "출판사 수정 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "출판사 수정 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> updatePublisher(
            @PathVariable Long publisherId,
            @Valid @RequestBody PublisherRequest request) {
        publisherService.updatePublisher(publisherId, request);
        return ApiResponse.ok(null, "출판사 수정 성공");
    }

    @DeleteMapping("/{publisherId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "출판사 삭제", description = "관리자가 출판사를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "출판사 삭제 성공 예시", value = """
            {
                "isSuccess": true,
                "message": "출판사 삭제 성공",
                "payload": null
            }
            """)))
    public ResponseEntity<ApiResponse<Void>> deletePublisher(@PathVariable Long publisherId) {
        publisherService.deletePublisher(publisherId);
        return ApiResponse.noContent("출판사 삭제 성공");
    }
}

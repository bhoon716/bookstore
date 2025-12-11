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
import wsd.bookstore.book.entity.Publisher;
import wsd.bookstore.book.repository.PublisherRepository;
import wsd.bookstore.book.request.PublisherRequest;
import wsd.bookstore.book.response.PublisherResponse;
import wsd.bookstore.common.error.CustomException;
import wsd.bookstore.common.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @InjectMocks
    private PublisherService publisherService;

    @Mock
    private PublisherRepository publisherRepository;

    @Nested
    @DisplayName("출판사 목록 조회 테스트")
    class GetPublishersTest {

        @Test
        @DisplayName("성공: 출판사 목록을 성공적으로 조회해야 한다")
        void success() {
            // given
            Publisher publisher1 = new Publisher("Publisher1");
            Publisher publisher2 = new Publisher("Publisher2");
            given(publisherRepository.findAll()).willReturn(List.of(publisher1, publisher2));

            // when
            List<PublisherResponse> responses = publisherService.getPublishers();

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getName()).isEqualTo(publisher1.getName());
            assertThat(responses.get(1).getName()).isEqualTo(publisher2.getName());
        }
    }

    @Nested
    @DisplayName("출판사 단건 조회 테스트")
    class GetPublisherTest {

        @Test
        @DisplayName("성공: 출판사를 성공적으로 조회해야 한다")
        void success() {
            // given
            Long publisherId = 1L;
            Publisher publisher = new Publisher("Publisher1");
            ReflectionTestUtils.setField(publisher, "id", publisherId);

            given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));

            // when
            PublisherResponse response = publisherService.getPublisher(publisherId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(publisherId);
            assertThat(response.getName()).isEqualTo(publisher.getName());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 출판사 조회 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long publisherId = 1L;
            given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> publisherService.getPublisher(publisherId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_PUBLISHER);
        }
    }

    @Nested
    @DisplayName("출판사 생성 테스트")
    class CreatePublisherTest {

        @Test
        @DisplayName("성공: 출판사를 성공적으로 생성해야 한다")
        void success() {
            // given
            PublisherRequest request = new PublisherRequest("NewPublisher");
            Publisher savedPublisher = new Publisher(request.getName());
            Long publisherId = 1L;
            ReflectionTestUtils.setField(savedPublisher, "id", publisherId);

            given(publisherRepository.existsByName(request.getName())).willReturn(false);
            given(publisherRepository.save(any(Publisher.class))).willReturn(savedPublisher);

            // when
            Long resultId = publisherService.createPublisher(request);

            // then
            assertThat(resultId).isEqualTo(publisherId);
            verify(publisherRepository).save(any(Publisher.class));
        }

        @Test
        @DisplayName("실패: 중복된 출판사 이름으로 생성 시 예외가 발생해야 한다")
        void fail_duplicateName() {
            // given
            PublisherRequest request = new PublisherRequest("DuplicatePublisher");
            given(publisherRepository.existsByName(request.getName())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> publisherService.createPublisher(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_PUBLISHER);
        }
    }

    @Nested
    @DisplayName("출판사 수정 테스트")
    class UpdatePublisherTest {

        @Test
        @DisplayName("성공: 출판사 정보를 성공적으로 수정해야 한다")
        void success() {
            // given
            Long publisherId = 1L;
            PublisherRequest request = new PublisherRequest("UpdatedPublisher");
            Publisher publisher = new Publisher("OldPublisher");
            ReflectionTestUtils.setField(publisher, "id", publisherId);

            given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
            given(publisherRepository.existsByName(request.getName())).willReturn(false);

            // when
            publisherService.updatePublisher(publisherId, request);

            // then
            assertThat(publisher.getName()).isEqualTo(request.getName());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 출판사 수정 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long publisherId = 1L;
            PublisherRequest request = new PublisherRequest("UpdatedPublisher");
            given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> publisherService.updatePublisher(publisherId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_PUBLISHER);
        }

        @Test
        @DisplayName("실패: 중복된 출판사 이름으로 수정 시 예외가 발생해야 한다")
        void fail_duplicateName() {
            // given
            Long publisherId = 1L;
            PublisherRequest request = new PublisherRequest("DuplicatePublisher");
            Publisher publisher = new Publisher("OldPublisher");
            ReflectionTestUtils.setField(publisher, "id", publisherId);

            given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));
            given(publisherRepository.existsByName(request.getName())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> publisherService.updatePublisher(publisherId, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_PUBLISHER);
        }
    }

    @Nested
    @DisplayName("출판사 삭제 테스트")
    class DeletePublisherTest {

        @Test
        @DisplayName("성공: 출판사를 성공적으로 삭제해야 한다")
        void success() {
            // given
            Long publisherId = 1L;
            Publisher publisher = new Publisher("DeletePublisher");
            ReflectionTestUtils.setField(publisher, "id", publisherId);

            given(publisherRepository.findById(publisherId)).willReturn(Optional.of(publisher));

            // when
            publisherService.deletePublisher(publisherId);

            // then
            verify(publisherRepository).delete(publisher);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 출판사 삭제 시 예외가 발생해야 한다")
        void fail_notFound() {
            // given
            Long publisherId = 1L;
            given(publisherRepository.findById(publisherId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> publisherService.deletePublisher(publisherId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_PUBLISHER);
        }
    }
}

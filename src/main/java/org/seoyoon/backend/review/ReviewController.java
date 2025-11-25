package org.seoyoon.backend.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.seoyoon.backend.item.Item;
import org.seoyoon.backend.item.ItemRepository;
import org.seoyoon.backend.orders.Orders;
import org.seoyoon.backend.orders.dto.OrdersResponseDTO;
import org.seoyoon.backend.review.dto.ReviewResponseDTO;
import org.seoyoon.backend.user.User;
import org.seoyoon.backend.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createUser(@Valid @RequestBody Review review){
        if(itemRepository.findById(review.getItemId()).isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "상품이 존재하지 않습니다.",404));
        }
        if(review.getContent()==null){
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "리뷰 입력은 필수입니다.",404));
        }
        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(new ApiResponse<>(true, saved, "리뷰 등록이 완료됐습니다.", 201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getReviews(){
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();
        var reviews = reviewRepository.findByStoreId(storeId);

        List<ReviewResponseDTO> dtoList = reviews.stream().map(review -> {

            Item item = itemRepository.findById(review.getItemId()).orElse(null);
            String itemTitle = (item != null) ? item.getTitle() : null;

            User user = userRepository.findById(review.getUserId()).orElse(null);
            String userName = user.getName();

            return new ReviewResponseDTO(
                    review.getReviewId(),
                    review.getItemId(),
                    itemTitle,
                    review.getUserId(),
                    userName,
                    review.getContent(),
                    review.getRate(),
                    review.getCreatedAt(),
                    review.getUpdatedAt()
            );

        }).toList();

        return ResponseEntity.ok(new ApiResponse<>(true, dtoList, "리뷰 조회를 완료했습니다.",200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> getOrder(@PathVariable Long id) {
        if(reviewRepository.findById(id).isPresent()) {
            Review review = reviewRepository.findById(id).orElseThrow();

            Item item = itemRepository.findById(review.getItemId()).orElse(null);
            String itemTitle = (item != null) ? item.getTitle() : null;

            User user = userRepository.findById(review.getUserId()).orElse(null);
            String userName = user.getName();

            ReviewResponseDTO dto = new ReviewResponseDTO(
                    review.getReviewId(),
                    review.getItemId(),
                    itemTitle,
                    review.getUserId(),
                    userName,
                    review.getContent(),
                    review.getRate(),
                    review.getCreatedAt(),
                    review.getUpdatedAt()
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, dto, "주문 조회 성공", 200)
            );
        }else {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Review>> updateUser(@PathVariable Long id , @RequestBody Map<String, Object> updates) {
        Optional<Review> optionalUser = reviewRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null ,"유저 데이터는 필수입니다.", 404));
        }

        Review review = optionalUser.get();

        if (updates.containsKey("content")) {
            review.setContent((String) updates.get("content"));
        }
        if (updates.containsKey("rate")) {
            review.setRate((Integer) updates.get("rate"));
        }

        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(new ApiResponse<>(true, saved, "성공적으로 업데이트 되었습니다.", 201));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if(!reviewRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

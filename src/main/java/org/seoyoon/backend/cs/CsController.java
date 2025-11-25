package org.seoyoon.backend.cs;

import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.seoyoon.backend.orders.OrdersRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cs")
@RequiredArgsConstructor
public class CsController {
    private final CsRepository csRepository;
    private final OrdersRepository ordersRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Cs>> createCs(@RequestBody Cs cs) {
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();
        Long userId = cs.getUserId();
        Long ordersId = cs.getOrdersId();

        // 1️⃣ 유저 구매 검증
        boolean purchased = ordersRepository.existsByOrderIdAndUserId(ordersId, userId);
        System.out.println("purchased: " + purchased);
        if (!purchased) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "해당 유저는 이 상품을 구매하지 않았습니다.", 403));
        }


        Cs savedCs = new Cs();
        savedCs.setStoreId(storeId);
        savedCs.setUserId(userId);
        savedCs.setOrdersId(ordersId);
        savedCs.setQuestion(cs.getQuestion());
        savedCs.setCsStatus(CsStatusType.WAITING);

        Cs saved = csRepository.save(savedCs);

        return ResponseEntity.ok(new ApiResponse<>(true, saved, "cs 생성 완료했습니다.", 201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cs>>> getAllCs() {
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Long storeId = (Long) authentication.getDetails();
        List<Cs> cs = csRepository.findByStoreId(storeId);

        return ResponseEntity.ok(new ApiResponse<>(true, cs, "cs 조회를 완료헀습니다.", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cs>> getCsById(@PathVariable Long id) {
        Cs cs =csRepository.findById(id).get();
        if(cs == null){
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, cs, "cs 조회를 완료헀습니다.", 200));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Cs>> updateCs(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Cs> optionalCs = csRepository.findById(id);
        if (optionalCs.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }

        Cs cs = optionalCs.get();

        if (updates.containsKey("ordersId") && updates.get("ordersId") != null) {
            Long newUserId = Long.valueOf(updates.get("userId").toString());
            Long newOrdersId = Long.valueOf(updates.get("ordersId").toString());
            // 유저 구매 검증
            boolean purchased = ordersRepository.existsByOrderIdAndUserId(newOrdersId, newUserId);
            if (!purchased) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "해당 유저는 이 상품을 구매하지 않았습니다.", 403));
            }
            cs.setUserId(newUserId);
        }
        if(updates.containsKey("question")){
            cs.setQuestion((String) updates.get("question"));
        }
        if(updates.containsKey("answer")){
            if(optionalCs.get().getAnswer() == null ){
                cs.setCsStatus(CsStatusType.ANSWERED);
            }
            cs.setAnswer((String) updates.get("answer"));
        }
        Cs saved = csRepository.save(cs);
        return ResponseEntity.ok(new ApiResponse<>(true, saved, "성공적으로 업데이트 되었습니다.", 201));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCs(@PathVariable Long id){
        if(!csRepository.existsById(id)){
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, null, "삭제할 대상을 찾지 못했습니다." , 404));
        }
        csRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "삭제를 완료했습니다.", 200));
    }

}

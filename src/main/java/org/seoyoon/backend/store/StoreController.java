package org.seoyoon.backend.store;

import org.seoyoon.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreRepository repository;
    private final StoreRepository storeRepository;

    public StoreController(StoreRepository repository, StoreRepository storeRepository) {
        this.repository = repository;
        this.storeRepository = storeRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Store>> createStore(@RequestBody Store store) {
        String storeName = store.getStoreName();

        Optional<Store> existedStore = storeRepository.findByStoreName(storeName);
        if (existedStore.isPresent()) {
            return ResponseEntity.status(409) // 409 Conflict
                    .body(new ApiResponse<>(false, null, "이미 동일한 이름의 스토어가 존재합니다.", 409));
        }

        Store newStore = new Store();
        newStore.setStoreName(store.getStoreName());
        newStore.setActive(true);
        Store save = repository.save(newStore);
        return ResponseEntity.ok(new ApiResponse<>(true, save, "스토어 생성을 완료헀습니다.", 201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        List<Store> stores = repository.findAll();
        if (stores.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, stores, "스토어 조회를 완료헀습니다.", 200));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Store>> getStoreById(@PathVariable Long id) {
        Store store = repository.findById(id).get();
        if (store == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, store, "스토어 조회를 완료헀습니다.", 200));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Store>> updateStore(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Store> optionalStore = repository.findById(id);

        if (optionalStore.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }

        Store store = optionalStore.get();

        // 부분 업데이트 처리
        if (updates.containsKey("storeName")) {
            store.setStoreName((String) updates.get("storeName"));
        }
        if (updates.containsKey("active")) {
            store.setActive((Boolean) updates.get("active"));
        }

        Store save = repository.save(store);
        return ResponseEntity.ok(new ApiResponse<>(true, save, "스토어 조회를 완료헀습니다.", 200));
    }
//
//    @PutMapping
//    public ResponseEntity<Store> updateStore(@RequestBody Map<String, Object> updates) {
//        Long id = ((Number) updates.get("id")).longValue();
//        Optional<Store> optionalStore = repository.findById(id);
//
//        if (optionalStore.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Store store = optionalStore.get();
//
//        // 부분 업데이트 처리
//        if (updates.containsKey("storeName")) {
//            store.setStoreName((String) updates.get("storeName"));
//        }
//        if (updates.containsKey("active")) {
//            store.setActive((Boolean) updates.get("active"));
//        }
//
//        return ResponseEntity.ok(repository.save(store));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStore(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
        repository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "스토어 삭제를 완료헀습니다.", 200));
    }
}


package org.seoyoon.backend.item;

import org.seoyoon.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Item>> saveItem(@RequestBody Item item){
        var authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        Long storeId = (Long) authentication.getDetails();
        item.setImages(null);
        item.setStoreId(storeId);
        Item save = itemRepository.save(item);
        return ResponseEntity.ok(new ApiResponse<>(true, save, save.getTitle() + "저장 완료했습니다.", 201));
    }

@GetMapping
public ResponseEntity<ApiResponse<List<Item>>> getItems(
        @RequestParam(required = false) String season) {

    var authentication = (UsernamePasswordAuthenticationToken)
            SecurityContextHolder.getContext().getAuthentication();
    Long storeId = (Long) authentication.getDetails();

    List<Item> items;

    // season 값이 없으면 전체 조회
    if (season == null || season.isEmpty()) {
        items = itemRepository.findByStoreId(storeId);
    }
    // season 값이 있으면 SeasonType으로 변환 후 필터링 조회
    else {
        try {
            SeasonType seasonType = SeasonType.valueOf(season.toUpperCase());
            items = itemRepository.findByStoreIdAndSeason(storeId, seasonType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "유효하지 않은 season 값입니다.", 400));
        }
    }

    return ResponseEntity.ok(new ApiResponse<>(true, items, "조회 완료했습니다.", 200));
}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>> getItemById(@PathVariable Long id){
        Item item = itemRepository.findById(id).get();
        if (item == null){
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, item, "item 조회를 완료했습니다.", 200));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>> updateItem(@PathVariable Long id, @RequestBody Item updates) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, "대상을 찾지 못했습니다.", 404));
        }

       Item item = optionalItem.get();
        if (updates.getTitle() != null) {
            item.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            item.setDescription(updates.getDescription());
        }
        if (updates.getCategory() != null) {
            item.setCategory(updates.getCategory());
        }
        if (updates.getCostPrice() != null) {
            item.setCostPrice(updates.getCostPrice());
        }
        if (updates.getListPrice() != null) {
            item.setListPrice(updates.getListPrice());
        }
        if (updates.getSalePrice() != null) {
            item.setSalePrice(updates.getSalePrice());
        }
        if (updates.getDiscountRateBps() != null) {
            item.setDiscountRateBps(updates.getDiscountRateBps());
        }
        if (updates.getStock() != null) {
            item.setStock(updates.getStock());
        }
        if (updates.getSeason() != null) {
            item.setSeason(updates.getSeason());
        }

        Item save = itemRepository.save(item);
        return ResponseEntity.ok(new ApiResponse<>(true, save, "업데이트 완료했습니다.", 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id){
        if(!itemRepository.existsById(id)){
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, null, "삭제할 대상을 찾지 못했습니다." , 404));
        }
        itemRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "삭제를 완료했습니다.", 200));
    }

}



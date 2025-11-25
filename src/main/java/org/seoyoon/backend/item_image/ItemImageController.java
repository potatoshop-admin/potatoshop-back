package org.seoyoon.backend.item_image;

import lombok.RequiredArgsConstructor;
import org.seoyoon.backend.common.dto.ApiResponse;
import org.seoyoon.backend.item.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itemsImage")
public class ItemImageController {

    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final S3Service s3Service;

    @GetMapping
    public List<ItemImage> getItemImage() {
        return itemImageRepository.findAll();
    }


    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = s3Service.createPresignedUrl( file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "파일 업로드 실패"));
        }
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<ApiResponse<List<ItemImage>>> uploadImages(
            @PathVariable Long itemId,
            @RequestParam("file") List<MultipartFile> files) throws IOException {

            List<ItemImage> responseImages = new ArrayList<>();
        var item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        List<ItemImage> currentImages = itemImageRepository.findByItem(item);

        int maxSort = currentImages.stream()
            .map(ItemImage::getSortOrder)
            .max(Comparator.naturalOrder())
            .orElse(-1);


        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // 1️⃣ presigned URL 생성 (프론트 PUT용)
            String presignedUrl = s3Service.createPresignedUrl(file);
            String finalUrl = presignedUrl.split("\\?")[0]; // DB 저장용 URL

            // 2️⃣ DB 저장 (정적 URL만 저장)
            ItemImage image = new ItemImage();
            image.setUrl(finalUrl);
            image.setSortOrder(i);
            image.setSortOrder(maxSort + i + 1);
            image.setItem(item);
            ItemImage saved = itemImageRepository.save(image);

            // 3️⃣ 응답용 DTO만 presigned URL로 교체
            ItemImage responseCopy = new ItemImage();
            responseCopy.setItemImageId(saved.getItemImageId());
            responseCopy.setSortOrder(saved.getSortOrder());
            responseCopy.setItem(saved.getItem());
            responseCopy.setUrl(presignedUrl); // 응답 전용 URL

            responseImages.add(responseCopy);
        }

        return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    responseImages,
                    "이미지 업로드 완료",
                    200
            )
        );
    }
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<List<ItemImage>>> deleteImages(
            @PathVariable Long itemId,
            @RequestBody List<Map<String, String>> images) {

        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        List<ItemImage> currentImages = itemImageRepository.findByItem(item);

        // 삭제할 대상 찾기
        for (ItemImage dbImage : currentImages) {
            boolean existsInRequest = images.stream()
                    .anyMatch(img -> dbImage.getUrl().equals(img.get("url")));

            if (!existsInRequest) {
                try {
                    s3Service.deleteFile(dbImage.getUrl());
                } catch (Exception e) {
                    System.err.println("S3 파일 삭제 실패: " + dbImage.getUrl());
                }
                itemImageRepository.delete(dbImage);
            }
        }

        List<ItemImage> result = itemImageRepository.findByItem(item);
        return ResponseEntity.ok(new ApiResponse<>(true, result, "이미지 수정 완료", 200));
    }
}

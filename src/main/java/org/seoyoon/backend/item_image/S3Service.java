package org.seoyoon.backend.item_image;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    // application.properies 에 있는 bucket명으로 사용해주세요 라는 뜻
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretKey;

    private final S3Presigner s3Presigner;

    public String createPresignedUrl(MultipartFile file) throws IOException {
        String uniqueFilename = "test/"+ UUID.randomUUID() + file.getOriginalFilename();

        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueFilename)
                .contentType("image/jpeg")
                .build();
        var preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();
        return s3Presigner.presignPutObject(preSignRequest).url().toString();
    }

    public void deleteFile(String url) {
        try {
            String key = url.substring(url.lastIndexOf(".com/") + 5);

            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .build();

            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key));
            System.out.println("파일 삭제 완료: " + key);

        }catch (Exception e){
            System.err.println("파일 삭제 실패:" + e.getMessage());
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }
}
package com.example.demo.jobscheduler;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }


    public InputStream getFileStream(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    // ✅ Upload file to MinIO with correct partSize
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), 5242880)  // Set partSize to 5MB
                            .contentType(file.getContentType())
                            .build()
            );
        }

        // ✅ Generate a pre-signed URL valid for 7 days (7 * 24 * 60 * 60)
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(7 * 24 * 60 * 60)  // 30 days in seconds
                        .build()
        );
    }


    public String downloadFile(String objectName) {
        try {
            Path path = Paths.get(objectName);
            String fileName = Paths.get(objectName).getFileName().toString();
            // Define local file path
            //String localPath = "C:\\Users\\askha\\Downloads\\JobSchedulerUserFiles" + objectName.substring(objectName.lastIndexOf("/") + 1);
            String localPath = "C:\\Users\\askha\\Downloads\\JobSchedulerUserFiles" + fileName;

            // Check if file already exists locally
            File file = new File(localPath);
            if (file.exists()) {
                System.out.println("File already exists locally: " + localPath);
                return localPath;
            }

            InputStream inputStream = getFileStream(objectName);
            FileOutputStream outputStream = new FileOutputStream(localPath);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            return localPath;
        } catch (Exception e) {
            System.err.println("Error downloading file: " + e.getMessage());
            return null;
        }




            // Fetch file from MinIO
            /*InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // Write to local storage
            try (FileOutputStream outputStream = new FileOutputStream(localPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            inputStream.close();

            System.out.println("File downloaded from MinIO: " + localPath);
            return localPath;

        } catch (MinioException e) {
            System.err.println("Error downloading file from MinIO: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error downloading file: " + e.getMessage());
            return null;
        }*/


            /*FileOutputStream outputStream = new FileOutputStream(localPath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded from MinIO: " + localPath);
            return localPath;

        } catch (MinioException e) {
            System.err.println("Error downloading file from MinIO: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error downloading file: " + e.getMessage());
            return null;
        }*/
    }
}

    /*// ✅ Upload file to MinIO
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        //return minioClient.getObjectUrl(bucketName, fileName); // Return file URL

        // ✅ Generate a pre-signed URL to access the uploaded file
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(30 * 24 * 60 * 60) // URL valid for 7 days
                        .build()
        );

    }

    // ✅ Download file from MinIO
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}*/


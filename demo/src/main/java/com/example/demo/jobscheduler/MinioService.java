/*
package com.example.demo.jobscheduler;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;
import java.io.*;
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
                        .expiry(7 * 24 * 60 * 60)  // 7 days in seconds
                        .build()
        );
    }


    public String downloadFile(String objectUrlOrPath) {
        try {
            System.out.println("Download request for: " + objectUrlOrPath);

            // Check if this is a JSON string containing a binary_path
            String objectName = extractObjectNameFromInput(objectUrlOrPath);
            if (objectName == null || objectName.isEmpty()) {
                System.err.println("Could not extract valid object name from input");
                return null;
            }

            // Define download location
            String downloadDir = "C:\\Users\\askha\\Downloads\\JobSchedulerUserFiles";
            String fileName = getFileNameFromPath(objectName);
            String localPath = downloadDir + File.separator + fileName;

            System.out.println("Extracted object name: " + objectName);
            System.out.println("Will download as: " + fileName);
            System.out.println("To local path: " + localPath);

            // Ensure the download directory exists
            File directory = new File(downloadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Check if file already exists locally
            File file = new File(localPath);
            if (file.exists()) {
                System.out.println("File already exists locally: " + localPath);
                return localPath;
            }

            // Get file stream from MinIO
            InputStream inputStream = null;
            try {
                inputStream = getFileStream(objectName);
                System.out.println("Successfully got input stream from MinIO");
            } catch (Exception e) {
                System.err.println("Failed to get input stream from MinIO: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

            // Write file to disk
            try (FileOutputStream outputStream = new FileOutputStream(localPath)) {
                byte[] buffer = new byte[8192]; // Increased buffer size for better performance
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                System.out.println("File download complete. Total bytes: " + totalBytesRead);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            System.out.println("File successfully saved to: " + localPath);
            return localPath;
        } catch (Exception e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    */
/*private String extractObjectNameFromInput(String input) {
        try {
            // Case 1: Input is a JSON string
            if (input.trim().startsWith("{")) {
                try {
                    // Try to parse JSON from input
                    org.springframework.boot.configurationprocessor.json.JSONObject json =
                            new org.springframework.boot.configurationprocessor.json.JSONObject(input);

                    // Check for binary_path in JSON
                    if (json.has("binary_path")) {
                        String binaryPath = json.getString("binary_path");
                        System.out.println("Found binary_path in JSON: " + binaryPath);
                        return extractObjectNameFromUrl(binaryPath);
                    }
                } catch (Exception jsonEx) {
                    System.err.println("Failed to parse JSON: " + jsonEx.getMessage());
                    // Continue to other methods if JSON parsing fails
                }
            }

            // Case 2: Input is a presigned URL
            if (input.startsWith("http://") || input.startsWith("https://")) {
                return extractObjectNameFromUrl(input);
            }

            // Case 3: Input is already an object name/path
            return input;
        } catch (Exception e) {
            System.err.println("Error extracting object name: " + e.getMessage());
            return null;
        }
    }*//*


    private String extractObjectNameFromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.err.println("Input is null or empty");
            return null;
        }

        try {
            System.out.println("Extracting object name from: " + input);

            // Case 1: Input is a JSON string with binary_path
            if (input.contains("binary_path")) {
                try {
                    // Try to parse JSON
                    org.springframework.boot.configurationprocessor.json.JSONObject json =
                            new org.springframework.boot.configurationprocessor.json.JSONObject(input);

                    if (json.has("binary_path")) {
                        String binaryPath = json.getString("binary_path");
                        System.out.println("Found binary_path in JSON: " + binaryPath);
                        return extractObjectNameFromUrl(binaryPath);
                    }
                } catch (Exception jsonEx) {
                    System.err.println("Failed to parse JSON normally: " + jsonEx.getMessage());

                    // Fallback: Try to extract URL directly
                    int urlStart = input.indexOf("http://");
                    if (urlStart == -1) {
                        urlStart = input.indexOf("https://");
                    }

                    if (urlStart != -1) {
                        // Find the end of the URL (look for quote, space, or end of string)
                        int urlEnd = input.indexOf("\"", urlStart);
                        if (urlEnd == -1) {
                            urlEnd = input.indexOf(" ", urlStart);
                        }
                        if (urlEnd == -1) {
                            urlEnd = input.length();
                        }

                        String url = input.substring(urlStart, urlEnd);
                        System.out.println("Extracted URL directly: " + url);
                        return extractObjectNameFromUrl(url);
                    }
                }
            }

            // Case 2: Input is a presigned URL
            if (input.startsWith("http://") || input.startsWith("https://")) {
                return extractObjectNameFromUrl(input);
            }

            // Case 3: Input appears to be a direct object name
            if (!input.contains("://") && !input.startsWith("{")) {
                System.out.println("Using input as direct object name: " + input);
                return input;
            }

            System.err.println("Could not determine how to extract object name from: " + input);
            return null;

        } catch (Exception e) {
            System.err.println("Error extracting object name: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String extractObjectNameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();

            // Remove /bucket-name/ from the beginning
            if (path.startsWith("/" + bucketName + "/")) {
                path = path.substring(("/" + bucketName + "/").length());
            }

            System.out.println("Extracted object name from URL: " + path);
            return path;
        } catch (Exception e) {
            System.err.println("Error parsing URL: " + e.getMessage());
            return null;
        }
    }

    private String getFileNameFromPath(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < path.length() - 1) {
            return path.substring(lastSlash + 1);
        }
        return path;
    }
}

*/


package com.example.demo.jobscheduler;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;
import java.io.*;
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
        System.out.println("Getting file stream for object: " + objectName);
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
                        .expiry(7 * 24 * 60 * 60)  // 7 days in seconds
                        .build()
        );
    }


    public String downloadFile(String objectUrlOrPath) {
        try {
            System.out.println("Download request for: " + objectUrlOrPath);
            if (objectUrlOrPath == null || objectUrlOrPath.trim().isEmpty()) {
                System.err.println("Empty or null object path provided");
                return null;
            }

            // Extract the object name - improved handling
            String objectName = extractObjectNameFromInput(objectUrlOrPath);
            if (objectName == null || objectName.isEmpty()) {
                System.err.println("Could not extract valid object name from input: " + objectUrlOrPath);
                return null;
            }

            // Define download location
            String downloadDir = "C:\\Users\\askha\\Downloads\\JobSchedulerUserFiles";
            String fileName = getFileNameFromPath(objectName);
            String localPath = downloadDir + File.separator + fileName;

            System.out.println("Extracted object name: " + objectName);
            System.out.println("Will download as: " + fileName);
            System.out.println("To local path: " + localPath);

            // Ensure the download directory exists
            File directory = new File(downloadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Check if file already exists locally
            File file = new File(localPath);
            if (file.exists()) {
                System.out.println("File already exists locally: " + localPath);
                return localPath;
            }

            // Get file stream from MinIO
            InputStream inputStream = null;
            try {
                inputStream = getFileStream(objectName);
                System.out.println("Successfully got input stream from MinIO");
            } catch (Exception e) {
                System.err.println("Failed to get input stream from MinIO: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

            // Write file to disk
            try (FileOutputStream outputStream = new FileOutputStream(localPath)) {
                byte[] buffer = new byte[8192]; // Increased buffer size for better performance
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                System.out.println("File download complete. Total bytes: " + totalBytesRead);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            System.out.println("File successfully saved to: " + localPath);
            return localPath;
        } catch (Exception e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String extractObjectNameFromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.err.println("Input is null or empty");
            return null;
        }

        try {
            // Case 1: Input is a JSON string
            if (input.trim().startsWith("{")) {
                try {
                    // Try to parse JSON from input
                    org.springframework.boot.configurationprocessor.json.JSONObject json =
                            new org.springframework.boot.configurationprocessor.json.JSONObject(input);

                    // Check for binary_path in JSON
                    if (json.has("binary_path")) {
                        String binaryPath = json.getString("binary_path");
                        System.out.println("Found binary_path in JSON: " + binaryPath);
                        return extractObjectNameFromUrl(binaryPath);
                    } else if (json.has("binaryPath")) {
                        // Also check for "binaryPath" key (camelCase)
                        String binaryPath = json.getString("binaryPath");
                        System.out.println("Found binaryPath in JSON: " + binaryPath);
                        return extractObjectNameFromUrl(binaryPath);
                    }
                } catch (Exception jsonEx) {
                    System.err.println("Failed to parse JSON: " + jsonEx.getMessage());
                    // Continue to other methods if JSON parsing fails
                }
            }

            // Case 2: Input is a presigned URL
            if (input.startsWith("http://") || input.startsWith("https://")) {
                return extractObjectNameFromUrl(input);
            }

            // Case 3: Input is already an object name/path
            // Clean up the input by removing any extra quotes or whitespace
            String cleanedInput = input.trim();
            if (cleanedInput.startsWith("\"") && cleanedInput.endsWith("\"")) {
                cleanedInput = cleanedInput.substring(1, cleanedInput.length() - 1);
            }
            System.out.println("Using direct object name: " + cleanedInput);
            return cleanedInput;
        } catch (Exception e) {
            System.err.println("Error extracting object name: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String extractObjectNameFromUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            System.err.println("URL string is null or empty");
            return null;
        }

        try {
            URL url = new URL(urlString);
            String path = url.getPath();

            // Remove /bucket-name/ from the beginning
            if (path.startsWith("/" + bucketName + "/")) {
                path = path.substring(("/" + bucketName + "/").length());
            }

            System.out.println("Extracted object name from URL: " + path);
            return path;
        } catch (Exception e) {
            System.err.println("Error parsing URL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String getFileNameFromPath(String path) {
        if (path == null) {
            return null;
        }
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < path.length() - 1) {
            return path.substring(lastSlash + 1);
        }
        return path;
    }
}

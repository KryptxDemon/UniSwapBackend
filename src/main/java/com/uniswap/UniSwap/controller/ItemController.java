// src/main/java/com/uniswap/UniSwap/controller/ItemController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.service.ItemService;
import com.uniswap.UniSwap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    private String uploadImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new IOException("Invalid file type. Only JPG, PNG, GIF, and WebP are allowed");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String fileExtension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Return just the filename (not the full URL)
        return uniqueFilename;
    }

    private boolean isValidImageFile(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        for (String extension : ALLOWED_EXTENSIONS) {
            if (lowerCaseFilename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex != -1) ? filename.substring(lastDotIndex) : "";
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Item>> getAvailableItems() {
        return ResponseEntity.ok(itemService.getItemsByStatus("available"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Integer id) {
        return itemService.getItemById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Item>> getItemsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(itemService.getItemsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Map<String, Object> itemData, Authentication auth) {
        String email = (auth != null) ? auth.getName() : null;
        
        User currentUser = null;
        
        // For testing: if no authentication, use a default user
        if (email == null) {
            try {
                currentUser = userService.getUserByEmail("final1@student.cuet.ac.bd")
                        .orElse(null);
                if (currentUser == null) {
                    System.out.println("No authentication and no test user found - creating test user");
                    // Create a test user
                    User testUser = new User();
                    testUser.setUsername("TestUser");
                    testUser.setEmail("final1@student.cuet.ac.bd");
                    testUser.setPassword("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi."); // "password"
                    testUser.setStudentId("1234567890");
                    testUser.setBio("Test user for development");
                    currentUser = userService.createUser(testUser);
                    System.out.println("Created test user: " + currentUser.getEmail());
                }
            } catch (Exception e) {
                System.out.println("Error finding/creating test user: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                currentUser = userService.getUserByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            Item item = new Item();
            item.setItemName((String) itemData.get("itemName"));
            item.setDescription((String) itemData.get("description"));
            item.setItemType((String) itemData.get("itemType"));
            item.setItemCondition((String) itemData.get("itemCondition"));
            item.setStatus((String) itemData.get("status"));
            item.setPhone((String) itemData.get("phone"));
            item.setSwapWith((String) itemData.get("swapWith"));
            item.setDepartment((String) itemData.get("department"));
            
            // Set simple category and location as strings
            item.setCategory((String) itemData.get("category"));
            item.setLocation((String) itemData.get("location"));

            String postDateStr = (String) itemData.get("postDate");
            if (postDateStr != null) {
                item.setPostDate(LocalDate.parse(postDateStr));
            }

            // Handle image data
            String imageData = (String) itemData.get("imageData");
            if (imageData != null) {
                try {
                    // URL decode if necessary
                    imageData = java.net.URLDecoder.decode(imageData, "UTF-8");
                } catch (Exception e) {
                    System.out.println("Image data decode failed, using original: " + e.getMessage());
                }
                item.setImageData(imageData);
            }

            item.setUser(currentUser);

            Item savedItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
            
        } catch (Exception e) {
            System.err.println("Error creating item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/multipart", consumes = "multipart/form-data")
    public ResponseEntity<Item> createItemMultipart(
            @RequestParam("itemName") String itemName,
            @RequestParam("description") String description,
            @RequestParam("itemType") String itemType,
            @RequestParam("itemCondition") String itemCondition,
            @RequestParam("status") String status,
            @RequestParam("phone") String phone,
            @RequestParam(value = "swapWith", required = false) String swapWith,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication auth) {

        String email = (auth != null) ? auth.getName() : null;
        User currentUser = null;

        // For testing: if no authentication, use a default user
        if (email == null) {
            try {
                currentUser = userService.getUserByEmail("final1@student.cuet.ac.bd").orElse(null);
            } catch (Exception e) {
                System.out.println("Error finding test user: " + e.getMessage());
            }
        } else {
            try {
                currentUser = userService.getUserByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            Item item = new Item();
            item.setItemName(itemName);
            item.setDescription(description);
            item.setItemType(itemType);
            item.setItemCondition(itemCondition);
            item.setStatus(status);
            item.setPhone(phone);
            item.setSwapWith(swapWith);
            item.setDepartment(department);
            item.setCategory(category);
            item.setLocation(location);
            item.setUser(currentUser);

            // Handle image upload
            if (image != null && !image.isEmpty()) {
                try {
                    String fileUrl = uploadImageFile(image);
                    item.setImageData(fileUrl);
                } catch (Exception e) {
                    System.err.println("Error processing image: " + e.getMessage());
                }
            }

            Item savedItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
            
        } catch (Exception e) {
            System.err.println("Error creating item with multipart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Integer id, @RequestBody Item item, Authentication auth) {
        try {
            return itemService.getItemById(id).map(existingItem -> {
                item.setItemId(id);
                return ResponseEntity.ok(itemService.updateItem(id, item));
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id, Authentication auth) {
        try {
            if (itemService.getItemById(id).isPresent()) {
                itemService.deleteItem(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/exchange")
    public ResponseEntity<Item> markAsExchanged(@PathVariable Integer id, Authentication auth) {
        try {
            Item updatedItem = itemService.markAsExchanged(id);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getItemImage(@PathVariable Integer id) {
        try {
            // Get the item by ID
            Item item = itemService.getItemById(id).orElse(null);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }

            // Get the image filename from the item
            String imageFilename = item.getImageData();
            if (imageFilename == null || imageFilename.trim().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Construct the file path
            Path filePath = Paths.get(uploadDir).resolve(imageFilename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Read the file content
            byte[] fileContent = Files.readAllBytes(filePath);
            
            // Determine content type based on file extension
            String contentType = getContentType(imageFilename);
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Cache-Control", "max-age=3600") // Cache for 1 hour
                    .body(fileContent);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method to determine content type
    private String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
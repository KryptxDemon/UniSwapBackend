// src/main/java/com/uniswap/UniSwap/controller/ItemController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.entity.Post;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.entity.Category;
import com.uniswap.UniSwap.entity.Location;
import com.uniswap.UniSwap.service.ItemService;
import com.uniswap.UniSwap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired private ItemService itemService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Integer id) {
        return itemService.getItemById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Item>> getItemsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(itemService.getItemsByUserId(userId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Item>> getItemsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(itemService.getItemsByCategory(categoryId));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Map<String, Object> itemData, Authentication auth) {
        String email = (auth != null) ? auth.getName() : null;
        
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser;
        try {
            currentUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

            String postDateStr = (String) itemData.get("postDate");
            if (postDateStr != null) {
                item.setPostDate(LocalDate.parse(postDateStr));
            }

            Object categoryIdObj = itemData.get("categoryId");
            if (categoryIdObj instanceof Number n) {
                Category category = new Category();
                category.setCategoryId(n.intValue());
                item.setCategory(category);
            }

            Object locationIdObj = itemData.get("locationId");
            if (locationIdObj instanceof Number n) {
                Location location = new Location();
                location.setLocationId(n.intValue());
                item.setLocation(location);
            }

            Post post = new Post();
            Object postObj = itemData.get("post");
            if (postObj instanceof Map<?, ?> postData) {
                post.setImageUrls((String) postData.get("imageUrls"));
                String postTimeStr = (String) postData.get("postTime");
                if (postTimeStr != null) {
                    try { 
                        post.setPostTime(OffsetDateTime.parse(postTimeStr).toLocalDateTime()); 
                    } catch (Exception ignored) { 
                        post.setPostTime(LocalDateTime.parse(postTimeStr.replace("Z",""))); 
                    }
                }
            }
            post.setUser(currentUser);
            item.setPost(post);

            Item savedItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (Exception e) {
            System.err.println("Error creating item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Integer id, @RequestBody Item itemDetails) {
        return ResponseEntity.ok(itemService.updateItem(id, itemDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/exchange")
    public ResponseEntity<Item> markAsExchanged(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.markAsExchanged(id));
    }
}

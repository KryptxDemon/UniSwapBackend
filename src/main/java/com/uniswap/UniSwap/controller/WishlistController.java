// src/main/java/com/uniswap/UniSwap/controller/WishlistController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.Wishlist;
import com.uniswap.UniSwap.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wishlist>> getUserWishlists(@PathVariable Integer userId) {
        return ResponseEntity.ok(wishlistService.getWishlistsByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<Wishlist> addToWishlist(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        Integer itemId = (Integer) request.get("itemId");
        String notes = (String) request.get("notes");

        try {
            Wishlist wishlist = wishlistService.addItemToWishlist(userId, itemId, notes);
            return ResponseEntity.ok(wishlist);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userId");
        Integer itemId = request.get("itemId");

        wishlistService.removeItemFromWishlist(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{wishlistId}/notes")
    public ResponseEntity<Wishlist> updateWishlistNotes(
            @PathVariable Integer wishlistId,
            @RequestBody Map<String, String> request) {
        String notes = request.get("notes");
        Wishlist updated = wishlistService.updateWishlistNotes(wishlistId, notes);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/check/{userId}/{itemId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlistStatus(
            @PathVariable Integer userId,
            @PathVariable Integer itemId) {
        boolean isInWishlist = wishlistService.isItemInUserWishlist(userId, itemId);
        return ResponseEntity.ok(Map.of("isInWishlist", isInWishlist));
    }
}

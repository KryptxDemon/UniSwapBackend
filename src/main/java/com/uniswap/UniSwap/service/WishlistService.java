package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.entity.Wishlist;
import com.uniswap.UniSwap.repository.ItemRepository;
import com.uniswap.UniSwap.repository.UserRepository;
import com.uniswap.UniSwap.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    
    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<Wishlist> getWishlistsByUserId(Integer userId) {
        return wishlistRepository.findByUserUserId(userId);
    }

    @Transactional
    public Wishlist addItemToWishlist(Integer userId, Integer itemId, String notes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        // Check if user already has a wishlist for this item
        List<Wishlist> userWishlists = wishlistRepository.findByUserUserId(userId);
        for (Wishlist wishlist : userWishlists) {
            if (wishlist.getItems().contains(item)) {
                throw new RuntimeException("Item already in wishlist");
            }
        }

        // Create new wishlist entry
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setCreatedDate(LocalDate.now());
        wishlist.setNotes(notes);
        wishlist.getItems().add(item);
        
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeItemFromWishlist(Integer userId, Integer itemId) {
        List<Wishlist> userWishlists = wishlistRepository.findByUserUserId(userId);
        
        for (Wishlist wishlist : userWishlists) {
            wishlist.getItems().removeIf(item -> item.getItemId().equals(itemId));
            if (wishlist.getItems().isEmpty()) {
                wishlistRepository.delete(wishlist);
            } else {
                wishlistRepository.save(wishlist);
            }
        }
    }

    @Transactional
    public Wishlist updateWishlistNotes(Integer wishlistId, String notes) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
            .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + wishlistId));
        
        wishlist.setNotes(notes);
        return wishlistRepository.save(wishlist);
    }

    @Transactional(readOnly = true)
    public boolean isItemInUserWishlist(Integer userId, Integer itemId) {
        List<Wishlist> userWishlists = wishlistRepository.findByUserUserId(userId);
        
        for (Wishlist wishlist : userWishlists) {
            if (wishlist.getItems().stream().anyMatch(item -> item.getItemId().equals(itemId))) {
                return true;
            }
        }
        return false;
    }
}

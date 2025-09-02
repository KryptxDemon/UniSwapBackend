package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUserUserId(Integer userId);
    
    // Added method to find wishlists containing a specific item
    List<Wishlist> findByItemsItemId(Integer itemId);
}
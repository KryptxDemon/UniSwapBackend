package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByCategoryCategoryId(Integer categoryId);
    List<Item> findByLocationLocationId(Integer locationId);
    List<Item> findByPostPostId(Integer postId);
    List<Item> findByItemType(String itemType);
    List<Item> findByStatus(String status);
}
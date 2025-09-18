package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByCategory(String category);
    List<Item> findByLocation(String location);
    List<Item> findByItemType(String itemType);
    List<Item> findByStatus(String status);
    List<Item> findByUserUserId(Integer userId);
    List<Item> findByDepartment(String department);
}
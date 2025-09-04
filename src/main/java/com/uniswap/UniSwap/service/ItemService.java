package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Item> getItemById(Integer id) {
        return itemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByUserId(Integer userId) {
        return itemRepository.findByPostUserUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByCategory(Integer categoryId) {
        return itemRepository.findByCategoryCategoryId(categoryId);
    }

    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Integer id, Item itemDetails) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        item.setItemName(itemDetails.getItemName());
        item.setDescription(itemDetails.getDescription());
        item.setItemCondition(itemDetails.getItemCondition());
        item.setItemType(itemDetails.getItemType());
        item.setCategory(itemDetails.getCategory());
        item.setLocation(itemDetails.getLocation());
        item.setStatus(itemDetails.getStatus());
        
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Integer id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        itemRepository.delete(item);
    }

    @Transactional
    public Item markAsExchanged(Integer id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        item.setStatus("exchanged");
        return itemRepository.save(item);
    }
}

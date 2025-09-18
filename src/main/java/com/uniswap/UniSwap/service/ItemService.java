package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.repository.ItemRepository;
import com.uniswap.UniSwap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;

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
        return itemRepository.findByUserUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByLocation(String location) {
        return itemRepository.findByLocation(location);
    }

    @Transactional
    public Item createItem(Item item) {
        // Ensure user relationship is properly set
        if (item.getUser() != null && item.getUser().getUserId() != null) {
            User user = userRepository.findById(item.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + item.getUser().getUserId()));
            item.setUser(user);
        }
        
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
        item.setPhone(itemDetails.getPhone());
        item.setSwapWith(itemDetails.getSwapWith());
        item.setDepartment(itemDetails.getDepartment());
        
        if (itemDetails.getImageData() != null) {
            item.setImageData(itemDetails.getImageData());
        }
        
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

    public List<Item> getItemsByStatus(String status) {
        return itemRepository.findByStatus(status);
    }
}

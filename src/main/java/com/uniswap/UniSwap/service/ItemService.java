package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.entity.Post;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.repository.ItemRepository;
import com.uniswap.UniSwap.repository.PostRepository;
import com.uniswap.UniSwap.repository.UserRepository;
import com.uniswap.UniSwap.repository.CategoryRepository;
import com.uniswap.UniSwap.repository.LocationRepository;
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
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private LocationRepository locationRepository;

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
        // Set up Category and Location relationships if IDs are provided
        if (item.getCategory() != null && item.getCategory().getCategoryId() != null) {
            item.setCategory(categoryRepository.findById(item.getCategory().getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + item.getCategory().getCategoryId())));
        }
        
        if (item.getLocation() != null && item.getLocation().getLocationId() != null) {
            item.setLocation(locationRepository.findById(item.getLocation().getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + item.getLocation().getLocationId())));
        }
        
        // If item has a post, ensure the post is saved first with proper user relationship
        if (item.getPost() != null) {
            Post post = item.getPost();
            // Set the user relationship if user is provided
            if (post.getUser() != null && post.getUser().getUserId() != null) {
                User user = userRepository.findById(post.getUser().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + post.getUser().getUserId()));
                post.setUser(user);
            }
            // Save the post first to get the postId
            Post savedPost = postRepository.save(post);
            item.setPost(savedPost);
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

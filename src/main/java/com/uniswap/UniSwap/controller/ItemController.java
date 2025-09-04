package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Integer id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        return ResponseEntity.ok(itemService.createItem(item));
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

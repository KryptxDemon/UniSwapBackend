package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUserUserId(Integer userId);
    Page<Post> findByUserUserId(Integer userId, Pageable pageable);
}
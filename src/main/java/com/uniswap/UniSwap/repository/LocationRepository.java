package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByLocationType(String locationType);
    List<Location> findByAreaName(String areaName);
    List<Location> findByBuilding(String building);
}
// src/main/java/com/example/demo/repository/MenuRepository.java
package com.example.demo.repository;

import com.example.demo.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> { }

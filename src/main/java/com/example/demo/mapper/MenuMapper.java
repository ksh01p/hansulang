// src/main/java/com/example/demo/mapper/MenuMapper.java
package com.example.demo.mapper;

import com.example.demo.dto.MenuDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper {
    /**
     * 정렬 기준(params) 에 따라 menu_items 테이블에서
     * 모든 컬럼을 쿼리하여 MenuDto 로 매핑해 반환한다.
     *
     * @param params 정렬 정보 {"sortBy": "price"|"reviewCount"|"avgScore"|…, "direction": "ASC"/"DESC"}
     * @return MenuDto 리스트
     */
    List<MenuDto> findAllMenus(@Param("params") Map<String, Object> params);
}

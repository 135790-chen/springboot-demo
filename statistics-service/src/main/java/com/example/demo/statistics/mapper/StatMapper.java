package com.example.demo.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.StatSnapshot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatMapper extends BaseMapper<StatSnapshot> {
}

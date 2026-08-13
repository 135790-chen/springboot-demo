package com.example.demo.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.TimeSlot;
import com.example.demo.teaching.mapper.TimeSlotMapper;
import com.example.demo.teaching.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Override
    public List<TimeSlot> listAll() {
        LambdaQueryWrapper<TimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TimeSlot::getSlotStatus, 1)
               .orderByAsc(TimeSlot::getDayOfWeek, TimeSlot::getStartPeriod);
        return timeSlotMapper.selectList(wrapper);
    }
}

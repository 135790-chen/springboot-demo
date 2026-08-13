package com.example.demo.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.StatSnapshot;
import com.example.demo.statistics.mapper.StatMapper;
import com.example.demo.statistics.service.StatService;
import com.example.demo.vo.StatSnapshotVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private StatMapper statMapper;

    @Override
    public List<StatSnapshotVO> getRecent30() {
        LambdaQueryWrapper<StatSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(StatSnapshot::getStatDate)
               .last("LIMIT 30");
        List<StatSnapshot> snapshots = statMapper.selectList(wrapper);
        return snapshots.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public StatSnapshotVO getLatest() {
        LambdaQueryWrapper<StatSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(StatSnapshot::getStatDate)
               .last("LIMIT 1");
        StatSnapshot snapshot = statMapper.selectOne(wrapper);
        if (snapshot == null) {
            return null;
        }
        return toVO(snapshot);
    }

    @Override
    public StatSnapshotVO save(StatSnapshot snapshot) {
        statMapper.insert(snapshot);
        return toVO(snapshot);
    }

    private StatSnapshotVO toVO(StatSnapshot entity) {
        StatSnapshotVO vo = new StatSnapshotVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

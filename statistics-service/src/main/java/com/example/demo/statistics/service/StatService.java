package com.example.demo.statistics.service;

import com.example.demo.entity.StatSnapshot;
import com.example.demo.vo.StatSnapshotVO;

import java.util.List;

/**
 * 统计快照业务接口
 */
public interface StatService {

    List<StatSnapshotVO> getRecent30();

    StatSnapshotVO getLatest();

    /**
     * 保存快照并返回 VO
     */
    StatSnapshotVO save(StatSnapshot snapshot);
}

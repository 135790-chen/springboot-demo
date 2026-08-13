package com.example.demo.organization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Major;
import com.example.demo.vo.MajorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MajorMapper extends BaseMapper<Major> {

    Page<MajorVO> selectMajorVOPage(Page<MajorVO> page,
                                     @Param("majorName") String majorName,
                                     @Param("majorCode") String majorCode,
                                     @Param("collegeId") Long collegeId,
                                     @Param("majorStatus") Integer majorStatus);

    MajorVO selectMajorVOById(@Param("majorId") Long majorId);
}

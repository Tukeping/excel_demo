package com.tukeping.service;

import com.tukeping.entity.StationApproval;
import com.tukeping.repository.StationApprovalRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author tukeping
 * @date 2019/11/29
 **/
@Service
public class StationApprovalService {

    @Resource
    private StationApprovalRepository stationApprovalRepo;

    public List<StationApproval> findAll() {
        return stationApprovalRepo.findAll();
    }
}

package com.tukeping.repository;

import com.tukeping.entity.DutyFeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
public interface DutyFeeRecordRepository extends JpaRepository<DutyFeeRecord, Integer> {
}

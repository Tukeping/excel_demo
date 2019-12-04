package com.tukeping.dto;

import com.tukeping.entity.DutyFeeRecord;
import lombok.Data;

import java.util.List;

/**
 * @author tukeping
 * @date 2019/12/4
 **/
@Data
public class DutyFeeDTO {

    private DutyFeeRecord record;
    private List<DutyFeeDetailDTO> dutyFeeDetail;
}

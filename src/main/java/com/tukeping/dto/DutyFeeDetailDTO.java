package com.tukeping.dto;

import com.tukeping.entity.DutyFeeDate;
import com.tukeping.entity.DutyFeeDetail;
import lombok.Data;

import java.util.List;

/**
 * @author tukeping
 * @date 2019/12/4
 **/
@Data
public class DutyFeeDetailDTO {
    private DutyFeeDetail dutyFeeDetail;
    private List<DutyFeeDate> dutyFeeDateList;
}

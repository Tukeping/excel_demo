package com.tukeping.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@UtilityClass
public class BeanUtil {

    public <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }
}

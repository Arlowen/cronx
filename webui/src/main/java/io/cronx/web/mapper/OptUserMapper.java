package io.cronx.web.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.cronx.web.model.entity.OptUserDO;

public interface OptUserMapper extends BaseMapper<OptUserDO> {

    OptUserDO queryByAccountName(String accountName);

    void updateLoginLimitInfo(@Param("lastTryLoginTime") Date lastTryLoginTime, @Param("loginFailCount") int loginFailCount, @Param("loginLocked") boolean loginLocked,
                              @Param("id") Long id);

}

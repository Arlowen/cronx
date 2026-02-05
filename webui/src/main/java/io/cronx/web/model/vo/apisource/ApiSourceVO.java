package io.cronx.web.model.vo.apisource;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.cronx.web.model.entity.ApiSourceDO;
import lombok.Data;

import java.util.Date;

@Data
public class ApiSourceVO {

    private long   id;

    private String host;

    public void convertVO(ApiSourceDO sourceDO) {
        this.id = sourceDO.getId();
        this.host = sourceDO.getHost();
    }
}

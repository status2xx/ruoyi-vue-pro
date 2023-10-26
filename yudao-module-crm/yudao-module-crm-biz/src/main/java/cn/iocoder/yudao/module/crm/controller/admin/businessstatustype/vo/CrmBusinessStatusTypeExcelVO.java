package cn.iocoder.yudao.module.crm.controller.admin.businessstatustype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 商机状态类型 Excel VO
 *
 * @author ljlleo
 */
@Data
public class CrmBusinessStatusTypeExcelVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("状态类型名")
    private String name;

    @ExcelProperty("使用的部门编号")
    private String deptIds;

    @ExcelProperty("开启状态")
    private Boolean status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}

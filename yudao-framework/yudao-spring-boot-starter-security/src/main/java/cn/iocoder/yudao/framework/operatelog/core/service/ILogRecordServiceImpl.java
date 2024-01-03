package cn.iocoder.yudao.framework.operatelog.core.service;

import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.logger.OperateLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogV2CreateReqDTO;
import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

// TODO @puhui999：LogRecordServiceImpl 改成这个名字哈
/**
 * 操作日志 ILogRecordService 实现类
 *
 * 基于 {@link OperateLogApi} 实现，记录操作日志
 *
 * @author HUIHUI
 */
@Slf4j
public class ILogRecordServiceImpl implements ILogRecordService {

    @Resource
    private OperateLogApi operateLogApi;

    @Override
    public void record(LogRecord logRecord) {
        // 1. 补全通用字段
        OperateLogV2CreateReqDTO reqDTO = new OperateLogV2CreateReqDTO();
        reqDTO.setTraceId(TracerUtils.getTraceId());
        // 补充用户信息
        fillUserFields(reqDTO);
        // 补全模块信息
        fillModuleFields(reqDTO, logRecord);
        // 补全请求信息
        fillRequestFields(reqDTO);

        // 2. 异步记录日志
        operateLogApi.createOperateLogV2(reqDTO);
        // TODO 测试结束删除或搞个开关
        log.info("操作日志 ===> {}", reqDTO);
    }

    private static void fillUserFields(OperateLogV2CreateReqDTO reqDTO) {
        // TODO @puhui999：使用 SecurityFrameworkUtils。因为要考虑，rpc、mq、job，它其实不是 web；
        reqDTO.setUserId(WebFrameworkUtils.getLoginUserId());
        reqDTO.setUserType(WebFrameworkUtils.getLoginUserType());
    }

    public static void fillModuleFields(OperateLogV2CreateReqDTO reqDTO, LogRecord logRecord) {
        reqDTO.setType(logRecord.getType()); // 大模块类型，例如：CRM 客户
        reqDTO.setSubType(logRecord.getSubType());// 操作名称，例如：转移客户
        reqDTO.setBizId(Long.parseLong(logRecord.getBizNo())); // 业务编号，例如：客户编号
        reqDTO.setAction(logRecord.getAction());// 操作内容，例如：修改编号为 1 的用户信息，将性别从男改成女，将姓名从芋道改成源码。
        reqDTO.setExtra(logRecord.getExtra()); // 拓展字段，有些复杂的业务，需要记录一些字段 ( JSON 格式 )，例如说，记录订单编号，{ orderId: "1"}
    }

    private static void fillRequestFields(OperateLogV2CreateReqDTO reqDTO) {
        // 获得 Request 对象
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // 补全请求信息
        reqDTO.setRequestMethod(request.getMethod());
        reqDTO.setRequestUrl(request.getRequestURI());
        reqDTO.setUserIp(ServletUtils.getClientIP(request));
        reqDTO.setUserAgent(ServletUtils.getUserAgent(request));
    }

    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

}
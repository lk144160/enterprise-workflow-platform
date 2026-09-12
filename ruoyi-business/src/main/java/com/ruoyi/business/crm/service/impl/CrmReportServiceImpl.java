package com.ruoyi.business.crm.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.crm.domain.CrmCustomer;
import com.ruoyi.business.crm.domain.CrmFollowRecord;
import com.ruoyi.business.crm.domain.CrmReportQuery;
import com.ruoyi.business.crm.mapper.CrmReportMapper;
import com.ruoyi.business.crm.service.ICrmReportService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 客情统计服务实现
 *
 * @author renovationops
 */
@Service
public class CrmReportServiceImpl implements ICrmReportService
{
    @Autowired
    private CrmReportMapper reportMapper;

    @Override
    public Map<String, Object> statistics(CrmReportQuery query)
    {
        checkTimeRange(query);
        Map<String, Object> data = new HashMap<>();
        // 汇总
        Map<String, Object> summary = new HashMap<>();
        summary.put("newCustomers", reportMapper.countNewCustomers(query));
        summary.put("followCount", reportMapper.countFollows(query));
        summary.put("visitCount", reportMapper.countVisits(query));
        summary.put("quoteCount", reportMapper.countQuotes(query));
        summary.put("signCount", reportMapper.countContracts(query));
        summary.put("signAmount", reportMapper.sumContractAmount(query));
        data.put("summary", summary);
        // 明细
        data.put("customers", reportMapper.selectCustomerDetails(query));
        data.put("follows", reportMapper.selectFollowDetails(query));
        data.put("contracts", reportMapper.selectContractDetails(query));
        return data;
    }

    @Override
    public void exportExcel(HttpServletResponse response, CrmReportQuery query)
    {
        checkTimeRange(query);
        List<CrmCustomer> customers = reportMapper.selectCustomerDetails(query);
        List<CrmFollowRecord> follows = reportMapper.selectFollowDetails(query);
        List<BizContract> contracts = reportMapper.selectContractDetails(query);

        try (XSSFWorkbook workbook = new XSSFWorkbook())
        {
            CellStyle headStyle = headStyle(workbook);

            // Sheet1 客户明细
            List<String[]> customerRows = new ArrayList<>();
            for (CrmCustomer c : customers)
            {
                customerRows.add(new String[] {
                    nvl(c.getCustomerName()), nvl(c.getPhone()), nvl(c.getEstate()), nvl(c.getHouseType()),
                    dictLabel("crm_customer_source", c.getSource()),
                    dictLabel("crm_intention_level", c.getIntentionLevel()),
                    dictLabel("crm_customer_status", c.getStatus()),
                    nvl(c.getOwnerName()),
                    DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", c.getCreateTime())
                });
            }
            createSheet(workbook, headStyle, "客户明细",
                    new String[] { "客户姓名", "手机号", "楼盘", "户型", "来源", "意向等级", "状态", "业务员", "创建时间" },
                    customerRows);

            // Sheet2 跟进明细
            List<String[]> followRows = new ArrayList<>();
            for (CrmFollowRecord r : follows)
            {
                followRows.add(new String[] {
                    nvl(r.getCustomerName()), nvl(r.getPhone()), nvl(r.getCustomerEstate()),
                    dictLabel("crm_follow_type", r.getFollowType()),
                    nvl(r.getContent()), nvl(r.getCreateByName()),
                    DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", r.getFollowTime())
                });
            }
            createSheet(workbook, headStyle, "跟进明细",
                    new String[] { "客户姓名", "手机号", "楼盘", "跟进方式", "跟进内容", "跟进人", "跟进时间" },
                    followRows);

            // Sheet3 签约明细
            List<String[]> contractRows = new ArrayList<>();
            for (BizContract t : contracts)
            {
                contractRows.add(new String[] {
                    nvl(t.getContractNo()), nvl(t.getCustomerName()), nvl(t.getCustomerPhone()), nvl(t.getEstate()),
                    money(t.getContractAmount()), money(t.getPaidAmount()),
                    DateUtils.parseDateToStr("yyyy-MM-dd", t.getSignDate()),
                    nvl(t.getOwnerUserName())
                });
            }
            createSheet(workbook, headStyle, "签约明细",
                    new String[] { "合同号", "客户姓名", "手机号", "楼盘", "合同金额", "已收金额", "签约日期", "负责人" },
                    contractRows);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("客情统计_" + query.getBeginTime() + "_" + query.getEndTime(), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            workbook.write(response.getOutputStream());
        }
        catch (IOException e)
        {
            throw new ServiceException("导出客情统计失败");
        }
    }

    /** 校验并填充默认时间范围（默认本月） */
    private void checkTimeRange(CrmReportQuery query)
    {
        if (StringUtils.isEmpty(query.getBeginTime()) || StringUtils.isEmpty(query.getEndTime()))
        {
            query.setBeginTime(DateUtils.parseDateToStr("yyyy-MM-01", new java.util.Date()));
            query.setEndTime(DateUtils.getDate());
        }
    }

    /** 创建单个 Sheet：表头 + 数据行 */
    private void createSheet(XSSFWorkbook workbook, CellStyle headStyle, String sheetName, String[] headers, List<String[]> rows)
    {
        Sheet sheet = workbook.createSheet(sheetName);
        Row head = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++)
        {
            Cell cell = head.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headStyle);
            sheet.setColumnWidth(i, 16 * 256);
        }
        int rowIndex = 1;
        for (String[] row : rows)
        {
            Row dataRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < row.length; i++)
            {
                dataRow.createCell(i).setCellValue(row[i]);
            }
        }
    }

    /** 表头样式 */
    private CellStyle headStyle(XSSFWorkbook workbook)
    {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /** 字典值转标签 */
    private String dictLabel(String dictType, String dictValue)
    {
        if (StringUtils.isEmpty(dictValue))
        {
            return "";
        }
        String label = DictUtils.getDictLabel(dictType, dictValue);
        return StringUtils.isEmpty(label) ? dictValue : label;
    }

    private String nvl(String value)
    {
        return value == null ? "" : value;
    }

    private String money(BigDecimal value)
    {
        return value == null ? "" : value.toPlainString();
    }
}

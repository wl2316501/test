package com.byd.bigdata.eis.module.media.easyexecl.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
public class ModelExcelListener<T> extends AnalysisEventListener<T> {
    Class clazz;

    private Collection<String> col;

    public ModelExcelListener(Class clazz) {
        this.clazz = clazz;
    }

    public ModelExcelListener(Class clazz, Collection<String> col) {
        this.clazz = clazz;
        this.col = col;
    }

    private List<T> datas = new ArrayList<>();

    private List<String> errorMsgs = new ArrayList<>();

    private Set<String> repeatSet = new HashSet<>();

    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        //根据类上的注解进行校验每一行数据。
        Set<ConstraintViolation<T>> violations = ValidationUtils.getValidator().validate(data);
        if (violations.size() > 0) {
            StringBuilder sb = new StringBuilder();
            violations.stream().forEach(violation -> sb.append("第【").append(context.readRowHolder().getRowIndex() + 1).append("行】").append(violation.getMessage()).append("\n"));
            errorMsgs.add(sb.toString());

        }
        //对媒体信息库差异化需求进行单独处理，国内媒体所在省份和海外媒体位置必须维护其中一项
        if (data instanceof LibraryConfImportExcel) {
            LibraryConfImportExcel libraryConfImportExcel = (LibraryConfImportExcel) data;
            if (libraryConfImportExcel.getIntlType().equals("0")) {
                //选国内省，市不能为空
                if (libraryConfImportExcel.getProvince() == null || libraryConfImportExcel.getLocation() == null) {
                    errorMsgs.add("选择国内后，国内媒体所在省份和国内媒体所在城市不能为空");
                } else if (libraryConfImportExcel.getIntlType().equals("1")) {
                    errorMsgs.add("选择国内后，国内媒体所在省份和国内媒体所在城市不能为空");
                }

            }
        } else if (data instanceof ActivitySpreadImportExcel) {
            ActivitySpreadImportExcel activitySpreadImportExcel = (ActivitySpreadImportExcel) data;
            BigDecimal decimalAmount = new BigDecimal(activitySpreadImportExcel.getCooperationAmount());
            String key = String.join("", activitySpreadImportExcel.getProjectId(), activitySpreadImportExcel.getLibraryName().toUpperCase(), StrUtil.isEmpty(activitySpreadImportExcel.getCooperationAmount()) ? "0.00" : decimalAmount.setScale(2, RoundingMode.HALF_UP).toString());
            //1.项目编号为空不校验 2、校验excel本身是否有重复
            if (StrUtil.isNotEmpty(activitySpreadImportExcel.getProjectId())) {
                if (repeatSet.contains(key) && activitySpreadImportExcel.getIgnoreSelfRepeated() != 1) {
                    errorMsgs.add("Excel中第【" + (context.readRowHolder().getRowIndex() + 1) + "行】重复\n");
                } else {
                    repeatSet.add(key);
                }
                //2.校验和数据库是否存在重复
                if (col.contains(key) && activitySpreadImportExcel.getIgnoreDBRepeated() != 1) {
                    errorMsgs.add("数据库中第【" + (context.readRowHolder().getRowIndex() + 1) + "行】重复\n");
                }
            }
        } else if(data instanceof ActivityRedLineImportExcel) {
            ActivityRedLineImportExcel activityRedLineImportExcel = (ActivityRedLineImportExcel) data;
            if (repeatSet.contains(activityRedLineImportExcel.getLibraryName())) {
                errorMsgs.add("Excel中第【" + (context.readRowHolder().getRowIndex() + 1) + "行】重复\n");
            } else {
                repeatSet.add(activityRedLineImportExcel.getLibraryName());
            }
            String key = String.join("", activityRedLineImportExcel.getYear()+"", activityRedLineImportExcel.getQuarter()+"", activityRedLineImportExcel.getLibraryName().toUpperCase());
            //2.校验和数据库是否存在重复
            if (col.contains(key)) {
                errorMsgs.add("数据库中第【" + (context.readRowHolder().getRowIndex() + 1) + "行】重复\n");
            }
        }
        if (errorMsgs.size() == 0)
            datas.add(data);
        //根据业务自行处理，可以写入数据库等等

    }

    //所有的数据解析完了调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        List<String> voHead = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            ExcelProperty myField2 = fields[i].getAnnotation(ExcelProperty.class);
            if (myField2 != null)
                voHead.add(myField2.value()[0]);
        }
        //对比表头
        int i = 0;
        for (Integer key : headMap.keySet()) {
            if (headMap.get(key) == null) {
                errorMsgs.add("第" + (i + 1) + "列表头格式不正确！");
            }
            if (i == voHead.size()) {
                errorMsgs.add("上传文件表头超过模板列数，正确表头为：" + voHead);
            }
            if (!headMap.get(key).equals(voHead.get(i))) {
                errorMsgs.add("上传文件表头超过模板列数，正确表头为：" + voHead);
            }
            if (errorMsgs.size() != 0) {
                break;
            }
            i++;
        }
    }

    /**
     * 返回数据
     *
     * @return 返回读取的数据集合
     **/
    public List<T> getDatas() {
        return datas;
    }

    public List<String> getErrorMsgs() {
        return errorMsgs;
    }
}

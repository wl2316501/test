@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySpreadImportExcel {
    /**
     * 日期
     */
    @ExcelProperty(value = "日期")
    @NotBlank(message="日期不能为空")
    @Pattern(regexp = "^\\d{4}/\\d{1,2}/\\d{1,2}$",message = "日期格式不正确")
    private String fillingDate;
    /**
     * 媒体类型
     */
    @ExcelProperty("媒体类型")
    private String categorize;
    /**
     * 品关负责部门名称
     */
    @ExcelProperty("品关负责部门")
    private String deptName;
    /**
     * 项目编号
     */
    @ExcelProperty("项目编号")
    private String projectId;
    /**
     * 项目名称
     */
    @ExcelProperty("项目名称")
    private String projectName;
    /**
     * 事业部名称
     */
    @ExcelProperty("事业部")
    @NotBlank(message="事业部不能为空")
    private String businessUnitName;
    /**
     * 发起部门名称
     */
    @ExcelProperty("发起部门")
    private String initialDeptName;
    /**
     * 发起责任人
     */
    @ExcelProperty("发起负责人")
    private String initialPerson;
    /**
     * 投放车型/品牌
     */
    @ExcelProperty("投放车型/品牌")
    private String launchVehicle;


    /**
     * 媒体名称
     */
    @ExcelProperty("媒体名称")
    private String libraryOriginalName;
    /**
     * 媒体名称(别名)
     */
    @ExcelProperty("媒体名称（可调整）")
//    @NotBlank(message="媒体名称（可调整）不能为空")
    private String libraryName;
    /**
     * 媒介
     */
    @ExcelProperty("媒介")
    private String mediumPerson;
    /**
     * 合作金额(万元)
     */
    @ExcelProperty("合作金额（万元）")
    @Pattern(regexp = "^[-+]?\\d+(\\.\\d+)?$",message = "合作金额（万元）格式不正确")
    private String cooperationAmount;
    /**
     * 合作权益
     */
    @ExcelProperty("合作权益")
    private String cooperationRights;
    /**
     * 执行时间
     */
//    @ExcelProperty("执行时间")
//    @Pattern(regexp = "^\\d{4}/\\d{1,2}/\\d{1,2}(?:-\\d{4}/\\d{1,2}/\\d{1,2})?$",message = "执行时间格式不正确")
//    private String executionTimes;

    /**
     * 执行开始时间
     */
    @ExcelProperty("执行开始时间")
    @Pattern(regexp = "^\\d{4}/\\d{1,2}/\\d{1,2}$",message = "执行开始时间格式不正确")
    private String executionStartTime;
    /**
     * 执行结束时间
     */
    @ExcelProperty("执行结束时间")
    @Pattern(regexp = "^\\d{4}/\\d{1,2}/\\d{1,2}$",message = "执行结束时间格式不正确")
    private String executionEndTime;
    /**
     * 媒体中心回复意见
     */
    @ExcelProperty("媒体中心回复意见")
    private String replyComments;
    /**
     * 是否审批
     */
    @ExcelProperty(value = "是否报批",converter = IsOrNotConverter.class)
    private Integer approved;
    /**
     * 报批金额
     */
    @ExcelProperty("报批金额")
    @Pattern(regexp = "^[-+]?\\d+(\\.\\d+)?$",message = "报批金额格式不正确")
    private String approvedAmount;
    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;

    /**
     * 是否忽略表格校验
     */
    @ExcelProperty(value = "是否忽略表格校验",converter = IsOrNotConverter.class)
    private Integer ignoreSelfRepeated =0;
    /**
     * 是否忽略数据库校验
     */
    @ExcelProperty(value = "是否忽略数据库校验",converter = IsOrNotConverter.class)
    private Integer ignoreDBRepeated =0;


}

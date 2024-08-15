    @PostMapping("/importexcel")
    @Operation(summary = "导入传播连战 Excel")
    @MediumOperationLog(operationType = OperationType.IMPORT, serviceClass = ActivitySpreadService.class, businessType = BusinessType.ACTIVITY_SPREAD, name ={"libraryName","projectId"})
    public CommonResult importexcel(@RequestParam(value = "file") MultipartFile file) throws Exception {
        return importexcel(file);
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult importexcel(MultipartFile file) throws IOException {
        ModelExcelListener<ActivitySpreadImportExcel> modelExcelListener = new ModelExcelListener<>(ActivitySpreadImportExcel.class, getDataMaps().values());
        EasyExcel.read(file.getInputStream(), ActivitySpreadImportExcel.class, modelExcelListener).sheet().doRead();
        if (modelExcelListener.getErrorMsgs().size() != 0) {
            return CommonResult.error(500, modelExcelListener.getErrorMsgs().toString());
        }
        Map<String, LibraryConfSimpleRespVO> libraryConfDOMap = libraryConfService.selectLibraryMap();
        List<ActivitySpreadImportExcel> list = modelExcelListener.getDatas();
        List<ActivitySpreadDO> activitySpreadDOS = new ArrayList<>();
        list.forEach(activitySpreadImportExcel -> {
            ActivitySpreadDO activitySpreadDO = ActivitySpreadDO.convert(activitySpreadImportExcel, libraryConfDOMap);
            activitySpreadDOS.add(activitySpreadDO);
        });
        activitySpreadMapper.insertBatch(activitySpreadDOS);
        return CommonResult.success(activitySpreadDOS.stream().map(activitySpreadDO -> activitySpreadDO.getId()).collect(Collectors.toList()));
    }

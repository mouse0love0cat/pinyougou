 //控制层 
 app.controller('typeTemplateController', function ($scope, $controller, typeTemplateService, brandService, specificationService) {

     /*依赖注入brandservice，使用查询下拉品牌列表页面*/
     /*依赖注入specificationService，使用查询下拉规格列表页面*/

     $controller('baseController', {$scope: $scope});//继承

     //读取列表数据绑定到表单中  
     $scope.findAll = function () {
         typeTemplateService.findAll().success(
             function (response) {
                 $scope.list = response;
             }
         );
     }


     //分页
     $scope.findPage = function (page, rows) {
         typeTemplateService.findPage(page, rows).success(
             function (response) {
                 $scope.list = response.data;
                 $scope.paginationConf.totalItems = response.total;//更新总记录数
             }
         );
     }

     //查询实体 --根据id查询  用于修改
     $scope.findOne = function (id) {
         typeTemplateService.findOne(id).success(
             function (response) {
                 $scope.entity = response;
                 /*将查询的字符串转换为json对象*/
                 /*品牌列表*/
                 $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                 /*规格列表*/
                 $scope.entity.specIds = JSON.parse($scope.entity.specIds);
                 /*转换扩展属性*/
                 $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
             }
         );
     }


     /*品牌列表*/
     $scope.brandList = {data: []};

     /*查询下拉品牌列表*/
     $scope.findBrandList = function () {
         brandService.selectOptionList().success(
             function (response) {
                 $scope.brandList = {data: response};
             });
     };

     /*规格列表*/
     $scope.specList = {data: []};

     /*查询下拉品牌列表*/
     $scope.selectSpecOptionList = function () {
         specificationService.selectSpecOptionList().success(
             function (response) {
                 $scope.specList = {data: response};
             });
     }

     /*新增扩展属性---新增一行*/
     $scope.addTabRow = ()=>{
         $scope.entity.customAttributeItems.push({});
     }

     /*删除一行操作,根据每一行的索引进行删除*/
     $scope.deleteTabRow = (index)=>{
         $scope.entity.customAttributeItems.splice(index, 1);
     }


     //保存
     $scope.save = function () {
         var serviceObject;//服务层对象  				
         if ($scope.entity.id != null) {//如果有ID
             serviceObject = typeTemplateService.update($scope.entity); //修改  
         } else {
             serviceObject = typeTemplateService.add($scope.entity);//增加 
         }
         serviceObject.success(
             function (response) {
                 if (response.success) {
                     //重新查询 
                     $scope.reloadList();//重新加载
                 } else {
                     alert(response.message);
                 }
             }
         );
     }


     //批量删除 
     $scope.dele = function () {
         //获取选中的复选框			
         typeTemplateService.dele($scope.selectIds).success(
             function (response) {
                 if (response.success) {
                     $scope.reloadList();//刷新列表
                     $scope.selectIds = [];
                 }
             }
         );
     }

     $scope.searchEntity = {};//定义搜索对象 

     //搜索
     $scope.search = function (page, rows) {
         typeTemplateService.search(page, rows, $scope.searchEntity).success(
             function (response) {
                 $scope.list = response.data;
                 $scope.paginationConf.totalItems = response.total;//更新总记录数
             }
         );
     }


 });	

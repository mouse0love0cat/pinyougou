 //控制层 
app.controller('goodsController' ,function($scope  ,goodsService,uploadService,itemCatService,typeTemplateService){

	//保存
	$scope.save=function(){
        // 提取kindeditor编辑器的内容
        $scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					//重新查询 
		        	alert("保存成功!");
		        	//清空实体对象
					$scope.entity={};
					//清空富文本编辑器的内容
					editor.html(' ');
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	$scope.imageEntity = {};
        //上传文件
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success) {
					$scope.imageEntity.url = response.message;
				} else {
					alert(response.message);
				}
			}
        )
    }

    //初始化实体类对象
	$scope.entity={goods:{},goodsDesc:{itemImages:[]}};

	//保存图片
	$scope.savePic=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }


    /*加载一级列表*/
	$scope.findByParentId = function (parentId) {
		itemCatService.findByParentId(parentId).success(
			(response)=>{
				$scope.itemCatList = response;
			}
		)
    }

    /*通过监听一节列表的改变，查询对应的二级列表的值*/
	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			(response)=>{
				$scope.itemCat2List = response;
			}
		)
    })
    /*通过监听二级列表的改变，查询对应的三级列表的值*/
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue){
        itemCatService.findByParentId(newValue).success(
            (response)=>{
                $scope.itemCat3List = response;
            }
        )
    })
    /*通过监听三级列表的改变，查询对应的模板id值*/
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue){
        itemCatService.findOne(newValue).success(
            (response)=>{
            	// 获取对应的模板id
                $scope.entity.goods.typeTemplateId = response.typeId;

            }
        )
    })

	/*根据模板id查询对应的品牌列表和 规参数列表*/
	$scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {

		typeTemplateService.findOne(newValue).success(
			(response)=>{
				//获取模板对象
				$scope.typeTemplate = response;
				//获取模板对象中的 brandId 即品牌列表
				$scope.brandList=JSON.parse(response.brandIds);
				//获取扩展属性列表
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
			}
		)
		$scope.findSpecList(newValue);
    });

	$scope.findSpecList = function (id) {
        //查询规格参数列表
        typeTemplateService.findSpecList(id).success(
            (response)=>{
                $scope.specList=response;
            }
        )
    };


});	

 //控制层 
app.controller('goodsController' ,function($scope  ,goodsService,uploadService){

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


});	

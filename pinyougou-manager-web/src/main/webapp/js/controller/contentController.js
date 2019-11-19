 //控制层 
app.controller('contentController' ,function($scope,$controller   ,contentService,contentCategoryService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		contentService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    

	//0查询分类id列表
	//1 定义一个分类的数组，用于显示分类
	$scope.categorys = [];
	//2 查询分类列表
	$scope.findCategoryList = function () {
		contentCategoryService.findAll().success(
			function (response) {
				//2.1 返回分类列表
				$scope.categoryList = response;
				//2.2 遍历列表 取值
				for (let i = 0;i<response.length;i++){
                    let concategorty = response[i];
                    //2.3 根据key取对应的value值
                    $scope.categorys[concategorty.id] = concategorty.name;
				}

            }
		)
    }

    //上传文件
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.entity.pic = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    }

    //保存
	$scope.save = function () {
		//1 获取id 用于判断是添加还是修改
		let id = $scope.entity.id;
		//2 定义默认的url
		let url = "../content/add";
		//3 对id进行判断
		if (id){
			url  = "../content/update";
		}
		alert(url)
		contentService.save(url,$scope.entity).success(function (response) {
			if (response.success){
				//刷新页面
				$scope.findAll();
			}else {
				alert(response.message)
			}
        });
    }
    
    //修改操作
	$scope.findOne = function (id) {
		contentService.findOne(id).success(
			function (response) {
				$scope.entity = response;
            }
		);
    }
    
});	

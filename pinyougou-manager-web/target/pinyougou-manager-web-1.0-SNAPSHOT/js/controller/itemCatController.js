 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	



    //读取列表数据绑定到表单中
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.itemList=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}



	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			//将当前的parentId赋给上级ID
			$scope.entity.parentId = $scope.parentId;
			serviceObject=itemCatService.add( $scope.entity);//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询   根据parentId进行查询
		        	$scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.delete=function(){
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    $scope.findByParentId($scope.parentId);//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.itemList=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//定义一个父类的id，用于查询上一级
	$scope.parentId = 0;

	//根据parentid查询对象
	$scope.findByParentId=function (parentId) {
		// 记住父类的id  在save（）方法时调用
		$scope.parentId = parentId;
		//通过parentId查询分类列表
		itemCatService.findByParentId(parentId).success(
			function (response) {
				// 返回一个list列表
				$scope.list = response;
            }
		);
    }

    //1 设置默认级别 为1级
    $scope.grade= 1;

	// 2 设置级别
	$scope.setGrade = function (value) {

		$scope.grade = value;
    }
    //读取列表
	$scope.selectList=function (p_entity) {

		//如果为1级  则下两级的内容为空
		if ($scope.grade == 1){
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		//】若当前为二级分类，则将上一级的值赋给p_entity，下一级的值为空
		if ($scope.grade == 2){
			$scope.entity_1 = p_entity;
			$scope.entity_2 = null;
		}
		//若当前为第三级,则将上一级的内容进行赋值
		if ($scope.grade==3){
			$scope.entity_2= p_entity;
		}

		//查询此级的下级列表
		$scope.findByParentId(p_entity.id);
    }

    //定义一个模板类型列表
    $scope.itemCatList = {};

	$scope.selectTypeList= function () {
		typeTemplateService.findAll().success(
			function (response) {
				$scope.itemCatList = response;
            }

		);
    }

    
});	

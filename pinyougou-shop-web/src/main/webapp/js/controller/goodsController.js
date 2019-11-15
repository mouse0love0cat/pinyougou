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



	//保存图片
	$scope.savePic=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }

    //列表中移除图片
    $scope.removePic=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }


    //初始化实体类对象
    $scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},items:[]};

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

	// 定义一个方法 显示规格参数列表
	// 遍历list集合，查询相关的数据，若为空，则返回null
	searchObject = function (list,key,value) {
		//遍历集合
		for (let i =0,len = list.length;i<len;i++){
			if (list[i][key] == value){
				return list[i];
			}
		}
        return null;
    }

    //复选框选中时，触发的事件
    $scope.updateSpecAttribute = function (event,name,value) {
		//1 定义一个查询的对象
		var obj = searchObject( $scope.entity.goodsDesc.specificationItems,'attributeName',name);
		// 2 判断对象是否为空
		if (obj){         //当前不为空
			//2.1 复选框选中的情况
			if(event.target.checked){
				//将value值放入attributeValue的数组中
				obj.attributeValue.push(value);
			}else{   //2.2 表示未被选中  ，分为两种情况
				//2.2.1 数组中还有值，则直接未被勾选的选项
				//获取未被选中的选项在数组中的下标
				let indexValue = obj.attributeValue.indexOf(value);
				//根据下标进行删除
				obj.attributeValue.splice(indexValue,1);
				//2.2.2 若attribute数组中的值为空，则从大数组中移出该attribute小数组
				if (obj.attributeValue.length == 0){
					//获取attribute在大数组中的下标位置
					let index  = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
					//通过下标移出小数组
					$scope.entity.goodsDesc.specificationItems.splice(index,1);
				}
			}
		}else {  //2.3 表示第一次添加  则直接初始化该数组
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
		//选中复选框时，同时显示添加的选中的规格参数
		createItemList();
    }


    //动态生成用户选择的规格参数列表
	createItemList = function () {
		//1 初始化Item选项列表
		 $scope.entity.items =[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
		 //2 获取规格列表
		var itemList = $scope.entity.goodsDesc.specificationItems;
		//3 遍历规格列表
		for (let i = 0,len = itemList.length;i<len;i++){
			$scope.entity.items = addColum($scope.entity.items,itemList[i].attributeName,itemList[i].attributeValue);
		}
    }
    //自定义方法，循环列表，为item中添加赋值，新增行
    addColum = function (list,name,value) {
		// 0 构造一个空的集合
		var newList = [];
		//1 遍历循环列表
		for (let j = 0,n =list.length;j < n;j++){
			//1.1 获取列表中的原始数据
			let oldValue = list[j];
			//1.2 遍历value集合
			for (let m=0,l=value.length;m<l;m++){
				//1.3 对原始数据进行深度克隆，得到一个新的行
				let newRow = JSON.parse(JSON.stringify(oldValue));
				//1.4 给新的行中添加元素
				newRow.spec[name] = value[m];
				//1.5 新行添加到列表中
				newList.push(newRow);
			}
		}
		//2 返回集合
		return newList;
    }



});	

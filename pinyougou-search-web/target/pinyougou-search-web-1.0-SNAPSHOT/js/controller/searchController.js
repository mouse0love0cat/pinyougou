app.controller("searchController",function ($scope,$location,$controller,searchService) {
    $controller('baseController',{$scope:$scope});//继承

    //0.定义向后台提交的查询对象
    $scope.searchMap = {"keywords":'',"category":'',"brand":'',"spec":{},"price":'',"sort":'',"sortField":'',"page":1,"pageSize":20};
    //1.查询商品
    $scope.search=()=>{
       // 获取从首页传递过来的值
        searchService.search($scope.searchMap).success(response=>{
            $scope.resultMap = response;
            //调用分页导航
            buildLable();
        })
    }
    
    //2 添加搜索项功能
    $scope.addItemSearch = function (key,value) {
        if (key == "category" || key == "brand" || key == "price") {
             $scope.searchMap[key] = value;
        }else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }
    
    //3 删除指定搜索项
    $scope.removeItem = function (key) {
        if (key == "category" || key == "brand" || key == "price"){
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.keywords = '';
        $scope.search();
    }

    //4 定义排序查询的方法
    $scope.sortSearch = function (sort,sortField) {
        $scope.searchMap.sort = sort;
        alert(sort)
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    //5 定义分页导航
    buildLable = function () {
        //5.1 定义分页导航栏
        $scope.pageLabel = [];
        //5.2 定义首页与尾页
        var firstPage = 1;       //首页
        var lastPage = $scope.resultMap.totalPages;  //尾页
/*
        alert( $scope.resultMap.totalPages);
        //5.3 定义代表省略号的两个变量
        console.log(typeof ($scope.searchMap.page));
        console.log(typeof 2);
        console.log(typeof lastPage);
*/
        $scope.firstDot = false;
        $scope.lastDot = false;
        //5.4 确定首页和尾页
        if($scope.resultMap.totalPages >5){
            if ($scope.searchMap.page <= 3){
                lastPage = 5;
                $scope.lastDot = true;
            }else if ($scope.searchMap.page >=lastPage -2){
                firstPage = $scope.resultMap.totalPages-4;
                $scope.firstDot = true;
            }else {
               firstPage = $scope.searchMap.page - 2;
                lastPage = Number ($scope.searchMap.page) + Number(2);
                $scope.firstDot = true;
                $scope.lastDot = true;
            }
        }
        //5.5 绘制分页导航
        // alert(lastPage)
        for (let i = firstPage,len = lastPage;i <= len ; i++ ){
            $scope.pageLabel.push(i);
        }
    }
    //6 点击某一页  进行跳转分页
    $scope.skipToPage = function (page) {
        //将当前页设置为指定跳转的页面
        // alert($scope.searchMap.page);
        $scope.searchMap.page = Number(page);
        //执行查询
        $scope.search();
    }

    // 7.判断关键字 是否是品牌，如果是就隐藏
    //7.判断输入的关键字是否是品牌，如果是就隐藏它
    $scope.hideBrandList = ()=>{
        //7.1)得到品牌列表
        let brandList = $scope.resultMap.brandList;
        //7.2)遍历品牌列表，并查询当前的关键字是否在此品牌列表中
        for(let i = 0, len = brandList.length; i < len; i++){
            if($scope.keywords.indexOf(brandList[i].text) >= 0){
                $scope.searchMap.brand = brandList[i].text;
                return true;
            }
        }
        return false;
    }

    //8 加载关键字
    $scope.loadKeyWords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

})
app.controller("searchController",function ($scope,$controller,searchService) {
    $controller('baseController',{$scope:$scope});//继承
    //0.定义向后台提交的查询对象
    $scope.searchMap = {};
    //1.查询商品
    $scope.search=()=>{
        searchService.search($scope.searchMap).success(response=>{
            $scope.resultMap = response;
        })
    }

})
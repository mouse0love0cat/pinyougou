app.controller('contentController',function ($scope,contentService) {

    $scope.categoryList=[];

    /**
     * 根据categoryId 查询信息
     * @param categoryId
     */
    $scope.findCategoryById=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                for (let i = 0 ;i<response.length;i++) {
                    $scope.categoryList[categoryId]=response;
                }
            }
        )
    };

    //搜索页面与首页进行对接
    $scope.search = function () {
        //带搜索关键字的跳转
        location.href = "http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }



});
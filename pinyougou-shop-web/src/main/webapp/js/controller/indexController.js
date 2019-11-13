app.controller('indexController',function($scope,loginService){

    //读取当前登录的人

    $scope.showName = function () {
        loginService.loginName().success(
            function (response) {
                $scope.userName = response.name;
            }
        );
    }

})
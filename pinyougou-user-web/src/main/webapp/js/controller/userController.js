app.controller("userController",function ($scope,$controller,userService) {
    $controller('baseController',{$scope:$scope});//继承

    //1.在后台获取验证码
    $scope.getValidCode=()=>{
        userService.getValidCode($scope.entity.phone).success(response=>{
            if(response.success){

            }else{
                alert(response.message);
            }
        })
    }
    //2.用户注册
    $scope.add=()=>{
        //2.1)比较两次密码是否一致
        if($scope.entity.password != $scope.repassword){
            alert("两次输入的密码不一致！");
            return;
        }
        //2.2) 进行用户注册
        userService.add($scope.entity,$scope.validCode).success(response=>{
            if(response.success){
                $scope.entity = {};
            }else{
                alert("注册失败！");
            }
        })
    }
})
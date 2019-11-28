//品牌控制层
app.controller('baseController', function ($scope) {

    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    $scope.selectIds = [];//选中的ID集合

    //更新复选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果是被选中,则增加到数组
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除
        }
    }

    /*提取json格式中的数据中的某些属性，转换为字符串，通过拼接显示在页面*/
    $scope.JsonToString = (jsonString,key)=>{

        //1 将字符串转换为json对象
        var json = JSON.parse(jsonString);
        //2 定义一个空的String类型的变量，用于拼接属性
        var value = "";
        //3 循环遍历json数据
        for (let i = 0;i<json.length;i++){
            //3.1 拼接json属性，以“，”分割
            if (i>0){
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }

});
var app = angular.module("pinyougou",[]);

//定义一个过滤器，解析html语法
app.filter("trustHtml",["$sce",function ($sce) {

    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])
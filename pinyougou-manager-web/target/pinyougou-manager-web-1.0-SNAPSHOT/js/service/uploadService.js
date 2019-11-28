app.service("uploadService",function ($http) {


    //文件上传
    this.uploadFile=function () {
        var  formDate = new FormData();
        formDate.append("file",file.files[0]);

        return $http({
            url:'../upload',
            data:formDate,
            //设置提交方式
            method:'post',
            headers:{'content-Type':undefined},
            //序列化
            transformRequest:angular.identity

        })

        /*anjularjs 对于post 和get 请求默认的Content-Type header 是
        application/json。
    * 通过设置‘Content-Type’: undefined，
    * 这样浏览器会帮我们把Content-Type 设置为multipart/form-data.
    * 通过设置transformRequest: angular.identity ，anjularjs  ransformRequest function
    * 将序列化我们的formdata object.*/
    }

})
app.service("searchService",function ($http) {

    //1.向后台发出查询请求
    this.search = (searchMap)=>{
        return $http.post("./itemsearch/search.do",searchMap);
    }
})
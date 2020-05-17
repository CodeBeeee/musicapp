import request from '../utils/request';





export const AdminLoginCheck = (query) => {
    return request({
        url: 'admin/checkLogin',
        method: 'post',
        data:JSON.stringify(query)
    })
}


 
//查询分页信息列表
export const getPartUser = (query) => {
    return request({
        url: 'user/getuserinfo',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询搜索信息列表
export const getSearchUser = (query) => {
    return request({
        url: 'user/getsearchuserinfo',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询用户总数
export const getCount = () => {
    return request({
        url: 'user/getusercount',
        method: 'get',
    })
}
//查询分页用户播放记录列表
export const getPartUserPlay = (query) => {
    return request({
        url: 'music/getuserplay',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询搜索信息列表
export const getSearchUserPlay = (query) => {
    return request({
        url: 'music/getsearchuserplay',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询分页信息列表
export const getMusicPlay = (query) => {
    return request({
        url: 'music/getmusicplay',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询搜索信息列表
export const getSearchMusicPlay = (query) => {
    return request({
        url: 'music/getsearchmusicplay',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询音乐播放记录总数
export const getMusicPlayCount = () => {
    return request({
        url: 'music/getmusicplaycount',
        method: 'get',
    })
}










//添加学生信息
export const addStudentPost = (query) => {
    return request({
        url: 'stuinfo/addStudent',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//查询所有学生信息列表
export const getPartStudent = (query) => {
    return request({
        url: 'stuinfo/getPartStudent',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//删除学生信息
export const delStudent = (query) => {
    return request({
        url: 'stuinfo/deleteStudent?id='+query.id,
        method: 'get',
    })
}

//上传课程封面图片
export const addBookImg = (query) => {
    return request({
        url: 'bookinfo/upload',
        method: 'post',
        data:query,
        header: { // 已经在request.js里面进行全局设置，也可以在请求里面局部设置其他headers
            'Content-Type': 'multipart/form-data'
        }
    })
}
//添加课程信息
export const addBookInfo = (query) => {
    return request({
        url: 'bookinfo/addBook',
        method: 'post',
        data:JSON.stringify(query),
         
    })
}
//获取分页课程信息
export const getPartBook = (query) => {
    return request({
        url: 'bookinfo/getPartBook',
        method: 'post',
        data:JSON.stringify(query),
         
    })
}
//添加课程文章信息
export const addZhangjie = (query) => {
    return request({
        url: 'zhangjieinfo/addZhangjie',
        method: 'post',
        data:JSON.stringify(query),
    })
}

//删除课程信息
export const delBook = (query) => {
    return request({
        url: 'bookinfo/deleteBook?id='+query.id,
        method: 'get',
    })
}

//查询当前课程的的所有章节
export const getZhangjie = (query) => {
    return request({
        url: 'zhangjieinfo/getZhangjie?id='+query,
        method: 'get',
    })
}
//查询学生数量
export const getStudentCount = () => {
    return request({
        url: 'stuinfo/getcount',
        method: 'get',
    })
}
//查询课程数量
export const getBookCount = () => {
    return request({
        url: 'bookinfo/getcount',
        method: 'get',
    })
}
//添加答题
export const addProblem = (query) => {
    return request({
        url: 'probleminfo/addProblem',
        method: 'post',
        data:JSON.stringify(query),
    })
}
//查询对应课程题库
export const getProblem = (query) => {
    return request({
        url: 'probleminfo/getbookproblem/'+query,
        method: 'post',
    })
}














export const fetchData = (query) => {
    return request({
        url: 'news/findNews?id='+query.id,
        method: 'get',
    })
}
// 新闻数据处理
export const uploadImg = (query) => {
    return request({
        url: 'news/uploadPics',
        method:'post',
        data:query,
        header: { // 已经在request.js里面进行全局设置，也可以在请求里面局部设置其他headers
            'Content-Type': 'multipart/form-data'
        }
    })
}
export const updateNewsClick = (query) => {
    return request({
        url: 'news/updateNewsClick?uid='+query.uid,
        method:'get',
    })
}

export const addNews = (query) => {
    return request({
        url: 'news/addnews',
        method:'post',
        data:JSON.stringify(query),
    
    })
}
export const findNewsInfo = (query) => {
    return request({
        url: 'news/getNewsInfo',
        method:'post',
        data:JSON.stringify(query),
    })
}
export const getAllNews = () => {
    return request({
        url: 'news/getAllNews',
        method:'get',
    })
}
export const getNewsCount = () => {
    return request({
        url: 'news/getNewsCount',
        method:'get'
    })
}
export const getNewsSearch = (query) => {
    return request({
        url: 'news/searchNews',
        method:'post',
        data:JSON.stringify(query),
    })
}
export const delNews = (query) => {
    return request({
        url: 'news/deleteNews?id='+query.id,
        method:'get',
    })
}
export const getNewsnums = () => {
    return request({
        url: 'visitnum/getsum/newsclicknum',
        method:'post'
    })
}
export const getWeekNewsnums = () => {
    return request({
        url: 'visitnum/weeknewsinfo',
        method:'post'
    })
}
// 用户数据处理
export const getUsernums = () => {
    return request({
        url: 'visitnum/getsum/uservisitnum',
        method:'post'
    })
}
export const getWeekUsernums = () => {
    return request({
        url: 'visitnum/weekusersinfo',
        method:'post'
    })
}
export const findUserInfo = (query) => {
    return request({
        url: 'user/getUserInfo',
        method:'post',
        data:JSON.stringify(query),
    })
}
export const getUserCount = () => {
    return request({
        url: 'user/getUserCount',
        method:'get'
    })
}
export const getUserSearch = (query) => {
    return request({
        url: 'user/searchUser',
        method:'post',
        data:JSON.stringify(query),
    })
}
export const delUser = (query) => {
    return request({
        url: 'user/deleteUser?id='+query.id,
        method:'get',
    })
}
export const updateUser = (query) => {
    return request({
        url: 'user/updateuser',
        method:'post',
        data:JSON.stringify(query),
    })
}
export const addUser = (query) => {
    return request({
        url: 'user/adduser',
        method:'post',
        data:JSON.stringify(query),
    })
}
export const userloginCheck = (query) => {
    return request({
        url: 'user/checkUserLogin',
        method: 'post',
        data:JSON.stringify(query)
    })
}
//评论信息处理
export const addReply = (query) => {
    return request({
        url: 'command/addReply',
        method:'post',
        data:query,
    })
}
export const getReply = (query) => {
    return request({
        url: 'command/getReply?uid='+query.uid,
        method:'get',
    })
}

export const addCommand = (query) => {
    return request({
        url: 'command/addCommand',
        method:'post',
        data:query,
    })
}
export const getCommand = (query) => {
    return request({
        url: 'command/getCommand?uid='+query.uid,
        method:'get',
    })
}
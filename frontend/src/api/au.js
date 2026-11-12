import request from './request' // 引入请求模块

export function login(data){
    return request.post('/login',data) // 请求登录接口
}

export function register(data){
    return request.post('/register',data) // 请求注册接口
}
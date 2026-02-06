import request from '../utils/request';

export interface LoginFO {
    username?: string;
    password?: string;
}

export interface LoginMO {
    token: string;
    username: string;
}

export function login(data: LoginFO) {
    return request.post<any, LoginMO>('/login', data);
}

export function logout() {
    return request.post<any, void>('/logout');
}

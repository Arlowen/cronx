import axios from 'axios';

// Define standard ResponseData interface matching Backend
export interface ResponseData<T = any> {
    code: string;
    msg: string;
    data: T;
}

const service = axios.create({
    baseURL: '/cronx/api/v1',
    timeout: 5000,
});

service.interceptors.response.use(
    (response) => {
        const res = response.data as ResponseData;
        // Success code is "1" based on ResultEnum.java
        if (res.code !== '1') {
            console.error('API Error:', res.msg);
            return Promise.reject(new Error(res.msg || 'Error'));
        }
        return res.data;
    },
    (error) => {
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

export default service;

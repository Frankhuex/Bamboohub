import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig, Method } from 'axios';
export interface ResponseData<T = unknown> {
    data: T;
    success: boolean;
    errorMsg: string;
}
// 创建 Axios 实例
const createAxiosInstance = (): AxiosInstance => {

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    timeout: 10000,
});

// 请求拦截器 - 添加 token
instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('token');
        if (token && config.headers) {
            config.headers.Authorization = token;
        }
        return config;
    },
    (error: unknown) => {
        return Promise.reject(error);
    }
);

// 响应拦截器 - 处理响应数据
instance.interceptors.response.use(
    (response: AxiosResponse<ResponseData>)=> {
        return {
            ...response,
            data: response.data.data,
            success: response.data.success,
            errorMsg: response.data.errorMsg,
        }
    }
);

return instance;
};

const http = createAxiosInstance();

// 定义请求方法参数类型
interface RequestOptions<D = unknown> extends AxiosRequestConfig {
    data?: D;
}

// 封装三种请求方法
export const httpService = {
// JSON 格式请求
json: <T = unknown, D = unknown>(url: string, data?: D, config?: Omit<RequestOptions<D>, 'data'>): Promise<ResponseData<T>> => {
    return http({
        url,
        method: 'POST' as Method,
        data,
        headers: {
            'Content-Type': 'application/json',
            ...config?.headers,
        },
        ...config,
    });
},

// 表单格式请求
form: <T = unknown, D extends Record<string, unknown> = Record<string, unknown>>(
    url: string,
    data?: D,
    config?: Omit<RequestOptions<FormData>, 'data'>
): Promise<ResponseData<T>> => {
    const formData = new FormData();
    if (data) {
    Object.entries(data).forEach(([key, value]) => {
        if (value !== undefined) {
        formData.append(key, value instanceof Blob ? value : String(value));
        }
    });
    }

    return http({
    url,
    method: 'POST' as Method,
    data: formData,
    headers: {
        'Content-Type': 'multipart/form-data',
        ...config?.headers,
    },
    ...config,
    });
},

// 无请求体的 GET 请求
get: <T = unknown>(url: string, config?: Omit<RequestOptions, 'data'>): Promise<ResponseData<T>> => {
    return http({
        url,
        method: 'GET' as Method,
        ...config,
    });
},
};
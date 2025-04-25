import { useState } from "react";
import {httpService, ResponseData} from "../api/client"
import {LoginReq, RegisterReq} from "../types/request.types"

interface AuthProps {
    setLoggedIn: (hasLoggedIn: boolean) => void;
}
export default function Auth({setLoggedIn}:AuthProps) {
    const [username,setUsername]=useState('');
    const [password,setPassword]=useState('');
    const [confirmPassword,setConfirmPassword]=useState('');
    const [nickname,setNickname]=useState('');
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading]=useState<boolean>(false)

    const login = async () => {
        setLoading(true)
        const loginReq:LoginReq={
            username:username,
            password:password
        }
        try {
            const response:ResponseData<UserDTOWithToken> = await httpService.json<UserDTOWithToken>("/login","POST",loginReq);
            if (response.success!==true) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            const userDTOWithToken:UserDTOWithToken=response.data
            localStorage.setItem('token',userDTOWithToken.token)    
            setLoggedIn(true)
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const register = async () => {
        setLoading(true)
        if (password!==confirmPassword) {
            setError('两次密码不一致')
            return
        }
        // if (password.length<6) {
        //     setError('密码长度必须大于等于6')
        //     return
        // }
        // if (password.match(/[a-zA-Z]/)===null) {
        //     setError('密码必须包含字母')
        //     return
        // }
        // if (password.match(/[0-9]/)===null) {
        //     setError('密码必须包含数字')
        //     return
        // }
        

        const registerReq:RegisterReq={
            username:username,
            password:password,
            nickname:nickname
        }
        try {
            const response:ResponseData<UserDTOWithToken> = await httpService.json<UserDTOWithToken>("/register","POST",registerReq);
            if (response.success!==true) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            const userDTOWithToken:UserDTOWithToken=response.data
            localStorage.setItem('token',userDTOWithToken.token)    
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const loginCard=(
        <div className="flex justify-center w-full">
            <fieldset className="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4">
                <legend className="fieldset-legend">登录</legend>

                <label className="label">用户名</label>
                <input onChange={(e)=>setUsername(e.target.value)} type="text" className="input" placeholder="用户名" />

                <label className="label">密码</label>
                <input onChange={(e)=>setPassword(e.target.value)} type="password" className="input" placeholder="密码" />

                <button onClick={()=>login()} className="btn btn-neutral mt-4">登录</button>
                <p className="text-red-500">{error}</p>
            </fieldset>
        </div>
    )

    const registerCard=(
        <div className="flex justify-center w-full">
            <fieldset className="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4">
                <legend className="fieldset-legend">注册</legend>

                <label className="label"><strong>用户名</strong><br /></label>
                <p className="text-xs text-gray-500">· 用于登录，不可与他人重名</p>
                <input onChange={(e)=>setUsername(e.target.value)} type="text" className="input" placeholder="用户名" />

                <label className="label"><strong>昵称</strong><br /></label>
                <p className="text-xs text-gray-500">· 用于展示，可为任何名称</p>
                <input onChange={(e)=>setNickname(e.target.value)} type="text" className="input" placeholder="昵称" />

                <label className="label"><strong>密码</strong><br /></label>
                <p className="text-xs text-gray-500"></p>
                <input onChange={(e)=>setPassword(e.target.value)} type="password" className="input" placeholder="密码" />

                <label className="label"><strong>确认密码</strong><br /></label>
                <p className="text-xs text-gray-500">· 请再次输入密码</p>
                <input onChange={(e)=>setConfirmPassword(e.target.value)} type="password" className="input" placeholder="确认密码" />

                <button onClick={()=>register()} className="btn btn-neutral mt-4">注册</button>
                <p className="text-red-500">{error}</p>
            </fieldset>
        </div>
    )
    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    return (<>
                <br></br>
                <div className="tabs tabs-box mx-auto w-full flex justify-center lg:flex-none lg:justify-start" >
                    <input type="radio" name="my_tabs_1" className="tab" aria-label="登录" defaultChecked />
                    <div className="tab-content border-base-300 bg-base-100 p-10">
                        {loginCard}
                    </div>
                    
                    <input type="radio" name="my_tabs_1" className="tab" aria-label="注册" />
                    <div className="tab-content border-base-300 bg-base-100 p-10">
                        {registerCard}
                    </div>
                </div>
            </>)
}
import { useState } from "react";
import {httpService, ResponseData} from "../api/client"
import {ChangePwdReq, UserUpdateReq} from "../types/request.types"

interface Props {
    setChangingUsnPwd: (changingUsnPwd: boolean) => void;
}
export default function ChangeUsnPwd({setChangingUsnPwd}:Props) {
    const [username,setUsername]=useState('');
    const [oldPwd,setOldPwd]=useState('');
    const [newPwd,setNewPwd]=useState('');
    const [confirmNewPwd,setConfirmNewPwd]=useState('');

    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)
    async function changeUsername() {
        setLoading(true)
        const userUpdateReq:UserUpdateReq={
            username:username,
            nickname:''
        }
        try {
            const response:ResponseData<UserDTO> = await httpService.json<UserDTOWithToken>("/myProfile/update","PUT",userUpdateReq);
            if (response.success!==true) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }   
            localStorage.removeItem('token')
            setChangingUsnPwd(false)
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    async function changePassword() {
        setLoading(true)
        if (newPwd!==confirmNewPwd) {
            setError('两次密码不一致')
            setLoading(false)
            return
        }

        const changePwdReq:ChangePwdReq={
            oldPassword:oldPwd,
            newPassword:confirmNewPwd
        }
        try {
            const response:ResponseData<UserDTOWithToken> = await httpService.json<UserDTOWithToken>("/changePwd","POST",changePwdReq);
            if (response.success!==true) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            localStorage.removeItem('token')
            setChangingUsnPwd(false)
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const changeUsernameCard=(
        <div className="flex justify-center w-full">
            <fieldset className="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4">
                <legend className="fieldset-legend">修改用户名</legend>

                <label className="label">新用户名</label>
                <input onChange={(e)=>setUsername(e.target.value)} type="text" className="input" placeholder="用户名" />

                <button onClick={()=>changeUsername()} className="btn btn-neutral mt-4">修改</button>
                <p className="text-red-500">{error}</p>
            </fieldset>
        </div>
    )

    const changePwdCard=(
        <div className="flex justify-center w-full">
            <fieldset className="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4">
                <legend className="fieldset-legend">修改密码</legend>

                <label className="label"><strong>旧密码</strong><br /></label>
                <input onChange={(e)=>setOldPwd(e.target.value)} type="password" className="input" placeholder="密码" />


                <label className="label"><strong>新密码</strong><br /></label>
                <input onChange={(e)=>setNewPwd(e.target.value)} type="password" className="input" placeholder="密码" />

                <label className="label"><strong>确认新密码</strong><br /></label>
                <p className="text-xs text-gray-500">· 请再次输入新密码</p>
                <input onChange={(e)=>setConfirmNewPwd(e.target.value)} type="password" className="input" placeholder="确认密码" />

                <button onClick={()=>changePassword()} className="btn btn-neutral mt-4">修改</button>
                <p className="text-red-500">{error}</p>
            </fieldset>
        </div>
    )
    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    return (<>
                <br></br>
                <div className="tabs tabs-box mx-auto w-full flex justify-center lg:flex-none lg:justify-start" >
                    <input type="radio" name="my_tabs_1" className="tab" aria-label="修改用户名" defaultChecked />
                    <div className="tab-content border-base-300 bg-base-100 p-10">
                        {changeUsernameCard}
                    </div>
                    
                    <input type="radio" name="my_tabs_1" className="tab" aria-label="修改密码" />
                    <div className="tab-content border-base-300 bg-base-100 p-10">
                        {changePwdCard}
                    </div>
                </div>
                <div className="grid place-content-center p-4 bottom-0 bg-base-10 mb-40">
                    <button onClick={() => setChangingUsnPwd(false)} className="btn w-full max-w-xs mb-5">取消</button>
                </div>
            </>)
}
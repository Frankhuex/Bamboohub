import Auth from "../components/Auth";
import {httpService, ResponseData} from '../api/client'
import {useEffect, useState} from "react";
import WhoIFollow from "../components/WhoIFollow";
import { UserUpdateReq } from "../types/request.types";
import ChangeUsnPwd from "../components/ChangeUsnPwd";
import { utc2current } from "../utils/util";
export default function Profile() {
    


    const [userDTO, setUserDTO]=useState<UserDTO | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState<boolean>(false)
    const [loggedIn, setLoggedIn]=useState<boolean>(false)

    const [changingNickname, setChangingNickname]=useState<boolean>(false)
    const [newNickname, setNewNickname]=useState<string>("")

    const [changingUsnPwd, setChangingUsnPwd]=useState<boolean>(false)

    async function fetchProfile() {
        try {
            const response:ResponseData<UserDTO>=await httpService.empty<UserDTO>("/myProfile",'GET')
            if (response.success===false) {
                setError(response.errorMsg)
                return
            }
            setUserDTO(response.data)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        }
    }

    async function logout() {
        localStorage.removeItem("token")
        setLoggedIn(false)
    }

    async function handleUpdateNickname(newNickname: string) {
        try {
            setLoading(true)
            const userUpdateReq:UserUpdateReq={
                nickname: newNickname,
                username: ""
            }
            const response:ResponseData<UserDTO>=await httpService.json<UserDTO>("/myProfile/update", 'PUT',userUpdateReq)
            if (response.success===true) {
                setUserDTO(response.data)
            }
            setChangingNickname(false)
        } catch (e) {
            if (e instanceof Error) 
                setError(e.message)
            console.log(e)
        } finally {
            setChangingNickname(false)
            setLoading(false)
        }
    }


    useEffect(() => {
        const token = localStorage.getItem("token")
        if (token) {
            setLoggedIn(true)
        } else {
            setLoggedIn(false)
        }
        const fetchData = async () => {
            if (!token) return
            setLoading(true)
            try {
                await Promise.all([fetchProfile()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [loggedIn,changingUsnPwd])

    if (!loggedIn) return (<Auth setLoggedIn={setLoggedIn} />)
    if (changingUsnPwd===true) return (<ChangeUsnPwd setChangingUsnPwd={setChangingUsnPwd} />)
    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>

    return (
    <div className="min-h-screen flex flex-col">
        <div className="flex-1">
            <div className="bg-base-100 border-base-300 border rounded-box">
                {/* <input type="checkbox" /> */}
                <div className="mt-5 grid place-content-center text-[clamp(3rem,5vw,5rem)] font-black">
                    Hello, {userDTO?.nickname}!
                </div>
                {changingNickname===true && 
                (<div className="flex items-stretch h-[5.5rem] place-content-center">
                    <input
                        type="text"
                        placeholder={userDTO?.nickname}
                        className="input input-bordered text-[clamp(2rem,5vw,2rem)] font-black p-4 
                               rounded-r-none border-r-0 h-full"
                        onChange={(e)=>setNewNickname(e.target.value)}
                    />
                    <button onClick={() => handleUpdateNickname(newNickname)} className="btn btn-neutral rounded-l-none text-[clamp(1.5rem,3vw,2.5rem)] 
                                     px-6 h-full border-l-0">
                        完成
                    </button>
                  </div>)
                }
                <div className=" text-sm flex justify-center text-xs font-semibold opacity-60">
                    @{userDTO?.username}
                    <a onClick={() => setChangingNickname(true)} className="link ml-3">修改昵称</a>
                </div>
                {userDTO && <div className="mb-5 mt-3 text-sm flex justify-center text-xs font-semibold opacity-60">
                    注册于{utc2current(userDTO?.createTime)}
                </div>}
                <p className="text-red-500">{error}</p>
                
                
            </div>
            <div className="mt-10">
                <WhoIFollow />
            </div>
        </div>
        <div className="grid place-content-center p-4 bottom-0 bg-base-10 mb-40">
            <button onClick={() => setChangingUsnPwd(true)} className="btn w-full max-w-xs mb-5">修改账密</button>
            <button onClick={logout} className="btn w-full max-w-xs">退出登录</button>
        </div>
    </div>)
    
}
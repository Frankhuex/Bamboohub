import Auth from "../components/Auth";
import {httpService, ResponseData} from '../api/client'
import {useEffect, useState} from "react";
import { UserUpdateReq } from "../types/request.types";
import ChangeUsnPwd from "../components/ChangeUsnPwd";
import { utc2current } from "../utils/util";
import MyFollowing from "../components/MyFollowing";
export default function Profile() {
    


    const [userDTO, setUserDTO]=useState<UserDTO | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState<boolean>(false)
    const [loggedIn, setLoggedIn]=useState<boolean>(false)

    const [changingNickname, setChangingNickname]=useState<boolean>(false)
    const [newNickname, setNewNickname]=useState<string>("")

    const [changingUsnPwd, setChangingUsnPwd]=useState<boolean>(false)

    // const fetchProfile = async () => {
    //     setLoading(true)
    //     try {
    //         const response:ResponseData<UserDTO>=await httpService.empty<UserDTO>("/myProfile",'GET')
    //         if (response.success===false) {
    //             setError(response.errorMsg)
    //             setLoggedIn(false)
    //             return
    //         }
    //         setUserDTO(response.data)
    //     } catch (e) {
    //         if (e instanceof Error)
    //             setError(e.message)
    //     } finally {
    //         setLoading(false)
    //     }
    // }

    const logout = () => {
        setLoading(true)
        localStorage.removeItem("token")
        setLoggedIn(false)
        setLoading(false)
    }

    const handleUpdateNickname = async (newNickname: string) => {
        if (newNickname.trim().length===0 || newNickname===userDTO?.nickname) {
            setChangingNickname(false)
            return
        }
        setLoading(true)
        try {
            const userUpdateReq:UserUpdateReq={
                nickname: newNickname,
                username: ""
            }
            const response:ResponseData<UserDTO>=await httpService.json<UserDTO>("/myProfile/update", 'PUT',userUpdateReq)
            if (response.success===true) {
                setUserDTO(response.data)
            }
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
        const token = localStorage.getItem("token");
        // 仅在token存在且当前未登录时更新状态
        if (token && !loggedIn) {
          fetchData(); // 立即获取数据
        } else if (!token && loggedIn) {
          setLoggedIn(false);
        }
    }, [loggedIn, changingUsnPwd]); // 空依赖数组，仅在组件挂载时执行一次
      
    const fetchData = async () => {
        if (!localStorage.getItem("token")) return; // 二次验证
        setLoading(true);
        try {
            const response = await httpService.empty<UserDTO>("/myProfile", 'GET');
            if (response.success===true) {
                setUserDTO(response.data);
                setLoggedIn(true)
            } else {
                localStorage.removeItem("token"); // 清除无效token
                setLoggedIn(false);
            }
        } catch (e) {
          setError(e instanceof Error ? e.message : "未知错误");
          localStorage.removeItem("token");
          setLoggedIn(false);
        } finally {
          setLoading(false);
        }
    };

    if (!loggedIn) return (<Auth setLoggedIn={setLoggedIn} />)
    if (changingUsnPwd===true) return (<ChangeUsnPwd setChangingUsnPwd={setChangingUsnPwd} />)
    if (error) return <div className="text-center py-4 text-error">{error}</div>

    return (
    <div className="min-h-screen flex flex-col">
        {loading && <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>}
        <div className="flex-1">
            <div className="bg-base-100 border-base-300 border shadow-xs rounded-box">
                {/* <input type="checkbox" /> */}
                <div className="m-5 grid place-content-center text-[clamp(3rem,5vw,5rem)] font-black text-center">
                    Hello, {userDTO?.nickname}!
                </div>
                {changingNickname===true && 
                (<div className="flex items-stretch h-[5.5rem] place-content-center m-3">
                    <input
                        type="text"
                        placeholder={userDTO?.nickname}
                        className="input input-bordered text-[clamp(3rem,5vw,2rem)] font-black p-4 
                               rounded-r-none border-r-0 h-full"
                        onChange={(e)=>setNewNickname(e.target.value)}
                    />
                    <button onClick={() => handleUpdateNickname(newNickname)} className="btn btn-neutral rounded-l-none text-[clamp(1.5rem,3vw,2.5rem)] 
                                     px-6 h-full border-l-0">
                        完成
                    </button>
                  </div>)
                }
                {userDTO && <div>
                    <div className="text-sm flex justify-center text-xs font-semibold opacity-60">
                        @{userDTO?.username}
                        <div className="ml-2">
                            注册于{utc2current(userDTO?.createTime)}
                        </div>
                    </div>
                    <div className="mb-5 mt-3 text-sm flex justify-center text-xs font-semibold opacity-60">
                        <a onClick={() => setChangingNickname(true)} className="link ml-3">修改昵称</a>
                    </div>
                </div>}
                <p className="text-red-500">{error}</p> 
            </div>
            <div className="mt-10">
                <MyFollowing />
            </div>
        </div>
        <div className="grid place-content-center p-4 bottom-0 bg-base-10 mb-20 mt-10">
            <button onClick={() => setChangingUsnPwd(true)} className="btn bg-base-200 border-base-300 border w-full max-w-xs mb-5 shadow">修改账密</button>
            <button onClick={logout} className="btn bg-base-200 border-base-300 border w-full max-w-xs text-error shadow">退出登录</button>
        </div>
    </div>)
    
}
import Auth from "../components/Auth";
import {httpService, ResponseData} from '../api/client'
import {useEffect, useState} from "react";
import WhoIFollow from "../components/WhoIFollow";
export default function Profile() {
    


    const [userDTO, setUserDTO]=useState<UserDTO | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState<boolean>(false)
    const [loggedIn, setLoggedIn]=useState<boolean>(false)

    
    async function fetchProfile() {
        try {
            const response:ResponseData<UserDTO>=await httpService.get<UserDTO>("/myProfile")
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


    useEffect(() => {
        const token = localStorage.getItem("token")
        if (token) {
            setLoggedIn(true)
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
    }, [loggedIn])

    if (!loggedIn) return (<Auth setLoggedIn={setLoggedIn} />)
    if (loading) return <div><span className="loading loading-spinner loading-xl"></span></div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>

    return (
    <div className="min-h-screen flex flex-col">
        <div className="flex-1">
            <div className="collapse collapse-arrow bg-base-100 border-base-300 border">
                <input type="checkbox" />
                <div className="collapse-title grid place-content-center text-[clamp(3rem,5vw,5rem)] font-black">
                    Hello, {userDTO?.nickname}!
                </div>
                <div className="collapse-content text-sm flex justify-center text-xs font-semibold opacity-60">
                    @{userDTO?.username}
                    <a className="link ml-3">修改用户名和昵称</a>
                </div>
                
            </div>
            <div className="mt-10">
                <WhoIFollow />
            </div>
        </div>
        <div className="grid place-content-center p-4 bottom-0 bg-base-10 mb-40">
            <button className="btn btn-error w-full max-w-xs mb-5">修改密码</button>
            <button onClick={logout} className="btn btn-error w-full max-w-xs">退出登录</button>
        </div>
    </div>)
    
}
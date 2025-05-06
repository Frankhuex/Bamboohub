import { useEffect, useState } from "react";
import {httpService, ResponseData} from "../api/client"
import {BookReq} from "../types/request.types"
import { useNavigate } from "react-router-dom";

export default function CreateBook() {
    const [title, setTitle] = useState<string>("")
    const [scope, setScope] = useState<"ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE">("PRIVATE")

    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading]=useState<boolean>(false)

    const [isCreating, setIsCreating]=useState<boolean>(true)
    const navigate=useNavigate()

    const setTitleAndSave = async (title: string) => {
        setTitle(title)
        localStorage.setItem("createBookTitle", title)
    }

    const setScopeAndSave = async (scope: "ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE") => {
        setScope(scope)
        localStorage.setItem("createBookScope", scope)
    }

    const initTitleAndScope = async () => {
        const title:string = localStorage.getItem("createBookTitle")|| ""
        if (title && title.trim()!="") {
            setTitleAndSave(title)
        }
        const scope:string = localStorage.getItem("createBookScope")|| "PRIVATE"
        if (scope && ["ALLEDIT","ALLREAD","ALLSEARCH","PRIVATE"].includes(scope)) {
            setScopeAndSave(scope as "ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE")
        }
    }

    useEffect( () => {
        const fetchData = async () => {
            setLoading(true)
            try {
                await Promise.all([initTitleAndScope()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [])


    const createBook = async () => {
        if (title.trim() === "") {
            setError("书名不能为空")
            setIsCreating(true)
            return
        }
        const bookReq:BookReq={
            title: title,
            scope: scope
        }
        setLoading(true)
        try {
            const response:ResponseData<BookDTO> = await httpService.json<BookDTO>("/book","POST",bookReq);
            if (response.success===false) {
                setError(response.errorMsg)
            } else {
                setIsCreating(false)
                navigate("/reading/"+response.data.id)
            }
            
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }


    const createBookCard=(
        <div className="flex justify-center w-full">
            <form className="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4"
                onSubmit={(e)=>{e.preventDefault();createBook()}}>
                <legend className="fieldset-legend">创建书籍</legend>

                <label className="label">书名</label>
                <input onChange={(e) => setTitleAndSave(e.target.value)} type="text" className="input" placeholder="书名" value={title?title:""} />


                <label className="label mt-4">权限</label>
                <div className="flex items-center gap-2">
                    <ul className="menu menu-vertical bg-base-200 rounded-box w-auto">
                        <li className="m-1 w-auto" key="private">
                            <a 
                                className={scope === "PRIVATE" ? "menu-active" : ""} 
                                onClick={() => { setScopeAndSave("PRIVATE") }}>
                                私密：仅指定人可访问
                            </a>
                        </li>

                        <li className="m-1 w-auto" key="allsearch">
                            <a 
                                className={scope === "ALLSEARCH" ? "menu-active" : ""} 
                                onClick={() => { setScopeAndSave("ALLSEARCH") }}>
                                公开搜索：所有人可搜索到书名
                            </a>
                        </li>

                        <li className="m-1 w-auto" key="allread">
                            <a 
                                className={scope === "ALLREAD" ? "menu-active" : ""} 
                                onClick={() => { setScopeAndSave("ALLREAD") }}>
                                公开阅读：所有人可阅读
                            </a>
                        </li>

                        <li className="m-1 w-auto" key="alledit">
                            <a 
                                className={scope === "ALLEDIT" ? "menu-active" : ""} 
                                onClick={() => { setScopeAndSave("ALLEDIT") }}>
                                公开编辑：所有人可编辑
                            </a>
                        </li>
                    </ul>
                </div>

                <button className="btn btn-neutral mt-4" type="submit" >创建</button>
                <p className="text-red-500">{error}</p>
            </form>
        </div>
    )

    const successCard = (
        <div className="flex justify-center w-full ">
            <div className="bg-base-200 border-base-300 rounded-box w-xs border p-4 flex justify-center items-center flex-col">
                <h1>创建成功</h1>
                <button onClick={()=>{setTitleAndSave("");setIsCreating(true);}} className="btn btn-neutral mt-4">再创一本</button>
            </div>
        </div>
    )

    if (localStorage.getItem("token")===null) return <div className="text-center py-4 text-error">请先登录</div>
    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    return (<>
                {isCreating ? createBookCard : successCard}
                {/* {createBookCard} */}
            </>)
}
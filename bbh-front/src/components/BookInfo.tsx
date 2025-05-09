import { useEffect, useState } from "react";
import { toChinese, utc2current } from "../utils/util";
import { httpService, ResponseData } from "../api/client";
import { BookUpdateReq } from "../types/request.types";
import { useNavigate } from "react-router-dom";

interface BookInfoProps {
    bookId?: number;
    myRole?: RoleDTO|null;
    setBookOut: (book: BookDTO) => void;
}
export default function BookInfo({bookId=14, myRole=null, setBookOut}:BookInfoProps) {
    const [book, setBook]=useState<BookDTO|null>(null);

    const [editingBook, setEditingBook]=useState<boolean>(false);

    const [error, setError]=useState<string>("");
    const [loading, setLoading]=useState<boolean>(false);

    const [newTitle, setNewTitle]=useState<string>("");
    const [newScope, setNewScope]=useState<"ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE">(book?.scope || "PRIVATE");

    const navigate = useNavigate();

    const fetchBook = async () => {
        setLoading(true)
        try {
            const response:ResponseData<BookDTO>=await httpService.empty<BookDTO>(`/book/${bookId}`,'GET')
            if (response.success===false) {
                setError(response.errorMsg)
                return
            }
            const book:BookDTO=response.data
            setBook(book)
            setNewScope(book.scope)
            setNewTitle(book.title)
            setBookOut(book)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    const handleEditBook = async () => {
        if (newTitle.trim().length===0) {
            setNewTitle(book?.title || "")
        }
        if (book?.title===newTitle && book?.scope===newScope) {
            setLoading(false)
            setEditingBook(false)
            return
        }
        const bookUpdateReq: BookUpdateReq = {
            title: newTitle,
            scope: newScope
        }
        setLoading(true)
        try {
            const response:ResponseData<BookDTO>=await httpService.json<BookDTO>(`/book/${bookId}`,'PUT', bookUpdateReq)
            if (response.success===false) {
                setError(response.errorMsg)
                return
            }
            setBook(response.data)
            setBookOut(response.data)
            setEditingBook(false)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    const handleCancelEdit = async () => {
        setEditingBook(false)
        setNewTitle(book?.title || "")
        setNewScope(book?.scope || "PRIVATE")
    }
    
    const handleDeleteBook = async () => {
        if (!window.confirm(`确定要删除《${book?.title}》吗？`)) return
        setLoading(true)
        try {
            const response:ResponseData<boolean>=await httpService.empty<boolean>(`/book/${bookId}`,'DELETE')
            if (response.success===false) {
                setError(response.errorMsg)
                return
            }
            localStorage.removeItem("lastRead")
            navigate("/")
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true)
            try {
                await Promise.all([fetchBook()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [])

    const editBookCard=(
        <div className="flex justify-center w-full">
            <form className="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4"
                onSubmit={(e)=>{e.preventDefault();handleEditBook()}}>
                <legend className="fieldset-legend">编辑书籍</legend>

                <label className="label">书名</label>
                <input onChange={(e) => setNewTitle(e.target.value)} type="text" className="input" placeholder={book?(book?.title):"书名"} value={newTitle?newTitle:""} />


                <label className="label mt-4">权限</label>
                <div className="flex items-center gap-2">
                    <ul className="menu menu-vertical bg-base-200 rounded-box w-full">
                        <li className="m-1 w-auto" key="private">
                            <a 
                                className={newScope === "PRIVATE" ? "menu-active" : ""} 
                                onClick={() => { setNewScope("PRIVATE") }}>
                                私密：仅成员可访问
                            </a>
                        </li>

                        <li className="m-1 w-auto" key="allsearch">
                            <a 
                                className={newScope === "ALLSEARCH" ? "menu-active" : ""} 
                                onClick={() => { setNewScope("ALLSEARCH") }}>
                                公开搜索：所有人可搜索到书名
                            </a>
                        </li>

                        <li className="m-1 w-auto" key="allread">
                            <a 
                                className={newScope === "ALLREAD" ? "menu-active" : ""} 
                                onClick={() => { setNewScope("ALLREAD") }}>
                                公开阅读：所有人可阅读
                            </a>
                        </li>

                        <li className="m-1 w-auto" key="alledit">
                            <a 
                                className={newScope === "ALLEDIT" ? "menu-active" : ""} 
                                onClick={() => { setNewScope("ALLEDIT") }}>
                                公开编辑：所有人可编辑
                            </a>
                        </li>
                    </ul>
                </div>
                <div className="flex flex-row justify-between gap-2 mt-4 w-full">
                    {myRole && myRole.roleType==="OWNER" ?
                    (<><button className="btn btn-neutral text-error mt-4 w-35" type="button" 
                        onClick={handleDeleteBook}>删除</button>
                    <button className="btn btn-neutral mt-4 w-35" type="submit" >保存</button>
                    </>):(<>
                    <button className="btn btn-neutral mt-4 w-full" type="submit" >保存</button>
                    </>)}
                </div>
                
                <button className="btn btn-neutral mt-4 w-full" type="button" onClick={handleCancelEdit} >取消</button>
                <p className="text-red-500">{error}</p>
            </form>
        </div>
    )

    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>
    return (
    <div className="pb-4">
        <div className="flex-1">
            <div className="bg-base-100 border-base-300 shadow-xs border rounded-box">
                <div className="m-5 grid place-content-center text-[clamp(3rem,5vw,5rem)] font-black text-center">
                    {book?.title}
                </div>
                {editingBook && editBookCard}
                
                {book && <div>
                    <div className="text-sm flex justify-center text-xs font-semibold opacity-60 mt-4 mb-4">
                        {toChinese(book?.scope)}
                        <div className="ml-2">
                            {utc2current(book?.createTime)}
                        </div>
                    </div>
                    {!editingBook && myRole && myRole.roleType!=="VIEWER" && 
                    <div className="mb-5 mt-3 text-sm flex justify-center text-xs font-semibold opacity-60">
                        <a className="link ml-3" onClick={()=>setEditingBook(true)}>
                            编辑
                        </a>
                    </div>}
                </div>}
                <p className="text-red-500">{error}</p> 
            </div>
        </div>
    </div>
    );
}
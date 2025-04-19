import { useEffect, useState } from "react";
import { httpService, ResponseData } from "../api/client";
import BookFilter from "../components/BookFilter";

interface BookFilterPageProps {
    url:"mine"|"plaza";
    defaultClassifiedBy: "scope"|"roleType"|"null";
    defaultSortedBy: "title"|"createTime";
}
export default function BookFilterPage({url="plaza",defaultClassifiedBy="roleType",defaultSortedBy="title"}:BookFilterPageProps) {
    const [books, setBooks]=useState<BookDTOWithRole[]|null>(null)
        const [loading, setLoading]=useState<boolean>(false)
        const [error, setError]=useState<string|null>(null)
        const [query, setQuery]=useState<string>("")
        const [sortedBy, setSortedBy]=useState<"title"|"createTime">(defaultSortedBy)
        const [classifiedBy, setClassifiedBy]=useState<"scope"|"roleType"|"null">(defaultClassifiedBy)
    
        async function fetchBooks() {
            try {
                const response: ResponseData<BookDTOWithRole[]> = await httpService.get<BookDTOWithRole[]>(`/books/${url}`)
                if (response.success===false) {
                    setError(response.errorMsg)
                    return
                }
                console.log(response.data)
                setBooks(response.data)
            } catch (error) {
                if (error instanceof Error)
                    setError(error.message)
            }
        }
    
        useEffect( () => {
            const fetchData = async () => {
                setLoading(true)
                try {
                    await Promise.all([fetchBooks()])
                } finally {
                    setLoading(false)
                }
            }
            fetchData()
        }, [])
    
        if (loading) return (<div><span className="loading loading-spinner loading-xl"></span></div>)
        if (error) return <div className="text-center py-4 text-error">{error}</div>
    
        return (<div>
            <div className="flex mt-7 justify-center">
                <label className="input flex items-center gap-4 w-full">
                    <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                        <g
                        strokeLinejoin="round"
                        strokeLinecap="round"
                        strokeWidth="2.5"
                        fill="none"
                        stroke="currentColor"
                        >
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.3-4.3"></path>
                        </g>
                    </svg>
                    <input onChange={(e) => setQuery(e.target.value)} type="search" className="grow" placeholder="搜索书名" />
                </label>
            </div>
            <div className="flex flex-row justify-between items-center gap-6 mt-7"> {/* 增加间隙并居中对齐 */}
                <div className="flex items-center gap-2">
                    <span className="font-semibold">分类：</span>
                    <ul className="menu menu-horizontal bg-base-200 pr-4 rounded-box">
                        <li className="w-full m-1 md:w-auto">
                            <a
                            className={classifiedBy === "roleType" ? "menu-active" : ""}
                            onClick={() => { setClassifiedBy("roleType") }}>
                            我的身份
                            </a>
                        </li>
                        <li className="w-full m-1 md:w-auto">
                            <a 
                            className={classifiedBy === "scope" ? "menu-active" : ""} 
                            onClick={() => { setClassifiedBy("scope") }}>
                            书本权限
                            </a>
                        </li>
                        <li className="w-full m-1 md:w-auto">
                            <a 
                            className={classifiedBy === "null" ? "menu-active" : ""} 
                            onClick={() => { setClassifiedBy("null") }}>
                            无
                            </a>
                        </li>
                    </ul>
                </div>
    
                {/* 排序选项组 */}
                <div className="flex items-center gap-2">
                    <span className="font-semibold">排序：</span>
                    <ul className="menu menu-horizontal bg-base-200 pr-4 rounded-box">
                        <li className="w-full m-1 md:w-auto">
                            <a 
                            className={sortedBy === "title" ? "menu-active" : ""}
                            onClick={() => { setSortedBy("title") }}>
                            书名
                            </a>
                        </li>
                        <li className="w-full m-1 md:w-auto">
                            <a 
                            className={sortedBy === "createTime" ? "menu-active" : ""} 
                            onClick={() => { setSortedBy("createTime") }}>
                            创建时间
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
            
            
    
            
            <BookFilter books={books} query={query} sortedBy={sortedBy} classifiedBy={classifiedBy} />
            </div>)
}
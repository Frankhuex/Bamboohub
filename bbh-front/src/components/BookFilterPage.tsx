import { useState } from "react";
import BookFilter from "../components/BookFilter";

interface BookFilterPageProps {
    books: BookDTOWithRole[] | null;
    defaultClassifiedBy?: "scope"|"roleType"|"null";
    defaultSortedBy?: "title"|"createTime";
    showSearchBox?: boolean;
    showClassifiedBy?: boolean;
    showSortedBy?: boolean;
}
export default function BookFilterPage({books,defaultClassifiedBy="roleType",defaultSortedBy="title",showSearchBox=true,showClassifiedBy=true,showSortedBy=true}:BookFilterPageProps) {
    const [query, setQuery]=useState<string>("")
    const [sortedBy, setSortedBy]=useState<"title"|"createTime">(defaultSortedBy)
    const [classifiedBy, setClassifiedBy]=useState<"scope"|"roleType"|"null">(defaultClassifiedBy)
    

    return (<div>
        {showSearchBox && (<div className="flex mt-7 justify-center">
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
        </div>)}
        <div className="flex justify-between gap-6 mt-7 flex-col md:flex-row"> {/* 增加间隙并居中对齐 */}
            {showClassifiedBy && (<div className="flex items-center gap-2">
                <span className="font-semibold">分类：</span>
                <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto">
                    <li className="m-1 w-auto">
                        <a
                        className={classifiedBy === "roleType" ? "menu-active" : ""}
                        onClick={() => { setClassifiedBy("roleType") }}>
                            我的身份
                        </a>
                    </li>
                    <li className="m-1 w-auto">
                        <a 
                        className={classifiedBy === "scope" ? "menu-active" : ""} 
                        onClick={() => { setClassifiedBy("scope") }}>
                            书本权限
                        </a>
                    </li>
                    <li className="m-1 w-auto">
                        <a
                        className={classifiedBy === "null" ? "menu-active" : ""} 
                        onClick={() => { setClassifiedBy("null") }}>
                            无
                        </a>
                    </li>
                </ul>
            </div>)}

            {/* 排序选项组 */}
            {showSortedBy && (<div className="flex items-center gap-2">
                <span className="font-semibold">排序：</span>
                <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto">
                    <li className="m-1 w-auto">
                        <a 
                        className={sortedBy === "title" ? "menu-active" : ""}
                        onClick={() => { setSortedBy("title") }}>
                        书名
                        </a>
                    </li>
                    <li className="m-1 w-auto">
                        <a 
                        className={sortedBy === "createTime" ? "menu-active" : ""} 
                        onClick={() => { setSortedBy("createTime") }}>
                        创建时间
                        </a>
                    </li>
                </ul>
            </div>)}
        </div>
        
        

        
        <BookFilter books={books} query={query} sortedBy={sortedBy} classifiedBy={classifiedBy} />
        </div>)
}
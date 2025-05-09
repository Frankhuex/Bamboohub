import { useEffect, useState } from "react";
import BookFilter from "../components/BookFilter";

interface BookFilterPageProps {
    books: BookDTOWithRole[] | null;
    defaultClassifiedBy?: "scope"|"roleType"|"null";
    defaultSortedBy?: "title"|"createTime";
    showSearchBox?: boolean;
    showClassifiedBy?: boolean;
    showSortedBy?: boolean;
    name?: string;
}
export default function BookFilterPage({books,defaultClassifiedBy="null",defaultSortedBy="createTime",showSearchBox=true,showClassifiedBy=true,showSortedBy=true,name}:BookFilterPageProps) {
    const [query, setQuery]=useState<string>("")
    const [sortedBy, setSortedBy]=useState<"title"|"createTime">(defaultSortedBy)
    const [classifiedBy, setClassifiedBy]=useState<"scope"|"roleType"|"null">(defaultClassifiedBy)
    
    const setQueryAndSave = (query: string) => {
        setQuery(query);
        if (name && name.trim()!== "") localStorage.setItem(`${name}_query`, query);
    };

    const setSortedByAndSave = (sortedBy: "title"|"createTime") => {
        setSortedBy(sortedBy);
        if (name && name.trim()!== "") localStorage.setItem(`${name}_sortedBy`, sortedBy);
    };

    const setClassifiedByAndSave = (classifiedBy: "scope"|"roleType"|"null") => {
        setClassifiedBy(classifiedBy);
        if (name && name.trim()!== "") localStorage.setItem(`${name}_classifiedBy`, classifiedBy);
    };

    const initInputs = async () => { 
        if (name && name.trim() !== "") {
            const savedQuery:string = localStorage.getItem(`${name}_query`) || "";
            const savedSortedBy:string = localStorage.getItem(`${name}_sortedBy`) || defaultSortedBy;
            const savedClassifiedBy:string = localStorage.getItem(`${name}_classifiedBy`) || defaultClassifiedBy;
            setQuery(savedQuery);
            if (['title', 'createTime'].includes(savedSortedBy))
                setSortedBy(savedSortedBy as "title"|"createTime");
            if (['null', 'scope', 'roleType'].includes(savedClassifiedBy))
                setClassifiedBy(savedClassifiedBy as "scope"|"roleType"|"null");
        }
    };

    useEffect( () => {
        const fetchData = async () => {
            try {
                await Promise.all([initInputs()])
            } catch (error) {
                console.error("Error initializing inputs:", error);
            }
        }
        fetchData()
    }, [])




    return (<div className="mb-20" >
        {showSearchBox && (
        <div className="flex mt-7 ml-4 mr-4 justify-center">
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
                <input onChange={(e) => setQueryAndSave(e.target.value)} type="search" className="grow" placeholder="搜索书名" value={query?query:""} />
                {query && (
                <button
                    type="button"
                    onClick={() => {
                        setQueryAndSave('');
                    }}
                    className="absolute right-2 btn btn-ghost btn-xs btn-circle opacity-50 hover:opacity-100"
                >
                    {/* 关闭图标 */}
                    <svg 
                        xmlns="http://www.w3.org/2000/svg" 
                        viewBox="0 0 20 20" 
                        fill="currentColor" 
                        className="w-4 h-4"
                    >
                        <path d="M6.28 5.22a.75.75 0 00-1.06 1.06L8.94 10l-3.72 3.72a.75.75 0 101.06 1.06L10 11.06l3.72 3.72a.75.75 0 101.06-1.06L11.06 10l3.72-3.72a.75.75 0 00-1.06-1.06L10 8.94 6.28 5.22z" />
                    </svg>
                </button>
                )}
            </label>
        </div>)}
        <div className="flex justify-between gap-6 mt-7 ml-4 mr-4 flex-col md:flex-row"> {/* 增加间隙并居中对齐 */}
            {showClassifiedBy && (<div>
                <span className="font-semibold">分类：</span>
                <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto shadow mt-2">
                    <li className="m-1 w-auto" key="null">
                        <a
                        className={classifiedBy === "null" ? "menu-active" : ""} 
                        onClick={() => { setClassifiedByAndSave("null") }}>
                            无
                        </a>
                    </li>
                    
                    <li className="m-1 w-auto" key="scope">
                        <a 
                        className={classifiedBy === "scope" ? "menu-active" : ""} 
                        onClick={() => { setClassifiedByAndSave("scope") }}>
                            书本权限
                        </a>
                    </li>

                    <li className="m-1 w-auto" key="roleType">
                        <a
                        className={classifiedBy === "roleType" ? "menu-active" : ""}
                        onClick={() => { setClassifiedByAndSave("roleType") }}>
                            我的身份
                        </a>
                    </li>
                    
                </ul>
            </div>)}

            {/* 排序选项组 */}
            {showSortedBy && (<div>
                <span className="font-semibold">排序：</span>
                <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto shadow mt-2">
                    <li className="m-1 w-auto" key="createTime">
                        <a 
                        className={sortedBy === "createTime" ? "menu-active" : ""} 
                        onClick={() => { setSortedByAndSave("createTime") }}>
                        创建时间
                        </a>
                    </li>

                    <li className="m-1 w-auto" key="title">
                        <a 
                        className={sortedBy === "title" ? "menu-active" : ""}
                        onClick={() => { setSortedByAndSave("title") }}>
                        书名
                        </a>
                    </li>
                    
                </ul>
            </div>)}
        </div>
        
        

        
        <BookFilter books={books} query={query} sortedBy={sortedBy} classifiedBy={classifiedBy} />
        </div>)
}
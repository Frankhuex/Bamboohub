import { useEffect, useState } from "react"
import BookFilterPage from "../components/BookFilterPage"
import { httpService, ResponseData } from "../api/client"
import UserList from "../components/UserList"
import ParaSearchResult from "../components/ParaSearchResult"

export default function Search() {
    const [query, setQuery] = useState<string>("")
    const [type, setType]=useState<"book"|"user"|"paragraph">("book")
    

    const [books, setBooks]=useState<BookDTOWithRole[]>([])
    const [paraSearchItems, setParaSearchItems]=useState<ParaSearchItem[]>([])
    const [users, setUsers]=useState<UserDTOWithFollow[]>([])

    const [loading, setLoading]=useState<boolean>(false)
    const [error, setError]=useState<string|null>(null)
    

    const initLast = async () => {
        setQuery(localStorage.getItem("searchQuery")||"")
        setType((localStorage.getItem("searchType") as "book"|"user"|"paragraph")||"book")
        setLoading(true)
        const searchBooksString: string|null = localStorage.getItem("searchBooksResult")
        const searchParagraphsString: string|null = localStorage.getItem("searchParagraphsResult")
        const searchUsersString: string|null = localStorage.getItem("searchUsersResult")
        if (searchBooksString) {
            try {
                const searchBooks: BookDTOWithRole[] = JSON.parse(searchBooksString)
                setBooks(searchBooks)
            } catch (error) {
                console.log(error)
            }
        }
        if (searchParagraphsString) {
            try {
                const searchParagraphs: ParaSearchDTO = JSON.parse(searchParagraphsString)
                setParaSearchItems(searchParagraphs.paraSearchItems)
            } catch (error) {
                console.log(error)
            }
        }
        if (searchUsersString) {
            try {
                const searchUsers: UserDTOWithFollow[] = JSON.parse(searchUsersString)
                setUsers(searchUsers)
            } catch (error) {
                console.log(error)
            }
        }     
        setLoading(false)

    }

    useEffect(() => {
        initLast()
    }, [])

    const setTypeAndSave = (type: "book"|"user"|"paragraph") => {
        setType(type)
        localStorage.setItem("searchType", type);
    }

    const fetchBooks = async () => {
        setLoading(true)
        try {
            const response: ResponseData<BookDTOWithRole[]> = await httpService.form<BookDTOWithRole[]>(`/books/search?title=${query}`, "GET")
            if (response.success===false) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            setBooks(response.data)
            localStorage.setItem("searchBooksResult", JSON.stringify(response.data))
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const fetchUsers = async () => {
        setLoading(true)
        try {
            const response: ResponseData<UserDTOWithFollow[]> = await httpService.form<UserDTOWithFollow[]>(`/users/search?query=${query}`, "GET")
            if (response.success===false) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            setUsers(response.data)
            localStorage.setItem("searchUsersResult", JSON.stringify(response.data))
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const fetchParagraphs = async () => {
        setLoading(true)
        try {
            const response: ResponseData<ParaSearchDTO> = await httpService.form<ParaSearchDTO>(`/paragraphs/search?query=${query}`, "GET")
            if (response.success===false) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            setParaSearchItems(response.data.paraSearchItems);
            localStorage.setItem("searchParagraphsResult", JSON.stringify(response.data.paraSearchItems))
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    const searchData = async () => {
        fetchBooks();
        fetchUsers();
        fetchParagraphs();
    }



    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) {return <div className="text-center py-4 text-error">{error}</div>}
    return (<>
        <h1 className="text-xl mt-5 ml-4 mr-4 flex justify-center">这里能搜索全站所有公开搜索的内容，以及您可访问的内容。</h1>
        <form onSubmit={() => searchData()} className="flex mt-7 justify-center ml-4 mr-4">
            <label className="input flex items-center gap-4 w-full rounded-r-none">
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
                <input onChange={(e) => {
                    setQuery(e.target.value);
                    localStorage.setItem("searchQuery", e.target.value);
                    }} type="search" className="grow" placeholder="搜索" value={query?query:""} />
                {query && (
                <button
                    type="button"
                    onClick={() => {
                        setQuery('');
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
            <button onClick={() => searchData()} className="btn btn-neutral rounded-l-none px-6 h-auto border-l-0" type="submit">
                搜索
            </button>
        </form>
        <div className="flex flex-row justify-between items-center gap-6 mt-7"> 
            <div>
                <span className="font-semibold ml-4 mr-4">搜索类型：</span>
                <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto md:w-auto shadow ml-4 mr-4 mt-2">
                    <li className="w-auto m-1 md:w-auto" key="book">
                        <a
                            className={type === "book" ? "menu-active" : ""}
                            onClick={() => { setTypeAndSave("book") }}>
                            书名
                        </a>
                    </li>
                    <li className="w-auto m-1 md:w-auto" key="paragraph">
                        <a 
                            className={type === "paragraph" ? "menu-active" : ""} 
                            onClick={() => { setTypeAndSave("paragraph") }}>
                            段落
                        </a>
                    </li>
                    <li className="w-auto m-1 md:w-auto" key="user">
                        <a
                            className={type === "user" ? "menu-active" : ""} 
                            onClick={() => { setTypeAndSave("user") }}>
                            用户
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div className="mt-10 mb-15">
            <span className="font-semibold ml-4 mr-4 mb-4">搜索结果：</span>
            {type==="book" && books && books.length>0 && <BookFilterPage books={books} defaultClassifiedBy="null" defaultSortedBy="title" showSearchBox={false} />}
            {type==="paragraph" && paraSearchItems && paraSearchItems.length>0 && <ParaSearchResult paraSearchItems={paraSearchItems} searchQuery={query} />}
            {type==="user" && users && users.length>0 && <UserList userDTOs={users} setUserDTOs={setUsers} />}
        </div>
        
    </>)
}
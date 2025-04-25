import { useState } from "react"
import BookFilterPage from "../components/BookFilterPage"
import { httpService, ResponseData } from "../api/client"
import UserList from "../components/UserList"

export default function Search() {
    const [query, setQuery] = useState<string|null>("")
    const [type, setType]=useState<"book"|"user"|"paragraph">("book")
    const [loading, setLoading]=useState<boolean>(false)
    const [error, setError]=useState<string|null>(null)

    const [books, setBooks]=useState<BookDTOWithRole[]|null>(null)
    const [users, setUsers]=useState<UserDTOWithFollow[]|null>(null)
    

    const fetchBooks = async () => {
        setLoading(true)
        try {
            const response: ResponseData<BookDTOWithRole[]> = await httpService.form<BookDTOWithRole[]>(`/books/search?title=${query}`, "GET")
            if (response.success===false) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
            console.log(response.data)
            setBooks(response.data)
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
            console.log(response.data)
            setUsers(response.data)
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
    }



    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) {return <div className="text-center py-4 text-error">{error}</div>}
    return (<>
        <h1 className="text-xl mt-5 flex justify-center">这里能搜索全站所有公开搜索的内容，以及您可访问的内容。</h1>
        <form onSubmit={() => searchData()} className="flex mt-7 justify-center">
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
                <input onChange={(e) => setQuery(e.target.value)} type="search" className="grow" placeholder="搜索" value={query?query:""} />
            </label>
            <button onClick={() => searchData()} className="btn btn-neutral rounded-l-none px-6 h-auto border-l-0" type="submit">
                搜索
            </button>
        </form>
        <div className="flex flex-row justify-between items-center gap-6 mt-7"> 
            <div className="flex items-center gap-2">
                <span className="font-semibold">搜索类型：</span>
                <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto md:w-auto">
                    <li className="w-auto m-1 md:w-auto">
                        <a
                            className={type === "book" ? "menu-active" : ""}
                            onClick={() => { setType("book") }}>
                            书名
                        </a>
                    </li>
                    <li className="w-auto m-1 md:w-auto">
                        <a 
                            className={type === "paragraph" ? "menu-active" : ""} 
                            onClick={() => { setType("paragraph") }}>
                            段落
                        </a>
                    </li>
                    <li className="w-auto m-1 md:w-auto">
                        <a
                            className={type === "user" ? "menu-active" : ""} 
                            onClick={() => { setType("user") }}>
                            用户
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div className="mt-10 mb-15">
            <span className="font-semibold">搜索结果：</span>
            {type==="book" && books && books.length>0 && <BookFilterPage books={books} defaultClassifiedBy="null" defaultSortedBy="title" showSearchBox={false} />}
            {type==="user" && users && users.length>0 && <UserList userDTOs={users} setUserDTOs={setUsers} />}
        </div>
        
    </>)
}
import { useEffect, useState } from "react";
import { httpService, ResponseData } from "../api/client";
import BookFilterPage from "../components/BookFilterPage";

interface BookFilterPageWithURLProps {
    url:"mine"|"plaza";
    defaultClassifiedBy?: "scope"|"roleType"|"null";
    defaultSortedBy?: "title"|"createTime";
}
export default function BookFilterPageWithURL({url="plaza",defaultClassifiedBy="null",defaultSortedBy="createTime"}:BookFilterPageWithURLProps) {
    const [books, setBooks]=useState<BookDTOWithRole[]|null>(null)
    const [loading, setLoading]=useState<boolean>(false)
    const [error, setError]=useState<string|null>(null)

    const fetchBooks = async () => {
        setLoading(true)
        try {
            const response: ResponseData<BookDTOWithRole[]> = await httpService.empty<BookDTOWithRole[]>(`/books/${url}`, "GET")
            if (response.success===false) {
                setError(response.errorMsg)
                return
            }
            setBooks(response.data)
        } catch (error) {
            if (error instanceof Error)
                setError(error.message)
        } finally {
            setLoading(false)
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

    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) {
        if (!localStorage.getItem("token")) {
            return <div className="text-center py-4 text-error">请先登录</div>
        }
        return <div className="text-center py-4 text-error">{error}</div>
    }
    

    return (<BookFilterPage books={books} defaultClassifiedBy={defaultClassifiedBy} defaultSortedBy={defaultSortedBy} name={url} />)
}
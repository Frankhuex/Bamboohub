import { useEffect, useState } from "react";
import { toChinese, utc2current } from "../utils/util";
import { httpService, ResponseData } from "../api/client";
import { BookUpdateReq } from "../types/request.types";

interface ReadingProps {
    bookId?: number;
}
export default function Reading({bookId=14}:ReadingProps) {
    const [book, setBook]=useState<BookDTO|null>(null);
    const [paragraphs, setParagraphs]=useState<ParagraphDTO[]>([]);

    const [error, setError]=useState<string>("");
    const [loading, setLoading]=useState<boolean>(false);

    const fetchParagraphs = async () => {
        setLoading(true)
        try {
            const response:ResponseData<ParagraphDTO[]>=await httpService.empty<ParagraphDTO[]>(`/book/${bookId}/paragraphs`,'GET')
            if (response.success===false) {
                setError(response.errorMsg)
                return
            }
            setParagraphs(response.data)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const token = localStorage.getItem("token")
        const fetchData = async () => {
            if (!token) return
            setLoading(true)
            try {
                await Promise.all([fetchParagraphs()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [])


    return (<div></div>)
}
import { useEffect, useState, useRef } from "react";
import { httpService, ResponseData } from "../api/client";
import ParaRead from "./ParaRead";

interface ReadingProps {
    bookId: number;
    bookDTO: BookDTO|null;
    myRole?: RoleDTO|null;
}
export default function Reading({bookId,bookDTO,myRole}:ReadingProps) {
    const [paragraphs, setParagraphs]=useState<ParagraphDTO[]>([]);
    const paraRefs=useRef<(HTMLDivElement|null)[]>([]);

    const [error, setError]=useState<string>("");
    const [loading, setLoading]=useState<boolean>(false);

    const scrollToId = window.location.hash.match(/#scrollTo=(\d+)/)?.[1];
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

    // 滚动到指定索引的段落
    const scrollToIndex = (index: number) => {
        const wrapperDiv = paraRefs.current[index];
        if (wrapperDiv) {
            wrapperDiv.scrollIntoView({ 
                behavior: "smooth", 
                block: "center", // 滚动后目标段落居中
            });
        }
    };
    
    // 根据段落 ID 查找索引并滚动
    const scrollToParaId = (paraId: number) => {
        const index = paragraphs.findIndex(p => p.id === paraId);
        if (index !== -1) {
            scrollToIndex(index);
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true)
            try {
                await Promise.all([fetchParagraphs()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [bookId, bookDTO, myRole])

    useEffect(() => {
        if (!scrollToId || paragraphs.length === 0) return;
    
        // 用 setTimeout 等待 DOM 更新完成 （更保险）
        const timer = setTimeout(() => {
            scrollToParaId(parseInt(scrollToId));
            window.location.hash = ""; // 清除 URL 中的锚点参数
        }, 100);
    
        return () => clearTimeout(timer);
    }, [paragraphs]);

    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>
    return (<div>
        <div className="flex justify-center mt-4">
            <button className="btn shadow" onClick={()=>scrollToIndex(paragraphs.length-2)}>跳到末尾</button>
        </div>
        {paragraphs.filter(p => p.id!==bookDTO?.endParaId).map(
            (paraDTO:ParagraphDTO, index:number) => (
            <div key={paraDTO.id}
                ref={(el) => {paraRefs.current[index]=el}}>
                <ParaRead 
                    paraDTO={paraDTO} 
                    bookDTO={bookDTO} 
                    myRole={myRole} 
                    setParagraphs={setParagraphs} 
                    paragraphs={paragraphs}
                />
            </div>
        ))}
        <div className="flex justify-center mt-4">
            <button className="btn shadow" onClick={()=>scrollToIndex(0)}>跳到开头</button>
        </div>
    </div>)
}
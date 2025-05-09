import { useEffect, useState } from "react";
import BookInfo from "../components/BookInfo";
import BookRoles from "../components/BookRoles";
import { useParams } from "react-router-dom";
import Paragraphs from "../components/Paragraphs";

export default function Reading() {
    const { bookId } = useParams<{ bookId?: string }>();
    const [bookDTO, setBookDTO]=useState<BookDTO|null>(null);
    const [myRole, setMyRole]=useState<RoleDTO|null>(null);
    
    useEffect(() => {
        if (!bookId) {
            console.log("bookId is undefined");
            return;
        }
        localStorage.setItem("lastRead", bookId.toString());
    },[bookId])

    if (!bookId) return <div className="text-center py-4">这里展示正在阅读的书。</div>
    return (<>
    <div className="mb-40">
        <BookInfo bookId={Number(bookId)} myRole={myRole} setBookOut={setBookDTO} />
        <BookRoles bookId={Number(bookId)} setRoleOut={setMyRole} />
        <Paragraphs bookId={Number(bookId)} bookDTO={bookDTO} myRole={myRole} />
    </div>
    </>)
}
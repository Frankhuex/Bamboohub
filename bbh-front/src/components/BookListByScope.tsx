import {httpService, ResponseData} from '../api/client'
import { useEffect, useState } from "react"
import {toChinese, utc2current} from '../utils/util'

interface Props {
    scope: 'allread'|'alledit'|'allsearch'|'private'
}

export default function BookListByScope({scope}:Props) {
    const [books, setBooks] = useState<BookDTO[] | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const fetchBooks = async () => {
        try {
            const response: ResponseData<BookDTO[]> = await httpService.get<BookDTO[]>(`/books/${scope}`)
            const sortedBooks: BookDTO[] = [...response.data].sort((b1, b2) => b1.title.localeCompare(b2.title,'zh-CN'))
            setBooks(sortedBooks)
            console.log(sortedBooks)
        } catch (err) {
            setError('Failed to fetch books')
            console.error(err)
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
    }, [scope])
  
    if (loading) return <div className="text-center py-4">Loading...</div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>
  
    const renderBookItem = (book: BookDTO) => (
        <li className="list-row" key={book.id}>
            <div>
                <img 
                    className="size-10 rounded-box" 
                    src="https://img.daisyui.com/images/profile/demo/1@94.webp" 
                    alt={book.title}
                />
            </div>
            <div>
                <div>{book.title}</div>
                <div className="text-xs uppercase font-semibold opacity-60">{toChinese(book.scope)}</div>
                <div className="text-xs uppercase font-semibold opacity-60">{utc2current(book.createTime)}</div>
            </div>
        </li>
    )
  
    return (
      <div className="space-y-4">
        <div>
            <h2 className="text-xl font-bold mb-2">{toChinese(scope)}</h2>
            <ul className="list bg-base-100 rounded-box shadow-md">
                {books?.map(renderBookItem)}
            </ul>
        </div>
      </div>
    )
  }
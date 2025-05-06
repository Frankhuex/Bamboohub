import { useMemo } from "react";
import { toChinese, utc2current } from "../utils/util";
import { Link } from "react-router-dom";

interface BookFilterProps {
    books: BookDTOWithRole[] | null;
    query: string;
    sortedBy: 'title' | 'createTime';
    classifiedBy: 'scope' | 'roleType'|'null';
}

// 将过滤分类逻辑提取为工具函数
const filterAndClassifyBooks = (
    books: BookDTOWithRole[] | null,
    query: string,
    sortedBy: 'title' | 'createTime',
    classifiedBy: 'scope' | 'roleType'|'null',
): BookDTOWithRole[][] => {
    if (!books) return [];

    // 过滤逻辑
    const filtered = query 
        ? books.filter(book => book.title.toLowerCase().includes(query.toLowerCase())) 
        : [...books];

    if (classifiedBy === 'null') return [
        filtered.sort((a, b) => sortedBy === 'title' 
        ? a.title.localeCompare(b.title,'zh-CN') 
        : new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
    ];

    // 分类配置
    const classificationMap = {
        scope: ["PRIVATE","ALLSEARCH","ALLREAD","ALLEDIT"] as const,
        roleType: ["OWNER", "ADMIN", "EDITOR", "VIEWER", null] as const
    };
    const categories = classificationMap[classifiedBy];

    // 分类并排序
    return categories.map(category => {
        const group = filtered.filter(book => 
            classifiedBy === 'scope' ? book.scope === category : book.roleType === category
        );
        return group.sort((a, b) => sortedBy === 'title' 
            ? a.title.localeCompare(b.title,'zh-CN') 
            : new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
        );
    });
};

const BookItem = ({ book }: { book: BookDTOWithRole }) => (
  
    <Link to={`/reading/${book.id}`} draggable="false"
        className="list-row 
        active:bg-gray-200 
        hover-hover:hover:bg-gray-50
        transition-colors 
        duration-100
        cursor-pointer"
        key={book.id}>
            <div> {/* 图片容器 */}
                <img 
                    className="size-10 rounded-box" 
                    src="https://img.daisyui.com/images/profile/demo/1@94.webp" 
                    alt={book.title}
                />
            </div>
            <div>
                <div>{book.title}</div>
                <div className="text-xs font-semibold opacity-60">{toChinese(book.scope)+" "+toChinese(book.roleType)}</div>
                <div className="text-xs font-semibold opacity-60">{utc2current(book.createTime)}</div>
            </div>
    </Link>
    
  
);

const GroupTab = ({ group, label, isFirst }: { 
    group: BookDTOWithRole[]; 
    label: string; 
    isFirst: boolean 
}) => (
    group.length > 0 && (
        <>
            <input 
                type="radio" 
                name="my_tabs_1" 
                className="tab" 
                aria-label={label}
                defaultChecked={isFirst} 
            />
            <div className="tab-content border-base-300 bg-base-100 p-0 space-y-4">
                <ul className="list bg-base-100 rounded-box shadow-md">
                    {group.map(book => <BookItem key={book.id} book={book} />)}
                </ul>
            </div>
        </>
    )
);

export default function BookFilter({ books, query, sortedBy, classifiedBy }: BookFilterProps) {
    const groups: BookDTOWithRole[][] = useMemo(
        () => filterAndClassifyBooks(books, query, sortedBy, classifiedBy),
        [books, query, sortedBy, classifiedBy]
    ).filter(g => g.length > 0);

    if (!books) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>;
    if (groups.every(g => g.length === 0)) return <div className="text-center py-8">暂无数据</div>;

    return (
        <div className="tabs tabs-lift mx-auto w-full flex flex-none justify-start mt-10">
            {groups.map((group, index) => {
                const label = (classifiedBy==='null'
                    ?'全部':(classifiedBy === 'scope' 
                    ? toChinese(group[0]?.scope):toChinese(group[0]?.roleType)))
                    +"（"+group.length+"）"
                return (
                    <GroupTab 
                        key={label || index}
                        group={group}
                        label={toChinese(label)}
                        isFirst={index === 0}
                    />
                );
            })}
        </div>
    );
}
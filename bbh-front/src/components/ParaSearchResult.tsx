import { JSX, useState } from "react";
import { Link } from "react-router-dom";

interface ParaSearchResultProps {
    paraSearchItems: ParaSearchItem[];
    searchQuery: string;
}

function bestWindow(text: string, pattern: string, maxLength: number): { start: number, end: number } {
    if (maxLength <= 0 || pattern.length === 0 || text.length === 0) {
        return { start: 0, end: Math.min(maxLength - 1, text.length - 1) };
    }

    // 查找所有完整pattern出现的位置
    const matches: Array<{ start: number, end: number }> = [];
    let pos = text.indexOf(pattern);
    while (pos !== -1) {
        matches.push({ start: pos, end: pos + pattern.length - 1 });
        pos = text.indexOf(pattern, pos + 1);
    }

    // 边界情况处理
    if (matches.length === 0 || pattern.length > maxLength) {
        return {
            start: 0,
            end: Math.min(maxLength - 1, text.length - 1)
        };
    }

    // 二分查找工具函数
    const findPrev = (target: number) => {
        let low = 0, high = matches.length - 1, res = -1;
        while (low <= high) {
            const mid = (low + high) >> 1;
            if (matches[mid].end < target) {
                res = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    };

    const findNext = (target: number) => {
        let low = 0, high = matches.length - 1, res = -1;
        while (low <= high) {
            const mid = (low + high) >> 1;
            if (matches[mid].start > target) {
                res = mid;
                high = mid - 1; 
            } else {
                low = mid + 1;
            }
        }
        return res;
    };

    // 滑动窗口算法优化
    let best = { count: 0, start: 0, end: 0, balance: -Infinity };
    let left = 0;

    for (let right = 0; right < matches.length; right++) {
        // 维护窗口长度约束
        while (matches[right].end - matches[left].start + 1 > maxLength) {
            left++;
        }

        // 计算当前窗口特征
        const currentStart = matches[left].start;
        const currentEnd = matches[right].end;
        const currentCount = right - left + 1;

        // 计算窗口两侧可用空间
        const prevIdx = findPrev(currentStart);
        const nextIdx = findNext(currentEnd);
        const prevEnd = prevIdx === -1 ? -1 : matches[prevIdx].end;
        const nextStart = nextIdx === -1 ? text.length : matches[nextIdx].start;
        
        const spaceLeft = currentStart - (prevEnd + 1);
        const spaceRight = (nextStart - 1) - currentEnd;
        const currentBalance = Math.min(spaceLeft, spaceRight);

        // 更新最优解（优先数量，其次平衡度，最后位置）
        if (currentCount > best.count ||
            (currentCount === best.count && currentBalance > best.balance) ||
            (currentCount === best.count && currentBalance === best.balance && currentStart < best.start)) {
            best = {
                count: currentCount,
                start: currentStart,
                end: currentEnd,
                balance: currentBalance
            };
        }
    }

    // 窗口扩展逻辑
    let [finalStart, finalEnd] = [best.start, best.end];
    const remaining = maxLength - (finalEnd - finalStart + 1);

    if (remaining > 0) {
        // 计算可扩展区域
        const prevIdx = findPrev(finalStart);
        const nextIdx = findNext(finalEnd);
        const prevEnd = prevIdx === -1 ? -1 : matches[prevIdx].end;
        const nextStart = nextIdx === -1 ? text.length : matches[nextIdx].start;

        // 可用扩展空间
        const maxLeft = finalStart - (prevEnd + 1);
        const maxRight = (nextStart - 1) - finalEnd;
        
        // 平衡分配剩余空间
        const allocate = (remain: number, a: number, b: number) => {
            const t = Math.min(a, b, Math.floor(remain / 2));
            let [x, y] = [t, t];
            remain -= 2 * t;
            
            if (remain > 0) {
                if (a > t) {
                    x += Math.min(remain, a - t)
                }
                remain -= x - t;
                if (b > t && remain > 0) {
                    y += Math.min(remain, b - t);
                }
            }
            return [x, y];
        };

        const [addLeft, addRight] = allocate(remaining, maxLeft, maxRight);
        
        // 应用扩展
        finalStart = Math.max(prevEnd + 1, finalStart - addLeft);
        finalEnd = Math.min(nextStart - 1, finalEnd + addRight);
        
        // 最终长度验证
        const finalLength = finalEnd - finalStart + 1;
        if (finalLength > maxLength) {
            finalEnd = finalStart + maxLength - 1;
        }
    }

    // 边界保护
    finalEnd = Math.min(finalEnd, text.length - 1);
    return { start: finalStart, end: finalEnd };
}

const HighlightText = ({
    text,
    pattern,
    maxLength=10
}:  {
    text: string;
    pattern: string;
    maxLength: number;
}) => {
    
    const { start, end } = bestWindow(text, pattern, maxLength);
    const parts: string[] = text.slice(start, end+1).split(pattern);
    const result: (string | JSX.Element)[] = [];

    if (start>0) {
        result.push("...");
    }
    result.push(parts[0]);

    for (let i = 1; i < parts.length; i++) {
        result.push(<span className="text-blue-500">{pattern}</span>);
        result.push(parts[i]);
    }

    if (end<text.length-1) {
        result.push("...");
    }
    return <>{result}</>;
};

export default function ParaSearchResult({paraSearchItems, searchQuery}: ParaSearchResultProps) {
    const [query]=useState<string>(searchQuery);

    return (
    <div className="flex flex-col gap-4 min-h-[300px] mt-4">
        <ul className="space-y-2 w-full max-w-4xl mx-auto">
          {paraSearchItems.map((item: ParaSearchItem) => (
            <li 
              key={item.bookDTOWithRole.id}
              className="bg-base-100 rounded-box shadow-sm transition-all duration-200"
            >
              <div className="collapse collapse-arrow group">
                {/* 折叠开关 */}
                <input type="checkbox" className="peer" />
                
                {/* 标题区域 */}
                <div className="collapse-title flex items-center gap-4 p-4  transition-colors">
                    <div className="w-12 rounded-box">
                        <img 
                            src="https://img.daisyui.com/images/profile/demo/1@94.webp"
                            alt={item.bookDTOWithRole.title}
                            className="size-10 rounded-box"
                        />
                    </div>

                    <div className="flex-1">
                        <h3 className="font-semibold">
                            {item.bookDTOWithRole.title}
                        </h3>
                        <p className="text-sm text-base-content/60">
                            搜索到 {item.paragraphDTOs.length} 个段落
                        </p>
                    </div>
                </div>
      
                {/* 折叠内容区域 */}
                <div className="collapse-content bg-base-100 rounded-b-box p-0 w-full">
                  <ul className="list w-full">
                    {item.paragraphDTOs.map((para: ParagraphDTO) => (
                        <Link 
                            to={`/reading/${item.bookDTOWithRole.id}#scrollTo=${para.id}`}
                            draggable="false"
                            key={para.id}
                            className="list-row 
                                active:bg-base-200 
                                hover-hover:hover:bg-base-50
                                transition-colors 
                                duration-100
                                cursor-pointer"
                            >
                            <div className="py-3 px-4 ">
                                <div className="flex justify-between items-start gap-2">
                                    <div className="flex-1">
                                        <div className="text-sm font-medium mb-4">
                                            <HighlightText 
                                                text={para.author} 
                                                pattern={query} 
                                                maxLength={50}
                                            />
                                        </div>
                                        <pre className="whitespace-pre-wrap font-sans text-base-content/90 leading-snug">
                                            <HighlightText 
                                                text={para.content} 
                                                pattern={query} 
                                                maxLength={50}
                                            />
                                        </pre>
                                    </div>
                                </div>
                            </div>
                        </Link>
                    ))}
                  </ul>
                </div>
              </div>
            </li>
          ))}
        </ul>
    </div>)
}
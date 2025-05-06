import { useEffect, useState } from 'react';
import {httpService, ResponseData} from '../api/client'
import UserList from './UserList';

export default function MyFollowing() {
    
    const [followeeList, setFolloweeList] = useState<UserDTOWithFollow[]>([]);
    const [sortedBy, setSortedBy] = useState<"nickname"|"followTime">("nickname");
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [processedList, setProcessedList] = useState<UserDTOWithFollow[]>([]);
    const [query, setQuery] = useState<string>("");

    const fetchFolloweeList = async () => {
        setLoading(true)
        try {
            const response:ResponseData<UserDTOWithFollow[]> = await httpService.empty<UserDTOWithFollow[]>('/myFollowing',"GET");
            if (response.success===false) {
                setError(response.errorMsg)
                console.log(response.errorMsg)
                return
            }
            setFolloweeList(response.data)
            const sortedList:UserDTOWithFollow[] = response.data.sort((a, b) => a.nickname.localeCompare(b.nickname,'zh-CN'))
            setProcessedList(sortedList)
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message)
            }
            console.log(error)
        } finally {
            setLoading(false)
        }
    }
    // function isFollowDTO(data: boolean | FollowDTO): data is FollowDTO {
    //     return typeof data === 'object' && 'createTime' in data;
    // }
    // const toggleFollow = async (targetId: number) => {
    //     try {
    //         const action = followeeList?.some(followee => followee.id===targetId && followee.followed===true)? 'unfollow' : 'follow';
    //         const endpoint = `/user/${targetId}/${action}`;
    //         const response: ResponseData<boolean | FollowDTO> = await httpService.empty<boolean | FollowDTO>(endpoint,"POST");
    //         const data: boolean | FollowDTO = response.data;
    //         if (response.success) {
    //             setFolloweeList(prev => 
    //                 prev?.map(followee => 
    //                     followee.id === targetId 
    //                     ? {...followee, 
    //                         followed: action === 'follow',
    //                         followTime: (isFollowDTO(data) && 'createTime' in data) ? data.createTime : null
    //                     } 
    //                     : followee
    //                 ) || null
    //             );
    //             console.log(followeeList)
    //         }
    //     } catch (error) {
    //         if (error instanceof Error) {
    //             setError(error.message)
    //         }
    //         console.log(error)
    //     }
    // }

    const resortList = (sortedBy: "nickname"|"followTime") => {
        setSortedBy(sortedBy)
        const sortedList:UserDTOWithFollow[] = processedList?.sort((a, b) => {
            if (sortedBy === "nickname") {
                return a.nickname.localeCompare(b.nickname,'zh-CN')
            } else {
                return new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
            }
        })
        setProcessedList(sortedList)
    }

    const filterListByQuery = (query: string) => {
        setQuery(query)
        const filteredList:UserDTOWithFollow[] = followeeList?.filter(user => (user.nickname.includes(query)||user.username.includes(query)))
        setProcessedList(filteredList)
    }

    useEffect( () => {
        const fetchData = async () => {
            setLoading(true)
            try {
                await Promise.all([fetchFolloweeList()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [])

    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>
    return (
        <div className="space-y-4">

            <h2 className="text-xl font-bold">我的关注</h2>
            <div className="flex flex-col justify-between mb-3">
                <div className="flex justify-center flex-1 mr-3 mb-3">
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
                        <input onChange={(e) => filterListByQuery(e.target.value)} type="search" className="grow" placeholder="搜索关注" value={query} />
                        {query && (
                        <button
                            type="button"
                            onClick={() => {
                                filterListByQuery('');
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
                </div>
                <div>
                    <span className="font-semibold">排序：</span>
                    <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto">
                        <li className="m-1 w-auto" key="nickname">
                            <a
                                className={sortedBy === "nickname" ? "menu-active" : ""}
                                onClick={()=>resortList("nickname")}
                            >
                                昵称
                            </a>
                        </li>
                        <li className="m-1 w-auto" key="followTime">
                            <a 
                                className={sortedBy === "followTime" ? "menu-active" : ""} 
                                onClick={()=>resortList("followTime")}
                            >
                                关注时间
                            </a>
                        </li>
                    </ul>
                </div>
                

            </div>
            <UserList userDTOs={processedList} setUserDTOs={setProcessedList} />

            
        </div>
    )
}
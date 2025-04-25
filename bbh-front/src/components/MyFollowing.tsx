import { useEffect, useState } from 'react';
import {httpService, ResponseData} from '../api/client'
import UserList from './UserList';

export default function MyFollowing() {
    
    const [followeeList, setFolloweeList] = useState<UserDTOWithFollow[]>([]);
    const [sortedBy, setSortedBy] = useState<"nickname"|"followTime">("nickname");
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchFolloweeList = async () => {
        setLoading(true)
        try {
            const response:ResponseData<UserDTOWithFollow[]> = await httpService.empty<UserDTOWithFollow[]>('/myFollowing',"GET");
            if (response.success===false) {
                setError(response.errorMsg)
                console.log(response.errorMsg)
                return
            }
            const sortedList:UserDTOWithFollow[] = response.data.sort((a, b) => a.nickname.localeCompare(b.nickname,'zh-CN'))
            setFolloweeList(sortedList)
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
        const sortedList:UserDTOWithFollow[] = followeeList?.sort((a, b) => {
            if (sortedBy === "nickname") {
                return a.nickname.localeCompare(b.nickname,'zh-CN')
            } else {
                return new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
            }
        })
        setFolloweeList(sortedList)
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
            <div>
                <div className="flex justify-between items-center mb-3">
                    <h2 className="text-xl font-bold">我的关注</h2>
                    <div className="flex justify-between gap-6 flex-col"> {/* 增加间隙并居中对齐 */}
                        <div className="flex items-center gap-2">
                            <span className="font-semibold">排序：</span>
                            <ul className="menu menu-horizontal bg-base-200 rounded-box w-auto">
                                <li className="m-1 w-auto">
                                    <a
                                        className={sortedBy === "nickname" ? "menu-active" : ""}
                                        onClick={()=>resortList("nickname")}
                                    >
                                        昵称
                                    </a>
                                </li>
                                <li className="m-1 w-auto">
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
                </div>
                <UserList userDTOs={followeeList} setUserDTOs={setFolloweeList} />
            </div>
            
        </div>
    )
}
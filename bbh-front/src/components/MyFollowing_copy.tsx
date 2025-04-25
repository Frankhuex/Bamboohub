import { useEffect, useState } from 'react';
import {httpService, ResponseData} from '../api/client'
import { utc2current } from '../utils/util';

export default function MyFollowing() {
    
    const [followeeList, setFolloweeList] = useState<UserDTOWithFollow[]|null>();
    const [sortedBy, setSortedBy] = useState<"nickname"|"followTime">("nickname");
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchFolloweeList = async () => {
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
        }
    }

    const toggleFollow = async (targetId: number) => {
        try {
            const action = followeeList?.some(followee => followee.id===targetId && followee.followed===true)? 'unfollow' : 'follow';
            const endpoint = `/user/${targetId}/${action}`;
            const response = await httpService.empty<boolean | FollowDTO>(endpoint,"POST");
            
            if (response.success) {
                setFolloweeList(prev => 
                    prev?.map(followee => 
                        followee.id === targetId 
                        ? {...followee, followed: action === 'follow'} 
                        : followee
                    ) || null
                );
                console.log(followeeList)
            }
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message)
            }
            console.log(error)
        }
    }

    const resortList = (sortedBy: "nickname"|"followTime") => {
        setSortedBy(sortedBy)
        const sortedList = followeeList?.sort((a, b) => {
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

    const renderFolloweeItem = (followee: UserDTOWithFollow) => (
        <li className="list-row" key={followee.id}>
            <div>
                <img 
                    className="size-10 rounded-box" 
                    src="https://img.daisyui.com/images/profile/demo/1@94.webp" 
                    alt={followee.nickname}
                />
            </div>
            <div>
                <div>{followee.nickname}</div>
                <div className="text-xs font-semibold opacity-60">@{followee.username}</div>
                <div className="text-xs uppercase font-semibold opacity-60">关注于{utc2current(followee.createTime)}</div>
            </div>
            <button onClick={() => toggleFollow(followee.id)} className="btn">
                <svg xmlns="http://www.w3.org/2000/svg" fill={followee.followed===true ? 'black' : "none"} viewBox="0 0 24 24" strokeWidth="2.5" stroke="currentColor" className="size-[1.2em]"><path strokeLinecap="round" strokeLinejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z" /></svg>
                {followee.followed===true ? '已关注' : '关注'}
            </button>
        </li>
        
    )

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
                <ul className="list bg-base-100 rounded-box shadow-md">
                    {followeeList?.map(renderFolloweeItem)}
                </ul>
            </div>
            
        </div>
    )
}
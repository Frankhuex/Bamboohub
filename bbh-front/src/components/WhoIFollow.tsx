import { useEffect, useState } from 'react';
import {httpService, ResponseData} from '../api/client'
import { utc2current } from '../utils/util';

interface FollowDTOWithStatus extends FollowDTO {
    followed:boolean
}

export default function WhoIFollow() {
    
    const [followeeList, setFolloweeList] = useState<FollowDTOWithStatus[]|null>();
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    async function fetchFolloweeList() {
        try {
            const response:ResponseData<FollowsDTO> = await httpService.get<FollowsDTO>('/whoIFollow');
            if (response.success===false) {
                setError(response.errorMsg)
                console.log(response.errorMsg)
                return
            }
            const sortedList:FollowDTO[] = response.data.follows.sort((a, b) => a.target.nickname.localeCompare(b.target.nickname))
            setFolloweeList(sortedList.map(followee => ({...followee, followed: true})))
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message)
            }
            console.log(error)
        }
    }

    async function toggleFollow(targetId: number) {
        try {
            const action = followeeList?.some(followee => followee.target.id===targetId && followee.followed===true)? 'unfollow' : 'follow';
            const endpoint = `/user/${targetId}/${action}`;
            const response = await httpService.post<boolean | FollowDTO>(endpoint);
            
            if (response.success) {
                setFolloweeList(prev => 
                    prev?.map(followee => 
                        followee.target.id === targetId 
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

    const renderFolloweeItem = (followee: FollowDTOWithStatus) => (
        
            <li className="list-row" key={followee.id}>
                <div>
                    <img 
                        className="size-10 rounded-box" 
                        src="https://img.daisyui.com/images/profile/demo/1@94.webp" 
                        alt={followee.target.nickname}
                    />
                </div>
                <div>
                    <div>{followee.target.nickname}</div>
                    <div className="text-xs uppercase font-semibold opacity-60">关注于{utc2current(followee.createTime)}</div>
                </div>
                <button onClick={() => toggleFollow(followee.target.id)} className="btn">
                    <svg xmlns="http://www.w3.org/2000/svg" fill={followee.followed===true ? 'black' : "none"} viewBox="0 0 24 24" strokeWidth="2.5" stroke="currentColor" className="size-[1.2em]"><path strokeLinecap="round" strokeLinejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z" /></svg>
                        {followee.followed===true ? '已关注' : '关注'}
                </button>
            </li>
        
    )

    if (loading) return (<div><span className="loading loading-spinner loading-xl"></span></div>)
    if (error) return <div className="text-center py-4 text-error">{error}</div>
    return (
        <div className="space-y-4">
            <div>
                <h2 className="text-xl font-bold mb-2">我的关注</h2>
                <ul className="list bg-base-100 rounded-box shadow-md">
                    {followeeList?.map(renderFolloweeItem)}
                </ul>
            </div>
            
        </div>
    )
}
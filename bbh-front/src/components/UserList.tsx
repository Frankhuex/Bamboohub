import { useState } from 'react';
import { httpService, ResponseData } from '../api/client';
import { utc2current } from '../utils/util';

interface UserListProps {
    userDTOs: UserDTOWithFollow[];
    setUserDTOs: React.Dispatch<React.SetStateAction<UserDTOWithFollow[]>>
}



export default function UserList({userDTOs, setUserDTOs}: UserListProps) {
    const [error, setError] = useState<string | null>(null);
    const [toggledId, setToggledId]=useState<number|null>(null);

    const renderFolloweeItem = (user: UserDTOWithFollow) => {
        const followButton = (toggledId === user.id) 
            ? (<button className="btn w-13 md:w-25">
                <span className="loading loading-spinner loading-xl m-auto"></span>
            </button>)
            : (<button onClick={() => toggleFollow(user.id)} className="btn w-13 md:w-25">
                <svg xmlns="http://www.w3.org/2000/svg" fill={user.followed===true ? 'currentColor' : "none"} viewBox="0 0 24 24" strokeWidth="2.5" stroke="currentColor" className="size-[1.2em]"><path strokeLinecap="round" strokeLinejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z" /></svg>
                <span className="hidden md:inline">{user.followed===true ? '已关注' : '关注'}</span>
            </button>)
        return (
            <li className="list-row" key={user.id}>
                <div>
                    <img 
                        className="size-10 rounded-box" 
                        src="https://img.daisyui.com/images/profile/demo/1@94.webp" 
                        alt={user.nickname}
                    />
                </div>
                <div>
                    <div>{user.nickname}</div>
                    <div className="text-xs font-semibold opacity-60">@{user.username}</div>
                    {user.followed===true && user.followTime && <div className="text-xs uppercase font-semibold opacity-60">关注于{user.followTime && utc2current(user.followTime)}</div>}
                </div>
                <div className="mt-auto mb-auto">
                {followButton}
                </div>
            </li>
        )
    }

    
    const toggleFollow = async (targetId: number) => {
        const isFollowDTO = (data: boolean | FollowDTO): data is FollowDTO => {
            return typeof data === 'object' && 'createTime' in data;
        }
        setToggledId(targetId)
        try {
            const action = userDTOs?.some(followee => followee.id===targetId && followee.followed===true)? 'unfollow' : 'follow';
            const endpoint = `/user/${targetId}/${action}`;
            const response: ResponseData<boolean | FollowDTO> = await httpService.empty<boolean | FollowDTO>(endpoint,"POST");
            const data: boolean | FollowDTO = response.data;
            if (response.success) {
                setUserDTOs(prev => 
                    prev.map(user => 
                        user.id === targetId 
                        ? {...user, 
                            followed: action === 'follow',
                            followTime: (isFollowDTO(data) && 'createTime' in data) ? data.createTime : null
                        } 
                        : user
                    )
                )
            }
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message)
            }
            console.log(error)
        } finally {
            setToggledId(null)
        }
    }

    if (error) return <div className="text-center py-4 text-error">{error}</div>

    return (
        <div className="space-y-4">
            <ul className="list bg-base-100 rounded-box shadow-md">
                {userDTOs?.map(renderFolloweeItem)}
            </ul>
        </div>
    )
}
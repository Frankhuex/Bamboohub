import { useEffect, useState } from "react";
import { httpService, ResponseData } from "../api/client";

interface ChooseFromFollowingProps {
    dialogId: string;
    handleConfirm: (selectedUsers: UserDTO[])=>void;
    openCount: number;

    limit?: number;
    fixAddedUserIds?: number[];
    optionalAddedUserIds?: number[];
    
    title?: string;
    confirmBtnText?: string;

    confirmLoading?: boolean;
}

interface UserDTOWithFollowAndAdded extends UserDTOWithFollow {
    alreadyAdded: boolean;
    newAdded: boolean;
}

export default function ChooseFriends({dialogId, handleConfirm, openCount, limit=Infinity, fixAddedUserIds=[], optionalAddedUserIds=[], title="选择好友", confirmBtnText="确定", confirmLoading=false}: ChooseFromFollowingProps) {
    const [myFollowingUserDTOWithAdded, setMyFollowingUserDTOWithAdded]=useState<UserDTOWithFollowAndAdded[]>([]);
    const [selectCount, setSelectCount]=useState<number>(fixAddedUserIds.length + optionalAddedUserIds.length);

    const [loading, setLoading]=useState<boolean>(false);
    const [error, setError]=useState<string>("");

    const fetchMyFollowing = async () => {
        setLoading(true);
        try {
            const response:ResponseData<UserDTOWithFollow[]> = await httpService.empty<UserDTOWithFollow[]>('/myFollowing',"GET");
            const userDTOListWithFollow: UserDTOWithFollow[] = response.data;
            if (response.success===false)  {
                setError(response.errorMsg);
                setLoading(false);
                return;
            }

            const transformedList: UserDTOWithFollowAndAdded[] = userDTOListWithFollow.map(userDTOWithFollow => ({
                ...userDTOWithFollow,
                alreadyAdded: fixAddedUserIds.some(userId => userId === userDTOWithFollow.id),
                newAdded: optionalAddedUserIds.some(userId => userId === userDTOWithFollow.id),
            })).sort((a, b) => a.nickname.localeCompare(b.nickname,'zh-CN'));
            setMyFollowingUserDTOWithAdded(transformedList);
            
            // Transform userDTOWithFollow[] to UserDTOWithFollowAndAdded[]
            
            // if (includeSelf===true) {
            //     try {
            //         const myUser=await fetchProfile();
            //         if (myUser) {
            //             setMyFollowingUserDTOWithAdded( 
            //                 [{
            //                     ...myUser,
            //                     followed: true,
            //                     followTime: null,
            //                     alreadyAdded: fixAddedUserIds.some(userId => userId === myUser.id),
            //                     newAdded: optionalAddedUserIds.some(userId => userId === myUser.id),
            //                 },...transformedList]
            //             )
            //         }
            //     } catch (e) {
            //         console.log(e)
            //         setMyFollowingUserDTOWithAdded(transformedList);
            //     }
            // } else {
            //     setMyFollowingUserDTOWithAdded(transformedList);
            // }
            
        } catch (error) {
            console.log(error)
            if (error instanceof Error)
                setError(error.message);
        } finally {
            setLoading(false);
        }
    }

    // const fetchProfile = async ():Promise<UserDTO|undefined> => {
    //     setLoading(true)
    //     try {
    //         const response:ResponseData<UserDTO>=await httpService.empty<UserDTO>("/myProfile",'GET')
    //         if (response.success===false) {
    //             setError(response.errorMsg)
    //             return
    //         }
    //         setMyUserDTO(response.data)
    //         return response.data;
    //     } catch (e) {
    //         if (e instanceof Error)
    //             setError(e.message)
    //     } finally {
    //         setLoading(false)
    //     }
    // }


    useEffect(() => {
        const token = localStorage.getItem("token")
        if (!token) {
            setError("请先登录");
            return;
        }
        const fetchData = async () => {
            setLoading(true);
            setSelectCount(fixAddedUserIds.length + optionalAddedUserIds.length);
            try {
                await Promise.all([fetchMyFollowing()]);
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [openCount])


    const renderCheckBox = (userDTOWithFollow: UserDTOWithFollowAndAdded) => {
        if (userDTOWithFollow.alreadyAdded===true) {
            return <input type="checkbox" className="checkbox ml-auto" disabled checked={userDTOWithFollow.alreadyAdded} />
        } else {
            return <input type="checkbox" className="checkbox ml-auto" 
                checked={userDTOWithFollow.newAdded}
                disabled={selectCount >= limit && !userDTOWithFollow.newAdded} 
                onChange={(e)=>{
                    setMyFollowingUserDTOWithAdded(prev => prev.map(userDTO => 
                        userDTO.id === userDTOWithFollow.id 
                        ? {...userDTO, newAdded: e.target.checked} 
                        : userDTO
                    ))
                    console.log(selectCount)
                    setSelectCount(prev => prev + (e.target.checked ? 1 : -1))
                }}
            />
        }
    }

    return (

        <dialog id={dialogId} className="modal">
            <div className="modal-box flex flex-col max-h-[90vh]">
                <h3 className="font-bold text-lg mb-4 shrink-0">{title}</h3>
                {loading ? (
                <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
                ):(
                <ul className="list bg-base-100 rounded-box flex-1 min-h-[100px] overflow-y-auto">
                    {myFollowingUserDTOWithAdded.map(userDTOWithFollow => (
                    <label className="list-row label w-full text-lg" key={userDTOWithFollow.id}>
                        <p>{userDTOWithFollow.nickname}</p>
                        {renderCheckBox(userDTOWithFollow)}
                    </label>
                    ))}
                </ul>)}
                <div className="modal-action shrink-0">
                    <form method="dialog">
                        {/* Any button inside the form will close the dialog. */}
                        <button className="btn border-base-300 border"
                            onClick={fetchMyFollowing}>
                            取消
                        </button>
                    </form>
                    {confirmLoading ? (
                    <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
                    ):(
                    <button className="btn border-base-300 border" 
                        onClick={()=>{
                            handleConfirm(myFollowingUserDTOWithAdded.filter(userDTOWithAdded => userDTOWithAdded.newAdded===true));
                            fetchMyFollowing();
                        }}>
                        {confirmBtnText}
                    </button>
                    )}
                </div>
                {error && <div className="text-center py-4 text-error">{error}</div>}
            </div>
           
        </dialog>)
}
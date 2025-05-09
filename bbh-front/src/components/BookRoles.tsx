import { useEffect, useState } from "react";
import { toChinese, utc2current } from "../utils/util";
import { httpService, ResponseData } from "../api/client";
import { RolesAsViewerReq } from "../types/request.types";
import ChooseFriends from "./ChooseFriends";

interface ReadingProps {
    bookId?: number;
    setRoleOut: (role: RoleDTO|null) => void;
}

interface RoleDTOWithFollow extends RoleDTO {
    followed: boolean;
}

interface UserDTOWithFollowAndAdded extends UserDTOWithFollow {
    alreadyAdded: boolean;
    newAdded: boolean;
}

export default function BookRoles({bookId=14, setRoleOut}:ReadingProps) {

    const [roleListWithFollow, setRoleListWithFollow]=useState<RoleDTOWithFollow[]>([]);
    const [myRole, setMyRole]=useState<RoleDTO|null>(null)
    const [myFollowingUserDTOWithAdded, setMyFollowingUserDTOWithAdded]=useState<UserDTOWithFollowAndAdded[]>([]);

    const [error, setError]=useState<string>("");
    const [loading, setLoading]=useState<boolean>(false);
    const [editLoading, setEditLoading]=useState<boolean>(false);
    const [deleteLoading, setDeleteLoading]=useState<boolean>(false);

    const [fetchFolloweeListLoading, setFetchFolloweeListLoading]=useState<boolean>(false);
    const [inviteLoading, setInviteLoading]=useState<boolean>(false);

    const [toggledFollowUserId, setToggledFollowUserId]=useState<number|null>(null);
    const [inviteDialogOpenCount, setInviteDialogOpenCount]=useState<number>(0);

    const [editedRoleId, setEditedRoleId]=useState<number|null>(null);
    const [oldRoleType, setOldRoleType]=useState<"OWNER"|"ADMIN"|"EDITOR"|"VIEWER"|null>(null);
    const [newRoleType, setNewRoleType]=useState<"OWNER"|"ADMIN"|"EDITOR"|"VIEWER"|null>(null);

    const dialogId="book-roles-dialog";


////////////////////////////////////////////////////////////////////////
    // Fetch data from backend
    const fetchRoles = async () => {
        setLoading(true)
        // Fetch roles of this book
        try {
            const response:ResponseData<RolesDTO>=await httpService.empty<RolesDTO>(`/book/${bookId}/roles`,'GET')
            if (response.success===false) {
                setError(response.errorMsg)
                setLoading(false)
                return
            }
        
            const rolesDTO: RolesDTO = response.data;
            const flatList: RoleDTO[] = rolesDTO.owners.concat(rolesDTO.admins, rolesDTO.editors, rolesDTO.viewers)
            
            // Fetch my following list if any, so just pass if error occurs
            if (localStorage.getItem("token")) {
                try {
                    const response2:ResponseData<UserDTOWithFollow[]> = await httpService.empty<UserDTOWithFollow[]>('/myFollowing',"GET");
                    const userDTOListWithFollow: UserDTOWithFollow[] = response2.data;
                    if (response2.success===true) {
                        // Transform userDTOWithFollow[] to UserDTOWithFollowAndAdded[]
                        setMyFollowingUserDTOWithAdded(userDTOListWithFollow.map(userDTOWithFollow => ({
                            ...userDTOWithFollow,
                            alreadyAdded: flatList.some(roleDTO => roleDTO.userDTO.id === userDTOWithFollow.id),
                            newAdded: false
                        })))
                        // Set roleListWithFollow with book's roles and my following list
                        setRoleListWithFollow(flatList
                            .map(roleDTO => ({
                                ...roleDTO,
                                followed: userDTOListWithFollow.some(userDTOWithFollow => userDTOWithFollow.id === roleDTO.userDTO.id)
                            })
                        ))
                    }
                } catch (error) {
                    console.log(error)
                    // Set roleListWithFollow with book's roles only
                    setRoleListWithFollow(flatList
                        .map(roleDTO => ({
                            ...roleDTO,
                            followed: false
                        })
                    ))
                }
            } else {
                setRoleListWithFollow(flatList
                    .map(roleDTO => ({
                        ...roleDTO,
                        followed: false
                    })
                ))
            }
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    const updateRole = async ()=>{
        if (oldRoleType===newRoleType) {
            setEditedRoleId(null)
            return;
        }
        setEditLoading(true)
        try {
            const response:ResponseData<RoleDTO>=await httpService.json<RoleDTO>(`/bookRole/${editedRoleId}/${newRoleType}`,'PUT')
            if (response.success===false) {
                setError(response.errorMsg)
                setEditLoading(false)
                return
            }
            const updatedRole:RoleDTO=response.data;
            // Update the one in roleListWithFollow with the updated role
            setRoleListWithFollow(roleListWithFollow.map(roleDTOWithFollow=>
                roleDTOWithFollow.id===editedRoleId
                ? {...updatedRole, followed: roleDTOWithFollow.followed}
                : roleDTOWithFollow
            ))
            setEditedRoleId(null)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setEditLoading(false)
        }
    }

    const deleteRole = async () => {
        const roleDTO:RoleDTO|undefined=roleListWithFollow.find(roleDTO=>roleDTO.id===editedRoleId)
        if (!window.confirm(`确认删除${roleDTO?.userDTO.nickname}？`)) return;
        setDeleteLoading(true)
        try {
            const response:ResponseData<boolean>=await httpService.empty<boolean>(`/bookRole/${editedRoleId}`,'DELETE')
            if (response.success===false) {
                setError(response.errorMsg)
                setDeleteLoading(false)
                return
            }
            // Delete the role from roleListWithFollow
            setRoleListWithFollow(roleListWithFollow.filter(roleDTO=>roleDTO.id!==editedRoleId))
            setMyFollowingUserDTOWithAdded(prev=>prev.map(userDTOWithFollowAndAdded => ({
                ...userDTOWithFollowAndAdded,
                alreadyAdded: userDTOWithFollowAndAdded.id===roleDTO?.userDTO.id ? false : userDTOWithFollowAndAdded.alreadyAdded,
                newAdded: false
            })))
            setEditedRoleId(null)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setDeleteLoading(false)
        }
    }

    const fetchMyRole = async () => {
        if (localStorage.getItem("token")===null) return;
        setLoading(true)
        try {
            const response:ResponseData<RoleDTO|null>=await httpService.empty<RoleDTO|null>(`/book/${bookId}/myRole`,'GET')
            if (response.success===false) {
                setLoading(false)
                return
            }
            setMyRole(response.data)
            // Set role in father component
            setRoleOut(response.data)
        } catch(e){
            console.log(e)
        } finally {
            setLoading(false)
        }
    }

    const fetchFolloweeList = async () => {
        setFetchFolloweeListLoading(true)
        try {
            const response:ResponseData<UserDTOWithFollow[]> = await httpService.empty<UserDTOWithFollow[]>('/myFollowing',"GET");
            if (response.success===false) {
                setFetchFolloweeListLoading(false)
                return
            }
            const sortedList:UserDTOWithFollow[] = response.data.sort((a, b) => a.nickname.localeCompare(b.nickname,'zh-CN'))
            setMyFollowingUserDTOWithAdded(sortedList.map(userDTOWithFollow => ({
                ...userDTOWithFollow,
                alreadyAdded: roleListWithFollow.some(roleDTO => roleDTO.userDTO.id === userDTOWithFollow.id),
                newAdded: false
            })))
        } catch(error){
            console.log(error)
        } finally {
            setFetchFolloweeListLoading(false)
        }
    }

    const toggleFollow = async (targetId: number) => {
        setToggledFollowUserId(targetId)
        try {
            const action = roleListWithFollow?.some(roleDTO => roleDTO.userDTO.id===targetId && roleDTO.followed===true)? 'unfollow' : 'follow';
            const endpoint = `/user/${targetId}/${action}`;
            const response: ResponseData<boolean | FollowDTO> = await httpService.empty<boolean | FollowDTO>(endpoint,"POST");
            if (response.success===false) {
                setError(response.errorMsg)
                setToggledFollowUserId(null)
                return
            }
            // Update the follow status of the user in roleListWithFollow
            setRoleListWithFollow(prev => 
                prev.map(roleDTO => 
                    roleDTO.userDTO.id === targetId 
                    ? {...roleDTO, 
                        followed: action === 'follow',
                    } 
                    : roleDTO
                )
            )
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message)
            }
            console.log(error)
        } finally {
            setToggledFollowUserId(null)
        }
    }

    const handleInviteNew = async (userDTOs: UserDTO[]) => {
        if (userDTOs.length===0) {
            return
        }
        const userIds: number[] = userDTOs.map(userDTO => userDTO.id);
        const rolesReq:RolesAsViewerReq={
            bookId: bookId,
            userIds: userIds
        }
        setInviteLoading(true)
        try {
            const response:ResponseData<RoleDTO[]>=await httpService.json<RoleDTO[]>(`/bookRole`, 'POST', rolesReq);
            if (response.success===false) {
                setError(response.errorMsg)
                setInviteLoading(false)
                return
            }
            const newRoles:RoleDTO[]=response.data;
            // Concat the new roles to the roleListWithFollow
            setRoleListWithFollow(prev=>prev.concat(newRoles.map(roleDTO=>(
                {
                    ...roleDTO, 
                    // Check whether followed
                    followed: myFollowingUserDTOWithAdded.some(userDTOWithFollow => userDTOWithFollow.id === roleDTO.userDTO.id)
                }
            ))));
            setMyFollowingUserDTOWithAdded(prev=>prev.map(userDTOWithFollowAndAdded => ({
                ...userDTOWithFollowAndAdded,
                newAdded: false,
                alreadyAdded: newRoles.some(roleDTO => roleDTO.userDTO.id === userDTOWithFollowAndAdded.id)?true:userDTOWithFollowAndAdded.alreadyAdded
            })));
            (document.getElementById(dialogId) as HTMLDialogElement).close();
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message)
            }
        } finally {
            setInviteLoading(false)
        }
    }

    
    // Handle click on the role item
    const startEditRole=(roleDTO:RoleDTOWithFollow)=>{
        if (canEditRole(roleDTO.roleType)) {
            setOldRoleType(roleDTO?.roleType);
            setNewRoleType(roleDTO?.roleType);
            setEditedRoleId(roleDTO.id);
        }
    }
    const canEditRole=(targetType: "OWNER"|"ADMIN"|"EDITOR"|"VIEWER"):boolean=>{
        if (!myRole) return false;
        if ((myRole.roleType==="OWNER" && targetType!=="OWNER")
            ||(myRole.roleType==="ADMIN" && (targetType==="EDITOR" || targetType==="VIEWER"))) 
            return true;
        return false;
    }

/////////////////////////////////////////////////////////////////////////////////////////
    // Render JSX

    const renderEditRoleType=(
        <ul className="menu menu-horizontal bg-base-100 rounded-box w-auto whitespace-nowrap flex-nowrap">
            <li className="m-1 w-auto" key="viewer">
                <a 
                    className={newRoleType === "VIEWER" ? "menu-active" : ""} 
                    onClick={() => { setNewRoleType("VIEWER") }}>
                    读者
                </a>
            </li>

            <li className="m-1 w-auto" key="editor">
                <a 
                    className={newRoleType === "EDITOR" ? "menu-active" : ""} 
                    onClick={() => { setNewRoleType("EDITOR") }}>
                    编辑
                </a>
            </li>

            <li className="m-1 w-auto" key="admin">
                <a 
                    className={newRoleType === "ADMIN" ? "menu-active" : ""} 
                    onClick={() => { setNewRoleType("ADMIN") }}>
                    管理员
                </a>
            </li>
        </ul>
    )

    const renderEditSaveButton=(editLoading:boolean)=>(
        editLoading===true ? 
        (<button className="btn btn-square md:w-25">
            <span className="loading loading-spinner loading-xl m-auto"></span>
        </button>):(
        <button onClick={updateRole} className="btn btn-square md:w-25">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6"><path strokeLinecap="round" strokeLinejoin="round" d="m4.5 12.75 6 6 9-13.5" /></svg>
            <span className="hidden md:inline">保存</span>
        </button>)
    )

    const renderDeleteButton=(deleteLoading:boolean)=>(
        deleteLoading===true ?
        (<button className="btn btn-square md:w-25">
            <span className="loading loading-spinner loading-xl m-auto"></span>
        </button>):(
        <button onClick={deleteRole} className="btn text-error btn-square md:w-25">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6"><path strokeLinecap="round" strokeLinejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" /></svg>
            <span className="hidden md:inline">删除</span>
        </button>)
    )

    const renderFollowButton=(roleDTO: RoleDTOWithFollow)=>(
        (toggledFollowUserId === roleDTO.userDTO.id || fetchFolloweeListLoading) 
        ? (<button className="btn btn-square mt-auto mb-auto md:w-25">
            <span className="loading loading-spinner loading-xl m-auto"></span>
        </button>)
        : (<button onClick={(e) => {toggleFollow(roleDTO.userDTO.id);e.stopPropagation();}} 
            className="btn btn-square mt-auto mb-auto md:w-25">
            <svg xmlns="http://www.w3.org/2000/svg" fill={roleDTO.followed===true ? 'currentColor' : "none"} viewBox="0 0 24 24" strokeWidth="2.5" stroke="currentColor" className="size-[1.2em]"><path strokeLinecap="round" strokeLinejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z" /></svg>
            <span className="hidden md:inline">{roleDTO.followed ? '已关注' : '关注'}</span>
        </button>)
    )

    const renderRoleItem = (roleDTO: RoleDTOWithFollow) => (
        <div key={roleDTO.id}>
            <li className="list-row 
                active:bg-base-200 
                hover-hover:hover:bg-base-50
                transition-colors 
                duration-100
                cursor-pointer" 
                onClick={() => {
                    if (editedRoleId === roleDTO.id)
                        setEditedRoleId(null)
                    else
                        startEditRole(roleDTO)
                }}
                >
                <div>
                    <img className="size-10 rounded-box" src="https://img.daisyui.com/images/profile/demo/1@94.webp" alt={roleDTO.userDTO.nickname}/>
                </div>
                <div>
                    <div>{roleDTO.userDTO.nickname}</div>
                    <div className="text-xs font-semibold opacity-60">{toChinese(roleDTO.roleType)}</div>
                    <div className="text-xs uppercase font-semibold opacity-60">加入于{roleDTO.createTime && utc2current(roleDTO.createTime)}</div>
                </div>
                {myRole && renderFollowButton(roleDTO)}
            </li>
            {editedRoleId === roleDTO.id && (
            <li className="list-row flex flex-wrap items-center justify-between bg-base-200 gap-4 p-4">
                <div className="flex-1 min-w-[200px]">
                    {renderEditRoleType}
                </div>
                <div className="mr-0">
                    {renderEditSaveButton(editLoading)}
                </div>
                <div className="ml-auto">
                    {renderDeleteButton(deleteLoading)}
                </div>
            </li>)}
        </div>
    )



    useEffect(() => {
        const token = localStorage.getItem("token")
        const fetchData = async () => {
            setLoading(true);
            try {
                await Promise.all(token?[fetchRoles(),fetchMyRole()]:[fetchRoles()])
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [])

    if (loading) return <div className="fixed inset-0 flex"><span className="loading loading-spinner loading-xl m-auto"></span></div>
    if (error) return <div className="text-center py-4 text-error">{error}</div>
    return (<div>
        <div className="collapse collapse-arrow bg-base-100 border-base-300 border shadow-xs">
            <input type="checkbox" />
            <div className="collapse-title">
                <p>成员：{roleListWithFollow.map(roleDTO=>roleDTO.userDTO.nickname).join("，")}</p>
            </div>
            <div className="collapse-content p-0">
                <ul className="list bg-base-100 rounded-box">
                    {myRole && (
                    <li className="list-row flex justify-center items-center gap-4 p-4" key="invite">
                        <button className="btn border-base-300 border w-25" 
                            onClick={()=>{
                                fetchFolloweeList();
                                (document.getElementById(dialogId) as HTMLDialogElement).showModal();
                                setInviteDialogOpenCount(prev=>prev+1);
                            }}>
                            邀请
                        </button>

                        <ChooseFriends 
                            dialogId={dialogId} 
                            handleConfirm={handleInviteNew}
                            openCount={inviteDialogOpenCount}
                            fixAddedUserIds={
                                roleListWithFollow.map(roleDTO=>roleDTO.userDTO.id)
                            }
                            title="邀请"
                            confirmBtnText="邀请"
                            confirmLoading={inviteLoading}
                        />

                    </li>)}
                    {roleListWithFollow?.map(renderRoleItem)}
                </ul>
            </div>
        </div>
        
        
    </div>)
}
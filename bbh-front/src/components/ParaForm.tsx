import { useEffect, useState } from "react";
import { httpService, ResponseData } from "../api/client";
import { ParagraphReq, ParagraphUpdateReq } from "../types/request.types";
// import ChooseFriends from "./ChooseFriends";

interface ParaFormProps {
    paraDTO: ParagraphDTO;
    bookDTO: BookDTO|null;
    myRole?: RoleDTO|null;
    action: "create"|"edit";
    handleDelete?: (paraDTO:ParagraphDTO)=>void;
    handleCancel: ()=>void;
    handleSave: (paraDTO:ParagraphDTO)=>void;
}

export default function ParaForm({paraDTO, bookDTO, myRole, action, handleDelete, handleCancel, handleSave}:ParaFormProps) {
    // const [creators, setCreators]=useState<UserDTO[]>([]);
    // const [contributors, setContributors]=useState<UserDTO[]>([]);

    const [author, setAuthor]=useState<string>(action==="edit" ? paraDTO.author : "");
    const [content, setContent]=useState<string>(action==="edit" ? paraDTO.content : "");

    const [initAuthor, setInitAuthor]=useState<string>(action==="edit" ? paraDTO.author : "");
    const [initContent, setInitContent]=useState<string>(action==="edit" ? paraDTO.content : "");



    const [error, setError]=useState<string>("");
    const [saveLoading, setSaveLoading]=useState<boolean>(false);
    const [deleteLoading, setDeleteLoading]=useState<boolean>(false);

    // const creatorDialogId="creator-dialog"
    // const contributorDialogId="contributor-dialog"

    // const [creatorDialogOpenCount, setCreatorDialogOpenCount]=useState<number>(0);
    // const [contributorDialogOpenCount, setContributorDialogOpenCount]=useState<number>(0);


    

    const fetchParaRoles = async () => {
        try {
            const response:ResponseData<ParaRolesDTO>=await httpService.empty<ParaRolesDTO>(`paragraph/${paraDTO.id}/paraRoles`,'GET')
            if (response.success===false) {
                return
            }
            // const paraRolesDTO:ParaRolesDTO=response.data
            // setParaRoles(paraRolesDTO.creators.concat(paraRolesDTO.contributors));
            // setParaRolesDTO(paraRolesDTO)
            // setCreators(paraRolesDTO.creators.map(paraRoleDTO=>paraRoleDTO.userDTO));
            // setContributors(paraRolesDTO.contributors.map(paraRoleDTO=>paraRoleDTO.userDTO));
        } catch (e) {
            console.log(e)
        }
    }
    const requestCreatePara = async()=>{
        const paraReq: ParagraphReq = {
            author: author,
            content: content,
            prevParaId: paraDTO.id,
        }
        setSaveLoading(true)
        try {
            const response:ResponseData<ParagraphDTO>=await httpService.json<ParagraphDTO>(`/paragraph`,'POST',paraReq)
            if (response.success===false) {
                setError(response.errorMsg)
                setSaveLoading(false)
                return
            }
            if (handleSave)
                handleSave(response.data)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setSaveLoading(false)
        }
    }

    const requestDeletePara = async()=>{
        if (!window.confirm("确定删除吗？")) return;
        setDeleteLoading(true)
        try {
            const response:ResponseData<boolean>=await httpService.empty<boolean>(`/paragraph/${paraDTO.id}`,'DELETE')
            if (response.success===false) {
                setError(response.errorMsg)
                setDeleteLoading(false)
                return
            }
            if (handleDelete)
                handleDelete(paraDTO)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setDeleteLoading(false)
        }
    }

    const requestEditPara = async()=>{ 
        if (author===initAuthor && content===initContent) {
            handleCancel();
            return;
        }
        if (!window.confirm("确定修改吗？")) return;
        const paraUpdReq: ParagraphUpdateReq = {
            author: author,
            content: content,
        }
        setSaveLoading(true)
        try {
            const response:ResponseData<ParagraphDTO>=await httpService.json<ParagraphDTO>(`/paragraph/${paraDTO.id}`,'PUT',paraUpdReq)
            if (response.success===false) {
                setError(response.errorMsg)
                setSaveLoading(false)
                return
            }
            if (handleSave)
                handleSave(response.data)
        } catch (e) {
            if (e instanceof Error)
                setError(e.message)
        } finally {
            setSaveLoading(false)
        }
    }


    const confirmCancel = ()=>{
        if ((action==="create"
            && (author.trim().length>0 || content.trim().length>0)
            ) || (
            action==="edit"
            && (author!==initAuthor || content!==initContent))
        ) {
            if (!window.confirm("确定要放弃修改吗？"))
                return
        }
        handleCancel()
    }

    // const changeParaUsers = (
    //     userList: UserDTO[], 
    //     setUserList: React.Dispatch<React.SetStateAction<UserDTO[]>>, 
    //     selectedUsers: UserDTO[]
    // ) => {
    //     const oldUsers:UserDTO[]=userList.filter(userDTO=>selectedUsers.some(selectedUser=>selectedUser.id===userDTO.id));
    //     const newUsers:UserDTO[]=selectedUsers.filter(selectedUser=>!userList.some(userDTO=>userDTO.id===selectedUser.id));
    //     setUserList(oldUsers.concat(newUsers));
    //     (document.getElementById(creatorDialogId) as HTMLDialogElement).close();
    //     (document.getElementById(contributorDialogId) as HTMLDialogElement).close();
    // }

    const handleKeyDown = (event: React.KeyboardEvent) => {
        // 检测 Ctrl/Cmd + Enter
        if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
            event.preventDefault();
            
            // 根据当前模式触发对应操作
            if (action === "edit") {
                requestEditPara();
            } else {
                requestCreatePara();
            }
        }

        else if (event.key === 'Escape') {
            event.preventDefault();
            confirmCancel();
        } 
    };


    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    // const renderParaRoles = () => {
    //     if (!myRole || myRole.roleType=== "EDITOR") {
    //         const paraUsers:UserDTO[]=creators.concat(contributors)
    //         return (paraUsers.length>0 && <p className="mt-2 mb-2">By: {paraUsers.map(userDTO=>userDTO.nickname).join("，")}</p>)
    //     } else {
    //         return (<div>
    //             <div className="mt-2 mb-2">一作：
    //                 {creators.map((user)=>(
    //                     <button className="btn mr-1" key={user.id}
    //                         onClick={()=>{
    //                             setCreatorDialogOpenCount(creatorDialogOpenCount+1);
    //                             (document.getElementById(creatorDialogId) as HTMLDialogElement).showModal();
    //                         }}>
    //                         {user.nickname}
    //                     </button>
    //                 ))}
    //                 {creators.length===0 && (<button 
    //                     className="btn btn-circle"
    //                     onClick={()=>{
    //                         setCreatorDialogOpenCount(creatorDialogOpenCount+1);
    //                         (document.getElementById(creatorDialogId) as HTMLDialogElement).showModal()
    //                     }}>
    //                     <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg>
    //                 </button>)}

    //                 <ChooseFriends
    //                     dialogId={creatorDialogId}
    //                     handleConfirm={(selectedUsers:UserDTO[])=>changeParaUsers(creators,setCreators,selectedUsers)}
    //                     limit={1}
    //                     optionalAddedUserIds={creators.map(user=>user.id)}
    //                     openCount={creatorDialogOpenCount}
    //                     title="选择一作"
    //                     confirmBtnText="确定"
    //                 />
    //             </div>

    //             <div className="mb-2">贡献者：
    //                 {contributors.map((user)=>(
    //                     <button className="btn mr-1" key={user.id}
    //                         onClick={()=>{
    //                             setContributorDialogOpenCount(contributorDialogOpenCount+1);
    //                             (document.getElementById(contributorDialogId) as HTMLDialogElement).showModal()
    //                         }}>
    //                         {user.nickname}
    //                     </button>
    //                 ))}
    //                 <button 
    //                     className="btn btn-circle"
    //                     onClick={()=>{
    //                         setContributorDialogOpenCount(contributorDialogOpenCount+1);
    //                         (document.getElementById(contributorDialogId) as HTMLDialogElement).showModal()
    //                     }}>
    //                     <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg>
    //                 </button>

    //                 <ChooseFriends
    //                     dialogId={contributorDialogId}
    //                     handleConfirm={(selectedUsers:UserDTO[])=>changeParaUsers(contributors,setContributors,selectedUsers)}
    //                     optionalAddedUserIds={contributors.map(user=>user.id)}
    //                     openCount={contributorDialogOpenCount}
    //                     title="选择贡献者"
    //                     confirmBtnText="确定"
    //                 />
    //             </div>
    //         </div>)
    //     }
    // }

    useEffect(() => {
        const fetchData = async () => {
            try {
                await Promise.all([fetchParaRoles()])
            } catch (e) {
                console.log(e)
            }
        }
        fetchData()
        setInitAuthor(paraDTO.author)
        setInitContent(paraDTO.content)
    }, [paraDTO,bookDTO,myRole,action])

    return (
        <>
            <div className="card-body" onKeyDown={handleKeyDown}>
                
                <input type="text" className="input input-lg w-full" placeholder="署名" value={author} onChange={(e)=>setAuthor(e.target.value)}></input>
                
                
                {/* {renderParaRoles()} */}

                <textarea 
                    className="textarea textarea-lg w-full h-100" 
                    placeholder="正文" 
                    onChange={(e)=>{setContent(e.target.value)}}
                    defaultValue={action==="edit" ? paraDTO.content : ""}>
                </textarea>
            </div>


            <div className="flex justify-between mb-4 ml-4 mr-4">
                <div className="flex flex-row gap-2">
                {action==="edit" && 
                    <button className="btn border-base-300 text-error border shadow-xs" 
                        onClick={requestDeletePara}>
                        {deleteLoading?(<span className="loading loading-spinner loading-xl m-auto"></span>)
                        :<span>删除</span>}
                    </button>}
                </div>
                <div className="flex flex-row gap-2">
                    <button className="btn border-base-300 border shadow-xs" 
                        onClick={confirmCancel}>
                        取消
                    </button>
                    <button className="btn border-base-300 border shadow-xs" 
                        onClick={action==="edit" ? requestEditPara : requestCreatePara}>
                        {saveLoading?(<span className="loading loading-spinner loading-xl m-auto"></span>)
                        :<span>保存</span>}
                    </button>
                </div>
            </div>
            <p className="text-red-500">{error}</p>
        </>
    )
}
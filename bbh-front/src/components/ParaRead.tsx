import { useEffect, useState } from "react";
import { uniqueBy, utc2current } from "../utils/util";
import { httpService, ResponseData } from "../api/client";
import ParaForm from "./ParaForm";

interface ParaReadProps {
    paraDTO: ParagraphDTO;
    bookDTO: BookDTO|null;
    myRole?: RoleDTO|null;
    setParagraphs:React.Dispatch<React.SetStateAction<ParagraphDTO[]>>
    paragraphs:ParagraphDTO[]
}
export default function ParaRead({paraDTO, bookDTO, myRole, setParagraphs, paragraphs}:ParaReadProps) {

    const [paraRoles, setParaRoles]=useState<ParaRoleDTO[]>([]);
    const [editing, setEditing]=useState<boolean>(false);
    const [creating, setCreating]=useState<boolean>(false);
    const [canEdit, setCanEdit]=useState<boolean>(false);

    const [moveUpLoading, setMoveUpLoading]=useState<boolean>(false);
    const [moveDownLoading, setMoveDownLoading]=useState<boolean>(false);
    const [error, setError]=useState<string>("");

    const checkCanEdit = (scope:"PRIVATE"|"ALLSEARCH"|"ALLREAD"|"ALLEDIT",roleType:"OWNER"|"ADMIN"|"EDITOR"|"VIEWER"|null)=>{
        if (scope==="ALLEDIT") return true;
        if (roleType && roleType!=="VIEWER") return true;
        return false;
    }

    const fetchParaRoles = async () => {
        try {
            const response:ResponseData<ParaRolesDTO>=await httpService.empty<ParaRolesDTO>(`paragraph/${paraDTO.id}/paraRoles`,'GET')
            if (response.success===false) {
                return
            }
            const paraRolesDTO:ParaRolesDTO=response.data
            setParaRoles(paraRolesDTO.creators.concat(paraRolesDTO.contributors));
        } catch (e) {
            console.log(e)
        }
    }

    const handleAdd = async (paraDTO:ParagraphDTO) => {
        setParagraphs(prev=>{
            const targetIndex = prev.findIndex(p => p.id === paraDTO.prevParaId);
            return [
                ...prev.slice(0, targetIndex + 1), 
                paraDTO,                              
                ...prev.slice(targetIndex + 1) 
            ]
        });
        setCreating(false)
    }

    const handleUpdate = async (paraDTO: ParagraphDTO) => {
        setParagraphs(prev=>prev.map(p=>p.id===paraDTO.id?paraDTO:p))
        setEditing(false)
    }

    const handleDelete = async (paraDTO:ParagraphDTO) => {
        setParagraphs(prev=>prev.filter(p => p.id!== paraDTO.id))
        setEditing(false)
    }

    const handleMoveUp = async () => {
        setMoveUpLoading(true)
        try {
            const response:ResponseData<ParagraphDTO>=await httpService.empty<ParagraphDTO>(`paragraph/${paraDTO.id}/moveUp`,'POST')
            if (response.success===false) {
                setError(response.errorMsg)
                setMoveUpLoading(false)
                return
            }
            // 0,1,2,3
            const new1Old2: ParagraphDTO = response.data; // Old para 2
            const index2: number = paragraphs.findIndex(p => p.id === paraDTO.id);
            const oldParas: ParagraphDTO[] = paragraphs.slice(index2-2, index2+2); // 0:PrePre, 1:Pre, 2:This, 3:Next
            setParagraphs(prev => prev.map((p,index) => {
                switch (index) {
                    case index2-2:
                        return {...p, nextParaId: new1Old2.id};
                    case index2-1:
                        return new1Old2;
                    case index2:
                        return {...(oldParas[1]), prevParaId: new1Old2.id, nextParaId: oldParas[3].id};
                    case index2+1:
                        return {...p, prevParaId: oldParas[1].id};
                    default:
                        return p;
                }
            }))
        } catch (e) {
            if (e instanceof Error) {
                setError(e.message)
            }
            console.log(e)
        }
        setMoveUpLoading(false)
    }

    const handleMoveDown = async () => {
        setMoveDownLoading(true)
        try {
            const response:ResponseData<ParagraphDTO>=await httpService.empty<ParagraphDTO>(`paragraph/${paraDTO.id}/moveDown`,'POST')
            if (response.success===false) {
                setError(response.errorMsg)
                setMoveDownLoading(false)
                return
            }
            // 0,1,2,3
            const new2Old1: ParagraphDTO = response.data; // Old para 2
            const index1: number = paragraphs.findIndex(p => p.id === paraDTO.id);
            const oldParas: ParagraphDTO[] = paragraphs.slice(index1-1, index1+3); // 0:PrePre, 1:Pre, 2:This, 3:Next
            setParagraphs(prev => prev.map((p,index) => {
                switch (index) {
                    case index1-1:
                        return {...p, nextParaId: oldParas[2].id};
                    case index1:
                        return {...(oldParas[2]), prevParaId: oldParas[0].id, nextParaId: new2Old1.id};
                    case index1+1:
                        return new2Old1;
                    case index1+2:
                        return {...p, prevParaId: new2Old1.id};
                    default:
                        return p;
                }
            }))
        } catch (e) {
            if (e instanceof Error) {
                setError(e.message)
            }
            console.log(e)
        }
        setMoveDownLoading(false)
    }



    useEffect(() => {
        const fetchData = async () => {
            try {
                await Promise.all([fetchParaRoles()])
            } catch (e) {
                console.log(e)
            }
        }
        fetchData()
        setCanEdit(checkCanEdit(bookDTO?.scope||"PRIVATE",myRole?.roleType||null))
    }, [paraDTO])

    return (<>
        {paraDTO.id!==bookDTO?.startParaId &&
        <div className="card card-dash bg-base-100 mt-4 w-full border-base-300 border shadow-xs">
            {editing===false?
            (<>
                <div className="card-body">
                    <h2 className="card-title">{paraDTO.author}</h2>
                    {paraRoles.length>0 && <p>By: {uniqueBy(paraRoles,"userDTO.id").map(paraRoleDTO=>paraRoleDTO.userDTO.nickname).join("，")}</p>}
                    <p className="text-xs uppercase font-semibold opacity-60">{paraDTO.createTime && utc2current(paraDTO.createTime)}</p>
                    <pre className="whitespace-pre-wrap font-sans">{paraDTO.content}</pre>
                </div>
                {canEdit &&
                <div className="collapse collapse-arrow bg-base-100">
                    <input type="checkbox" />
                    <div className="collapse-title"></div>
                    <div className="collapse-content flex justify-between">
                        <div className="flex flex-row gap-2">
                            {paraDTO.prevParaId!==bookDTO?.startParaId && paraDTO.id!==bookDTO?.startParaId &&
                                <button className="btn border-base-300 border shadow-xs"
                                    onClick={handleMoveUp}>
                                    {moveUpLoading?<span className="loading loading-spinner loading-xl m-auto"></span>:
                                    <span>上移</span>}
                                </button>}
                            {paraDTO.nextParaId!==bookDTO?.endParaId && paraDTO.id!==bookDTO?.endParaId &&
                                <button className="btn border-base-300 border shadow-xs"
                                    onClick={handleMoveDown}>
                                    {moveDownLoading?<span className="loading loading-spinner loading-xl m-auto"></span>:
                                    <span>下移</span>}
                                </button>}
                            <p className="text-red-500">{error}</p>
                        </div>
                        <div className="flex flex-row gap-2">
                            <button className="btn border-base-300 border shadow-xs" onClick={()=>setEditing(true)}>编辑</button>
                        </div>
                    </div>
                </div>}
            </>)
            :(<ParaForm paraDTO={paraDTO} bookDTO={bookDTO} myRole={myRole} action="edit" handleDelete={(paraDTO)=>handleDelete(paraDTO)} handleCancel={()=>setEditing(false)} handleSave={(paraDTO)=>handleUpdate(paraDTO)} />)
            }
        </div>}
        {canEdit && <div className="flex justify-center">
            {creating===false ? (
            <button className="btn btn-ghost btn-circle mt-4"
                onClick={()=>setCreating(true)}>
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" /></svg>
            </button>
            ):(
            <div className="card card-dash bg-base-100 mt-4 w-full border-base-300 border shadow-xs">
                <ParaForm paraDTO={paraDTO} bookDTO={bookDTO} myRole={myRole} action="create" handleCancel={()=>setCreating(false)} handleSave={(paraDTO)=>handleAdd(paraDTO)} />
            </div>)}
        </div>}
    </>)
}
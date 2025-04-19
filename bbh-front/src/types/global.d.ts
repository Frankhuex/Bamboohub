interface BookDTO {
    id: number;
    createTime: Date;
    title: string;
    startParaId: number;
    endParaId: number;
    scope: "ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE";
}

interface BookDTOWithRole extends BookDTO {
    roleType: "OWNER"|"ADMIN"|"EDITOR"|"VIEWER"|null;
}

interface FollowDTO {
    id: number;
    createTime: Date;
    sourceId: number;
    target: UserDTO;
}

interface FollowsDTO {
    follows: FollowDTO[];
}

interface ParagraphDTO {
    id: number;
    createTime: Date;
    bookId: number;
    author: string;
    content: string;
    prevParaId: number;
    nextParaId: number;
}

interface ParaRoleDTO {
    id: number;
    createTime: Date;
    paraId: number;
    userId: number;
    roleType: "CREATOR"|"CONTRIBUTOR";
}

interface ParaRolesDTO {
    creators: ParaRoleDTO[];
    contributors: ParaRoleDTO[];
}

interface RoleDTO {
    id: number;
    createTime: Date;
    bookId: number;
    userDTO: UserDTO;
    roleType: "OWNER"|"ADMIN"|"EDITOR"|"VIEWER";
}

interface RolesDTO {
    owners: RoleDTO[];
    admins: RoleDTO[];
    editors: RoleDTO[];
    viewers: RoleDTO[];
}

interface UserDTO {
    id: number;
    createTime: Date;
    username: string;
    nickname: string;
}

interface UserDTOWithToken extends UserDTO {
    token: string;
}

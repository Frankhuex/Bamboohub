export interface BookReq {
    title: string;
    scope: "ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE";
}

export interface BookUpdateReq {
    title: string;
    scope: "ALLEDIT"|"ALLREAD"|"ALLSEARCH"|"PRIVATE";
}

export interface ChangePwdReq {
    oldPassword: string;
    newPassword: string;
}

export interface LoginReq {
    username: string;
    password: string;
}

export interface ParagraphReq {
    author: string;
    content: string;
    prevParaId: number;
}

export interface ParagraphUpdateReq {
    author: string;
    content: string;
}

export interface ParaRoleReq {
    userId: number;
    paraId: number;
    roleType: "CREATOR"|"CONTRIBUTOR";
}

export interface RegisterReq {
    username: string;
    password: string;
    nickname: string;
}

export interface RoleDeleteReq {
    username: string;
    bookId: number;
}

export interface RoleReq {
    userId: number;
    bookId: number;
    roleType: "OWNER"|"ADMIN"|"EDITOR"|"VIEWER"
}

export interface RolesAsViewerReq {
    bookId: number;
    userIds: number[];
}

export interface UserUpdateReq {
    username: string;
    nickname: string;
}
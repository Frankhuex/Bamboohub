export interface BookReq {
    title: string;
    scope: BookScope;
}

export interface BookUpdateReq {
    title: string;
    scope: BookScope;
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
    roleType: ParaRoleType;
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
    roleType: RoleType
}

export interface UserUpdateReq {
    username: string;
    nickname: string;
}
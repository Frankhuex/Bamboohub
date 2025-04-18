export function utc2current(utc: Date): string {
    const cur: Date=new Date(utc);
    return cur.toLocaleString("zh-CN");
}

export function toChinese(scope: string) {
    switch(scope.toLowerCase()) {
        case "alledit":
            return "公开编辑";
        case "allread":
            return "公开阅读";
        case "allsearch":
            return "公开搜索";
        case "private":
            return "私密";
        case "owner":
            return "所有者";
        case "admin":
            return "管理员";
        case "editor":
            return "编辑";
        case "viewer":
            return "读者";
        default:
            return "未知";
    }
}

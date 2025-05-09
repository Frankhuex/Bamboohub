export function utc2current(utc: Date): string {
    const cur: Date=new Date(utc);
    // return cur.toLocaleDateString("zh-CN");
    console.log(utc)
    return cur.toLocaleString("zh-CN");
}

export function toChinese(scope: string|null) {
    if (scope===null) return "无";
    switch(scope.toLowerCase()) {
        case "alledit":
            return "公开编辑";
        case "allread":
            return "公开阅读";
        case "allsearch":
            return "公开搜索";
        case "private":
            return "私密";

        case "mine":
            return "我的";
        case "scope":
            return "公共权限"
        case "createTime":
            return "创建时间";

        case "owner":
            return "所有者";
        case "admin":
            return "管理员";
        case "editor":
            return "编辑";
        case "viewer":
            return "读者";
        
        case "role":
            return "我的角色";
        
        case "creator":
            return "一作";
        case "contributor":
            return "贡献者";

        case null:
            return "无";

        default:
            return scope;
    }
}


export function uniqueBy<T>(arr: T[], key: string): T[] {
    return Array.from(
        new Map(
            arr.map(item => {
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                const value = key.split('.').reduce((obj: any, key) => obj?.[key], item);
                return [value, item];
            })
        ).values()
    );
  }
// src/components/Navbar.tsx
import { Link, useLocation } from "react-router-dom";

export default function Navbar() {
  const location = useLocation();
  
  const pageNameMap: Record<string, string> = {
    '/bookshelf': '书架',
    '/plaza': '广场',
    '/create': '新建',
    '/reading': '阅读',
    '/search': '搜索',
    '/profile': '我的'
  };

  const getPageName = () => {
    const basePath = Object.keys(pageNameMap)
      .find(path => location.pathname.startsWith(path));
    return basePath ? pageNameMap[basePath] : '未知页面';
  };

  return (
    <div className="navbar bg-base-100/90 backdrop-blur-sm">
      <div className="navbar-start"></div>
      <div className="navbar-center">
        <a className="btn btn-ghost text-xl">
          Bamboohub · {getPageName()}
        </a>
      </div>
      <div className="navbar-end">
        <Link to="/search" className="btn btn-ghost btn-circle" draggable={false}>
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </Link>
        <Link to="/create" className="btn btn-ghost btn-circle" draggable={false}>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
          </svg>
        </Link>
      </div>
    </div>
  );
}
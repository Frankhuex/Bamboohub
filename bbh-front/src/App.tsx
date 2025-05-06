// 修改后的 App.tsx
import './App.css';
import Navbar from './components/Navbar';
import Dock from './components/Dock';
import SideMenu from './components/SideMenu';
import Profile from './pages/Profile';
import MyBookshelf from './pages/MyBookshelf';
import Plaza from './pages/Plaza';
import Search from './pages/Search';
import CreateBook from './pages/CreateBook';
import Reading from './pages/Reading';
import { Routes, Route, Navigate } from 'react-router-dom';

const pageRoutes = [
  { path: '/bookshelf', component: MyBookshelf },
  { path: '/plaza', component: Plaza },
  { path: '/create', component: CreateBook },
  { path: '/reading', component: Reading },
  { path: '/search', component: Search },
  { path: '/profile', component: Profile },
];

function App() {
  return (
    // 修改主容器背景色
    <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-gray-800/80 transition-colors duration-300">
      {/* 修改导航栏背景 */}
      <header className="fixed top-0 left-0 right-0 z-50 h-16 bg-white/80 dark:bg-gray-800/80 shadow-sm backdrop-blur-sm">
        <Navbar />
      </header>

      <div className="flex flex-1 pt-16">
        {/* 统一侧边栏背景 */}
        <aside className="hidden lg:block fixed left-0 top-16 h-[calc(100vh-4rem)] w-64 z-40 bg-white/80 dark:bg-gray-800/80 shadow-lg backdrop-blur-sm">
          <SideMenu />
        </aside>

        {/* 主内容区背景调整 */}
        <main className="flex-1 overflow-y-auto h-[calc(100vh-4rem)]">
          <div className="mx-auto p-4 max-w-4xl lg:ml-[max(16rem,calc(50%-28rem))] bg-white/50 dark:bg-gray-800/80 backdrop-blur-sm">
            <Routes>
              <Route path="/" element={<Navigate to="/plaza" replace />} />
              {pageRoutes.map(({ path, component: Component }) => 
                <Route key={path} path={path} element={<Component />} />
              )}
              <Route path="/reading/:bookId" element={<Reading />} />
              <Route path="*" element={<div>404 Not Found</div>} />
            </Routes>
          </div>
        </main>
      </div>

      {/* 统一底部 Dock 背景 */}
      <footer className="fixed bottom-0 left-0 right-0 z-40 lg:hidden bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm">
        <Dock />
      </footer>
    </div>
  );
}

export default App;
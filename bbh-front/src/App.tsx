import { useState } from 'react'

import './App.css'
import Navbar from './components/Navbar'
import Dock from './components/Dock'
import Plaza from './pages/Plaza'
import SideMenu from './components/SideMenu'
import BookShelf from './pages/BookShelf'


function App() {
  const [chosenPage, setChosenPage] = useState<number>(1);

  return (
    <div className="min-h-screen flex flex-col">
      {/* 1. 固定在顶部的 Navbar */}
      <header className="fixed top-0 left-0 right-0 z-50 h-16 bg-white shadow-sm">
        <Navbar />
      </header>

      {/* 2. 主容器（flex布局，SideMenu + 内容区） */}
      <div className="flex flex-1 pt-16"> {/* pt-16 为 Navbar 预留空间 */}
        {/* 2.1 左侧 SideMenu（固定宽度，不滚动） */}
        <aside className="hidden lg:block w-64 h-[calc(100vh-4rem)] sticky top-16 overflow-y-auto">
          <SideMenu 
            chosenPage={chosenPage}
            onPageChange={setChosenPage}
          />
        </aside>

        {/* 2.2 右侧内容区（可滚动，内容居中） */}
        <main className="flex-1 overflow-y-auto h-[calc(100vh-4rem)]">
          <div className="max-w-4xl mx-auto p-4"> {/* 内容居中 + 内边距 */}
            {chosenPage === 0 && <BookShelf />}
            {chosenPage === 1 && <Plaza />}
            {/* 其他页面... */}
          </div>
        </main>
      </div>

      {/* 3. 固定在底部的 Dock（移动端显示） */}
      <footer className="fixed bottom-0 left-0 right-0 z-40 lg:hidden">
        <Dock 
          chosenPage={chosenPage}
          onPageChange={setChosenPage}
        />
      </footer>
    </div>
  );
}

export default App

import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useState, useEffect } from 'react';
import { FiHome, FiPackage, FiTag, FiShoppingCart, FiBarChart2, FiAlertTriangle, FiTrendingUp, FiLogOut, FiMenu, FiX, FiBell } from 'react-icons/fi';
import api from '../../services/api';

export default function Layout() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [alertCount, setAlertCount] = useState(0);

  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const res = await api.get('/alertas/count');
        setAlertCount(res.data.count);
      } catch (e) {}
    };
    fetchAlerts();
    const interval = setInterval(fetchAlerts, 30000);
    return () => clearInterval(interval);
  }, []);

  const menu = [
    { to: '/', icon: <FiHome />, label: 'Dashboard' },
    { to: '/productos', icon: <FiPackage />, label: 'Productos' },
    { to: '/categorias', icon: <FiTag />, label: 'Categorías' },
    { to: '/ventas', icon: <FiShoppingCart />, label: 'Ventas' },
    { to: '/alertas', icon: <FiAlertTriangle />, label: 'Alertas' },
    { to: '/predicciones', icon: <FiTrendingUp />, label: 'Predicciones' },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <aside className={`fixed inset-y-0 left-0 z-50 w-64 bg-slate-900 text-white transform transition-transform lg:translate-x-0 lg:static ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="p-4 border-b border-slate-700">
          <h1 className="text-xl font-bold text-blue-400">📦 Inventario</h1>
          <p className="text-xs text-slate-400 mt-1">Sistema de Gestión</p>
        </div>
        <nav className="p-4 space-y-1">
          {menu.map((item) => (
            <Link key={item.to} to={item.to} onClick={() => setSidebarOpen(false)}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors ${isActive(item.to) ? 'bg-blue-600 text-white' : 'text-slate-300 hover:bg-slate-800'}`}>
              {item.icon}
              {item.label}
              {item.to === '/alertas' && alertCount > 0 && (
                <span className="ml-auto bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">{alertCount}</span>
              )}
            </Link>
          ))}
        </nav>
        <div className="absolute bottom-0 w-full p-4 border-t border-slate-700">
          <div className="text-sm text-slate-400 mb-2">{user?.nombre}</div>
          <button onClick={logout} className="flex items-center gap-2 text-slate-400 hover:text-red-400 text-sm">
            <FiLogOut /> Cerrar sesión
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="bg-white shadow-sm border-b px-4 py-3 flex items-center justify-between lg:justify-end">
          <button onClick={() => setSidebarOpen(!sidebarOpen)} className="lg:hidden text-gray-600">
            {sidebarOpen ? <FiX size={24} /> : <FiMenu size={24} />}
          </button>
          <div className="flex items-center gap-4">
            <Link to="/alertas" className="relative text-gray-600 hover:text-blue-600">
              <FiBell size={20} />
              {alertCount > 0 && <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">{alertCount}</span>}
            </Link>
            <span className="text-sm text-gray-600">{user?.email}</span>
          </div>
        </header>
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>

      {/* Overlay */}
      {sidebarOpen && <div onClick={() => setSidebarOpen(false)} className="fixed inset-0 bg-black/50 z-40 lg:hidden" />}
    </div>
  );
}

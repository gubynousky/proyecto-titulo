import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/layout/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Productos from './pages/Productos';
import Categorias from './pages/Categorias';
import Ventas from './pages/Ventas';
import Alertas from './pages/Alertas';
import Predicciones from './pages/Predicciones';

function PrivateRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="flex justify-center items-center h-screen"><div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" /></div>;
  return user ? children : <Navigate to="/login" />;
}

function AppRoutes() {
  const { user } = useAuth();
  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" /> : <Login />} />
      <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
        <Route index element={<Dashboard />} />
        <Route path="productos" element={<Productos />} />
        <Route path="categorias" element={<Categorias />} />
        <Route path="ventas" element={<Ventas />} />
        <Route path="alertas" element={<Alertas />} />
        <Route path="predicciones" element={<Predicciones />} />
      </Route>
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Toaster position="top-right" />
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

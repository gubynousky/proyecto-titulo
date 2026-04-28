import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function Login() {
  const [isRegistro, setIsRegistro] = useState(false);
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const { login, registro } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (isRegistro) {
        await registro(nombre, email, password);
        toast.success('Registro exitoso');
      } else {
        await login(email, password);
        toast.success('Bienvenido/a');
      }
      navigate('/');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Error de autenticación');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-slate-800">📦 Inventario</h1>
          <p className="text-slate-500 mt-2">Sistema de Gestión para Pymes</p>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          {isRegistro && (
            <input type="text" placeholder="Nombre completo" value={nombre} onChange={e => setNombre(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none" required />
          )}
          <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none" required />
          <input type="password" placeholder="Contraseña" value={password} onChange={e => setPassword(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none" required minLength={6} />
          <button type="submit" disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50">
            {loading ? 'Cargando...' : isRegistro ? 'Registrarse' : 'Iniciar Sesión'}
          </button>
        </form>
        <p className="text-center mt-6 text-sm text-gray-500">
          {isRegistro ? '¿Ya tienes cuenta?' : '¿No tienes cuenta?'}
          <button onClick={() => setIsRegistro(!isRegistro)} className="text-blue-600 font-semibold ml-1 hover:underline">
            {isRegistro ? 'Inicia sesión' : 'Regístrate'}
          </button>
        </p>
      </div>
    </div>
  );
}

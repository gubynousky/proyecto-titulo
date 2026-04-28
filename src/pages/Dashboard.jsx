import { useState, useEffect } from 'react';
import api from '../services/api';
import { FiPackage, FiAlertTriangle, FiShoppingCart, FiDollarSign } from 'react-icons/fi';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/dashboard').then(res => { setData(res.data); setLoading(false); }).catch(() => setLoading(false));
  }, []);

  if (loading) return <div className="flex justify-center py-20"><div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" /></div>;
  if (!data) return <p className="text-center text-gray-500">Error al cargar dashboard</p>;

  const kpis = [
    { label: 'Total Productos', value: data.totalProductos, icon: <FiPackage />, color: 'bg-blue-500' },
    { label: 'Bajo Stock', value: data.productosBajoStock, icon: <FiAlertTriangle />, color: 'bg-red-500' },
    { label: 'Ventas Hoy', value: data.ventasHoy, icon: <FiShoppingCart />, color: 'bg-green-500' },
    { label: 'Ingresos Hoy', value: `$${(data.ingresosHoy || 0).toLocaleString('es-CL')}`, icon: <FiDollarSign />, color: 'bg-purple-500' },
  ];

  const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>
      {/* KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {kpis.map((kpi, i) => (
          <div key={i} className="bg-white rounded-xl shadow-sm p-5 flex items-center gap-4">
            <div className={`${kpi.color} text-white p-3 rounded-lg`}>{kpi.icon}</div>
            <div>
              <p className="text-sm text-gray-500">{kpi.label}</p>
              <p className="text-2xl font-bold text-gray-800">{kpi.value}</p>
            </div>
          </div>
        ))}
      </div>
      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h2 className="text-lg font-semibold text-gray-700 mb-4">Ventas últimos 7 días</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={data.ventasPorDia}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="fecha" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="cantidad" fill="#3B82F6" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h2 className="text-lg font-semibold text-gray-700 mb-4">Top Productos Vendidos</h2>
          {data.topProductos?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie data={data.topProductos} dataKey="cantidadVendida" nameKey="nombre" cx="50%" cy="50%" outerRadius={100} label={({ nombre }) => nombre}>
                  {data.topProductos.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          ) : <p className="text-gray-400 text-center py-20">Sin datos de ventas aún</p>}
        </div>
      </div>
    </div>
  );
}

import { useState, useEffect, useRef } from 'react';
import api from '../services/api';
import { FiTrendingUp, FiRefreshCw } from 'react-icons/fi';

// Cache global para que persista entre navegaciones
let cacheData = null;
let cacheTime = null;

export default function Predicciones() {
  const [predicciones, setPredicciones] = useState(cacheData || []);
  const [loading, setLoading] = useState(!cacheData);
  const [error, setError] = useState(null);

  const fetchData = (force = false) => {
    // Si hay cache de menos de 5 minutos, no recargar
    if (!force && cacheData) {
      setPredicciones(cacheData);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    api.get('/predicciones', { timeout: 180000 })
      .then(r => {
        cacheData = r.data;
        cacheTime = Date.now();
        setPredicciones(r.data);
        setLoading(false);
      })
      .catch(e => { setError('Error al cargar predicciones. Intenta de nuevo.'); setLoading(false); });
  };

  useEffect(() => { fetchData(); }, []);

  const riesgoColor = { CRITICO: 'bg-red-100 text-red-700', ALTO: 'bg-orange-100 text-orange-700', MEDIO: 'bg-yellow-100 text-yellow-700', BAJO: 'bg-green-100 text-green-700' };

  if (loading) return (
    <div className="flex flex-col items-center justify-center py-20 gap-4">
      <div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" />
      <p className="text-gray-500">Calculando predicciones para {cacheData ? 'actualizar' : '156 productos'}...</p>
      <p className="text-gray-400 text-sm">Esto puede tardar hasta 2 minutos</p>
    </div>
  );

  if (error) return (
    <div className="text-center py-20">
      <p className="text-red-500 mb-4">{error}</p>
      <button onClick={() => fetchData(true)} className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 mx-auto hover:bg-blue-700"><FiRefreshCw /> Reintentar</button>
    </div>
  );

  const conVentas = predicciones.filter(p => p.diasRestantes < 999);
  const sinVentas = predicciones.filter(p => p.diasRestantes >= 999);

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800 flex items-center gap-2"><FiTrendingUp /> Predicción de Stock</h1>
        <button onClick={() => fetchData(true)} className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"><FiRefreshCw /> Actualizar</button>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden mb-6">
        <div className="bg-blue-50 px-4 py-3 border-b">
          <h2 className="font-semibold text-blue-800">Productos con historial de ventas ({conVentas.length})</h2>
        </div>
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Producto', 'Stock', 'Mín.', 'Consumo/día', 'Días restantes', 'Riesgo', 'Recomendación'].map(h => (
                <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {conVentas.map(p => (
              <tr key={p.productoId} className={`hover:bg-gray-50 ${p.nivelRiesgo === 'CRITICO' ? 'bg-red-50' : p.nivelRiesgo === 'ALTO' ? 'bg-orange-50' : ''}`}>
                <td className="px-4 py-3 text-sm font-medium">{p.productoNombre}</td>
                <td className="px-4 py-3 text-sm">{p.stockActual}</td>
                <td className="px-4 py-3 text-sm">{p.stockMinimo}</td>
                <td className="px-4 py-3 text-sm">{p.consumoPromedioDiario}</td>
                <td className="px-4 py-3 text-sm font-bold">{p.diasRestantes}</td>
                <td className="px-4 py-3"><span className={`px-2 py-1 rounded-full text-xs font-medium ${riesgoColor[p.nivelRiesgo]}`}>{p.nivelRiesgo}</span></td>
                <td className="px-4 py-3 text-sm text-gray-600">{p.recomendacion}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="bg-gray-50 px-4 py-3 border-b">
          <h2 className="font-semibold text-gray-600">Sin datos de ventas suficientes ({sinVentas.length})</h2>
        </div>
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Producto', 'Stock', 'Mín.', 'Estado'].map(h => (
                <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {sinVentas.map(p => (
              <tr key={p.productoId} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-sm font-medium">{p.productoNombre}</td>
                <td className="px-4 py-3 text-sm">{p.stockActual}</td>
                <td className="px-4 py-3 text-sm">{p.stockMinimo}</td>
                <td className="px-4 py-3 text-sm text-gray-400">Sin ventas registradas</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

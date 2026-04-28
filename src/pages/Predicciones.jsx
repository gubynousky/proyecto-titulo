import { useState, useEffect } from 'react';
import api from '../services/api';
import { FiTrendingUp } from 'react-icons/fi';

export default function Predicciones() {
  const [predicciones, setPredicciones] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { api.get('/predicciones').then(r => { setPredicciones(r.data); setLoading(false); }).catch(() => setLoading(false)); }, []);

  const riesgoColor = { CRITICO: 'bg-red-100 text-red-700', ALTO: 'bg-orange-100 text-orange-700', MEDIO: 'bg-yellow-100 text-yellow-700', BAJO: 'bg-green-100 text-green-700' };

  if (loading) return <div className="flex justify-center py-20"><div className="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full" /></div>;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2"><FiTrendingUp /> Predicción de Stock</h1>
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Producto', 'Stock', 'Mín.', 'Consumo/día', 'Días restantes', 'Riesgo', 'Recomendación'].map(h => (
                <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {predicciones.map(p => (
              <tr key={p.productoId} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-sm font-medium">{p.productoNombre}</td>
                <td className="px-4 py-3 text-sm">{p.stockActual}</td>
                <td className="px-4 py-3 text-sm">{p.stockMinimo}</td>
                <td className="px-4 py-3 text-sm">{p.consumoPromedioDiario}</td>
                <td className="px-4 py-3 text-sm font-bold">{p.diasRestantes === 999 ? '∞' : p.diasRestantes}</td>
                <td className="px-4 py-3"><span className={`px-2 py-1 rounded-full text-xs font-medium ${riesgoColor[p.nivelRiesgo]}`}>{p.nivelRiesgo}</span></td>
                <td className="px-4 py-3 text-sm text-gray-600">{p.recomendacion}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {predicciones.length === 0 && <p className="text-center py-10 text-gray-400">No hay productos para predecir</p>}
      </div>
    </div>
  );
}

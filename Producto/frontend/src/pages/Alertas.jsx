import { useState, useEffect } from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';
import { FiAlertTriangle, FiCheckCircle, FiRefreshCw } from 'react-icons/fi';

export default function Alertas() {
  const [alertas, setAlertas] = useState([]);
  const fetch = () => api.get('/alertas').then(r => setAlertas(r.data));
  useEffect(() => { fetch(); }, []);

  const generar = async () => { await api.post('/alertas/generar'); toast.success('Alertas generadas'); fetch(); };
  const marcarLeida = async (id) => { await api.put(`/alertas/${id}/leer`); fetch(); };
  const marcarTodas = async () => { await api.put('/alertas/leer-todas'); toast.success('Todas marcadas como leídas'); fetch(); };

  const nivelColor = { CRITICO: 'bg-red-100 text-red-700 border-red-300', ALTO: 'bg-orange-100 text-orange-700 border-orange-300', MEDIO: 'bg-yellow-100 text-yellow-700 border-yellow-300', BAJO: 'bg-blue-100 text-blue-700 border-blue-300' };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Alertas</h1>
        <div className="flex gap-2">
          <button onClick={generar} className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"><FiRefreshCw /> Generar</button>
          {alertas.length > 0 && <button onClick={marcarTodas} className="bg-gray-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-gray-700"><FiCheckCircle /> Leer todas</button>}
        </div>
      </div>
      <div className="space-y-3">
        {alertas.map(a => (
          <div key={a.id} className={`rounded-xl border p-4 flex justify-between items-center ${nivelColor[a.nivel] || 'bg-gray-100'}`}>
            <div className="flex items-center gap-3">
              <FiAlertTriangle className="text-xl" />
              <div>
                <span className="font-semibold text-xs uppercase">{a.nivel}</span>
                <p className="text-sm mt-0.5">{a.mensaje}</p>
                <p className="text-xs opacity-60 mt-1">{new Date(a.createdAt).toLocaleString('es-CL')}</p>
              </div>
            </div>
            <button onClick={() => marcarLeida(a.id)} className="text-sm hover:underline opacity-70">Marcar leída</button>
          </div>
        ))}
        {alertas.length === 0 && <p className="text-center py-10 text-gray-400">No hay alertas pendientes</p>}
      </div>
    </div>
  );
}

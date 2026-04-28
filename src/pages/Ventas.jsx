import { useState, useEffect } from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';
import { FiPlus, FiXCircle, FiTrash2 } from 'react-icons/fi';

export default function Ventas() {
  const [ventas, setVentas] = useState([]);
  const [productos, setProductos] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [detalles, setDetalles] = useState([{ productoId: '', cantidad: 1 }]);

  const fetchVentas = () => api.get('/ventas').then(r => setVentas(r.data));
  const fetchProductos = () => api.get('/productos?size=200').then(r => setProductos(r.data.content || []));
  useEffect(() => { fetchVentas(); fetchProductos(); }, []);

  const addDetalle = () => setDetalles([...detalles, { productoId: '', cantidad: 1 }]);
  const removeDetalle = (i) => setDetalles(detalles.filter((_, idx) => idx !== i));
  const updateDetalle = (i, field, value) => { const d = [...detalles]; d[i][field] = value; setDetalles(d); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = { detalles: detalles.filter(d => d.productoId).map(d => ({ productoId: parseInt(d.productoId), cantidad: parseInt(d.cantidad) })) };
    if (data.detalles.length === 0) return toast.error('Agrega al menos un producto');
    try { await api.post('/ventas', data); toast.success('Venta registrada'); setShowModal(false); setDetalles([{ productoId: '', cantidad: 1 }]); fetchVentas(); fetchProductos(); }
    catch (err) { toast.error(err.response?.data?.error || 'Error'); }
  };

  const handleAnular = async (id) => {
    if (!confirm('¿Anular esta venta? Se restaurará el stock.')) return;
    try { await api.put(`/ventas/${id}/anular`); toast.success('Venta anulada'); fetchVentas(); }
    catch (err) { toast.error(err.response?.data?.error || 'Error'); }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Ventas</h1>
        <button onClick={() => setShowModal(true)} className="bg-green-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-green-700"><FiPlus /> Nueva Venta</button>
      </div>
      <div className="space-y-3">
        {ventas.map(v => (
          <div key={v.id} className={`bg-white rounded-xl shadow-sm p-5 ${v.anulada ? 'opacity-50' : ''}`}>
            <div className="flex justify-between items-start">
              <div>
                <div className="flex items-center gap-3">
                  <span className="font-bold text-gray-800">Venta #{v.id}</span>
                  {v.anulada && <span className="bg-red-100 text-red-700 text-xs px-2 py-0.5 rounded-full">Anulada</span>}
                </div>
                <p className="text-sm text-gray-500 mt-1">{new Date(v.createdAt).toLocaleString('es-CL')} — {v.usuarioNombre}</p>
                <div className="mt-2 space-y-1">
                  {v.detalles?.map((d, i) => (
                    <p key={i} className="text-sm text-gray-600">{d.cantidad}x {d.productoNombre} — ${d.subtotal?.toLocaleString('es-CL')}</p>
                  ))}
                </div>
              </div>
              <div className="text-right">
                <p className="text-xs text-gray-400">Subtotal: ${v.subtotal?.toLocaleString('es-CL')}</p>
                <p className="text-xs text-gray-400">IVA 19%: ${v.iva?.toLocaleString('es-CL')}</p>
                <p className="text-lg font-bold text-gray-800">${v.total?.toLocaleString('es-CL')}</p>
                {!v.anulada && <button onClick={() => handleAnular(v.id)} className="text-red-500 text-sm mt-2 hover:underline flex items-center gap-1 ml-auto"><FiXCircle /> Anular</button>}
              </div>
            </div>
          </div>
        ))}
        {ventas.length === 0 && <p className="text-center py-10 text-gray-400">No hay ventas registradas</p>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl w-full max-w-lg p-6">
            <h2 className="text-xl font-bold mb-4">Nueva Venta</h2>
            <form onSubmit={handleSubmit} className="space-y-3">
              {detalles.map((d, i) => (
                <div key={i} className="flex gap-2 items-center">
                  <select value={d.productoId} onChange={e => updateDetalle(i, 'productoId', e.target.value)} className="flex-1 border rounded-lg px-3 py-2 focus:outline-none" required>
                    <option value="">Seleccionar producto</option>
                    {productos.filter(p => p.activo && p.stock > 0).map(p => <option key={p.id} value={p.id}>{p.nombre} (Stock: {p.stock}) - ${p.precio?.toLocaleString('es-CL')}</option>)}
                  </select>
                  <input type="number" min="1" value={d.cantidad} onChange={e => updateDetalle(i, 'cantidad', e.target.value)} className="w-20 border rounded-lg px-3 py-2 focus:outline-none" />
                  {detalles.length > 1 && <button type="button" onClick={() => removeDetalle(i)} className="text-red-500"><FiTrash2 /></button>}
                </div>
              ))}
              <button type="button" onClick={addDetalle} className="text-blue-600 text-sm hover:underline">+ Agregar producto</button>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 border py-2 rounded-lg hover:bg-gray-50">Cancelar</button>
                <button type="submit" className="flex-1 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700">Registrar Venta</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

import { useState, useEffect } from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';
import { FiPlus, FiEdit2, FiTrash2 } from 'react-icons/fi';

export default function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ nombre: '', descripcion: '' });

  const fetch = () => api.get('/categorias/todas').then(r => setCategorias(r.data));
  useEffect(() => { fetch(); }, []);

  const openNew = () => { setEditing(null); setForm({ nombre: '', descripcion: '' }); setShowModal(true); };
  const openEdit = (c) => { setEditing(c); setForm({ nombre: c.nombre, descripcion: c.descripcion || '' }); setShowModal(true); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editing) { await api.put(`/categorias/${editing.id}`, form); toast.success('Categoría actualizada'); }
      else { await api.post('/categorias', form); toast.success('Categoría creada'); }
      setShowModal(false); fetch();
    } catch (err) { toast.error(err.response?.data?.error || 'Error'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar esta categoría?')) return;
    try { await api.delete(`/categorias/${id}`); toast.success('Categoría eliminada'); fetch(); }
    catch { toast.error('Error al eliminar'); }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Categorías</h1>
        <button onClick={openNew} className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"><FiPlus /> Nueva</button>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {categorias.map(c => (
          <div key={c.id} className={`bg-white rounded-xl shadow-sm p-5 border-l-4 ${c.activa ? 'border-blue-500' : 'border-gray-300'}`}>
            <div className="flex justify-between items-start">
              <div>
                <h3 className="font-semibold text-gray-800">{c.nombre}</h3>
                <p className="text-sm text-gray-500 mt-1">{c.descripcion || 'Sin descripción'}</p>
                <p className="text-xs text-gray-400 mt-2">{c.totalProductos} productos</p>
              </div>
              <div className="flex gap-2">
                <button onClick={() => openEdit(c)} className="text-blue-600 hover:text-blue-800"><FiEdit2 /></button>
                <button onClick={() => handleDelete(c.id)} className="text-red-600 hover:text-red-800"><FiTrash2 /></button>
              </div>
            </div>
          </div>
        ))}
      </div>
      {categorias.length === 0 && <p className="text-center py-10 text-gray-400">No hay categorías</p>}

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl w-full max-w-md p-6">
            <h2 className="text-xl font-bold mb-4">{editing ? 'Editar Categoría' : 'Nueva Categoría'}</h2>
            <form onSubmit={handleSubmit} className="space-y-3">
              <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" required />
              <input placeholder="Descripción" value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})} className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" />
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 border py-2 rounded-lg hover:bg-gray-50">Cancelar</button>
                <button type="submit" className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700">{editing ? 'Guardar' : 'Crear'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

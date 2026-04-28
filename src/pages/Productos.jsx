import { useState, useEffect } from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';
import { FiPlus, FiEdit2, FiTrash2, FiSearch } from 'react-icons/fi';

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ codigo: '', nombre: '', descripcion: '', precio: '', stock: '', stockMinimo: '10', categoriaId: '' });

  const fetchProductos = () => api.get('/productos', { params: { nombre: search || undefined, size: 100 } }).then(r => setProductos(r.data.content || []));
  const fetchCategorias = () => api.get('/categorias').then(r => setCategorias(r.data));

  useEffect(() => { fetchProductos(); fetchCategorias(); }, []);
  useEffect(() => { const t = setTimeout(fetchProductos, 300); return () => clearTimeout(t); }, [search]);

  const openNew = () => { setEditing(null); setForm({ codigo: '', nombre: '', descripcion: '', precio: '', stock: '', stockMinimo: '10', categoriaId: '' }); setShowModal(true); };
  const openEdit = (p) => { setEditing(p); setForm({ codigo: p.codigo, nombre: p.nombre, descripcion: p.descripcion || '', precio: String(p.precio), stock: String(p.stock), stockMinimo: String(p.stockMinimo), categoriaId: p.categoriaId ? String(p.categoriaId) : '' }); setShowModal(true); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = { ...form, precio: parseFloat(form.precio), stock: parseInt(form.stock), stockMinimo: parseInt(form.stockMinimo), categoriaId: form.categoriaId ? parseInt(form.categoriaId) : null };
    try {
      if (editing) { await api.put(`/productos/${editing.id}`, data); toast.success('Producto actualizado'); }
      else { await api.post('/productos', data); toast.success('Producto creado'); }
      setShowModal(false); fetchProductos();
    } catch (err) { toast.error(err.response?.data?.error || 'Error'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar este producto?')) return;
    try { await api.delete(`/productos/${id}`); toast.success('Producto eliminado'); fetchProductos(); }
    catch (err) { toast.error('Error al eliminar'); }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Productos</h1>
        <button onClick={openNew} className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"><FiPlus /> Nuevo</button>
      </div>
      <div className="relative mb-4">
        <FiSearch className="absolute left-3 top-3 text-gray-400" />
        <input type="text" placeholder="Buscar productos..." value={search} onChange={e => setSearch(e.target.value)}
          className="w-full pl-10 pr-4 py-2.5 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none" />
      </div>
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b">
            <tr>
              {['Código', 'Nombre', 'Categoría', 'Precio', 'Stock', 'Estado', 'Acciones'].map(h => (
                <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {productos.map(p => (
              <tr key={p.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-sm font-mono">{p.codigo}</td>
                <td className="px-4 py-3 text-sm font-medium">{p.nombre}</td>
                <td className="px-4 py-3 text-sm">{p.categoriaNombre || '-'}</td>
                <td className="px-4 py-3 text-sm">${p.precio?.toLocaleString('es-CL')}</td>
                <td className="px-4 py-3 text-sm">
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${p.stock <= p.stockMinimo ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>{p.stock}</span>
                </td>
                <td className="px-4 py-3 text-sm">
                  <span className={`px-2 py-1 rounded-full text-xs ${p.activo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>{p.activo ? 'Activo' : 'Inactivo'}</span>
                </td>
                <td className="px-4 py-3 text-sm flex gap-2">
                  <button onClick={() => openEdit(p)} className="text-blue-600 hover:text-blue-800"><FiEdit2 /></button>
                  <button onClick={() => handleDelete(p.id)} className="text-red-600 hover:text-red-800"><FiTrash2 /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {productos.length === 0 && <p className="text-center py-10 text-gray-400">No hay productos</p>}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl w-full max-w-lg p-6">
            <h2 className="text-xl font-bold mb-4">{editing ? 'Editar Producto' : 'Nuevo Producto'}</h2>
            <form onSubmit={handleSubmit} className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <input placeholder="Código" value={form.codigo} onChange={e => setForm({...form, codigo: e.target.value})} className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" required />
                <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" required />
              </div>
              <input placeholder="Descripción" value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})} className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" />
              <div className="grid grid-cols-3 gap-3">
                <input type="number" placeholder="Precio" value={form.precio} onChange={e => setForm({...form, precio: e.target.value})} className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" required min="1" />
                <input type="number" placeholder="Stock" value={form.stock} onChange={e => setForm({...form, stock: e.target.value})} className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" required min="0" />
                <input type="number" placeholder="Stock mín." value={form.stockMinimo} onChange={e => setForm({...form, stockMinimo: e.target.value})} className="border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none" min="0" />
              </div>
              <select value={form.categoriaId} onChange={e => setForm({...form, categoriaId: e.target.value})} className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                <option value="">Sin categoría</option>
                {categorias.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
              </select>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 border border-gray-300 py-2 rounded-lg hover:bg-gray-50">Cancelar</button>
                <button type="submit" className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700">{editing ? 'Guardar' : 'Crear'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

import React, { useState, useEffect } from 'react'
import { orderAPI, paymentAPI } from '../../services/api'
import toast from 'react-hot-toast'

const STATUS_ORDER = ['PENDING','CONFIRMED','IN_PROGRESS','READY','SERVED','CLOSED','CANCELLED']
const STATUS_COLORS = { PENDING:'badge-warning', CONFIRMED:'badge-info', IN_PROGRESS:'badge-purple', READY:'badge-orange', SERVED:'badge-success', CLOSED:'badge-gray', CANCELLED:'badge-danger' }
const NEXT_STATUS = { PENDING:'CONFIRMED', CONFIRMED:'IN_PROGRESS', IN_PROGRESS:'READY', READY:'SERVED', SERVED:'CLOSED' }

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [filter, setFilter] = useState('')
  const [selected, setSelected] = useState(null)
  const [payModal, setPayModal] = useState(null)
  const [payMethod, setPayMethod] = useState('CASH')
  const [loading, setLoading] = useState(true)

  const load = () => {
    orderAPI.getAll().then(r => setOrders(r.data.data || []))
      .catch(()=>toast.error('Failed to load orders'))
      .finally(()=>setLoading(false))
  }
  useEffect(()=>{ load() },[])

  const filtered = filter ? orders.filter(o=>o.status===filter) : orders

  const updateStatus = async (id, status) => {
    try { await orderAPI.updateStatus(id, status); toast.success(`Status → ${status}`); load(); setSelected(null) }
    catch { toast.error('Update failed') }
  }

  const processPayment = async () => {
    try {
      await paymentAPI.process({ orderId: payModal.id, paymentMethod: payMethod })
      toast.success('Payment processed! ✅'); setPayModal(null); load()
    } catch(e) { toast.error(e.response?.data?.message || 'Payment failed') }
  }

  if (loading) return <div className="page-content"><div className="loading-page"><div className="spinner"/></div></div>

  return (
    <div className="page-content">
      <div className="flex-between page-header">
        <div><h1>📋 Orders</h1><p>Manage and track all orders</p></div>
        <button className="btn btn-secondary" onClick={load}>🔄 Refresh</button>
      </div>

      {/* Filter Tabs */}
      <div style={{display:'flex',gap:'6px',marginBottom:'20px',flexWrap:'wrap'}}>
        <button onClick={()=>setFilter('')} className={`btn btn-sm ${!filter?'btn-primary':'btn-secondary'}`}>All ({orders.length})</button>
        {STATUS_ORDER.map(s=>{
          const count = orders.filter(o=>o.status===s).length
          return count > 0 ? (
            <button key={s} onClick={()=>setFilter(s)} className={`btn btn-sm ${filter===s?'btn-primary':'btn-secondary'}`}>
              {s} ({count})
            </button>
          ) : null
        })}
      </div>

      <div className="card">
        <div className="table-wrap">
          <table>
            <thead><tr><th>Order</th><th>Customer</th><th>Table</th><th>Items</th><th>Amount</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>
              {filtered.length === 0
                ? <tr><td colSpan={7}><div className="empty-state"><div className="icon">📋</div><p>No orders found</p></div></td></tr>
                : filtered.map(o=>(
                  <tr key={o.id}>
                    <td><strong style={{color:'var(--primary)'}}>#ORD-{o.id}</strong><div style={{fontSize:'11px',color:'var(--text-muted)'}}>{o.createdAt?new Date(o.createdAt).toLocaleDateString():''}</div></td>
                    <td>{o.customerName||'Walk-in'}</td>
                    <td>{o.tableNumber?`🪑 ${o.tableNumber}`:'—'}</td>
                    <td><span className="badge badge-gray">{(o.orderItems||[]).length} items</span></td>
                    <td><span className="price">৳{parseFloat(o.totalAmount||0).toFixed(0)}</span></td>
                    <td><span className={`badge ${STATUS_COLORS[o.status]}`}>{o.status}</span></td>
                    <td>
                      <div style={{display:'flex',gap:'6px',flexWrap:'wrap'}}>
                        <button className="btn btn-secondary btn-sm" onClick={()=>setSelected(o)}>👁 View</button>
                        {NEXT_STATUS[o.status] && (
                          <button className="btn btn-primary btn-sm" onClick={()=>updateStatus(o.id, NEXT_STATUS[o.status])}>
                            → {NEXT_STATUS[o.status]}
                          </button>
                        )}
                        {o.status==='SERVED' && (
                          <button className="btn btn-success btn-sm" onClick={()=>setPayModal(o)}>💰 Pay</button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              }
            </tbody>
          </table>
        </div>
      </div>

      {/* Order Detail Modal */}
      {selected && (
        <div className="modal-overlay" onClick={()=>setSelected(null)}>
          <div className="modal" style={{maxWidth:'600px'}} onClick={e=>e.stopPropagation()}>
            <div className="modal-header">
              <h3>📋 Order #ORD-{selected.id}</h3>
              <button className="btn btn-secondary btn-sm" onClick={()=>setSelected(null)}>✕</button>
            </div>
            <div className="modal-body">
              <div className="form-grid-2" style={{marginBottom:'16px'}}>
                <div><span className="text-muted">Customer</span><div style={{fontWeight:600}}>{selected.customerName||'Walk-in'}</div></div>
                <div><span className="text-muted">Table</span><div style={{fontWeight:600}}>{selected.tableNumber||'—'}</div></div>
                <div><span className="text-muted">Status</span><div><span className={`badge ${STATUS_COLORS[selected.status]}`}>{selected.status}</span></div></div>
                <div><span className="text-muted">Notes</span><div style={{fontSize:'13px'}}>{selected.notes||'—'}</div></div>
              </div>
              <div className="divider"/>
              <h4 style={{marginBottom:'10px',fontWeight:700}}>Order Items</h4>
              {(selected.orderItems||[]).map(item=>(
                <div key={item.id} style={{display:'flex',justifyContent:'space-between',alignItems:'center',padding:'8px 0',borderBottom:'1px solid var(--border)'}}>
                  <div>
                    <div style={{fontWeight:600}}>{item.menuItemName}</div>
                    {item.specialRequest && <div style={{fontSize:'12px',color:'var(--text-muted)'}}>Note: {item.specialRequest}</div>}
                  </div>
                  <div style={{textAlign:'right'}}>
                    <div style={{fontSize:'13px',color:'var(--text-muted)'}}>{item.quantity} × ৳{parseFloat(item.unitPrice).toFixed(0)}</div>
                    <div className="price">৳{parseFloat(item.subtotal||0).toFixed(0)}</div>
                  </div>
                </div>
              ))}
              <div style={{display:'flex',justifyContent:'space-between',marginTop:'12px',paddingTop:'12px',borderTop:'2px solid var(--border)'}}>
                <div>
                  <div style={{fontSize:'13px',color:'var(--text-muted)'}}>Tax: ৳{parseFloat(selected.taxAmount||0).toFixed(0)}</div>
                  <div style={{fontWeight:800,fontSize:'18px',color:'var(--primary)'}}>Total: ৳{parseFloat(selected.totalAmount||0).toFixed(0)}</div>
                </div>
                {NEXT_STATUS[selected.status] && (
                  <button className="btn btn-primary" onClick={()=>updateStatus(selected.id, NEXT_STATUS[selected.status])}>
                    → Move to {NEXT_STATUS[selected.status]}
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Payment Modal */}
      {payModal && (
        <div className="modal-overlay" onClick={()=>setPayModal(null)}>
          <div className="modal" style={{maxWidth:'400px'}} onClick={e=>e.stopPropagation()}>
            <div className="modal-header">
              <h3>💰 Process Payment</h3>
              <button className="btn btn-secondary btn-sm" onClick={()=>setPayModal(null)}>✕</button>
            </div>
            <div className="modal-body">
              <div style={{textAlign:'center',marginBottom:'20px',padding:'20px',background:'var(--primary-light)',borderRadius:'10px'}}>
                <div style={{fontSize:'14px',color:'var(--text-muted)'}}>Order #ORD-{payModal.id}</div>
                <div style={{fontSize:'32px',fontWeight:800,color:'var(--primary)'}}>৳{parseFloat(payModal.totalAmount||0).toFixed(0)}</div>
              </div>
              <div className="form-group">
                <label className="form-label">Payment Method</label>
                <div style={{display:'flex',gap:'10px'}}>
                  {['CASH','CARD','ONLINE'].map(m=>(
                    <button key={m} onClick={()=>setPayMethod(m)}
                      className={`btn ${payMethod===m?'btn-primary':'btn-secondary'}`}
                      style={{flex:1,justifyContent:'center'}}>
                      {m==='CASH'?'💵':m==='CARD'?'💳':'📱'} {m}
                    </button>
                  ))}
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={()=>setPayModal(null)}>Cancel</button>
              <button className="btn btn-success" onClick={processPayment}>✅ Confirm Payment</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

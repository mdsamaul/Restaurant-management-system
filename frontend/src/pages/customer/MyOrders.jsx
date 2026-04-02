import React, { useState, useEffect } from 'react'
import { orderAPI } from '../../services/api'
import toast from 'react-hot-toast'

const SC = { PENDING:'badge-warning',CONFIRMED:'badge-info',IN_PROGRESS:'badge-purple',READY:'badge-orange',SERVED:'badge-success',CLOSED:'badge-gray',CANCELLED:'badge-danger' }
const ICONS = { PENDING:'⏳',CONFIRMED:'✅',IN_PROGRESS:'👨‍🍳',READY:'🔔',SERVED:'🍽️',CLOSED:'💰',CANCELLED:'❌' }

export default function MyOrders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState(null)

  const load = () => orderAPI.getMy().then(r=>setOrders(r.data.data||[])).catch(()=>toast.error('Failed')).finally(()=>setLoading(false))
  useEffect(()=>{ load() },[])

  const cancel = async (id) => {
    if (!confirm('Cancel this order?')) return
    try { await orderAPI.cancel(id); toast.success('Order cancelled'); load() }
    catch(e) { toast.error(e.response?.data?.message||'Cannot cancel') }
  }

  if (loading) return <div className="page-content"><div className="loading-page"><div className="spinner"/></div></div>

  return (
    <div className="page-content">
      <div className="flex-between page-header">
        <div><h1>📋 My Orders</h1><p>Track all your orders</p></div>
        <button className="btn btn-secondary" onClick={load}>🔄 Refresh</button>
      </div>

      {orders.length===0
        ? <div className="card"><div className="empty-state"><div className="icon">📋</div><h3>No orders yet</h3><p>Browse the menu and place your first order!</p></div></div>
        : <div style={{display:'flex',flexDirection:'column',gap:'14px'}}>
            {orders.map(o=>(
              <div key={o.id} className="card" style={{padding:'20px',cursor:'pointer',transition:'box-shadow .2s'}}
                onClick={()=>setSelected(selected?.id===o.id?null:o)}>
                <div style={{display:'flex',alignItems:'center',justifyContent:'space-between',flexWrap:'wrap',gap:'12px'}}>
                  <div style={{display:'flex',alignItems:'center',gap:'14px'}}>
                    <div style={{fontSize:'32px'}}>{ICONS[o.status]}</div>
                    <div>
                      <div style={{fontWeight:700,fontSize:'16px'}}>Order #ORD-{o.id}</div>
                      <div style={{fontSize:'13px',color:'var(--text-muted)'}}>{o.createdAt?new Date(o.createdAt).toLocaleString():'—'}</div>
                      <div style={{fontSize:'13px',marginTop:'2px'}}>{(o.orderItems||[]).length} items {o.tableNumber?`• Table ${o.tableNumber}`:''}</div>
                    </div>
                  </div>
                  <div style={{textAlign:'right'}}>
                    <span className={`badge ${SC[o.status]}`} style={{marginBottom:'6px',display:'block'}}>{o.status}</span>
                    <div style={{fontSize:'18px',fontWeight:800,color:'var(--primary)'}}>৳{parseFloat(o.totalAmount||0).toFixed(0)}</div>
                  </div>
                </div>
                {selected?.id===o.id && (
                  <div style={{marginTop:'16px',borderTop:'1px solid var(--border)',paddingTop:'16px'}}>
                    {(o.orderItems||[]).map(item=>(
                      <div key={item.id} style={{display:'flex',justifyContent:'space-between',padding:'6px 0',fontSize:'14px'}}>
                        <span>{item.menuItemName} × {item.quantity}</span>
                        <span className="price">৳{parseFloat(item.subtotal||0).toFixed(0)}</span>
                      </div>
                    ))}
                    <div style={{display:'flex',justifyContent:'space-between',fontWeight:700,fontSize:'15px',borderTop:'1px solid var(--border)',paddingTop:'8px',marginTop:'6px'}}>
                      <span>Total (incl. tax)</span>
                      <span style={{color:'var(--primary)'}}>৳{parseFloat(o.totalAmount||0).toFixed(0)}</span>
                    </div>
                    {(o.status==='PENDING'||o.status==='CONFIRMED') && (
                      <button className="btn btn-danger btn-sm" style={{marginTop:'12px'}} onClick={e=>{e.stopPropagation();cancel(o.id)}}>
                        ❌ Cancel Order
                      </button>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
      }
    </div>
  )
}

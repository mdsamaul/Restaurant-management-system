package com.rms.service;
import com.rms.dto.request.CreateOrderRequest;
import com.rms.dto.response.OrderResponse;
import com.rms.entity.Order;
import java.util.List;
public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest req, String customerEmail);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getMyOrders(String customerEmail);
    List<OrderResponse> getOrdersByStatus(Order.OrderStatus status);
    OrderResponse updateStatus(Long id, Order.OrderStatus newStatus);
    void cancelOrder(Long id, String userEmail);
}

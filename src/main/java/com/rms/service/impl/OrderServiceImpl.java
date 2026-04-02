package com.rms.service.impl;
import com.rms.dto.request.CreateOrderRequest;
import com.rms.dto.response.OrderResponse;
import com.rms.entity.*;
import com.rms.exception.BadRequestException;
import com.rms.exception.ResourceNotFoundException;
import com.rms.repository.*;
import com.rms.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;
    private final MenuItemRepository menuItemRepo;
    private final RestaurantTableRepository tableRepo;
    private final UserRepository userRepo;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");

    @Override
    public OrderResponse createOrder(CreateOrderRequest req, String customerEmail) {
        User customer = userRepo.findByEmail(customerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = Order.builder().customer(customer).notes(req.getNotes()).build();

        if (req.getTableId() != null) {
            RestaurantTable table = tableRepo.findById(req.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found: " + req.getTableId()));
            order.setTable(table);
            table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
            tableRepo.save(table);
        }

        for (CreateOrderRequest.OrderItemRequest itemReq : req.getItems()) {
            MenuItem menuItem = menuItemRepo.findById(itemReq.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemReq.getMenuItemId()));
            if (!menuItem.getIsAvailable())
                throw new BadRequestException("Menu item not available: " + menuItem.getName());
            OrderItem oi = OrderItem.builder().order(order).menuItem(menuItem)
                .quantity(itemReq.getQuantity()).unitPrice(menuItem.getPrice())
                .specialRequest(itemReq.getSpecialRequest()).build();
            order.getOrderItems().add(oi);
        }

        BigDecimal subtotal = order.getOrderItems().stream()
            .map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(TAX_RATE);
        order.setTotalAmount(subtotal.add(tax));
        order.setTaxAmount(tax);

        return OrderResponse.from(orderRepo.save(order));
    }

    @Override @Transactional(readOnly=true)
    public OrderResponse getOrderById(Long id) {
        return OrderResponse.from(orderRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    @Override @Transactional(readOnly=true)
    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAll().stream().map(OrderResponse::from).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly=true)
    public List<OrderResponse> getMyOrders(String customerEmail) {
        User customer = userRepo.findByEmail(customerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepo.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
            .stream().map(OrderResponse::from).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly=true)
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepo.findByStatusOrderByCreatedAtAsc(status)
            .stream().map(OrderResponse::from).collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateStatus(Long id, Order.OrderStatus newStatus) {
        Order order = orderRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setStatus(newStatus);
        if (newStatus == Order.OrderStatus.CLOSED && order.getTable() != null) {
            order.getTable().setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableRepo.save(order.getTable());
        }
        return OrderResponse.from(orderRepo.save(order));
    }

    @Override
    public void cancelOrder(Long id, String userEmail) {
        Order order = orderRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS || order.getStatus() == Order.OrderStatus.READY)
            throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
        order.setStatus(Order.OrderStatus.CANCELLED);
        if (order.getTable() != null) {
            order.getTable().setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableRepo.save(order.getTable());
        }
        orderRepo.save(order);
    }
}

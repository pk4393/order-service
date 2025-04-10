package com.example.order_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.order_service.entity.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.order_service.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  // Find orders containing a specific product
  @Query("SELECT DISTINCT o FROM OrderEntity o JOIN o.orderItems oi WHERE oi.productId = :productId")
  List<OrderEntity> findOrdersByProductId(@Param("productId") Long productId);

  // Find orders within a date range
  @Query("SELECT o FROM OrderEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate")
  List<OrderEntity> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  // Find orders containing a specific product within a date range
  @Query("SELECT DISTINCT o FROM OrderEntity o JOIN o.orderItems oi "
      + "WHERE oi.productId = :productId AND o.createdAt BETWEEN :startDate AND :endDate")
  List<OrderEntity> findOrdersByProductAndDateRange(@Param("productId") Long productId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


  @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId")
  Page<OrderEntity> findOrdersByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.order.orderId IN :orderIds")
  List<OrderItemEntity> findOrderItemsByOrderIds(@Param("orderIds") List<Long> orderIds);

}

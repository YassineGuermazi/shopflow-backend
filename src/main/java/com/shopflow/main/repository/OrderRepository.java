package com.shopflow.main.repository;

import com.shopflow.main.entity.Order;
import com.shopflow.main.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    boolean existsByCustomerIdAndItemsProductIdAndStatut(Long customerId, Long productId, OrderStatus statut);

    @Query("SELECT SUM(o.totalTTC) FROM Order o WHERE o.statut != 'CANCELLED'")
    Optional<BigDecimal> sumTotalTTC();

    @Query("SELECT SUM(o.totalTTC) FROM Order o JOIN o.items oi WHERE oi.product.seller.id = :sellerId AND o.statut != 'CANCELLED'")
    Optional<BigDecimal> sumRevenueBySeller(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(o) FROM Order o JOIN o.items oi WHERE oi.product.seller.id = :sellerId AND o.statut = :statut")
    Long countBySellerIdAndStatut(@Param("sellerId") Long sellerId, @Param("statut") OrderStatus statut);

    List<Order> findTop10ByOrderByDateCommandeDesc();

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.seller.id = :sellerId ORDER BY o.dateCommande DESC")
    List<Order> findRecentOrdersBySeller(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT oi.product.id, oi.product.nom, SUM(oi.quantite), SUM(oi.prixUnitaire * oi.quantite) " +
            "FROM OrderItem oi GROUP BY oi.product.id, oi.product.nom ORDER BY SUM(oi.quantite) DESC")
    List<Object[]> findTopProducts(Pageable pageable);

    @Query("SELECT sp.user.id, sp.nomBoutique, SUM(o.totalTTC), COUNT(DISTINCT o.id) " +
            "FROM Order o JOIN o.items oi JOIN oi.product p JOIN p.seller s JOIN s.sellerProfile sp " +
            "WHERE o.statut != 'CANCELLED' GROUP BY sp.user.id, sp.nomBoutique ORDER BY SUM(o.totalTTC) DESC")
    List<Object[]> findTopSellers(Pageable pageable);
}

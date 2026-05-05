package com.shopflow.main.service;

import com.shopflow.main.dto.dashboard.*;
import com.shopflow.main.entity.*;
import com.shopflow.main.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public AdminDashboardResponse getAdminDashboard() {
        BigDecimal totalRevenue = orderRepository.sumTotalTTC().orElse(BigDecimal.ZERO);
        Long totalOrders = orderRepository.count();
        Long totalUsers = userRepository.count();
        Long totalProducts = productRepository.count();

        List<TopProductDTO> topProducts = orderRepository.findTopProducts(PageRequest.of(0, 10))
                .stream().map(r -> new TopProductDTO(
                        (Long) r[0], (String) r[1], (Long) r[2], (BigDecimal) r[3]))
                .collect(Collectors.toList());

        List<TopSellerDTO> topSellers = orderRepository.findTopSellers(PageRequest.of(0, 10))
                .stream().map(r -> new TopSellerDTO(
                        (Long) r[0], (String) r[1], (BigDecimal) r[2], (Long) r[3]))
                .collect(Collectors.toList());

        List<RecentOrderDTO> recentOrders = orderRepository
                .findTop10ByOrderByDateCommandeDesc()
                .stream().map(o -> new RecentOrderDTO(
                        o.getId(), o.getNumeroCommande(),
                        o.getCustomer().getPrenom() + " " + o.getCustomer().getNom(),
                        o.getTotalTTC(), o.getStatut(), o.getDateCommande()))
                .collect(Collectors.toList());

        return AdminDashboardResponse.builder()
                .chiffreAffairesGlobal(totalRevenue)
                .totalCommandes(totalOrders)
                .totalUtilisateurs(totalUsers)
                .totalProduits(totalProducts)
                .topProduits(topProducts)
                .topVendeurs(topSellers)
                .commandesRecentes(recentOrders)
                .build();
    }

    public SellerDashboardResponse getSellerDashboard() {
        User seller = getCurrentUser();
        BigDecimal revenus = orderRepository.sumRevenueBySeller(seller.getId()).orElse(BigDecimal.ZERO);
        Long pending = orderRepository.countBySellerIdAndStatut(seller.getId(), OrderStatus.PENDING);
        Long totalProducts = productRepository.countBySellerId(seller.getId());

        List<LowStockProductDTO> lowStock = productRepository.findLowStockBySeller(seller.getId(), 5)
                .stream().map(p -> new LowStockProductDTO(p.getId(), p.getNom(), p.getStock()))
                .collect(Collectors.toList());

        List<RecentOrderDTO> recentOrders = orderRepository.findRecentOrdersBySeller(seller.getId(), PageRequest.of(0, 10))
                .stream().map(o -> new RecentOrderDTO(
                        o.getId(), o.getNumeroCommande(),
                        o.getCustomer().getPrenom() + " " + o.getCustomer().getNom(),
                        o.getTotalTTC(), o.getStatut(), o.getDateCommande()))
                .collect(Collectors.toList());

        return SellerDashboardResponse.builder()
                .revenus(revenus)
                .commandesEnAttente(pending)
                .totalProduits(totalProducts)
                .produitsStockFaible(lowStock)
                .commandesRecentes(recentOrders)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.shopflow.main.exception.BusinessException("User not found"));
    }
}
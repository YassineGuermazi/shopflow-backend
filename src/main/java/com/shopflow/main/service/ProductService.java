package com.shopflow.main.service;

import com.shopflow.main.dto.product.*;
import com.shopflow.main.entity.*;
import com.shopflow.main.exception.BusinessException;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.exception.UnauthorizedException;
import com.shopflow.main.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public Page<ProductResponse> getAllProducts(int page, int size, Long categoryId,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                Long sellerId, Boolean promo) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<Product> products;

        if (categoryId != null) {
            products = productRepository.findByCategoriesIdAndActifTrue(categoryId, pageable);
        } else if (promo != null && promo) {
            products = productRepository.findByPrixPromoIsNotNullAndActifTrue(pageable);
        } else if (sellerId != null) {
            products = productRepository.findBySellerIdAndActifTrue(sellerId, pageable);
        } else {
            products = productRepository.findByActifTrue(pageable);
        }

        return products.map(this::toResponse);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return toResponseWithDetails(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User seller = getCurrentUser();
        if (seller.getRole() != Role.SELLER && seller.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only sellers can create products");
        }

        Product product = Product.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .prixPromo(request.getPrixPromo())
                .stock(request.getStock())
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .seller(seller)
                .images(request.getImages())
                .build();

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(categories);
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = getOwnedProduct(id);
        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setPrixPromo(request.getPrixPromo());
        product.setStock(request.getStock());
        product.setImages(request.getImages());

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(categories);
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getOwnedProduct(id);
        product.setActif(false);
        productRepository.save(product);
    }

    public Page<ProductResponse> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndActifTrue(query, query, pageable)
                .map(this::toResponse);
    }

    public List<ProductResponse> getTopSelling() {
        return productRepository.findTopSelling(PageRequest.of(0, 10))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ---- Mapper helpers ----

    private ProductResponse toResponse(Product p) {
        Double avgRating = reviewRepository.findAverageRatingByProductId(p.getId());
        Long reviewCount = reviewRepository.countByProductIdAndApprouveTrue(p.getId());
        return ProductResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .description(p.getDescription())
                .prix(p.getPrix())
                .prixPromo(p.getPrixPromo())
                .stock(p.getStock())
                .actif(p.isActif())
                .dateCreation(p.getDateCreation())
                .sellerName(p.getSeller().getPrenom() + " " + p.getSeller().getNom())
                .sellerId(p.getSeller().getId())
                .images(p.getImages())
                .categories(p.getCategories() != null
                        ? p.getCategories().stream().map(this::toCategoryResponse).collect(Collectors.toList())
                        : List.of())
                .categoryIds(p.getCategories() != null
                        ? p.getCategories().stream().map(Category::getId).collect(Collectors.toList())
                        : List.of())
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .build();
    }

    private ProductResponse toResponseWithDetails(Product p) {
        ProductResponse response = toResponse(p);
        List<ProductVariantResponse> variants = productVariantRepository.findByProductId(p.getId())
                .stream().map(v -> ProductVariantResponse.builder()
                        .id(v.getId())
                        .attribut(v.getAttribut())
                        .valeur(v.getValeur())
                        .stockSupplementaire(v.getStockSupplementaire())
                        .prixDelta(v.getPrixDelta())
                        .build())
                .collect(Collectors.toList());
        response.setVariants(variants);
        return response;
    }

    private com.shopflow.main.dto.category.CategoryResponse toCategoryResponse(Category c) {
        return com.shopflow.main.dto.category.CategoryResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .description(c.getDescription())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .build();
    }

    private Product getOwnedProduct(Long id) {
        User current = getCurrentUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (current.getRole() != Role.ADMIN && !product.getSeller().getId().equals(current.getId())) {
            throw new UnauthorizedException("You don't own this product");
        }
        return product;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found"));
    }
}
package com.soodora.infrastructure.sale;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.sale.Sale;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.sale.SaveSaleCommand;
import com.soodora.domain.user.value.Identifier;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.common.MoneyEmbeddable;
import com.soodora.infrastructure.product.ProductEntity;
import com.soodora.infrastructure.product.ProductJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class SaleRepositoryImpl implements SaleRepository {

    private final SaleJpaRepository repository;
    private final ArtistJpaRepository artistRepository;
    private final UserJpaRepository userRepository;
    private final ProductJpaRepository productRepository;

    @Override
    @Transactional
    public Sale save(SaveSaleCommand sale) {
        SaleEntity entity = new SaleEntity();
        UserEntity buyerEntity = userRepository.findBySlug(sale.buyer().getSlug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + sale.buyer().getSlug()));
        ArtistEntity vendorEntity = artistRepository.findByUserSlug(sale.vendor().getSlug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist #" + sale.vendor().getSlug()));
        ProductEntity productEntity = productRepository.findBySlug(sale.product().getSlug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find product#" + sale.product().getSlug()));

        entity.setProduct(productEntity);
        entity.setQuantitySold(sale.quantitySold());
        entity.setAmount(new MoneyEmbeddable(sale.amount().amount(), sale.amount().currency()));
        entity.setSlug(generateUniqueSlug());
        entity.setVendor(vendorEntity);
        entity.setBuyer(buyerEntity);
        return repository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public Sale findBySlug(String slug) {
        return findSaleBySlugOrThrow(slug).aggregate();

    }

    @Override
    @Transactional
    public Page<Sale> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<Sale> findArtistSales(Identifier identifier, Pageable pageable) {
        return repository.findByVendorUserSlug(identifier.value(), pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public void deleteSaleBySlug(String slug) {
        Optional<SaleEntity> sale = repository.findBySlug(slug);

        repository.deleteById(sale.orElseThrow().getId());
    }

    protected SaleEntity findSaleBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find sale with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }
}

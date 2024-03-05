package com.quote.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quote.entity.QuoteRate;

@Repository
public interface QuoteRateRepository extends JpaRepository<QuoteRate, String> {
   List<QuoteRate> findTop50ByOrderByRateDesc();
   List<QuoteRate> findAllByQuoteIdIn(Set<String> ids);
}

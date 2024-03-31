package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.repositories.LimitRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class LimitServiceImpl implements LimitService {

    private static final BigDecimal DEFAULT_LIMIT_VALUE = new BigDecimal(1_000);

    @Value("${local-zoned-id}")
    private String localZonedId;

    private final LimitRepository limitRepository;

    public LimitServiceImpl(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    @Override
    public Optional<Limit> findLastLimit() {
        return limitRepository.findByMaxId();
    }

    @Override
    public Limit createNewDefaultLimit(Category category) {
        // TODO: 30.03.2024 Если хватит времени - реализовать помесячное автоматическое обновление лимитов
        /* Создаем ДЕФОЛТНЫЙ лимит как бы в начале месяца (а по факту в момент первой транзакции в этом месяце) -
           - имитация автоматического создания дефолтного лимита реально в начале месяца */
        Limit newDefaultLimit = new Limit();

        newDefaultLimit.setForCategory(category);
        newDefaultLimit.setDatetimeStarts(getStartOfMonth());
        newDefaultLimit.setUsdValue(DEFAULT_LIMIT_VALUE);

        return limitRepository.save(newDefaultLimit);
    }

    @Override
    public void createNewCustomLimit(Category forCategory, BigDecimal value) {
        Limit newCastomLimit = new Limit();

        newCastomLimit.setForCategory(forCategory);
        newCastomLimit.setDatetimeStarts(ZonedDateTime.now());
        newCastomLimit.setUsdValue(value);

        limitRepository.save(newCastomLimit);
    }

    @Override
    public void addTransaction(Transaction newTransaction, Limit actualLimit) {
        Limit updatedLimit = actualLimit.addTransaction(newTransaction);
        limitRepository.save(updatedLimit);

    }

    @Override
    public ZonedDateTime getStartOfMonth() {
        Instant now = Instant.now();
        return now.atZone(ZoneId.of(localZonedId))
                .withDayOfMonth(1)
                .with(LocalTime.MIN);
    }
}

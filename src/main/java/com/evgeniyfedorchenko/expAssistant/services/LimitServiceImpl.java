package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.repositories.LimitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class LimitServiceImpl implements LimitService {

    private static final BigDecimal DEFAULT_LIMIT_VALUE = new BigDecimal(1000);

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
        Limit newDefaultLimit = new Limit();

        newDefaultLimit.setForCategory(category);
        newDefaultLimit.setDatetimeStarts(getStartOfMonth());
        newDefaultLimit.setValue(DEFAULT_LIMIT_VALUE);

        return limitRepository.save(newDefaultLimit);

    }

    @Override
    public void createNewCustomLimit(Category forCategory, BigDecimal value) {
        Limit newCastomLimit = new Limit();

        newCastomLimit.setForCategory(forCategory);
        newCastomLimit.setDatetimeStarts(getStartOfMonth());
        newCastomLimit.setValue(value);

        limitRepository.save(newCastomLimit);
    }

    private ZonedDateTime getStartOfMonth() {

        return ZonedDateTime.now()
                .withDayOfMonth(1)
                .with(LocalTime.MIN);
    }
}

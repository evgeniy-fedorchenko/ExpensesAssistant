package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.repositories.LimitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Класс для создания, регистрации и обслуживания объектов типа Limit
 * */
@Service
public class LimitServiceImpl implements LimitService {

    private static final BigDecimal DEFAULT_LIMIT_VALUE = new BigDecimal(1_000);
    @Value("${local-zoned-id}")
    private String localZonedId;
    private final Logger logger = LoggerFactory.getLogger(LimitServiceImpl.class);

    private final LimitRepository limitRepository;

    public LimitServiceImpl(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    /**
     * Метод для получения актуального лимита транзакций (последнего созданного)
     * @return Optional.empty(), если в базе данных еще не найдено ни одного лимита или
     *         Optional.of(Limit), если существует хотя бы один зарегистрированный в базе данных объект Limit
     * */
    @Override
    public Optional<Limit> findLastLimit() {
        Optional<Limit> byMaxId = limitRepository.findByMaxId();
        logger.info("Successfully found last limit { {} }", byMaxId.toString());
        return byMaxId;
    }

    /**
     * Метод для создания нового дефолтного лимита транзакций (недоступен с клиента).
     * Вызывается про регистрации первой транзакции месяца (для указанной категории) и устанавливает время
     * для создаваемого лимита на начало текущего месяца и сумму в 1_000 USD. Становится новым актуальным лимитом после создания
     * @param category доступная категория транзакций, для которой необходимо установить лимит
     * @return запрошенный объект Limit, сохраненный в базе данных
     * */
    @Override
    public Limit createNewDefaultLimit(Category category) {
        Limit newDefaultLimit = new Limit();

        newDefaultLimit.setForCategory(category);
        newDefaultLimit.setDatetimeStarts(getStartOfMonth());
        newDefaultLimit.setUsdValue(DEFAULT_LIMIT_VALUE);

        Limit savedLimit = limitRepository.save(newDefaultLimit);
        logger.info("Successfully create new default limit {}", savedLimit);
        return savedLimit;
    }

    /**
     * Метод для создания нового пользовательского лимита транзакций (доступен с клиента).
     * Устанавливает фактическое время вызова метода как ZonedDateTime.now(). Становится новым актуальным лимитом после создания
     * @param forCategory доступная категория транзакций, для которой необходимо установить лимит
     * @param value сумма устанавливаемого лимита
     * */
    @Override
    public void createNewCustomLimit(Category forCategory, BigDecimal value) {
        logger.debug("Was requested new custom limit for category={}, value={}", forCategory, value);
        Limit newCastomLimit = new Limit();

        newCastomLimit.setForCategory(forCategory);
        newCastomLimit.setDatetimeStarts(ZonedDateTime.now());
        newCastomLimit.setUsdValue(value);

        Limit savedLimit = limitRepository.save(newCastomLimit);
        logger.info("Successfully create new default Limit {}", savedLimit);

    }

    /**
     * Метод для поддержания актуальности поля List transactions в базе данных. Добавляет указанную транзакцию
     * к указанному лимиту и сохраняет в базе данных
     * @param newTransaction транзакция, которую нужно добавить в список транзакций существующего лимита
     *                       (транзакция должна быть предварительно сохранена в базе данных)
     * @param actualLimit валидный объект Limit, в список транзакций которого необходимо добавить указанную транзакцию
     * */
    @Override
    public void addTransaction(Transaction newTransaction, Limit actualLimit) {
        Limit updatedLimit = actualLimit.addTransaction(newTransaction);
        limitRepository.save(updatedLimit);
        logger.info("Add transaction {} to limit {} and save", newTransaction, actualLimit);

    }

    /** Метод для получения начальной точки отчета времени в текущем месяце. Рассчитывается как .now() и сбрасываются
     * к минимальным показателям значения дней и всех более мелких единиц времени
     * @return объект ZonedDateTime, как точка отсчета текущего месяца (для зонирования используется регион,
     *         указанный в конфигурационном файле)
     * */
    @Override
    public ZonedDateTime getStartOfMonth() {
        Instant now = Instant.now();
        ZonedDateTime startOfMonth = now.atZone(ZoneId.of(localZonedId))
                .withDayOfMonth(1)
                .with(LocalTime.MIN);
        logger.debug("Was invoked getStartOfMonth(), returned {}", startOfMonth);
        return startOfMonth;
    }
}

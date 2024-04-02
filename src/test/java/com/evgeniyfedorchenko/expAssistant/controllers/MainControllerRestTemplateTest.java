package com.evgeniyfedorchenko.expAssistant.controllers;

import com.evgeniyfedorchenko.expAssistant.TestUtils;
import com.evgeniyfedorchenko.expAssistant.client.TwelvedataClient;
import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.dto.TransactionOverLimitDto;
import com.evgeniyfedorchenko.expAssistant.entities.ExchangeRate;
import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.repositories.ExchangeRateRepository;
import com.evgeniyfedorchenko.expAssistant.repositories.LimitRepository;
import com.evgeniyfedorchenko.expAssistant.repositories.TransactionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.evgeniyfedorchenko.expAssistant.Constants.TRANSACTION_INPUT_DTO_CONSTANTS;
import static com.evgeniyfedorchenko.expAssistant.Constants.constantsInitialization;
import static com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    /*   Repositories   */
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private LimitRepository limitRepository;

    /*   Mocks   */
    @MockBean
    private TwelvedataClient clientMock;
    @InjectMocks
    private MainController mainController;

    /*   Others   */
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${local-zoned-id}")
    private String localZonedId;


    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @BeforeEach
    public void beforeEach() {
        constantsInitialization();
    }

    @AfterEach
    public void afterEach() {
        exchangeRateRepository.deleteAll();
        transactionRepository.deleteAll();
        limitRepository.deleteAll();
    }

    private String baseUrl() {
        return "http://localhost:%d".formatted(port);
    }

    @Test
    void commitTransactionTest() {

        TransactionInputDto targetTransaction = TRANSACTION_INPUT_DTO_CONSTANTS.get(0);

        when(clientMock.getCurrencyRate(RUB, USD)).thenReturn(BigDecimal.valueOf(0.01081D));
        when(clientMock.getCurrencyRate(KZT, USD)).thenReturn(BigDecimal.valueOf(0.00224D));

        long limitRepoCountBeforeInvoke = limitRepository.count();
        long rateRepoCountBeforeInvoke = exchangeRateRepository.count();

        ResponseEntity<Boolean> responseEntity = testRestTemplate.postForEntity(
                baseUrl() + "/transaction",
                targetTransaction,
                Boolean.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверка создания лимита
        assertThat(limitRepoCountBeforeInvoke + 1).isEqualTo(limitRepository.count());
        Optional<Limit> createdLimitOpt = limitRepository.findByMaxId();
        assertThat(createdLimitOpt).isPresent();

        Transaction expected = testUtils.fromDto(targetTransaction);
        List<Transaction> actualTransactions = createdLimitOpt.get()
                .getTransactions()
                .stream()
                .peek(transaction -> transaction.setSum(transaction.getSum().stripTrailingZeros()))
                .toList();

        // Проверка создания обменного курса
        List<ExchangeRate> all = exchangeRateRepository.findAll();
        if (!targetTransaction.getCurrency().equals(USD)) {
            assertThat(rateRepoCountBeforeInvoke + 1).isEqualTo(exchangeRateRepository.count());
        }

        assertThat(actualTransactions)
                .hasSize(1);

        /* Тк как expected - это локальный объект в этом тесте, он не прогоняется через алгоритмы приложения, где
           происходит присваивание лимита и выставление флага limitExceeded (даже одна транзакция может превысить лимит,
           так как их значения генерятся при инициализации тестовых констант). Следовательно, ему не присвоился свой лимит
           и limitExceeded, исправим это - посмотрим, превышен ли созданный лимит и поставим limitExceeded = true, если это так */
        expected.setLimit(createdLimitOpt.get());
        expected.setLimitExceeded(createdLimitOpt.get().getUsdValue()
                                          .compareTo
                                                  (createdLimitOpt.get().getTransactions().stream()
                                                          .map(Transaction::getSum)
                                                          .reduce(BigDecimal.ZERO, BigDecimal::add)) < 0);
        /* Комментарий к ignoringFields():
           id - ну тут все понятно, id присваивается в БД
           dateTime - если доставать из бд объект через приложение, то его дата подвергается форматированию по ZonedId,
           так что при ручном изъятии тоже надо это сделать*/
        assertThat(actualTransactions.get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "dateTime")
                .isEqualTo(expected);

        // Отдельно форматируем и проверяем дату
        ZonedDateTime actualFormattedZonedDateTime = actualTransactions.get(0)
                .getDateTime()
                .toInstant()
                .atZone(ZoneId.of(localZonedId));
        assertThat(actualFormattedZonedDateTime.compareTo(expected.getDateTime()))
                .isEqualTo(0);

        // Проверка создания транзакции
        Transaction actual = transactionRepository.findAll().stream()
                .max(Comparator.comparingLong(Transaction::getId))
                .orElseThrow();
        actual.setSum(actual.getSum().stripTrailingZeros());
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "dateTime")
                .isEqualTo(expected);
    }

    @Test
    void setNewLimitTest() {

        Category forCategory = Category.SERVICE;
        BigDecimal value = BigDecimal.valueOf(12345.67899);

        long countRepoBeforePosting = limitRepository.count();
        ResponseEntity<Boolean> responseEntity = testRestTemplate.postForEntity(
                baseUrl() + "/limit?forCategory={forCategory}&value={value}",
                null,
                Boolean.class,
                forCategory, value);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(countRepoBeforePosting + 1).isEqualTo(limitRepository.count());

        Limit postedLimit = limitRepository.findByMaxId().orElseThrow();
        assertThat(postedLimit.getUsdValue()).isEqualTo(value);
        assertThat(postedLimit.getForCategory()).isEqualTo(forCategory);
    }

    @Test
    void getOverLimitTransactions() {
        when(clientMock.getCurrencyRate(RUB, USD)).thenReturn(BigDecimal.valueOf(0.01081D));
        when(clientMock.getCurrencyRate(KZT, USD)).thenReturn(BigDecimal.valueOf(0.00224D));

        // Сохраняем много транзакций, чтобы они превысили лимит
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            TransactionInputDto randomTransaction = TRANSACTION_INPUT_DTO_CONSTANTS.get(random.nextInt(0, TRANSACTION_INPUT_DTO_CONSTANTS.size()));
            randomTransaction.setSum(randomTransaction.getSum() * 10);

            testRestTemplate.postForEntity(
                    baseUrl() + "/transaction",
                    randomTransaction,
                    Void.class);
        }

        ResponseEntity<List<TransactionOverLimitDto>> responseEntity = testRestTemplate.exchange(
                baseUrl() + "/over-limit",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        /* Руками берем из БД все транзакции с флагом limitExceeded = true и сравниваем с транзакциями вернувшимся из приложения:
           в транзакциях из бд обрезаем незначащие нули, а транзакции вернувшиеся с сервера мапим на тип Transaction,
           чтоб можно было сравнить напрямую: TransactionOverLimitDto -> Transaction */
        List<Transaction> overLimitFromDb = transactionRepository.findAll().stream()
                .filter(Transaction::isLimitExceeded)
                .peek(transaction -> transaction.setSum(transaction.getSum().stripTrailingZeros()))
                .toList();

        List<Transaction> overLimitFromApp = responseEntity.getBody().stream()
                .map(transactionOverLimit -> testUtils.fromOverLimitDto(transactionOverLimit))
                .toList();

        assertThat(overLimitFromDb.size()).isEqualTo(responseEntity.getBody().size());
        assertThat(overLimitFromDb.size() > 0).isTrue();
        assertThat(overLimitFromApp.size() > 0).isTrue();

        /* Комментарий к ignoringFields():
           id - в пришедшей с сервера объектах нет такого поля, тк (по заданию + эта тех.инфо, ненужная юзеру)
           limit - так же отсутствует в структуре объекта TransactionOverLimitDto, которая была задана в задании
           limitExceeded - в пришедших с сервера объектах нет такого поля (по заданию), а так же он там и не нужен,
                           тк сам факт наличия этого объекта в ответе говорит о том, что он "overLimit".
            Оставшиеся поля, подлежащие сравнению: accountFrom, accountTo, sum, currency, category */
        assertThat(overLimitFromDb)
                .usingRecursiveComparison()
                .ignoringFields("id", "limit", "limitExceeded")
                .isEqualTo(overLimitFromApp);

    }
}

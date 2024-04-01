package com.evgeniyfedorchenko.expAssistant;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.evgeniyfedorchenko.expAssistant.services.TransactionServiceImpl.DEFAULT_ACCOUNT_FROM_VALUE;

@Component
public class Constants {

    private static final Faker FAKER = new Faker();

    private static String localZonedId;

    private static final TestUtils testUtils = new TestUtils();

    public static final List<TransactionInputDto> TRANSACTION_INPUT_DTO_CONSTANTS = new ArrayList<>();
    public static final Limit FIRST_DEFAULT_SERVICE_LIMIT = new Limit();
    public static final Limit FIRST_DEFAULT_PRODUCT_LIMIT = new Limit();


    public static void constantsInitialization() {
        transactionInputDtoConstantsInitialize();
        limitConstantsInitialize();
    }

    @Value("${local-zoned-id}")
    public void initZonedId(String value) {
        localZonedId = value;
    }

    private static void transactionInputDtoConstantsInitialize() {

        Stream.generate(TransactionInputDto::new)
                .limit(10)
                .forEach(inputDto -> {
                    inputDto.setAccountTo(String.valueOf(FAKER.random().nextLong(0, 9_999_999_999L)));
                    inputDto.setAccountFrom(String.valueOf(DEFAULT_ACCOUNT_FROM_VALUE));

                    int rndCurrencyNum = FAKER.random().nextInt(CurrencyShortName.values().length);
                    inputDto.setCurrency(CurrencyShortName.values()[rndCurrencyNum]);

                    int rndCategoryNum = FAKER.random().nextInt(Category.values().length);
                    inputDto.setExpenseCategory(Category.values()[rndCategoryNum]);

                    String value = String.format("%.5f", FAKER.random().nextDouble()).replace(",", ".");
                    inputDto.setSum(Math.abs(Double.parseDouble(value)));

                    Instant randomInstant = FAKER.date().past(1, TimeUnit.HOURS).toInstant();
                    inputDto.setDateTime(randomInstant.atZone(ZoneId.of(localZonedId)));

                    TRANSACTION_INPUT_DTO_CONSTANTS.add(inputDto);

                });
    }
    private static void limitConstantsInitialize() {
        FIRST_DEFAULT_PRODUCT_LIMIT.setForCategory(Category.PRODUCT);
        FIRST_DEFAULT_PRODUCT_LIMIT.setDatetimeStarts(testUtils.getStartOfMonth());
        FIRST_DEFAULT_PRODUCT_LIMIT.setUsdValue(BigDecimal.valueOf(Math.abs(FAKER.random().nextDouble() * 10)));

        FIRST_DEFAULT_SERVICE_LIMIT.setForCategory(Category.SERVICE);
        FIRST_DEFAULT_SERVICE_LIMIT.setDatetimeStarts(testUtils.getStartOfMonth());
        FIRST_DEFAULT_SERVICE_LIMIT.setUsdValue(BigDecimal.valueOf(Math.abs(FAKER.random().nextDouble() * 10)));

    }
}

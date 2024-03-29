package com.evgeniyfedorchenko.expAssistant.services;

import java.math.BigDecimal;

public interface ExchangeRateService {

    String updateRubUsdRate();

    String updateRubKztRate();

    String updateKztUsdRate();

    BigDecimal getUsdRubRate();   // 92,4500

    BigDecimal getRubKztRate();   // 4,8300

    BigDecimal getUsdKztRate();   // 447,1842

}
